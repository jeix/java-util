package s.type.tuple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TupleTest {

    @Test
    void pair_of_createsPair() {
        Pair<String, Integer> pair = Pair.of("hello", 42);
        assertEquals("hello", pair.ord1());
        assertEquals(42, pair.ord2());
    }

    @Test
    void pair_toString_returnsFormattedString() {
        Pair<String, Integer> pair = Pair.of("hello", 42);
        assertEquals("(hello, 42)", pair.toString());
    }

    @Test
    void pair_equalsAndHashCode() {
        Pair<String, Integer> p1 = Pair.of("hello", 42);
        Pair<String, Integer> p2 = Pair.of("hello", 42);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void pair_notEqual_differentValues() {
        Pair<String, Integer> p1 = Pair.of("hello", 42);
        Pair<String, Integer> p2 = Pair.of("world", 42);
        assertNotEquals(p1, p2);
    }

    @Test
    void triplet_of_createsTriplet() {
        Triplet<String, Integer, Double> triplet = Triplet.of("hello", 42, 3.14);
        assertEquals("hello", triplet.ord1());
        assertEquals(42, triplet.ord2());
        assertEquals(3.14, triplet.ord3());
    }

    @Test
    void triplet_toString_returnsFormattedString() {
        Triplet<String, Integer, Double> triplet = Triplet.of("hello", 42, 3.14);
        assertEquals("(hello, 42, 3.14)", triplet.toString());
    }

    @Test
    void triplet_equalsAndHashCode() {
        Triplet<String, Integer, Double> t1 = Triplet.of("hello", 42, 3.14);
        Triplet<String, Integer, Double> t2 = Triplet.of("hello", 42, 3.14);
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    void triplet_notEqual_differentValues() {
        Triplet<String, Integer, Double> t1 = Triplet.of("hello", 42, 3.14);
        Triplet<String, Integer, Double> t2 = Triplet.of("hello", 42, 2.71);
        assertNotEquals(t1, t2);
    }

    @Test
    void quartet_of_createsQuartet() {
        Quartet<String, Integer, Double, Boolean> quartet = Quartet.of("hello", 42, 3.14, true);
        assertEquals("hello", quartet.ord1());
        assertEquals(42, quartet.ord2());
        assertEquals(3.14, quartet.ord3());
        assertEquals(true, quartet.ord4());
    }

    @Test
    void quartet_toString_returnsFormattedString() {
        Quartet<String, Integer, Double, Boolean> quartet = Quartet.of("hello", 42, 3.14, true);
        assertEquals("(hello, 42, 3.14, true)", quartet.toString());
    }

    @Test
    void quartet_equalsAndHashCode() {
        Quartet<String, Integer, Double, Boolean> q1 = Quartet.of("hello", 42, 3.14, true);
        Quartet<String, Integer, Double, Boolean> q2 = Quartet.of("hello", 42, 3.14, true);
        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
    }

    @Test
    void quartet_notEqual_differentValues() {
        Quartet<String, Integer, Double, Boolean> q1 = Quartet.of("hello", 42, 3.14, true);
        Quartet<String, Integer, Double, Boolean> q2 = Quartet.of("hello", 42, 3.14, false);
        assertNotEquals(q1, q2);
    }

    @Test
    void quartet_inheritsPairAndTripletMethods() {
        Quartet<String, Integer, Double, Boolean> quartet = Quartet.of("hello", 42, 3.14, true);
        assertEquals("hello", quartet.ord1());
        assertEquals(42, quartet.ord2());
        assertEquals(3.14, quartet.ord3());
        assertEquals(true, quartet.ord4());
    }

    @Test
    void pair_nullValues() {
        Pair<String, Integer> pair = Pair.of(null, null);
        assertNull(pair.ord1());
        assertNull(pair.ord2());
        assertEquals("(null, null)", pair.toString());
    }

    @Test
    void triplet_nullValues() {
        Triplet<String, Integer, Double> triplet = Triplet.of(null, null, null);
        assertNull(triplet.ord1());
        assertNull(triplet.ord2());
        assertNull(triplet.ord3());
        assertEquals("(null, null, null)", triplet.toString());
    }

    @Test
    void quartet_nullValues() {
        Quartet<String, Integer, Double, Boolean> quartet = Quartet.of(null, null, null, null);
        assertNull(quartet.ord1());
        assertNull(quartet.ord2());
        assertNull(quartet.ord3());
        assertNull(quartet.ord4());
        assertEquals("(null, null, null, null)", quartet.toString());
    }
}
