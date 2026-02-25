import java.util.ArrayList;

public class Predicate extends Function {
    public Predicate(String name, ArrayList<Term> args) {
        super(name, args);
    }

    public static Predicate fromFunction(Function f) {
        return new Predicate(f.getName(), f.getArgs());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Predicate) {
            Predicate predicate = (Predicate) obj;
            return this.getName().equals(predicate.getName()) && this.getArgs().equals(predicate.getArgs());
        } else {
            System.err.println(obj.toString() + " and " + this + " are incomparable");
            return false;
        }
    }
}
