package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Pair<T, U> {

    @JsonProperty("ord1")
    private final T first;

    @JsonProperty("ord2")
    private final U second;

    protected Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public static <T, U> Pair<T, U> of(T first, U second) {
        return new Pair<>(first, second);
    }

    public T ord1() {
        return first;
    }

    public U ord2() {
        return second;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
