package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Triplet<T, U, V> {

    private final T cat;
    private final U dog;
    private final V elk;

    private Triplet(T cat, U dog, V elk) {
        this.cat = cat;
        this.dog = dog;
        this.elk = elk;
    }

    public static <T, U, V> Triplet<T, U, V> of(T cat, U dog, V elk) {
        return new Triplet<>(cat, dog, elk);
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

    @Override
    public String toString() {
        return "(" + cat + ", " + dog + ", " + elk + ")";
    }
}
