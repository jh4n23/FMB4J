import FOL.*;
import org.sat4j.specs.ContradictionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ModelBuilder {
    private int currentDomainSize = 0;

    private final ArrayList<String> domainConstants = new ArrayList<>();

    private final MySolver mySolver;

    public ModelBuilder(MySolver mySolver) {
        this.mySolver = mySolver;
        incrementDomainSize();
    }

    public int getCurrentDomainSize() {
        return this.currentDomainSize;
    }

    public void incrementDomainSize() {
        String newConstant = "d" + currentDomainSize;
        domainConstants.add(newConstant);
        currentDomainSize++;
    }

    public void ground(ArrayList<Literal> clause) throws ContradictionException {
        HashSet<Variable> variables = new HashSet<>();
        for (Literal literal : clause) {
            Predicate predicate = literal.getPredicate();
            for (Term arg : predicate.getArgs()) {
                if (arg instanceof Variable) {
                    variables.add((Variable) arg);
                } else {
                    for (Term arg2 : arg.getArgs()) {
                        variables.add((Variable) arg2);
                    }
                }
            }
        }
        generateInstances(clause, new HashMap<>(), new ArrayList<>(variables));
    }

    public void generateInstances(ArrayList<Literal> clause, HashMap<Variable, String> map, ArrayList<Variable> variables) throws ContradictionException {
        if (!variables.isEmpty()) {
            Variable var = variables.remove(variables.size() - 1);
            for (String c : domainConstants) {
                map.put(var, c);
                generateInstances(clause, map, variables);
            }
            variables.add(var);
        } else {
            substitute(clause, map);
        }
    }

    public void substitute(ArrayList<Literal> clause, HashMap<Variable, String> instances) throws ContradictionException {
        ArrayList<Literal> newClause = new ArrayList<>();
        for (Literal literal : clause) {
            ArrayList<Term> newArgs = new ArrayList<>();
            for (Term term : literal.getPredicate().getArgs()) {
                if (term instanceof Function) {
                    ArrayList<Term> funcArgs = new ArrayList<>();
                    for (Term var : term.getArgs()) {
                        funcArgs.add(new Variable(instances.get(var)));
                    }
                    newArgs.add(new Function(term.getName(), funcArgs));
                }
                else if (instances.containsKey(term)) {

                    newArgs.add(new Variable(instances.get(term)));
                } else {
                    newArgs.add(term);
                }
            }
            newClause.add(
                    new Literal(
                        new Predicate(
                                literal.getPredicate().getName(),
                                newArgs
                        ),
                        literal.getPolarity()
                    )
            );
        }
        mySolver.addClause(newClause);
    }
}