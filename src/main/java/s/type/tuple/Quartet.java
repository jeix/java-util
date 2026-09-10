package s.type.tuple;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Quartet<T, U, V, W> {

    private final T ord1;
    private final U ord2;
    private final V ord3;
    private final W ord4;

    private Quartet(final T ord1, final U ord2, final V ord3, final W ord4) {
        this.ord1 = ord1;
        this.ord2 = ord2;
        this.ord3 = ord3;
        this.ord4 = ord4;
    }

    public static <T, U, V, W> Quartet<T, U, V, W> of(final T ord1, final U ord2, final V ord3, final W ord4) {
        return new Quartet<>(ord1, ord2, ord3, ord4);
    }

    public T ord1() {
        return ord1;
    }

    public U ord2() {
        return ord2;
    }

    public V ord3() {
        return ord3;
    }

    public W ord4() {
        return ord4;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ", " + ord3 + ", " + ord4 + ")";
    }
}