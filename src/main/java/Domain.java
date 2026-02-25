import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Domain {
    private int maxSize;
    private int currentSize = 0;

    private ArrayList<String> domainConstants = new ArrayList<>();

    public Domain(int maxSize) {
        this.maxSize = maxSize;
        incrementDomainSize();
    }

    public void incrementDomainSize() {
        String newConstant = "d" + currentSize;
        domainConstants.add(newConstant);
        currentSize++;
        assert currentSize < maxSize;
    }

    public ArrayList<Literal> ground(ArrayList<Literal> clause) {
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
        HashMap<Variable, String> instances = generateInstances(new HashMap<Variable, String>(), new ArrayList<>(variables));
        System.out.println(instances);
        ArrayList<Literal> groundClause = substitute(clause, instances);
        return groundClause;
    }

    /**
     * This is currently broken - the function should not return map as this only contains the final grounding
     * Maybe the solution is to create a list of all groundings and call substitute after each individual
     * grounding is generated?
     * @param map
     * @param variables
     * @return
     */
    public HashMap<Variable, String> generateInstances(HashMap<Variable, String> map, ArrayList<Variable> variables) {
        if (!variables.isEmpty()) {
            Variable var = variables.remove(variables.size() - 1);
            for (String c : domainConstants) {
                map.put(var, c);
                generateInstances(map, variables);
            }
            variables.add(var);
        }
        return map;
    }

    public static ArrayList<Literal> substitute(ArrayList<Literal> clause, HashMap<Variable, String> instances) {
        ArrayList<Literal> newClause = new ArrayList<>();
        for (Literal literal : clause) {
            ArrayList<Term> newArgs = new ArrayList<>();
            for (Term term : literal.getPredicate().getArgs()) {
                if (instances.containsKey(term)) {
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
        return newClause;
    }
}