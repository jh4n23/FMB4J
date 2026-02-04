import java.util.ArrayList;

public class Variable extends Term {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * It is meaningless to call getArgs() on a variable, so return null
     * There must be a better way to do this
     * @return null
     */
    @Override
    public ArrayList<Term> getArgs() {
        return null;
    }

    public String toString() {
        return this.getName();
    }
}
