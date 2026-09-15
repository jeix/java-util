package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Pair<T, U> {

    private final T cat;
    private final U dog;

    private Pair(T cat, U dog) {
        this.cat = cat;
        this.dog = dog;
    }

    public static <T, U> Pair<T, U> of(T cat, U dog) {
        return new Pair<>(cat, dog);
    }

    @JsonProperty("ord1")
    public T ord1() {
        return cat;
    }

    @JsonProperty("ord2")
    public U ord2() {
        return dog;
    }

    @Override
    public String toString() {
        return "(" + cat + ", " + dog + ")";
    }
}
