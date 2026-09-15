package s.type.tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("튜플 타입")
class TupleTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Nested
    @DisplayName("Pair")
    class PairTest {

        @Test
        @DisplayName("of 로 만들고 ord1, ord2 로 값을 꺼낸다")
        void values() {
            Pair<String, Integer> pair = Pair.of("cat", 7);

            assertEquals("cat", pair.ord1());
            assertEquals(Integer.valueOf(7), pair.ord2());
        }

        @Test
        @DisplayName("toString 은 (t, u) 형식")
        void toStringFormat() {
            assertEquals("(cat, 7)", Pair.of("cat", 7).toString());
            assertEquals("(null, null)", Pair.of(null, null).toString());
        }

        @Test
        @DisplayName("값이 같으면 같은 튜플로 본다")
        void equalsAndHashCode() {
            Pair<String, Integer> pair = Pair.of("cat", 7);

            assertEquals(Pair.of("cat", 7), pair);
            assertEquals(Pair.of("cat", 7).hashCode(), pair.hashCode());
            assertNotEquals(Pair.of("cat", 8), pair);
            assertNotEquals(Pair.of("dog", 7), pair);
        }

        @Test
        @DisplayName("Jackson 으로 JSON 변환")
        void json() throws Exception {
            Pair<String, Integer> pair = Pair.of("cat", 7);

            String json = MAPPER.writeValueAsString(pair);
            log.info("Pair JSON: {}", json);

            assertEquals("{\"ord1\":\"cat\",\"ord2\":7}", json);
            assertEquals(pair, MAPPER.readValue(json, new TypeReference<Pair<String, Integer>>() {
            }));
        }
    }

    @Nested
    @DisplayName("Triplet")
    class TripletTest {

        @Test
        @DisplayName("of 로 만들고 ord1, ord2, ord3 으로 값을 꺼낸다")
        void values() {
            Triplet<String, Integer, Boolean> triplet = Triplet.of("cat", 7, true);

            assertEquals("cat", triplet.ord1());
            assertEquals(Integer.valueOf(7), triplet.ord2());
            assertEquals(Boolean.TRUE, triplet.ord3());
        }

        @Test
        @DisplayName("toString 은 (t, u, v) 형식")
        void toStringFormat() {
            assertEquals("(cat, 7, true)", Triplet.of("cat", 7, true).toString());
        }

        @Test
        @DisplayName("값이 같으면 같은 튜플로 본다")
        void equalsAndHashCode() {
            Triplet<String, Integer, Boolean> triplet = Triplet.of("cat", 7, true);

            assertEquals(Triplet.of("cat", 7, true), triplet);
            assertEquals(Triplet.of("cat", 7, true).hashCode(), triplet.hashCode());
            assertNotEquals(Triplet.of("cat", 7, false), triplet);
            assertNotEquals(Triplet.of("cat", 8, true), triplet);
        }

        @Test
        @DisplayName("Jackson 으로 JSON 변환")
        void json() throws Exception {
            Triplet<String, Integer, Boolean> triplet = Triplet.of("cat", 7, true);

            String json = MAPPER.writeValueAsString(triplet);
            log.info("Triplet JSON: {}", json);

            assertEquals("{\"ord1\":\"cat\",\"ord2\":7,\"ord3\":true}", json);
            assertEquals(triplet, MAPPER.readValue(json, new TypeReference<Triplet<String, Integer, Boolean>>() {
            }));
        }
    }

    @Nested
    @DisplayName("Quartet")
    class QuartetTest {

        @Test
        @DisplayName("of 로 만들고 ord1 부터 ord4 까지로 값을 꺼낸다")
        void values() {
            Quartet<String, Integer, Boolean, Double> quartet = Quartet.of("cat", 7, true, 1.5);

            assertEquals("cat", quartet.ord1());
            assertEquals(Integer.valueOf(7), quartet.ord2());
            assertEquals(Boolean.TRUE, quartet.ord3());
            assertEquals(Double.valueOf(1.5), quartet.ord4());
        }

        @Test
        @DisplayName("toString 은 (t, u, v, w) 형식")
        void toStringFormat() {
            assertEquals("(cat, 7, true, 1.5)", Quartet.of("cat", 7, true, 1.5).toString());
        }

        @Test
        @DisplayName("값이 같으면 같은 튜플로 본다")
        void equalsAndHashCode() {
            Quartet<String, Integer, Boolean, Double> quartet = Quartet.of("cat", 7, true, 1.5);

            assertEquals(Quartet.of("cat", 7, true, 1.5), quartet);
            assertEquals(Quartet.of("cat", 7, true, 1.5).hashCode(), quartet.hashCode());
            assertNotEquals(Quartet.of("cat", 7, true, 2.5), quartet);
            assertNotEquals(Quartet.of("cat", 7, false, 1.5), quartet);
        }

        @Test
        @DisplayName("Jackson 으로 JSON 변환")
        void json() throws Exception {
            Quartet<String, Integer, Boolean, Double> quartet = Quartet.of("cat", 7, true, 1.5);

            String json = MAPPER.writeValueAsString(quartet);
            log.info("Quartet JSON: {}", json);

            assertEquals("{\"ord1\":\"cat\",\"ord2\":7,\"ord3\":true,\"ord4\":1.5}", json);
            assertEquals(quartet, MAPPER.readValue(
                    json, new TypeReference<Quartet<String, Integer, Boolean, Double>>() {
                    }));
        }

        @Test
        @DisplayName("null 값을 담아도 JSON 변환이 된다")
        void jsonWithNull() throws Exception {
            Quartet<String, Integer, Boolean, Double> quartet = Quartet.of(null, null, null, null);
            String json = MAPPER.writeValueAsString(quartet);

            assertEquals("{\"ord1\":null,\"ord2\":null,\"ord3\":null,\"ord4\":null}", json);
            assertEquals(quartet, MAPPER.readValue(
                    json, new TypeReference<Quartet<String, Integer, Boolean, Double>>() {
                    }));
        }
    }
}
