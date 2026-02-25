public class Literal {
    private final Boolean polarity;
    private final Predicate predicate;

    public Literal(Predicate predicate, Boolean polarity) {
        this.predicate = predicate;
        this.polarity = polarity;
    }

    public Boolean getPolarity() {
        return this.polarity;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    @Override
    public String toString() {
        return ((this.polarity ? "" : "~") + this.predicate.getName() + this.predicate.getArgs());
    }

    @Override
    public int hashCode() {
        return predicate.hashCode() ^ polarity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Literal) {
            Literal lit = (Literal) obj;
            return this.predicate.equals(lit.getPredicate()) && this.polarity == lit.getPolarity();
        } else {
            System.err.println((obj.toString() + " and " + this + " are incomparable"));
            return false;
        }
    }
}
