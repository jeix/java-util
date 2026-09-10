package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Pair<T, U> {

    private final T ord1;
    private final U ord2;

    private Pair(final T ord1, final U ord2) {
        this.ord1 = ord1;
        this.ord2 = ord2;
    }

    public static <T, U> Pair<T, U> of(final T ord1, final U ord2) {
        return new Pair<>(ord1, ord2);
    }

    @JsonProperty("1")
    public T ord1() {
        return ord1;
    }

    @JsonProperty("2")
    public U ord2() {
        return ord2;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ")";
    }
}