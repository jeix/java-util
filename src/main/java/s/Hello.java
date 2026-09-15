package s;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 프로젝트 뼈대가 제대로 동작하는지 확인하기 위한 예제 클래스.
 *
 * <p>여기서 확인하는 것:
 * <ul>
 *   <li>lombok 애노테이션 처리 ({@code @Getter}, {@code @ToString}, {@code @EqualsAndHashCode})</li>
 *   <li>Jackson 의 JSON 직렬화/역직렬화</li>
 *   <li>JUnit 5 기반 테스트 실행</li>
 * </ul>
 *
 * <p>실제 유틸리티 클래스는 이 클래스를 본보기로 {@code s} 패키지나 그 하위 패키지에 추가한다.
 */
@Getter
@ToString
@EqualsAndHashCode
public class Hello {

    private final String name;

    @JsonCreator
    public Hello(@JsonProperty("name") String name) {
        this.name = name;
    }

    /**
     * 인사말 문장을 만든다.
     *
     * @return 예) {@code "Hello, world!"}
     */
    public String greeting() {
        return "Hello, " + name + "!";
    }
}
