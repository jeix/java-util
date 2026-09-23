package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Pair<T, U> {

    private final T first;
    private final U second;

    private Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    @JsonCreator
    public static <T, U> Pair<T, U> of(
            @JsonProperty("ord1") T first,
            @JsonProperty("ord2") U second) {
        return new Pair<>(first, second);
    }

    @JsonProperty("ord1")
    public T ord1() {
        return first;
    }

    @JsonProperty("ord2")
    public U ord2() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
