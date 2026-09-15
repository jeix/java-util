package s.type.tuple;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;

/**
 * 값 세 개를 묶는 튜플 데이터 타입입니다.
 *
 * <p>인스턴스는 {@link #of(Object, Object, Object)} 스태틱 팩터리 메서드로 만들고, 값은 인스턴스 메서드인
 * {@link #ord1()}, {@link #ord2()}, {@link #ord3()}으로 읽습니다. 세 메서드는 {@code @JsonProperty}로 지정된
 * JSON 변환 대상입니다.
 *
 * @param <T> 첫 번째 값 타입
 * @param <U> 두 번째 값 타입
 * @param <V> 세 번째 값 타입
 */
@EqualsAndHashCode
public class Triplet<T, U, V> {

    private final T cat;

    private final U dog;

    private final V elk;

    /**
     * 세 값을 받아 {@link Triplet}을 만듭니다.
     *
     * @param cat 첫 번째 값
     * @param dog 두 번째 값
     * @param elk 세 번째 값
     * @param <T> 첫 번째 값 타입
     * @param <U> 두 번째 값 타입
     * @param <V> 세 번째 값 타입
     * @return 만들어진 {@link Triplet}
     */
    public static <T, U, V> Triplet<T, U, V> of(T cat, U dog, V elk) {
        return new Triplet<>(cat, dog, elk);
    }

    /**
     * 값을 받는 생성자입니다. {@link #of(Object, Object, Object)}를 사용합니다.
     *
     * @param cat 첫 번째 값
     * @param dog 두 번째 값
     * @param elk 세 번째 값
     */
    private Triplet(T cat, U dog, V elk) {
        this.cat = cat;
        this.dog = dog;
        this.elk = elk;
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
     * 세 번째 값을 반환합니다. JSON 변환 대상 필드입니다.
     *
     * @return 세 번째 값
     */
    @JsonProperty("ord3")
    public V ord3() {
        return elk;
    }

    /**
     * {@code (첫 번째 값, 두 번째 값, 세 번째 값)} 형식의 문자열을 반환합니다.
     *
     * @return 튜플 문자열
     */
    @Override
    public String toString() {
        return "(" + cat + ", " + dog + ", " + elk + ")";
    }
}
