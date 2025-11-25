import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
            Parser.CnfFormula formula = Parser.parse(br);
            System.out.println("Number of variables: " + formula.numVars);
            System.out.println("Clauses:");
            for (int[] clause : formula.clauses) System.out.println(Arrays.toString(clause));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static class Solver {
        public final Parser.CnfFormula cnfFormula;

        public Solver(Parser.CnfFormula cnfFormula) {
            this.cnfFormula = cnfFormula;
        }

        public static void solve() {

        }
    }
}
