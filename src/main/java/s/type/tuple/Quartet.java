package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class Quartet<T, U, V, W> extends Triplet<T, U, V> {

    @JsonProperty("ord4")
    private final W fourth;

    protected Quartet(T first, U second, V third, W fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }

    public static <T, U, V, W> Quartet<T, U, V, W> of(T first, U second, V third, W fourth) {
        return new Quartet<>(first, second, third, fourth);
    }

    public W ord4() {
        return fourth;
    }

    @Override
    public String toString() {
        return "(" + ord1() + ", " + ord2() + ", " + ord3() + ", " + fourth + ")";
    }
}
