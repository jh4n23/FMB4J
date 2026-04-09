import FOL.Literal;
import FOL.Predicate;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelSolver {
    private final HashMap<Predicate, Integer> propVarMap = new HashMap<>();
    private final ISolver solver = SolverFactory.newDefault();
    private final ArrayList<VecInt> clauses = new ArrayList<>();

    public ModelSolver() {}

    public ISolver getSolver() {
        return this.solver;
    }

    public HashMap<Predicate, Integer> getPropVarMap() {
        return this.propVarMap;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (VecInt vecInt : clauses) {
            str.append(vecInt.toString().replace(",", " | ")).append("\n");
        }
        return str.toString();
    }

    public void addClause(ArrayList<Literal> groundClause) throws ContradictionException, InterruptedException {
        int[] SATVars = new int[groundClause.size()];

        for (int i = 0; i < groundClause.size(); i++) {
            Literal literal = groundClause.get(i);
            int propVarNum = propVarMap.size() + 1;
            propVarMap.putIfAbsent(literal.getPredicate(), propVarNum);
            int var = propVarMap.get(literal.getPredicate());
            SATVars[i] = literal.getPolarity() ? var : -var;
            checkTimeout();
        }

        VecInt literals = new VecInt(SATVars);
        solver.addClause(literals);
        clauses.add(literals);
    }

    public static void checkTimeout() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("Timed out");
        }
    }
}