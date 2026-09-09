package s.util;

/** zip으로 묶은 두 값을 보관해요. null 값도 허용해요. */
public record Pair<T, U>(T first, U second) {
}
