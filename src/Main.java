import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("2.txt"))) {
            Parser.CnfFormula formula = Parser.parse(br);
            System.out.println("Number of variables: " + formula.numVars);
            System.out.println("Clauses:");
            for (int[] clause : formula.clauses) System.out.println(Arrays.toString(clause));
            solve(formula);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void solve(Parser.CnfFormula formula) {
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
