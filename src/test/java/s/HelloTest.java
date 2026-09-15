package s;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HelloTest {

    @Test
    void greetReturnsGreeting() {
        Hello hello = new Hello("world");

        assertEquals("Hello, world!", hello.greet());
    }

    @Test
    void toJsonSerializesName() {
        Hello hello = new Hello("world");

        assertEquals("{\"name\":\"world\"}", hello.toJson());
    }

    @Test
    void rejectsNullName() {
        assertThrows(NullPointerException.class, () -> new Hello(null));
    }
}
