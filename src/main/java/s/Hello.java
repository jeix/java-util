package s;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.Value;

@Value
public class Hello {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @NonNull
    String name;

    public String greet() {
        return "Hello, " + name + "!";
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize Hello", e);
        }
    }
}
