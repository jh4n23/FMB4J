import java.io.IOException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

            System.out.println("Original problem:\n\t" + formulas);
            System.out.println("Flattened problem:\n\t" + flatFormulas);

            Domain domain = new Domain(10);
            domain.incrementDomainSize();
            ArrayList<ArrayList<Literal>> groundFormulas = new ArrayList<>();
            for (ArrayList<Literal> flatClause : flatFormulas) {
                groundFormulas.add(domain.ground(flatClause));
            }

            System.out.println("Ground problem:\n\t" + groundFormulas);

            HashMap<Literal, Integer> propVarMap = new HashMap<>();
            int propVarNum = 1; // 0 not valid variable index in SAT4J
            for (ArrayList<Literal> clause : groundFormulas) {
                for (Literal literal : clause) {
                    if (!literal.getPolarity()) {
                        propVarNum += propVarMap.putIfAbsent(literal, -propVarNum) == null ? 1 : 0;
                    } else {
                        propVarNum += propVarMap.putIfAbsent(literal, propVarNum) == null ? 1 : 0;
                    }
                }
            }

            System.out.println(propVarMap);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
