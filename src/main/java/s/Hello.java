package s;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** 간단한 인사말을 만드는 유틸리티 클래스예요. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Hello {

    public static String greet(String name) {
        return "Hello, " + name + "!";
    }
}
