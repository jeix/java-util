package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Triplet<T, U, V> {

    private final T ord1;
    private final U ord2;
    private final V ord3;

    private Triplet(T cat, U dog, V elk) {
        this.ord1 = cat;
        this.ord2 = dog;
        this.ord3 = elk;
    }

    @JsonCreator
    public static <T, U, V> Triplet<T, U, V> of(
            @JsonProperty("ord1") T cat,
            @JsonProperty("ord2") U dog,
            @JsonProperty("ord3") V elk) {
        return new Triplet<>(cat, dog, elk);
    }

    @JsonProperty("ord1")
    public T ord1() {
        return ord1;
    }

    @JsonProperty("ord2")
    public U ord2() {
        return ord2;
    }

    @JsonProperty("ord3")
    public V ord3() {
        return ord3;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ", " + ord3 + ")";
    }
}
