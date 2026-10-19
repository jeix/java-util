package s;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** 초기화한 프로젝트 구조(lombok, jackson)를 확인하기 위한 유틸리티 클래스. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Hello {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String greet(String name) {
        return "Hello, " + name + "!";
    }

    public static String toJson(String name) throws Exception {
        return MAPPER.writeValueAsString(java.util.Map.of("message", greet(name)));
    }
}
