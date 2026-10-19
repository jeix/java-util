package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/** 세 값의 불변 튜플. */
@EqualsAndHashCode
public final class Triplet<T, U, V> {

    @JsonProperty("ord1")
    private final T ord1;

    @JsonProperty("ord2")
    private final U ord2;

    @JsonProperty("ord3")
    private final V ord3;

    @JsonCreator
    private Triplet(
            @JsonProperty("ord1") T ord1,
            @JsonProperty("ord2") U ord2,
            @JsonProperty("ord3") V ord3) {
        this.ord1 = ord1;
        this.ord2 = ord2;
        this.ord3 = ord3;
    }

    public static <T, U, V> Triplet<T, U, V> of(T cat, U dog, V elk) {
        return new Triplet<>(cat, dog, elk);
    }

    public T ord1() {
        return ord1;
    }

    public U ord2() {
        return ord2;
    }

    public V ord3() {
        return ord3;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ", " + ord3 + ")";
    }
}
