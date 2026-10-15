package s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class Hello {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String greet(String name) {
        if (name == null || name.trim().isEmpty()) {
            log.info("Greeting with default name: World");
            return "Hello, World!";
        }
        log.info("Greeting with name: {}", name.trim());
        return "Hello, " + name.trim() + "!";
    }

    public String toJson(String key, String value) throws JsonProcessingException {
        log.info("Converting to JSON: key={}, value={}", key, value);
        ObjectNode node = MAPPER.createObjectNode();
        node.put(key, value);
        return MAPPER.writeValueAsString(node);
    }

    public boolean isPalindrome(String input) {
        if (input == null) {
            log.info("Palindrome check: input is null");
            return false;
        }
        String reversed = new StringBuilder(input).reverse().toString();
        boolean result = input.equals(reversed);
        log.info("Palindrome check: input={}, result={}", input, result);
        return result;
    }
}
