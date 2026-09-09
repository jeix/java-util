package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Pair<T, U> {

    private final T ord1;
    private final U ord2;

    private Pair(T cat, U dog) {
        this.ord1 = cat;
        this.ord2 = dog;
    }

    @JsonCreator
    public static <T, U> Pair<T, U> of(
            @JsonProperty("ord1") T cat,
            @JsonProperty("ord2") U dog) {
        return new Pair<>(cat, dog);
    }

    @JsonProperty("ord1")
    public T ord1() {
        return ord1;
    }

    @JsonProperty("ord2")
    public U ord2() {
        return ord2;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ")";
    }
}
