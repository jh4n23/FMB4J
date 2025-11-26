import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


// TODO: Improve error-handling + problem reporting
public class Parser {
    public static class CnfFormula {
        public final int numVars;
        public final List<int[]> clauses;

        public CnfFormula(int numVars, List<int[]> clauses) {
            this.numVars = numVars;
            this.clauses = clauses;
        }
    }
    
    public static CnfFormula parse(BufferedReader br) throws IOException {
        String line;
        int numVars = 0;
        List<int[]> clauses = new ArrayList<>();

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("p")) { // header line
                String[] headers = line.split(" ");
                numVars = Integer.parseInt(headers[2]);
                continue;
            }

            if (line.startsWith("c") || line.isEmpty()) { // comments and empty lines
                continue;
            }

            int[] clause = parseClause(line);
            clauses.add(clause);
        }

        return new CnfFormula(numVars, clauses);
    }

    public static int[] parseClause(String line) {
        String[] literals = line.split(" ");
        int[] parsedLiterals = new int[literals.length];
        int numParsed = 0;

        for (String literal : literals) {
            int lit = Integer.parseInt(literal);
            if (lit == 0) break;
            parsedLiterals[numParsed++] = lit;
        }

        // trim clause to exclude terminal 0
        return Arrays.copyOf(parsedLiterals, numParsed);
    }
}
