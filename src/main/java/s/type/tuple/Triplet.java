package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Triplet<T, U, V> {

    private final T first;
    private final U second;
    private final V third;

    private Triplet(T first, U second, V third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @JsonCreator
    public static <T, U, V> Triplet<T, U, V> of(
            @JsonProperty("ord1") T first,
            @JsonProperty("ord2") U second,
            @JsonProperty("ord3") V third) {
        return new Triplet<>(first, second, third);
    }

    @JsonProperty("ord1")
    public T ord1() {
        return first;
    }

    @JsonProperty("ord2")
    public U ord2() {
        return second;
    }

    @JsonProperty("ord3")
    public V ord3() {
        return third;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }
}
