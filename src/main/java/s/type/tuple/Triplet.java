package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * 값 세 개를 묶는 튜플. 값이 같으면 같은 것으로 본다.
 *
 * <p>JSON 으로 바꾸면 {@code {"ord1": ..., "ord2": ..., "ord3": ...}} 모양이 된다.
 *
 * @param <T> 첫 번째 값(cat)의 타입
 * @param <U> 두 번째 값(dog)의 타입
 * @param <V> 세 번째 값(elk)의 타입
 */
@EqualsAndHashCode
public final class Triplet<T, U, V> {

    private final T cat;
    private final U dog;
    private final V elk;

    private Triplet(T cat, U dog, V elk) {
        this.cat = cat;
        this.dog = dog;
        this.elk = elk;
    }

    /**
     * 튜플을 만든다.
     *
     * @param cat 첫 번째 값
     * @param dog 두 번째 값
     * @param elk 세 번째 값
     * @return 값 세 개를 담은 튜플
     */
    @JsonCreator
    public static <T, U, V> Triplet<T, U, V> of(@JsonProperty("ord1") T cat, @JsonProperty("ord2") U dog,
                                                @JsonProperty("ord3") V elk) {
        return new Triplet<>(cat, dog, elk);
    }

    /** 첫 번째 값. JSON 으로 바꿀 때 {@code ord1} 이 된다. */
    @JsonProperty("ord1")
    public T ord1() {
        return cat;
    }

    /** 두 번째 값. JSON 으로 바꿀 때 {@code ord2} 가 된다. */
    @JsonProperty("ord2")
    public U ord2() {
        return dog;
    }

    /** 세 번째 값. JSON 으로 바꿀 때 {@code ord3} 이 된다. */
    @JsonProperty("ord3")
    public V ord3() {
        return elk;
    }

    /** {@code (cat, dog, elk)} 모양의 문자열. */
    @Override
    public String toString() {
        return "(" + cat + ", " + dog + ", " + elk + ")";
    }
}
