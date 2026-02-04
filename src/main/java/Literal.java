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

    public String toString() {
        return ((this.polarity ? "" : "~") + this.predicate.getName() + this.predicate.getArgs());
    }
}
