package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

/**
 * 값 두 개를 묶는 튜플. 값이 같으면 같은 것으로 본다.
 *
 * <p>JSON 으로 바꾸면 {@code {"ord1": ..., "ord2": ...}} 모양이 된다.
 *
 * @param <T> 첫 번째 값(cat)의 타입
 * @param <U> 두 번째 값(dog)의 타입
 */
@EqualsAndHashCode
public final class Pair<T, U> {

    private final T cat;
    private final U dog;

    private Pair(T cat, U dog) {
        this.cat = cat;
        this.dog = dog;
    }

    /**
     * 튜플을 만든다.
     *
     * @param cat 첫 번째 값
     * @param dog 두 번째 값
     * @return 값 두 개를 담은 튜플
     */
    @JsonCreator
    public static <T, U> Pair<T, U> of(@JsonProperty("ord1") T cat, @JsonProperty("ord2") U dog) {
        return new Pair<>(cat, dog);
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

    /** {@code (cat, dog)} 모양의 문자열. */
    @Override
    public String toString() {
        return "(" + cat + ", " + dog + ")";
    }
}
