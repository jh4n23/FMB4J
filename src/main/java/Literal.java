public class Literal {
    private final Boolean polarity;
    private final Atom atom;

    public Literal(Atom atom, Boolean polarity) {
        this.atom = atom;
        this.polarity = polarity;
    }

    public Boolean getPolarity() {
        return this.polarity;
    }

    public Atom getAtom() {
        return this.atom;
    }

    public String litToString() {
        return ((this.polarity ? "" : "~") + this.atom.getName() + this.atom.getArgs());
    }
}
