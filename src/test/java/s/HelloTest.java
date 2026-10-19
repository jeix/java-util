package s;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HelloTest {

    @Test
    void greet() {
        assertEquals("Hello, World!", Hello.greet("World"));
    }

    @Test
    void toJson() throws Exception {
        assertEquals("{\"message\":\"Hello, World!\"}", Hello.toJson("World"));
    }
}
