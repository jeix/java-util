package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Triplet<T, U, V> {

    private final T ord1;
    private final U ord2;
    private final V ord3;

    private Triplet(final T ord1, final U ord2, final V ord3) {
        this.ord1 = ord1;
        this.ord2 = ord2;
        this.ord3 = ord3;
    }

    public static <T, U, V> Triplet<T, U, V> of(final T ord1, final U ord2, final V ord3) {
        return new Triplet<>(ord1, ord2, ord3);
    }

    @JsonProperty("1")
    public T ord1() {
        return ord1;
    }

    @JsonProperty("2")
    public U ord2() {
        return ord2;
    }

    @JsonProperty("3")
    public V ord3() {
        return ord3;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ", " + ord3 + ")";
    }
}