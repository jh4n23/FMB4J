public class SymbolAndArity {
    private String symbol;
    private Integer arity;
    public SymbolAndArity(String symbol, Integer arity) {
        this.symbol = symbol;
        this.arity = arity;
    }

    public Integer getArity() {
        return arity;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol + arity.toString();
    }

    @Override
    public int hashCode() {
        return symbol.hashCode() ^ arity.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SymbolAndArity) {
            SymbolAndArity s = (SymbolAndArity) obj;
            return this.symbol.equals(s.symbol) && this.arity.equals(s.arity);
        } else {
            System.err.println((obj.toString() + " and " + this + " are incomparable"));
            return false;
        }
    }
}
