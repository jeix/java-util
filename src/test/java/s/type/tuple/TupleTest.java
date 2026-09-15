package s.type.tuple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class TupleTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void pairOfReturnsFields() {
        Pair<Integer, String> pair = Pair.of(1, "a");

        assertEquals(1, pair.ord1());
        assertEquals("a", pair.ord2());
    }

    @Test
    void pairEqualsAndHashCode() {
        Pair<Integer, String> pair = Pair.of(1, "a");

        assertEquals(Pair.of(1, "a"), pair);
        assertEquals(Pair.of(1, "a").hashCode(), pair.hashCode());
    }

    @Test
    void pairNotEqualsWhenValueDiffers() {
        Pair<Integer, String> pair = Pair.of(1, "a");

        assertNotEquals(Pair.of(2, "a"), pair);
        assertNotEquals(Pair.of(1, "b"), pair);
    }

    @Test
    void pairToStringUsesParentheses() {
        assertEquals("(1, a)", Pair.of(1, "a").toString());
    }

    @Test
    void pairHandlesNullFields() {
        Pair<String, String> pair = Pair.of(null, null);

        assertNull(pair.ord1());
        assertNull(pair.ord2());
        assertEquals("(null, null)", pair.toString());
        assertEquals(Pair.of(null, null), pair);
    }

    @Test
    void tripletOfReturnsFields() {
        Triplet<Integer, String, Long> triplet = Triplet.of(1, "a", 2L);

        assertEquals(1, triplet.ord1());
        assertEquals("a", triplet.ord2());
        assertEquals(2L, triplet.ord3());
    }

    @Test
    void tripletEqualsAndToString() {
        Triplet<Integer, String, Long> triplet = Triplet.of(1, "a", 2L);

        assertEquals(Triplet.of(1, "a", 2L), triplet);
        assertEquals(Triplet.of(1, "a", 2L).hashCode(), triplet.hashCode());
        assertEquals("(1, a, 2)", triplet.toString());
    }

    @Test
    void quartetOfReturnsFields() {
        Quartet<Integer, String, Long, Boolean> quartet = Quartet.of(1, "a", 2L, true);

        assertEquals(1, quartet.ord1());
        assertEquals("a", quartet.ord2());
        assertEquals(2L, quartet.ord3());
        assertEquals(true, quartet.ord4());
    }

    @Test
    void quartetEqualsAndHashCode() {
        Quartet<Integer, String, Long, Boolean> quartet = Quartet.of(1, "a", 2L, true);

        assertEquals(Quartet.of(1, "a", 2L, true), quartet);
        assertEquals(Quartet.of(1, "a", 2L, true).hashCode(), quartet.hashCode());
    }

    @Test
    void quartetNotEqualsWhenValueDiffers() {
        Quartet<Integer, String, Long, Boolean> quartet = Quartet.of(1, "a", 2L, true);

        assertNotEquals(Quartet.of(1, "a", 2L, false), quartet);
    }

    @Test
    void quartetToStringUsesParentheses() {
        assertEquals("(1, a, 2, true)", Quartet.of(1, "a", 2L, true).toString());
    }

    @Test
    void tuplesOfDifferentArityAreNotEqual() {
        assertNotEquals(Pair.of(1, "a"), Triplet.of(1, "a", 2L));
        assertNotEquals(Triplet.of(1, "a", 2L), Quartet.of(1, "a", 2L, true));
    }

    @Test
    void pairSerializesWithOrdNames() throws Exception {
        JsonNode node = mapper.readTree(mapper.writeValueAsString(Pair.of(1, "a")));

        assertEquals(1, node.get("ord1").asInt());
        assertEquals("a", node.get("ord2").asText());
    }

    @Test
    void quartetSerializesWithOrdNames() throws Exception {
        JsonNode node = mapper.readTree(mapper.writeValueAsString(Quartet.of(1, "a", 2L, true)));

        assertEquals(1, node.get("ord1").asInt());
        assertEquals("a", node.get("ord2").asText());
        assertEquals(2L, node.get("ord3").asLong());
        assertEquals(true, node.get("ord4").asBoolean());
    }
}
