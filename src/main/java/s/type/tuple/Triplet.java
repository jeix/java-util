package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class Triplet<T, U, V> extends Pair<T, U> {

    @JsonProperty("ord3")
    private final V third;

    protected Triplet(T first, U second, V third) {
        super(first, second);
        this.third = third;
    }

    public static <T, U, V> Triplet<T, U, V> of(T first, U second, V third) {
        return new Triplet<>(first, second, third);
    }

    public V ord3() {
        return third;
    }

    @Override
    public String toString() {
        return "(" + ord1() + ", " + ord2() + ", " + third + ")";
    }
}
