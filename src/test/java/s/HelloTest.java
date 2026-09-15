package s;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** {@link Hello} 클래스의 동작을 확인하는 JUnit 5 테스트입니다. */
class HelloTest {

    @Test
    @DisplayName("greet는 이름을 포함한 인사말을 반환한다")
    void greet() {
        Hello hello = new Hello("world");

        assertEquals("Hello, world!", hello.greet());
    }

    @Test
    @DisplayName("Lombok이 생성한 빌더와 접근자가 동작한다")
    void builderAndAccessors() {
        Hello hello = Hello.builder().name("lombok").build();

        assertEquals("lombok", hello.getName());
        assertEquals(new Hello("lombok"), hello);
        assertNotEquals(new Hello("other"), hello);
    }

    @Test
    @DisplayName("Jackson으로 JSON 직렬화와 역직렬화를 수행한다")
    void jacksonRoundTrip() throws Exception {
        Hello hello = new Hello("jackson");

        String json = hello.toJson();

        assertEquals("{\"name\":\"jackson\"}", json);
        assertEquals(hello, Hello.fromJson(json));
    }
}
