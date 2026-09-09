package s.type.tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TupleTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createsPair() {
        Pair<String, Integer> pair = Pair.of("cat", 2);

        assertEquals("cat", pair.ord1());
        assertEquals(2, pair.ord2());
        assertEquals("(cat, 2)", pair.toString());
    }

    @Test
    void createsTriplet() {
        Triplet<String, Integer, Boolean> triplet = Triplet.of("cat", 2, true);

        assertEquals("cat", triplet.ord1());
        assertEquals(2, triplet.ord2());
        assertEquals(true, triplet.ord3());
        assertEquals("(cat, 2, true)", triplet.toString());
    }

    @Test
    void createsQuartet() {
        Quartet<String, Integer, Boolean, Double> quartet = Quartet.of("cat", 2, true, 4.5);

        assertEquals("cat", quartet.ord1());
        assertEquals(2, quartet.ord2());
        assertEquals(true, quartet.ord3());
        assertEquals(4.5, quartet.ord4());
        assertEquals("(cat, 2, true, 4.5)", quartet.toString());
    }

    @Test
    void supportsNullValues() {
        Quartet<Object, Object, Object, Object> quartet = Quartet.of(null, null, null, null);

        assertNull(quartet.ord1());
        assertNull(quartet.ord2());
        assertNull(quartet.ord3());
        assertNull(quartet.ord4());
        assertEquals("(null, null, null, null)", quartet.toString());
    }

    @Test
    void comparesTupleValues() {
        assertEquals(Pair.of("cat", 2), Pair.of("cat", 2));
        assertEquals(Pair.of("cat", 2).hashCode(), Pair.of("cat", 2).hashCode());
        assertNotEquals(Pair.of("cat", 2), Pair.of("dog", 2));
        assertEquals(Triplet.of("cat", 2, true), Triplet.of("cat", 2, true));
        assertNotEquals(Triplet.of("cat", 2, true), Triplet.of("cat", 2, false));
        assertEquals(Quartet.of("cat", 2, true, 4.5), Quartet.of("cat", 2, true, 4.5));
        assertNotEquals(Quartet.of("cat", 2, true, 4.5), Quartet.of("cat", 2, true, 5.5));
    }

    @Test
    void serializesOrdinalsToJson() throws JsonProcessingException {
        assertEquals("{\"ord1\":\"cat\",\"ord2\":2}",
                objectMapper.writeValueAsString(Pair.of("cat", 2)));
        assertEquals("{\"ord1\":\"cat\",\"ord2\":2,\"ord3\":true}",
                objectMapper.writeValueAsString(Triplet.of("cat", 2, true)));
        String json = objectMapper.writeValueAsString(Quartet.of("cat", 2, true, 4.5));
        log.info("quartet json: {}", json);
        assertEquals("{\"ord1\":\"cat\",\"ord2\":2,\"ord3\":true,\"ord4\":4.5}", json);
    }

    @Test
    void deserializesJsonThroughFactories() throws JsonProcessingException {
        Pair<String, Integer> pair = objectMapper.readValue(
                "{\"ord1\":\"cat\",\"ord2\":2}", new TypeReference<>() {});
        Triplet<String, Integer, Boolean> triplet = objectMapper.readValue(
                "{\"ord1\":\"cat\",\"ord2\":2,\"ord3\":true}", new TypeReference<>() {});
        Quartet<String, Integer, Boolean, Double> quartet = objectMapper.readValue(
                "{\"ord1\":\"cat\",\"ord2\":2,\"ord3\":true,\"ord4\":4.5}",
                new TypeReference<>() {});

        assertEquals(Pair.of("cat", 2), pair);
        assertEquals(Triplet.of("cat", 2, true), triplet);
        assertEquals(Quartet.of("cat", 2, true, 4.5), quartet);
    }
}
