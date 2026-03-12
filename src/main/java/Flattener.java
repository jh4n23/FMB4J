import FOL.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Flattener {

    private final HashMap<Variable, Term> definitions = new HashMap<>();
    private int freshVarCount = 0;
    public Literal flattenLit(Literal lit) {
        return new Literal(flattenPredicate(lit.getPredicate()), lit.getPolarity());
    }

    public Predicate flattenPredicate(Predicate p) {
        return Predicate.fromFunction(flattenFunction(p));
    }

    public Term flattenTerm(Term term) {
        if (term instanceof Variable) {
            return term;
        } else if (term instanceof Function) {
            return flattenFunction((Function) term);
        } else {
            return null;
        }
    }

    public Function flattenFunction(Function f) {
        ArrayList<Term> flattenedArgs = new ArrayList<>();
        for (Term arg : f.getArgs()) {
            Term flatArg = flattenTerm(arg);
            if (arg instanceof Function) {
                Variable var = generateFreshVar();
                definitions.put(var, flatArg);
                flattenedArgs.add(var);
            } else {
                flattenedArgs.add(flatArg);
            }
        }
        return new Function(f.getName(), flattenedArgs);
    }

    public Variable generateFreshVar() {
        freshVarCount++;
        return new Variable("FRESH_X" + freshVarCount);
    }

    public HashMap<Variable, Term> getDefinitions() {
        return definitions;
    }
}