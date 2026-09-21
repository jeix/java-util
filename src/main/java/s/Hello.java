package s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Hello {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String greet(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Hello, World!";
        }
        return "Hello, " + name.trim() + "!";
    }

    public String toJson(String key, String value) throws JsonProcessingException {
        ObjectNode node = MAPPER.createObjectNode();
        node.put(key, value);
        return MAPPER.writeValueAsString(node);
    }

    public boolean isPalindrome(String input) {
        if (input == null) {
            return false;
        }
        String reversed = new StringBuilder(input).reverse().toString();
        return input.equals(reversed);
    }
}
