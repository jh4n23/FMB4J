import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String[] terms = line.split(" ");
        int[] tmp = new int[terms.length];
        int k = 0;

        for (String term : terms) {
            int name = Integer.parseInt(term);
            if (name == 0) break;
            tmp[k++] = name;
        }

        return Arrays.copyOf(tmp, k);
    }
}
