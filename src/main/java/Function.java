import java.util.ArrayList;

public class Function extends Term {
    private final String name;
    private final ArrayList<Term> args;

    public Function(String name, ArrayList<Term> args) {
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

    public String toString() {
        return this.getName() + (this.getArgs().isEmpty() ? "" : this.getArgs());
    }
}
