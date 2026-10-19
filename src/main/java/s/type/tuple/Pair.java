package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/** 두 값의 불변 튜플. */
@EqualsAndHashCode
public final class Pair<T, U> {

    @JsonProperty("ord1")
    private final T ord1;

    @JsonProperty("ord2")
    private final U ord2;

    @JsonCreator
    private Pair(
            @JsonProperty("ord1") T ord1,
            @JsonProperty("ord2") U ord2) {
        this.ord1 = ord1;
        this.ord2 = ord2;
    }

    public static <T, U> Pair<T, U> of(T cat, U dog) {
        return new Pair<>(cat, dog);
    }

    public T ord1() {
        return ord1;
    }

    public U ord2() {
        return ord2;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ")";
    }
}
