import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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
                Grounder grounder = new Grounder(mySolver);
                grounder.incrementDomainSize();

                System.out.println("Domain size: " + grounder.getCurrentDomainSize());
                for (ArrayList<Literal> flatClause : flatFormulas) {
                    grounder.ground(flatClause);
                }

                ISolver solver = mySolver.getSolver();
                stop = solver.isSatisfiable();
                System.out.println("SAT problem:\n" + mySolver);
                if (stop) {
                    System.out.println("SATISFIABLE: " + Arrays.toString(solver.model()));
                } else {
                    grounder.incrementDomainSize();
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