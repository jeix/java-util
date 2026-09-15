package s.type.tuple;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/** {@link Pair}, {@link Triplet}, {@link Quartet}를 검증하는 테스트입니다. */
@Slf4j
class TupleTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        log.info("@Slf4j 로거를 사용하는 테스트 클래스입니다: {}", TupleTest.class.getName());
    }

    /**
     * JSON 객체의 프로퍼티 순서는 의미가 없으므로 트리로 비교합니다.
     *
     * @param expected 기대 JSON
     * @param value    직렬화할 값
     * @throws Exception JSON 변환에 실패한 경우
     */
    private static void assertJson(String expected, Object value) throws Exception {
        assertEquals(MAPPER.readTree(expected), MAPPER.readTree(MAPPER.writeValueAsString(value)),
                "JSON 트리가 같아야 한다");
    }

    @Nested
    @DisplayName("Pair")
    class PairTest {

        @Test
        @DisplayName("of로 만들고 ord1, ord2로 값을 읽는다")
        void values() {
            Pair<String, Integer> pair = Pair.of("cat", 2);

            assertAll(
                    () -> assertEquals("cat", pair.ord1()),
                    () -> assertEquals(2, pair.ord2().intValue()),
                    () -> assertEquals("(cat, 2)", pair.toString()));
        }

        @Test
        @DisplayName("같은 값을 가진 Pair는 equals와 hashCode가 같다")
        void equalsAndHashCode() {
            Pair<String, String> first = Pair.of("cat", "dog");
            Pair<String, String> same = Pair.of("cat", "dog");
            Pair<String, String> other = Pair.of("cat", "fox");

            assertAll(
                    () -> assertEquals(first, same),
                    () -> assertEquals(first.hashCode(), same.hashCode()),
                    () -> assertNotEquals(first, other),
                    () -> assertNotEquals(first, null),
                    () -> assertNotEquals(first, Pair.of("dog", "cat")),
                    () -> assertEquals(2, new HashSet<>(List.of(first, same, other)).size()));
        }

        @Test
        @DisplayName("Jackson으로 {ord1, ord2} JSON으로 변환한다")
        void json() throws Exception {
            Pair<String, Integer> pair = Pair.of("cat", 2);

            assertJson("{\"ord1\":\"cat\",\"ord2\":2}", pair);
        }
    }

    @Nested
    @DisplayName("Triplet")
    class TripletTest {

        @Test
        @DisplayName("of로 만들고 ord1, ord2, ord3으로 값을 읽는다")
        void values() {
            Triplet<String, Integer, Boolean> triplet = Triplet.of("cat", 2, true);

            assertAll(
                    () -> assertEquals("cat", triplet.ord1()),
                    () -> assertEquals(2, triplet.ord2().intValue()),
                    () -> assertEquals(Boolean.TRUE, triplet.ord3()),
                    () -> assertEquals("(cat, 2, true)", triplet.toString()));
        }

        @Test
        @DisplayName("같은 값을 가진 Triplet은 equals와 hashCode가 같다")
        void equalsAndHashCode() {
            Triplet<String, Integer, Boolean> first = Triplet.of("cat", 2, true);
            Triplet<String, Integer, Boolean> same = Triplet.of("cat", 2, true);
            Triplet<String, Integer, Boolean> other = Triplet.of("cat", 2, false);

            assertAll(
                    () -> assertEquals(first, same),
                    () -> assertEquals(first.hashCode(), same.hashCode()),
                    () -> assertNotEquals(first, other),
                    () -> assertNotEquals(first, null),
                    () -> assertNotEquals(first, Pair.of("cat", 2)),
                    () -> assertEquals(2, new HashSet<>(List.of(first, same, other)).size()));
        }

        @Test
        @DisplayName("Jackson으로 {ord1, ord2, ord3} JSON으로 변환한다")
        void json() throws Exception {
            Triplet<String, Integer, Boolean> triplet = Triplet.of("cat", 2, true);

            assertJson("{\"ord1\":\"cat\",\"ord2\":2,\"ord3\":true}", triplet);
        }
    }

    @Nested
    @DisplayName("Quartet")
    class QuartetTest {

        @Test
        @DisplayName("of로 만들고 ord1, ord2, ord3, ord4로 값을 읽는다")
        void values() {
            Quartet<String, Integer, Boolean, BigDecimal> quartet =
                    Quartet.of("cat", 2, true, new BigDecimal("1.50"));

            assertAll(
                    () -> assertEquals("cat", quartet.ord1()),
                    () -> assertEquals(2, quartet.ord2().intValue()),
                    () -> assertEquals(Boolean.TRUE, quartet.ord3()),
                    () -> assertEquals(new BigDecimal("1.50"), quartet.ord4()),
                    () -> assertEquals("(cat, 2, true, 1.50)", quartet.toString()));
        }

        @Test
        @DisplayName("같은 값을 가진 Quartet은 equals와 hashCode가 같다")
        void equalsAndHashCode() {
            Quartet<String, Integer, Boolean, String> first = Quartet.of("cat", 2, true, "fox");
            Quartet<String, Integer, Boolean, String> same = Quartet.of("cat", 2, true, "fox");
            Quartet<String, Integer, Boolean, String> other = Quartet.of("cat", 2, true, "dog");

            assertAll(
                    () -> assertEquals(first, same),
                    () -> assertEquals(first.hashCode(), same.hashCode()),
                    () -> assertNotEquals(first, other),
                    () -> assertNotEquals(first, null),
                    () -> assertNotEquals(first, Triplet.of("cat", 2, true), "다른 튜플 타입과는 다르다"),
                    () -> assertEquals(2, new HashSet<>(List.of(first, same, other)).size()));
        }

        @Test
        @DisplayName("Jackson으로 {ord1, ord2, ord3, ord4} JSON으로 변환한다")
        void json() throws Exception {
            Quartet<String, Integer, Boolean, String> quartet = Quartet.of("cat", 2, true, "fox");

            assertJson("{\"ord1\":\"cat\",\"ord2\":2,\"ord3\":true,\"ord4\":\"fox\"}", quartet);
        }

        @Test
        @DisplayName("null 값도 그대로 표현한다")
        void nullValues() {
            Quartet<String, String, String, String> quartet = Quartet.<String, String, String, String>of(null, "dog",
                    null, "fox");

            assertAll(
                    () -> assertEquals("(null, dog, null, fox)", quartet.toString()),
                    () -> assertEquals(Quartet.<String, String, String, String>of(null, "dog", null, "fox"), quartet));
        }
    }
}
