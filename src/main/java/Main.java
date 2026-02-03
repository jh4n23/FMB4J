import java.io.IOException;
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
        String fileName = "1.txt";
        try {
            CharStream charStream = CharStreams.fromFileName(fileName);
            CNFLexer lexer = new CNFLexer(charStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CNFParser parser = new CNFParser(tokens);
            ParseTree tree = parser.tptp_file();
            // Trees.inspect(tree, parser);
            System.out.println(tree.toStringTree(parser));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
