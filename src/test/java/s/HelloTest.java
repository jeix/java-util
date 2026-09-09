package s;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.jupiter.api.Test;

class HelloTest {

    @Test
    void greetsByName() {
        assertEquals("Hello, Java!", Hello.greet("Java"));
    }

    @Test
    void roundTripsGreetingWithJacksonAndLombok() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Greeting greeting = new Greeting();
        greeting.setMessage(Hello.greet("자바"));

        String json = mapper.writeValueAsString(greeting);
        Greeting restored = mapper.readValue(json, Greeting.class);

        assertEquals("Hello, 자바!", mapper.readTree(json).get("message").asText());
        assertEquals(greeting, restored);
    }

    @Data
    public static class Greeting {
        private String message;
    }
}
