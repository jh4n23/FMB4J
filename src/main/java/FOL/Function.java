package FOL;

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

    @Override
    public int hashCode() {
        int nameHash = name.hashCode();
        for (Term arg : args) {
            nameHash ^= arg.hashCode();
        }
        return nameHash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Function) {
            Function function = (Function) obj;
            return this.getName().equals(function.getName()) && this.getArgs().equals(function.getArgs());
        } else {
            System.err.println((obj.toString() + " and " + this + " are incomparable"));
            return false;
        }
    }
}