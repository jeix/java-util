package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public final class Quartet<T, U, V, W> {

    private final T ord1;
    private final U ord2;
    private final V ord3;
    private final W ord4;

    private Quartet(T cat, U dog, V elk, W fox) {
        this.ord1 = cat;
        this.ord2 = dog;
        this.ord3 = elk;
        this.ord4 = fox;
    }

    @JsonCreator
    public static <T, U, V, W> Quartet<T, U, V, W> of(
            @JsonProperty("ord1") T cat,
            @JsonProperty("ord2") U dog,
            @JsonProperty("ord3") V elk,
            @JsonProperty("ord4") W fox) {
        return new Quartet<>(cat, dog, elk, fox);
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

    @JsonProperty("ord4")
    public W ord4() {
        return ord4;
    }

    @Override
    public String toString() {
        return "(" + ord1 + ", " + ord2 + ", " + ord3 + ", " + ord4 + ")";
    }
}
