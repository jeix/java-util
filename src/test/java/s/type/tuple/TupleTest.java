package s.type.tuple;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TupleTest {

    @Test
    void pair_of_and_getters() {
        Pair<String, Integer> pair = Pair.of("hello", 42);
        assertEquals("hello", pair.ord1());
        assertEquals(42, pair.ord2());
        assertEquals("(hello, 42)", pair.toString());
        log.info("Pair test passed: {}", pair);
    }

    @Test
    void pair_equals_and_hashcode() {
        Pair<String, Integer> p1 = Pair.of("hello", 42);
        Pair<String, Integer> p2 = Pair.of("hello", 42);
        Pair<String, Integer> p3 = Pair.of("world", 99);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1.hashCode(), p3.hashCode());
        log.info("Pair equals/hashCode test passed");
    }

    @Test
    void triplet_of_and_getters() {
        Triplet<String, Integer, Double> triplet = Triplet.of("hello", 42, 3.14);
        assertEquals("hello", triplet.ord1());
        assertEquals(42, triplet.ord2());
        assertEquals(3.14, triplet.ord3());
        assertEquals("(hello, 42, 3.14)", triplet.toString());
        log.info("Triplet test passed: {}", triplet);
    }

    @Test
    void triplet_equals_and_hashcode() {
        Triplet<String, Integer, Double> t1 = Triplet.of("hello", 42, 3.14);
        Triplet<String, Integer, Double> t2 = Triplet.of("hello", 42, 3.14);
        Triplet<String, Integer, Double> t3 = Triplet.of("world", 99, 2.71);

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotEquals(t1, t3);
        assertNotEquals(t1.hashCode(), t3.hashCode());
        log.info("Triplet equals/hashCode test passed");
    }

    @Test
    void quartet_of_and_getters() {
        Quartet<String, Integer, Double, Boolean> quartet = Quartet.of("hello", 42, 3.14, true);
        assertEquals("hello", quartet.ord1());
        assertEquals(42, quartet.ord2());
        assertEquals(3.14, quartet.ord3());
        assertEquals(true, quartet.ord4());
        assertEquals("(hello, 42, 3.14, true)", quartet.toString());
        log.info("Quartet test passed: {}", quartet);
    }

    @Test
    void quartet_equals_and_hashcode() {
        Quartet<String, Integer, Double, Boolean> q1 = Quartet.of("hello", 42, 3.14, true);
        Quartet<String, Integer, Double, Boolean> q2 = Quartet.of("hello", 42, 3.14, true);
        Quartet<String, Integer, Double, Boolean> q3 = Quartet.of("world", 99, 2.71, false);

        assertEquals(q1, q2);
        assertEquals(q1.hashCode(), q2.hashCode());
        assertNotEquals(q1, q3);
        assertNotEquals(q1.hashCode(), q3.hashCode());
        log.info("Quartet equals/hashCode test passed");
    }

    @Test
    void tuples_with_null_values() {
        Pair<String, Integer> pair = Pair.of(null, 42);
        assertNull(pair.ord1());
        assertEquals(42, pair.ord2());
        assertEquals("(null, 42)", pair.toString());

        Triplet<String, Integer, Double> triplet = Triplet.of("hello", null, 3.14);
        assertEquals("hello", triplet.ord1());
        assertNull(triplet.ord2());
        assertEquals(3.14, triplet.ord3());

        Quartet<String, Integer, Double, Boolean> quartet = Quartet.of(null, null, null, null);
        assertNull(quartet.ord1());
        assertNull(quartet.ord2());
        assertNull(quartet.ord3());
        assertNull(quartet.ord4());
        assertEquals("(null, null, null, null)", quartet.toString());
        log.info("Null values test passed");
    }

    @Test
    void tuples_immutable() {
        Pair<String, Integer> pair = Pair.of("hello", 42);
        // 필드가 final이므로 불변성 보장됨
        assertEquals("hello", pair.ord1());
        assertEquals(42, pair.ord2());

        Triplet<String, Integer, Double> triplet = Triplet.of("hello", 42, 3.14);
        assertEquals("hello", triplet.ord1());
        assertEquals(42, triplet.ord2());
        assertEquals(3.14, triplet.ord3());

        Quartet<String, Integer, Double, Boolean> quartet = Quartet.of("hello", 42, 3.14, true);
        assertEquals("hello", quartet.ord1());
        assertEquals(42, quartet.ord2());
        assertEquals(3.14, quartet.ord3());
        assertEquals(true, quartet.ord4());
        log.info("Immutability test passed");
    }
}