package s.type.tuple;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class TupleTest {

    // ---- Pair ----

    @Test
    void pair_of_createsInstance() {
        Pair<String, Integer> pair = Pair.of("hello", 42);
        assertEquals("hello", pair.ord1());
        assertEquals(42, pair.ord2());
    }

    @Test
    void pair_toString_format() {
        assertEquals("(hello, 42)", Pair.of("hello", 42).toString());
        assertEquals("(null, null)", Pair.of(null, null).toString());
    }

    @Test
    void pair_equalsAndHashCode() {
        Pair<String, Integer> p1 = Pair.of("hello", 42);
        Pair<String, Integer> p2 = Pair.of("hello", 42);
        Pair<String, Integer> p3 = Pair.of("world", 42);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
    }

    @Test
    void pair_nullValues() {
        Pair<String, String> pair = Pair.of(null, null);
        assertNull(pair.ord1());
        assertNull(pair.ord2());
        assertEquals("(null, null)", pair.toString());
    }

    @Test
    void pair_differentTypes() {
        Pair<Integer, Pair<String, Integer>> nested = Pair.of(1, Pair.of("inner", 2));
        assertEquals(1, nested.ord1());
        assertEquals(Pair.of("inner", 2), nested.ord2());
        assertEquals("(1, (inner, 2))", nested.toString());
    }

    @Test
    void pair_notEqualDifferentType() {
        assertNotEquals(Pair.of(1, 2), "not a pair");
        assertNotEquals(Pair.of(1, 2), null);
    }

    // ---- Triplet ----

    @Test
    void triplet_of_createsInstance() {
        Triplet<String, Integer, Double> t = Triplet.of("hello", 42, 3.14);
        assertEquals("hello", t.ord1());
        assertEquals(42, t.ord2());
        assertEquals(3.14, t.ord3());
    }

    @Test
    void triplet_toString_format() {
        assertEquals("(hello, 42, 3.14)", Triplet.of("hello", 42, 3.14).toString());
        assertEquals("(null, null, null)", Triplet.of(null, null, null).toString());
    }

    @Test
    void triplet_equalsAndHashCode() {
        Triplet<String, Integer, Double> t1 = Triplet.of("hello", 42, 3.14);
        Triplet<String, Integer, Double> t2 = Triplet.of("hello", 42, 3.14);
        Triplet<String, Integer, Double> t3 = Triplet.of("hello", 42, 2.71);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotEquals(t1, t3);
    }

    @Test
    void triplet_nullValues() {
        Triplet<String, String, String> t = Triplet.of(null, null, null);
        assertNull(t.ord1());
        assertNull(t.ord2());
        assertNull(t.ord3());
        assertEquals("(null, null, null)", t.toString());
    }

    // ---- Quartet ----

    @Test
    void quartet_of_createsInstance() {
        Quartet<String, Integer, Double, Boolean> q = Quartet.of("hello", 42, 3.14, true);
        assertEquals("hello", q.ord1());
        assertEquals(42, q.ord2());
        assertEquals(3.14, q.ord3());
        assertEquals(true, q.ord4());
    }

    @Test
    void quartet_toString_format() {
        assertEquals("(hello, 42, 3.14, true)",
            Quartet.of("hello", 42, 3.14, true).toString());
        assertEquals("(null, null, null, null)",
            Quartet.of(null, null, null, null).toString());
    }

    @Test
    void quartet_equalsAndHashCode() {
        Quartet<String, Integer, Double, Boolean> q1 = Quartet.of("hello", 42, 3.14, true);
        Quartet<String, Integer, Double, Boolean> q2 = Quartet.of("hello", 42, 3.14, true);
        Quartet<String, Integer, Double, Boolean> q3 = Quartet.of("hello", 42, 3.14, false);

        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
        assertNotEquals(q1, q3);
    }

    @Test
    void quartet_nullValues() {
        Quartet<String, String, String, String> q = Quartet.of(null, null, null, null);
        assertNull(q.ord1());
        assertNull(q.ord2());
        assertNull(q.ord3());
        assertNull(q.ord4());
        assertEquals("(null, null, null, null)", q.toString());
    }

    @Test
    void quartet_notEqualDifferentType() {
        assertNotEquals(Quartet.of(1, 2, 3, 4), "not a quartet");
        assertNotEquals(Quartet.of(1, 2, 3, 4), null);
    }

    // ---- Jackson JSON serialization ----

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void pair_jsonSerialize() throws JsonProcessingException {
        Pair<String, Integer> pair = Pair.of("hello", 42);
        String json = MAPPER.writeValueAsString(pair);
        log.debug("Pair JSON: {}", json);
        assertTrue(json.contains("\"ord1\":\"hello\""));
        assertTrue(json.contains("\"ord2\":42"));
    }

    @Test
    void triplet_jsonSerialize() throws JsonProcessingException {
        Triplet<String, Integer, Double> t = Triplet.of("hello", 42, 3.14);
        String json = MAPPER.writeValueAsString(t);
        log.debug("Triplet JSON: {}", json);
        assertTrue(json.contains("\"ord1\":\"hello\""));
        assertTrue(json.contains("\"ord2\":42"));
        assertTrue(json.contains("\"ord3\":3.14"));
    }

    @Test
    void pair_jsonDeserialize() throws JsonProcessingException {
        String json = "{\"ord1\":\"hello\",\"ord2\":42}";
        Pair<String, Integer> pair =
            MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(
                Pair.class, String.class, Integer.class));
        assertEquals("hello", pair.ord1());
        assertEquals(42, pair.ord2());
    }

    @Test
    void triplet_jsonDeserialize() throws JsonProcessingException {
        String json = "{\"ord1\":\"hello\",\"ord2\":42,\"ord3\":3.14}";
        Triplet<String, Integer, Double> t =
            MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(
                Triplet.class, String.class, Integer.class, Double.class));
        assertEquals("hello", t.ord1());
        assertEquals(42, t.ord2());
        assertEquals(3.14, t.ord3());
    }

    @Test
    void quartet_jsonSerialize() throws JsonProcessingException {
        Quartet<String, Integer, Double, Boolean> q = Quartet.of("hello", 42, 3.14, true);
        String json = MAPPER.writeValueAsString(q);
        log.debug("Quartet JSON: {}", json);
        assertTrue(json.contains("\"ord1\":\"hello\""));
        assertTrue(json.contains("\"ord2\":42"));
        assertTrue(json.contains("\"ord3\":3.14"));
        assertTrue(json.contains("\"ord4\":true"));
    }

    @Test
    void quartet_jsonDeserialize() throws JsonProcessingException {
        String json = "{\"ord1\":\"hello\",\"ord2\":42,\"ord3\":3.14,\"ord4\":true}";
        Quartet<String, Integer, Double, Boolean> q =
            MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(
                Quartet.class, String.class, Integer.class, Double.class, Boolean.class));
        assertEquals("hello", q.ord1());
        assertEquals(42, q.ord2());
        assertEquals(3.14, q.ord3());
        assertEquals(true, q.ord4());
    }
}
