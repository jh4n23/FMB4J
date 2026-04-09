import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import FOL.*;
import org.antlr.v4.runtime.*;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Main {
    public static void main(String[] args) {
        // String fileName = "src/main/java/resources/tptp/unsatisfiable/PUZ001-1.txt";
        String fileName = "src/main/java/resources/tptp/satisfiable/PUZ044-1.txt";
        // String fileName = "src/main/java/resources/tptp/1.txt";
        int timeout = 10;
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> result = executorService.submit(
                () -> runModelBuilder(fileName, true)
        );
        try {
            boolean modelFound = result.get(timeout, TimeUnit.SECONDS);
            System.out.println("Model found: " + modelFound);
        } catch (java.util.concurrent.TimeoutException e) {
            System.out.println("Model not found after " + timeout + " seconds");
        } catch (InterruptedException e) {
            System.err.println("System was interrupted: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            long finish = System.currentTimeMillis();
            System.out.println("Time elapsed: " + (finish - start) / 1000 + "s");
            executorService.shutdownNow();
        }
    }

    public static Boolean runModelBuilder(String fileName, boolean outputModel) throws IOException, ContradictionException, TimeoutException, InterruptedException {
        ArrayList<ArrayList<Literal>> formulas = loadFormulas(fileName);
        ArrayList<ArrayList<Literal>> flatFormulas = flattenFormulas(formulas);

        int currentDomainSize = 1;
        while (!Thread.interrupted()) {
            if (outputModel) System.out.println("Domain size: " + currentDomainSize);
            ModelSolver modelSolver = new ModelSolver();
            ModelBuilder modelBuilder = new ModelBuilder(modelSolver, currentDomainSize);

            Pair<ArrayList<ArrayList<Literal>>, ArrayList<ArrayList<Literal>>> functionConstraints
                    = modelBuilder.functionConstraints(flatFormulas);

            for (ArrayList<Literal> flatClause : flatFormulas) modelBuilder.ground(flatClause);
            for (ArrayList<Literal> constraint : functionConstraints.getFirst()) modelBuilder.ground(constraint);
            for (ArrayList<Literal> constraint : functionConstraints.getSecond()) modelBuilder.ground(constraint);

            ISolver solver = modelSolver.getSolver();
            if (solver.isSatisfiable()) {
                if (outputModel) {
                    System.out.println("Model found for domain size " + currentDomainSize);
                    Pair<HashMap<Literal, Boolean>, ArrayList<Pair<Function, Function>>> decodedModel = decodeModel(solver.model(), modelSolver);
                    System.out.println("Model:");
                    decodedModel.getFirst().forEach((p, q)-> System.out.println(p + " = " + q));
                    System.out.println("Number of functions: " + decodedModel.getSecond().size());
                    if (!decodedModel.getSecond().isEmpty()) {
                        System.out.println("Function assignments:");
                        decodedModel.getSecond().forEach(p -> System.out.println(p.getFirst() + " = " + p.getSecond()));
                    }
                }
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
            Function f = null;
            Function c = null;
            if (decodedModel.get(literal) && literal.getPredicate().getName().equals("equals")) {
                for (Term t : literal.getPredicate().getArgs()) {
                    if (t instanceof Function) {
                        if (!t.getArgs().isEmpty()) {
                            f = (Function) t;
                        } else {
                            c = (Function) t;
                        }
                    }
                }
                if (f != null && c != null) {
                    functionAssignments.add(Pair.of(f, c));
                }
            }
        }
        return functionAssignments;
    }

    /**
     * Method to parse a CNF problem file and produce a tree containing its formulas
     * @param fileName name of file containing CNF problem
     * @return the parsed problem in tree form
     * @throws IOException if there is a problem reading the file
     */
    private static ArrayList<ArrayList<Literal>> loadFormulas(String fileName) throws IOException {
        CharStream charStream = CharStreams.fromFileName(fileName);
        CNFLexer lexer = new CNFLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CNFParser parser = new CNFParser(tokens);
        CNFParser.Tptp_fileContext tree = parser.tptp_file();
        return tree.formulas;
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