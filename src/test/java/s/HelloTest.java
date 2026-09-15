package s;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link Hello} 로 프로젝트 뼈대가 동작하는지 확인하는 테스트.
 */
class HelloTest {

    @Test
    @DisplayName("lombok @Getter 가 만든 접근자로 필드를 읽는다")
    void getter() {
        Hello hello = new Hello("world");

        assertEquals("world", hello.getName());
    }

    @Test
    @DisplayName("인사말 문장을 만든다")
    void greeting() {
        Hello hello = new Hello("world");

        assertEquals("Hello, world!", hello.greeting());
    }

    @Test
    @DisplayName("Jackson 으로 JSON 직렬화와 역직렬화를 한다")
    void jsonRoundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Hello hello = new Hello("world");

        String json = mapper.writeValueAsString(hello);
        assertEquals("{\"name\":\"world\"}", json);

        assertEquals(hello, mapper.readValue(json, Hello.class));
    }
}
