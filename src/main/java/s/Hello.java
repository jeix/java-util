package s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 구조가 정상적으로 초기화되었는지 확인하기 위한 샘플 유틸리티 클래스입니다.
 *
 * <p>Lombok으로 접근자/생성자/빌더를 생성하고, Jackson으로 JSON 변환을 수행합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hello {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** 인사 대상 이름입니다. */
    private String name;

    /**
     * 이름을 포함한 인사말을 반환합니다.
     *
     * @return 인사말 문자열
     */
    public String greet() {
        return "Hello, " + name + "!";
    }

    /**
     * 이 객체를 JSON 문자열로 변환합니다.
     *
     * @return JSON 문자열
     * @throws JsonProcessingException JSON 변환에 실패한 경우
     */
    public String toJson() throws JsonProcessingException {
        return MAPPER.writeValueAsString(this);
    }

    /**
     * JSON 문자열을 {@link Hello} 객체로 변환합니다.
     *
     * @param json JSON 문자열
     * @return 변환된 객체
     * @throws JsonProcessingException JSON 변환에 실패한 경우
     */
    public static Hello fromJson(String json) throws JsonProcessingException {
        return MAPPER.readValue(json, Hello.class);
    }
}
