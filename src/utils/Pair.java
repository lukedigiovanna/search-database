package utils;

public class Pair<T, U extends Comparable<U>> implements Comparable<Pair<T, U>> {
    public T first;
    public U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Pair))
            return false;
        else
            return this.first.equals(((Pair<T, U>) other).first);
    }

    @Override
    public int compareTo(Pair<T, U> other) {
        return this.second.compareTo(((Pair<T, U>) other).second);
    }

    @Override
    public String toString() {
        return this.first.toString() + ": " + this.second.toString();
    }

}
