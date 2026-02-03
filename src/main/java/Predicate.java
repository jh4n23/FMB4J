import java.util.ArrayList;

public class Predicate extends Atom{
    private final String name;
    private final ArrayList<Term> args;

    public Predicate(String name, ArrayList<Term> args) {
        super(name, args);
        this.name = name;
        this.args = args;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ArrayList<Term> getArgs() {
        return this.args;
    }
}
