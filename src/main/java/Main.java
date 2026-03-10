import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import FOL.Literal;
import FOL.Predicate;
import FOL.Term;
import FOL.Variable;
import org.antlr.v4.runtime.*;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Main {
    public static void main(String[] args) {
        String fileName = "3.txt";
        try {
            ArrayList<ArrayList<Literal>> formulas = loadFormulas(fileName);
            ArrayList<ArrayList<Literal>> flatFormulas = flattenFormulas(formulas);
            System.out.println("Problem:\n\t" + formulas);

            boolean stop = false;
            while (!stop) {
                MySolver mySolver = new MySolver();
                ModelBuilder modelBuilder = new ModelBuilder(mySolver);
                modelBuilder.incrementDomainSize();

                System.out.println("Domain size: " + modelBuilder.getCurrentDomainSize());
                for (ArrayList<Literal> flatClause : flatFormulas) {
                    modelBuilder.ground(flatClause);
                }

                ISolver solver = mySolver.getSolver();
                stop = solver.isSatisfiable();
                System.out.println("SAT problem:\n" + mySolver);
                if (stop) {
                    System.out.println("SATISFIABLE: " + Arrays.toString(solver.model()));
                    System.out.println(decodeModel(solver.model(), mySolver));
                } else {
                    modelBuilder.incrementDomainSize();
                }
            }

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        } catch (ContradictionException e) {
            System.err.println("ContradictionException: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("TimeoutException: " + e.getMessage());
        }
    }

    /**
     * Method to convert the SAT model back into truth assignments of the predicates from the original problem
     * @param model the SAT model
     * @param mySolver the solver object containing the mapping of predicates to propositional variables
     * @return the model in terms of truth assignments to predicates
     */
    private static HashMap<Literal, Boolean> decodeModel(int[] model, MySolver mySolver) {
        HashMap<Literal, Boolean> decodedModel = new HashMap<>();
        HashMap<Predicate, Integer> propVarMap = mySolver.getPropVarMap();
        for (int literal : model) {
            boolean polarity = literal > 0;
            try {
                decodedModel.put(new Literal(getKeyByValue(propVarMap, literal), true), polarity);
            } catch (NullPointerException e) {
                // this exception is thrown when the polarity of the literal in the model is the opposite of that in the propVarMap
                // all we need to do is call getKeyByValue again with the inverse of the literal
                // I'm not sure if this is a good solution or not
                decodedModel.put(new Literal(getKeyByValue(propVarMap, -literal), true), polarity);
            }
        }
        return decodedModel;
    }

    /**
     * Helper method to find the key from its value in the map - this may be better with just a for loop
     * This method was adapted from one found on Stack Overflow: <a href="https://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value">...</a>
     * @param map the HashMap with keys of type T and values of type E (in this case, predicates and integers)
     * @param value the value of the object whose key we want to find
     * @return the key of the object (in this case, a predicate)
     */
    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
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