import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MySolver {
    private final HashMap<Predicate, Integer> propVarMap = new HashMap<>();
    private final ISolver solver = SolverFactory.newDefault();

    public MySolver() {}

    public ISolver getSolver() {
        return this.solver;
    }

    public void addClause(ArrayList<Literal> groundClause) throws ContradictionException {
        int[] SATVars = new int[groundClause.size()];
        for (Literal literal : groundClause) {
            int propVarNum = propVarMap.size() + 1; // 0 not valid variable index in SAT4J
            if (!literal.getPolarity()) {
                propVarMap.putIfAbsent(literal.getPredicate(), -propVarNum);
            } else {
                propVarMap.putIfAbsent(literal.getPredicate(), propVarNum);
            }
        }

        for (int i = 0; i < groundClause.size(); i++) {
            Literal literal = groundClause.get(i);
            int var = propVarMap.get(literal.getPredicate());
            SATVars[i] = literal.getPolarity() ? var : -var;
        }

        System.out.println(propVarMap);
        System.out.println(Arrays.toString(SATVars));

        VecInt literals = new VecInt(SATVars);
        System.out.println(literals.isEmpty());
        solver.addClause(literals);
    }
}
