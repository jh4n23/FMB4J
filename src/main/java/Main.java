import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import FOL.*;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.*;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Main {
    public static void main(String[] args) {
        String fileName = args[0];
        boolean verboseMode = false;
        int timeout = Integer.parseInt(args[1]);
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorService.submit(
                () -> runModelBuilder(fileName, verboseMode)
        );
        try {
            boolean modelFound = result.get(timeout, TimeUnit.SECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            System.out.println("% SZS status Timeout");
        } catch (InterruptedException e) {
            System.err.println("% SZS status Forced");
        } catch (Exception e) {
            System.err.println("% SZS status Error");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            long finish = System.currentTimeMillis();
            result.cancel(true);
            executorService.shutdownNow();
            System.exit(0);
        }
    }

    public static Boolean runModelBuilder(String fileName, boolean verboseMode) throws IOException, TimeoutException, InterruptedException {
        Pair<Boolean, ArrayList<ArrayList<Literal>>> load = loadFormulas(fileName);
        boolean hasConjecture = load.getFirst();
        ArrayList<ArrayList<Literal>> flatFormulas = flattenFormulas(load.getSecond());
        // System.out.println(flatFormulas);

        int currentDomainSize = 1;
        while (!Thread.interrupted()) {
            System.out.println("% domain size: " + currentDomainSize);
            ModelSolver modelSolver = new ModelSolver();
            ModelBuilder modelBuilder = new ModelBuilder(modelSolver, currentDomainSize);

            Pair<ArrayList<ArrayList<Literal>>, ArrayList<ArrayList<Literal>>> functionConstraints
                    = modelBuilder.functionConstraints(flatFormulas);

            try {
                for (ArrayList<Literal> flatClause : flatFormulas) modelBuilder.ground(flatClause);
                for (ArrayList<Literal> constraint : functionConstraints.getFirst()) modelBuilder.ground(constraint);
                for (ArrayList<Literal> constraint : functionConstraints.getSecond()) modelBuilder.ground(constraint);
            } catch (ContradictionException e) {
                System.out.println("% Unsatisfiable due to contradictory clauses?");
                System.err.println("% SZS status GaveUp");
                System.exit(1);
            }

            ISolver solver = modelSolver.getSolver();
            if (solver.isSatisfiable()) {
                String problem = new File(fileName).getName().replaceFirst("[.][^.]+$", "");
                System.out.println("% model found for domain size " + currentDomainSize);
                System.out.println("% SZS status " + (hasConjecture ? "CounterSatisfiable" : "Satisfiable") + " for " + problem);
                System.out.println("% SZS output start FiniteModel for " + problem);
                System.out.println("fof(domain, interpretation-domains,");
                System.out.print("\t![X]: (X = \"d0\"");
                for(int i = 1; i < currentDomainSize; i++) {
                    System.out.print(" | X = \"d" + i + "\"");
                }
                System.out.println(")\n).\n");

                Pair<HashMap<Literal, Boolean>, ArrayList<Pair<Function, Function>>> decodedModel = decodeModel(solver.model(), modelSolver);

                System.out.println("fof(mappings, interpretation-mappings,");
                boolean firstFunction = true;
                if(decodedModel.getSecond().size() == 0)
                    System.out.println("$true");
                for(Pair<Function, Function> assignment : decodedModel.getSecond()) {
                    Function defined = assignment.getFirst();
                    if(defined.getName().startsWith("sK"))
                        continue;
                    System.out.print("\t" + (firstFunction ? "  " : "& ") + defined.getName());
                    firstFunction = false;
                    if(defined.getArgs().size() > 0) {
                        System.out.print("(");
                        boolean firstArg = true;
                        for(Term t : defined.getArgs()) {
                            if(!firstArg) {
                                System.out.print(", ");
                            }
                            firstArg = false;
                            System.out.print("\"" + t + "\"");
                        }
                        System.out.print(")");
                    }
                    System.out.println(" = \"" + assignment.getSecond() + "\"");
                }
                if(firstFunction)
                    System.out.println("\t$true");
                System.out.println(").\n");

                System.out.println("fof(predicate_mappings, interpretation-mappings,");
                boolean firstPredicate = true;
                for(Map.Entry<Literal, Boolean> assignment : decodedModel.getFirst().entrySet()) {
                    Predicate defined = assignment.getKey().getPredicate();
                    String name = defined.getName();
                    if(name.equals("equals") || name.startsWith("sP") || name.startsWith("sQ"))
                        continue;
                    System.out.print("\t" + (firstPredicate ? "  " : "& ") + (assignment.getValue() ? " " : "~") + defined.getName());
                    firstPredicate = false;
                    if(defined.getArgs().size() > 0) {
                        System.out.print("(");
                        boolean firstArg = true;
                        for(Term t : defined.getArgs()) {
                            if(!firstArg) {
                                System.out.print(", ");
                            }
                            firstArg = false;
                            System.out.print("\"" + t + "\"");
                        }
                        System.out.print(")");
                    }
                    System.out.println();
                }
                if(firstPredicate)
                    System.out.println("\t$true");
                System.out.println(").");
                System.out.println("% SZS output end FiniteModel for " + problem);
                return true;
            }
            currentDomainSize++;
        }
        return false;
    }

    /**
     * Method to convert the SAT model back into truth assignments of the predicates from the original problem
     * @param model the SAT model
     * @param modelSolver the solver object containing the mapping of predicates to propositional variables
     * @return the model in terms of truth assignments to (ground) literals
     */
    private static Pair<HashMap<Literal, Boolean>, ArrayList<Pair<Function, Function>>> decodeModel(int[] model, ModelSolver modelSolver) {
        HashMap<Literal, Boolean> decodedModel = new HashMap<>();
        HashMap<Predicate, Integer> propVarMap = modelSolver.getPropVarMap();
        Map<Integer, Predicate> inversePropVarMap = propVarMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        for (int literal : model) {
            boolean polarity = literal > 0;
            decodedModel.put(new Literal(inversePropVarMap.get(Math.abs(literal)), true), polarity);
        }
        ArrayList<Pair<Function, Function>> functionAssignments = getFunctionAssignments(decodedModel);
        return Pair.of(decodedModel, functionAssignments);
    }

    /**
     * Method to extract function assignments from the decoded model
     * @param decodedModel model in terms of truth assignments to (ground) literals
     * @return A list of pairs of functions defining the mapping of functions to constants
     */
    public static ArrayList<Pair<Function, Function>> getFunctionAssignments(HashMap<Literal, Boolean> decodedModel) {
        ArrayList<Pair<Function, Function>> functionAssignments = new ArrayList<>();
        for (Literal literal : decodedModel.keySet()) {
            if (decodedModel.get(literal) && literal.getPredicate().getName().equals("equals")) {
                ArrayList<Term> args = literal.getPredicate().getArgs();
                functionAssignments.add(Pair.of((Function)args.get(1), (Function)args.get(0)));
            }
        }
        return functionAssignments;
    }

    /**
     * Method to parse a CNF problem file and produce a tree containing its formulas
     * @param fileName name of file containing CNF problem
     * @return the parsed problem in tree form (and whether there is a conjecture present)
     * @throws IOException if there is a problem reading the file
     */
    private static Pair<Boolean, ArrayList<ArrayList<Literal>>> loadFormulas(String fileName) throws IOException {
        CharStream charStream = CharStreams.fromFileName(fileName);
        CNFLexer lexer = new CNFLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CNFParser parser = new CNFParser(tokens);
        CNFParser.Tptp_fileContext tree = parser.tptp_file();
        ArrayList<ArrayList<Literal>> result = new ArrayList();
        boolean hasConjecture = false;
        for(Pair<Boolean, ArrayList<Literal>> clause : tree.formulas) {
            result.add(clause.getSecond());
            hasConjecture = hasConjecture || clause.getFirst();
        }
        return Pair.of(hasConjecture, result);
    }

    /**
     * Method to flatten CNF formulas
     * @param formulas the parsed formulas in their original form
     * @return a list containing the flattened formulas
     */
    private static ArrayList<ArrayList<Literal>> flattenFormulas(ArrayList<ArrayList<Literal>> formulas) {
        ArrayList<ArrayList<Literal>> flatFormulas = new ArrayList<>();
        for (ArrayList<Literal> formula : formulas) {
            Flattener flattener = new Flattener();
            ArrayList<Literal> flatLiterals = new ArrayList<>();
            for (Literal lit : formula) {
                flatLiterals.add(flattener.flattenLit(lit));
            }
            // unpack definitions for each clause
            for (Map.Entry<Variable, Term> entry : flattener.getDefinitions().entrySet()) {
                flatLiterals.add(new Literal(new Predicate(
                        "equals",
                        new ArrayList<>(Arrays.asList(entry.getKey(), entry.getValue()))), false)
                );
            }
            flatFormulas.add(flatLiterals);
        }
        return flatFormulas;
    }
}
