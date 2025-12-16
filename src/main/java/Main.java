import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.Trees;

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
        try (BufferedReader br = new BufferedReader(new FileReader("1.txt"))) {
//            Parser.CnfFormula formula = Parser.parse(br);
//            System.out.println("Number of variables: " + formula.numVars);
//            System.out.println("Clauses:");
//            for (int[] clause : formula.clauses) System.out.println(Arrays.toString(clause));
//            solve(formula);



            String input = "forall x. exists y. P(x, y) | Q(x) & P2(y) -> R(z)";
            CharStream charStream = CharStreams.fromString(input);
            FOLLexer lexer = new FOLLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            FOLParser parser = new FOLParser(tokens);
            ParseTree tree = parser.formula();

            Trees.inspect(tree, parser);

            System.out.println(tree.toStringTree(parser));

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void solve(CnfParser.CnfFormula formula) {
        ISolver solver = SolverFactory.newDefault();
        solver.newVar(formula.numVars);

        try {
            for (int[] clause : formula.clauses) {
                solver.addClause(new VecInt(clause));
            }

            boolean sat = solver.isSatisfiable();
            System.out.println("Satisfiable? " + sat);

            if (sat) {
                System.out.println("Model: " +  Arrays.toString(solver.model()));
            }
        } catch (ContradictionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.err.println("Solver timed out: ");
            e.printStackTrace();
        }
    }
}
