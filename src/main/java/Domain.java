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

    public ArrayList<ArrayList<Literal>> ground(ArrayList<Literal> clause) {
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
        generateInstances(new HashMap<Variable, String>(), new ArrayList<>(variables));
        return new ArrayList<ArrayList<Literal>>();
    }

    public void generateInstances(HashMap<Variable, String> map, ArrayList<Variable> variables) {
        if (variables.isEmpty()) {
            System.out.println(map);
        } else {
            Variable var = variables.remove(variables.size() - 1);
            for (String c : domainConstants) {
                map.put(var, c);
                generateInstances(map, variables);
            }
            variables.add(var);
        }
    }
}