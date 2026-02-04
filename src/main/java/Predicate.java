import java.util.ArrayList;

public class Predicate extends Function {
    public Predicate(String name, ArrayList<Term> args) {
        super(name, args);
    }

    public static Predicate fromFunction(Function f) {
        return new Predicate(f.getName(), f.getArgs());
    }
}
