package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;

/**
 * 값 두 개를 묶는 튜플 데이터 타입입니다.
 *
 * <p>인스턴스는 {@link #of(Object, Object)} 스태틱 팩터리 메서드로 만들고, 값은 인스턴스 메서드인
 * {@link #ord1()}과 {@link #ord2()}로 읽습니다. 두 메서드는 {@code @JsonProperty}로 지정된 JSON 변환 대상입니다.
 *
 * @param <T> 첫 번째 값 타입
 * @param <U> 두 번째 값 타입
 */
@EqualsAndHashCode
public class Pair<T, U> {

    private final T cat;

    private final U dog;

    /**
     * 두 값을 받아 {@link Pair}를 만듭니다.
     *
     * @param cat 첫 번째 값
     * @param dog 두 번째 값
     * @param <T> 첫 번째 값 타입
     * @param <U> 두 번째 값 타입
     * @return 만들어진 {@link Pair}
     */
    public static <T, U> Pair<T, U> of(T cat, U dog) {
        return new Pair<>(cat, dog);
    }

    /**
     * 값을 받는 생성자입니다. {@link #of(Object, Object)}를 사용합니다.
     *
     * @param cat 첫 번째 값
     * @param dog 두 번째 값
     */
    private Pair(T cat, U dog) {
        this.cat = cat;
        this.dog = dog;
    }

    /**
     * 첫 번째 값을 반환합니다. JSON 변환 대상 필드입니다.
     *
     * @return 첫 번째 값
     */
    @JsonProperty("ord1")
    public T ord1() {
        return cat;
    }

    /**
     * 두 번째 값을 반환합니다. JSON 변환 대상 필드입니다.
     *
     * @return 두 번째 값
     */
    @JsonProperty("ord2")
    public U ord2() {
        return dog;
    }

    /**
     * {@code (첫 번째 값, 두 번째 값)} 형식의 문자열을 반환합니다.
     *
     * @return 튜플 문자열
     */
    @Override
    public String toString() {
        return "(" + cat + ", " + dog + ")";
    }
}
