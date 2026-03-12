import FOL.*;
import org.sat4j.specs.ContradictionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ModelBuilder {
    private int currentDomainSize = 0;

    private final ArrayList<String> domainConstants = new ArrayList<>();

    private final MySolver mySolver;

    public ModelBuilder(MySolver mySolver) {
        this.mySolver = mySolver;
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

    public void functionConstraints(ArrayList<ArrayList<Literal>> flatFormulas) {
        Set<SymbolAndArity> functionNames = new HashSet<>();
        ArrayList<ArrayList<Literal>> totalityClauses = new ArrayList<>();
        for (ArrayList<Literal> formula : flatFormulas) {
            for (Literal literal : formula) {
                for (Term arg : literal.getPredicate().getArgs()) {
                    if (arg instanceof Function) {
                        functionNames.add(new SymbolAndArity(arg.getName(), arg.getArgs().size()));
                    }
                }
            }
        }
        System.out.println(functionNames);
        for (SymbolAndArity sa : functionNames) {
            ArrayList<Literal> totalityClause = new ArrayList<>();
            ArrayList<Term> args = new ArrayList<>();
            for (int i = 0; i < sa.getArity(); i++) {
                args.add(new Variable("ARG" + i));
            }
            Function f = new Function(sa.getSymbol(), args);
            System.out.println(f);
            for (String dc : domainConstants) {
                Predicate p = new Predicate("equals", new ArrayList<Term>() {{
                    add(f);
                    add(new Function(dc, new ArrayList<>()));
                }});
                totalityClause.add(new Literal(p, true));
            }
            totalityClauses.add(totalityClause);
        }
        System.out.println(totalityClauses);
    }
}