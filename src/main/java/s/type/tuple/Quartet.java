package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/** 네 값의 불변 튜플. */
@EqualsAndHashCode
public final class Quartet<T, U, V, W> {

    @JsonProperty("ord1")
    private final T ord1;

    @JsonProperty("ord2")
    private final U ord2;

    @JsonProperty("ord3")
    private final V ord3;

    @JsonProperty("ord4")
    private final W ord4;

    @JsonCreator
    private Quartet(
            @JsonProperty("ord1") T ord1,
            @JsonProperty("ord2") U ord2,
            @JsonProperty("ord3") V ord3,
            @JsonProperty("ord4") W ord4) {
        this.ord1 = ord1;
        this.ord2 = ord2;
        this.ord3 = ord3;
        this.ord4 = ord4;
    }

    public static <T, U, V, W> Quartet<T, U, V, W> of(T cat, U dog, V elk, W fox) {
        return new Quartet<>(cat, dog, elk, fox);
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

    public W ord4() {
        return ord4;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ", " + ord3 + ", " + ord4 + ")";
    }
}
