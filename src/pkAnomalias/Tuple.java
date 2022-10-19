package pkAnomalias;

public class Tuple<T, E> {
    private final T first;
    private final E second;

    public Tuple(T first, E second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return T return the first
     */
    public T getFirst() {
        return first;
    }

    /**
     * @return E return the second
     */
    public E getSecond() {
        return second;
    }

}
