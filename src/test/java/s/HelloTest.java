package s;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelloTest {

    @Test
    void greetReturnsExpectedMessage() {
        Hello hello = new Hello("World");
        assertEquals("Hello, World!", hello.greet());
    }

    @Test
    void greetWithEmptyMessage() {
        Hello hello = new Hello("");
        assertEquals("Hello, !", hello.greet());
    }

    @Test
    void lombokGeneratedMethodsWork() {
        Hello hello = new Hello();
        hello.setMessage("Test");
        assertEquals("Test", hello.getMessage());
    }
}