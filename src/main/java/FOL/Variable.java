package FOL;

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

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable) {
            Variable var = (Variable) obj;
            return this.getName().equals(var.getName());
        } else {
            System.err.println((obj.toString() + " and " + this + " are incomparable"));
            return false;
        }
    }
}