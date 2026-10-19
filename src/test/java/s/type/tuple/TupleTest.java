package s.type.tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class TupleTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void pair() {
        Pair<Integer, String> p = Pair.of(1, "a");
        log.info("pair: {}", p);
        assertEquals(1, p.ord1());
        assertEquals("a", p.ord2());
        assertEquals("(1, a)", p.toString());
    }

    @Test
    void triplet() {
        Triplet<Integer, String, Boolean> t = Triplet.of(1, "a", true);
        assertEquals(1, t.ord1());
        assertEquals("a", t.ord2());
        assertEquals(true, t.ord3());
        assertEquals("(1, a, true)", t.toString());
    }

    @Test
    void quartet() {
        Quartet<Integer, String, Boolean, Double> q = Quartet.of(1, "a", true, 1.5);
        assertEquals(1, q.ord1());
        assertEquals("a", q.ord2());
        assertEquals(true, q.ord3());
        assertEquals(1.5, q.ord4());
        assertEquals("(1, a, true, 1.5)", q.toString());
    }

    @Test
    void nullElements() {
        Quartet<String, String, String, String> q = Quartet.of(null, "b", null, "d");
        assertNull(q.ord1());
        assertEquals("(null, b, null, d)", q.toString());
        assertEquals(Pair.of(null, null), Pair.of(null, null));
    }

    @Test
    void equalsAndHashCode() {
        assertEquals(Pair.of(1, "a"), Pair.of(1, "a"));
        assertEquals(Pair.of(1, "a").hashCode(), Pair.of(1, "a").hashCode());
        assertNotEquals(Pair.of(1, "a"), Pair.of(1, "b"));
        assertNotEquals(Pair.of(1, "a"), Pair.of("a", 1));
        assertEquals(Triplet.of(1, 2, 3), Triplet.of(1, 2, 3));
        assertEquals(Triplet.of(1, 2, 3).hashCode(), Triplet.of(1, 2, 3).hashCode());
        assertNotEquals(Triplet.of(1, 2, 3), Triplet.of(1, 2, 4));
        assertEquals(Quartet.of(1, 2, 3, 4), Quartet.of(1, 2, 3, 4));
        assertEquals(Quartet.of(1, 2, 3, 4).hashCode(), Quartet.of(1, 2, 3, 4).hashCode());
        assertNotEquals(Quartet.of(1, 2, 3, 4), Quartet.of(1, 2, 3, 5));
        assertNotEquals(Pair.of(1, 2), Triplet.of(1, 2, null));
    }

    @Test
    void json() throws Exception {
        assertEquals("{\"ord1\":1,\"ord2\":\"a\"}", MAPPER.writeValueAsString(Pair.of(1, "a")));
        assertEquals("{\"ord1\":1,\"ord2\":\"a\",\"ord3\":true}", MAPPER.writeValueAsString(Triplet.of(1, "a", true)));
        assertEquals(
                "{\"ord1\":1,\"ord2\":\"a\",\"ord3\":true,\"ord4\":null}",
                MAPPER.writeValueAsString(Quartet.of(1, "a", true, null)));
    }

    @Test
    void jsonDeserialize() throws Exception {
        Pair<Integer, String> p = MAPPER.readValue("{\"ord1\":1,\"ord2\":\"a\"}", new TypeReference<>() {});
        assertEquals(Pair.of(1, "a"), p);

        Triplet<Integer, String, Boolean> t =
                MAPPER.readValue("{\"ord1\":1,\"ord2\":\"a\",\"ord3\":true}", new TypeReference<>() {});
        assertEquals(Triplet.of(1, "a", true), t);

        Quartet<Integer, String, Boolean, Double> q = MAPPER.readValue(
                "{\"ord1\":1,\"ord2\":\"a\",\"ord3\":true,\"ord4\":1.5}", new TypeReference<>() {});
        assertEquals(Quartet.of(1, "a", true, 1.5), q);
    }

    @Test
    void jsonRoundTrip() throws Exception {
        Quartet<Integer, String, Boolean, String> q = Quartet.of(1, "a", null, null);
        Quartet<Integer, String, Boolean, String> back =
                MAPPER.readValue(MAPPER.writeValueAsString(q), new TypeReference<>() {});
        assertEquals(q, back);
        assertEquals(Pair.of(null, null), MAPPER.readValue("{}", new TypeReference<Pair<String, String>>() {}));
    }
}
