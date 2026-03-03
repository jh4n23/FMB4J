import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.antlr.v4.runtime.*;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

/**
 * TODO:
 *  - Upgrade parser to parse first order logic (look into a standard syntax or use own)
 *      - Use ANTLR
 *  - Research Z3 Java API
 *  - If usable:
 *      - Ground clauses (generate instances)
 *      - Restrict everything (variables, constants, functions, predicates) to be within the domain
 *      - Pass to Z3 solver
 *  - If not:
 *      - Perform clause flattening
 *      - Grounding
 *      - Domain restrictions
 *      - Functionality definitions
 *      - Solve with SAT4J
 *  - Start with domain of size 1 and iteratively increase
 *  - Use nested for loops to replace variables with domain constants (for each variable [...] for each constant [...])
 */
public class Main {
    public static void main(String[] args) {
        String fileName = "3.txt";
        try {
            CharStream charStream = CharStreams.fromFileName(fileName);
            CNFLexer lexer = new CNFLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CNFParser parser = new CNFParser(tokens);
            CNFParser.Tptp_fileContext tree = parser.tptp_file();
            ArrayList<ArrayList<Literal>> formulas = tree.formulas;
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

            MySolver mySolver = new MySolver();
            Domain domain = new Domain(10, mySolver);
            domain.incrementDomainSize();

            System.out.println("Domain size: " + domain.getCurrentSize());
            for (ArrayList<Literal> flatClause : flatFormulas) {
                domain.ground(flatClause);
            }

            ISolver solver = mySolver.getSolver();
            if (solver.isSatisfiable()) {
                System.out.println("SATISFIABLE: " + Arrays.toString(solver.model()));
            } else {
                System.out.println("UNSATISFIABLE");
            }

        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
        } catch (ContradictionException e) {
            System.err.println("ContradictionException: " + e.getMessage());
        } catch (TimeoutException e) {
            System.err.println("TimeoutException: " + e.getMessage());
        }
    }
}
