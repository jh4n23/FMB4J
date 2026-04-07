import FOL.*;
import org.sat4j.specs.ContradictionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ModelBuilder {
    private final ArrayList<String> domainConstants = new ArrayList<>();

    private final MySolver mySolver;

    public ModelBuilder(MySolver mySolver, int currentDomainSize) {
        this.mySolver = mySolver;
        for (int i = 0; i < currentDomainSize; i++) {
            String dc = "d" + i;
            domainConstants.add(dc);
        }
    }

    public void ground(ArrayList<Literal> clause) throws ContradictionException, InterruptedException {
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

    public void generateInstances(ArrayList<Literal> clause, HashMap<Variable, String> map, ArrayList<Variable> variables) throws ContradictionException, InterruptedException {
        if (!variables.isEmpty()) {
            Variable var = variables.remove(variables.size() - 1);
            for (String c : domainConstants) {
                MySolver.checkTimeout();
                map.put(var, c);
                generateInstances(clause, map, variables);
            }
            variables.add(var);
        } else {
            substitute(clause, map);
        }
    }

    public void substitute(ArrayList<Literal> clause, HashMap<Variable, String> instances) throws ContradictionException, InterruptedException {
        ArrayList<Literal> newClause = new ArrayList<>();
        for (Literal literal : clause) {
            ArrayList<Term> newArgs = new ArrayList<>();
            for (Term term : literal.getPredicate().getArgs()) {
                if (term instanceof Function) {
                    ArrayList<Term> funcArgs = new ArrayList<>();
                    for (Term var : term.getArgs()) {
                        funcArgs.add(new Function(instances.get(var), new ArrayList<>()));
                    }
                    newArgs.add(new Function(term.getName(), funcArgs));
                }
                else if (instances.containsKey(term)) {
                    newArgs.add(new Function(instances.get(term), new ArrayList<>()));
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

    public Pair<ArrayList<ArrayList<Literal>>, ArrayList<ArrayList<Literal>>> functionConstraints(ArrayList<ArrayList<Literal>> flatFormulas) {
        Set<Pair<String, Integer>> functionNames = getFunctionSymbolAndArity(flatFormulas);
        ArrayList<ArrayList<Literal>> totalityClauses = new ArrayList<>();
        ArrayList<ArrayList<Literal>> functionalityClauses = new ArrayList<>();

        for (Pair<String, Integer> symbolAndArity : functionNames) {
            ArrayList<Literal> totalityClause = new ArrayList<>();
            ArrayList<Term> args = new ArrayList<>();
            for (int i = 0; i < symbolAndArity.getSecond(); i++) {
                args.add(new Variable("ARG" + i));
            }
            Function f = new Function(symbolAndArity.getFirst(), args);
            // System.out.println(f);
            for (int i = 0; i < domainConstants.size(); i++) {
                String dc1 = domainConstants.get(i);
                Predicate p = new Predicate("equals", new ArrayList<Term>() {{
                    add(new Function(dc1, new ArrayList<>()));
                    add(f);
                }});
                totalityClause.add(new Literal(p, true));

                for (int j = i + 1; j < domainConstants.size(); j++) {
                    ArrayList<Literal> functionalityClause = new ArrayList<>();
                    String dc2 = domainConstants.get(j);
                    Predicate q = new Predicate("equals", new ArrayList<Term>() {{
                        add(new Function(dc2, new ArrayList<>()));
                        add(f);
                    }});
                    functionalityClause.add(new Literal(p, false));
                    functionalityClause.add(new Literal(q, false));
                    functionalityClauses.add(functionalityClause);
                }
            }
            totalityClauses.add(totalityClause);
        }
        //System.out.println(totalityClauses);
        //System.out.println(functionalityClauses);
        return Pair.of(totalityClauses, functionalityClauses);
    }

    public Set<Pair<String, Integer>> getFunctionSymbolAndArity(ArrayList<ArrayList<Literal>> flatFormulas) {
        Set<Pair<String, Integer>> functionNames = new HashSet<>();
        for (ArrayList<Literal> formula : flatFormulas) {
            for (Literal literal : formula) {
                for (Term arg : literal.getPredicate().getArgs()) {
                    if (arg instanceof Function) {
                        functionNames.add(Pair.of(arg.getName(), arg.getArgs().size()));
                    }
                }
            }
        }
        // System.out.println(functionNames);
        return functionNames;
    }
}