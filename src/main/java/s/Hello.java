package s;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hello {
    private String message;

    public String greet() {
        return "Hello, " + message + "!";
    }
}