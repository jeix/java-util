package s;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class HelloTest {

    @Test
    void greet_withName_returnsHelloWithName() {
        assertEquals("Hello, Alice!", Hello.greet("Alice"));
    }

    @Test
    void greet_nullOrEmpty_returnsHelloWorld() {
        assertEquals("Hello, World!", Hello.greet(null));
        assertEquals("Hello, World!", Hello.greet(""));
        assertEquals("Hello, World!", Hello.greet("   "));
    }

    @Test
    void greet_trimsName() {
        assertEquals("Hello, Bob!", Hello.greet("  Bob  "));
    }

    @Test
    void toJson_returnsValidJson() throws JsonProcessingException {
        String result = Hello.toJson("message", "hi");
        assertEquals("{\"message\":\"hi\"}", result);
    }

    @Test
    void isPalindrome_palindromeReturnsTrue() {
        assertTrue(Hello.isPalindrome("racecar"));
        assertTrue(Hello.isPalindrome("level"));
        assertTrue(Hello.isPalindrome("a"));
        assertTrue(Hello.isPalindrome(""));
    }

    @Test
    void isPalindrome_nonPalindromeReturnsFalse() {
        assertFalse(Hello.isPalindrome("hello"));
        assertFalse(Hello.isPalindrome("java"));
    }

    @Test
    void isPalindrome_nullReturnsFalse() {
        assertFalse(Hello.isPalindrome(null));
    }

    @Test
    void logMessage_success() {
        log.info("HelloTest 실행 중입니다");
        log.info("테스트 결과: {}", "PASS");
        assertEquals("Hello, World!", Hello.greet(null));
        log.info("greet(null) 결과: {}", "Hello, World!");
    }
}
