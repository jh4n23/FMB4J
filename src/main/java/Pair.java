import java.util.Objects;

public class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() { return first; }
    public B getSecond() { return second; }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Pair) {
            Pair<?, ?> pair = (Pair<?, ?>) obj;
            return Objects.equals(this.first, pair.first) && Objects.equals(this.second, pair.second);
        } else  {
            System.err.println((obj.toString() + " and " + this + " are incomparable"));
            return false;
        }
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }
}