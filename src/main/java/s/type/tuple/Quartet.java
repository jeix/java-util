package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Quartet<T, U, V, W> {

    private final T first;
    private final U second;
    private final V third;
    private final W fourth;

    private Quartet(T first, U second, V third, W fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    @JsonCreator
    public static <T, U, V, W> Quartet<T, U, V, W> of(
            @JsonProperty("ord1") T first,
            @JsonProperty("ord2") U second,
            @JsonProperty("ord3") V third,
            @JsonProperty("ord4") W fourth) {
        return new Quartet<>(first, second, third, fourth);
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

    @JsonProperty("ord4")
    public W ord4() {
        return fourth;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ", " + fourth + ")";
    }
}
