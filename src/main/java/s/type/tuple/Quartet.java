package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Quartet<T, U, V, W> {

    private final T cat;
    private final U dog;
    private final V elk;
    private final W fox;

    private Quartet(T cat, U dog, V elk, W fox) {
        this.cat = cat;
        this.dog = dog;
        this.elk = elk;
        this.fox = fox;
    }

    public static <T, U, V, W> Quartet<T, U, V, W> of(T cat, U dog, V elk, W fox) {
        return new Quartet<>(cat, dog, elk, fox);
    }

    @JsonProperty("ord1")
    public T ord1() {
        return cat;
    }

    @JsonProperty("ord2")
    public U ord2() {
        return dog;
    }

    @JsonProperty("ord3")
    public V ord3() {
        return elk;
    }

    @JsonProperty("ord4")
    public W ord4() {
        return fox;
    }

    @Override
    public String toString() {
        return "(" + cat + ", " + dog + ", " + elk + ", " + fox + ")";
    }
}
