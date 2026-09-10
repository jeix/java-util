package s.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import s.type.tuple.Pair;

@Slf4j
class CollectionUtilTest {

    @Test
    void checksEmptyListsAndReturnsImmutableCopies() {
        assertTrue(CollectionUtil.isEmpty((List<?>) null));
        assertTrue(CollectionUtil.isEmpty(List.of()));
        assertFalse(CollectionUtil.isEmpty(Arrays.asList((String) null)));
        List<String> list = new ArrayList<>(List.of("a"));
        List<String> result = CollectionUtil.emptyIfNull(list);
        assertNotSame(list, result);
        assertEquals(list, result);
        _assertImmutable(result);
        list.add("b");
        assertEquals(List.of("a"), result);
        List<String> empty = CollectionUtil.emptyIfNull((List<String>) null);
        assertTrue(empty.isEmpty());
        _assertImmutable(empty);
        assertTrue(CollectionUtil.emptyIfNull((List<String>) null).isEmpty());
    }

    @Test
    void zipsToPairsUsingShorterLength() {
        List<Pair<String, Integer>> pairs = CollectionUtil.zip(
                new LinkedList<>(List.of("a", "b", "c")), List.of(1, 2));
        assertEquals(List.of(Pair.of("a", 1), Pair.of("b", 2)), pairs);
        _assertImmutable(pairs);
        assertEquals("a", pairs.getFirst().ord1());
        assertEquals(1, pairs.getFirst().ord2());
        assertEquals(List.of(Pair.of("a", 1)), CollectionUtil.zip(List.of("a"), List.of(1, 2)));
        assertTrue(CollectionUtil.zip(null, List.of(1)).isEmpty());
        assertTrue(CollectionUtil.zip(List.of(1), null).isEmpty());
        assertTrue(CollectionUtil.zip(List.of(), List.of(1)).isEmpty());
        assertEquals(List.of(Pair.of(null, null)), CollectionUtil.zip(
                Arrays.asList((String) null), Arrays.asList((Integer) null)));
    }

    @Test
    void zipCallsMixerOncePerPairInOrder() {
        AtomicInteger calls = new AtomicInteger();
        assertEquals(List.of("a1", "b2"), CollectionUtil.zip(List.of("a", "b"), List.of(1, 2, 3),
                (a, b) -> {
                    assertEquals(calls.incrementAndGet(), b);
                    return a + b;
                }));
        assertEquals(2, calls.get());
        assertEquals(Arrays.asList((String) null), CollectionUtil.zip(List.of(1), List.of(2),
                (a, b) -> null));
        assertTrue(CollectionUtil.zip(List.of(1), List.of(2), null).isEmpty());
        assertTrue(CollectionUtil.zip(List.of(), List.of(2), (a, b) -> {
            throw new AssertionError("must not call mixer");
        }).isEmpty());
    }

    @Test
    void infersArrayTypeFromFirstNonNullElement() {
        String[] result = CollectionUtil.toArray(Arrays.asList(null, "a", "b"));
        assertArrayEquals(new String[] {null, "a", "b"}, result);
        assertEquals(String[].class, result.getClass());
        assertArrayEquals(new Integer[] {1, 2}, CollectionUtil.toArray(List.of(1, 2)));
        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.toArray(null));
        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.toArray(List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> CollectionUtil.toArray(Arrays.asList(null, null)));
        assertThrows(ArrayStoreException.class, () -> CollectionUtil.toArray(List.<Number>of(1, 2.5)));
    }

    @Test
    void createsExplicitlyTypedArraysIncludingEmptyAndMixedSubtypes() {
        assertArrayEquals(new String[0], CollectionUtil.toArray(null, String.class));
        assertArrayEquals(new String[0], CollectionUtil.toArray(List.of(), String.class));
        assertArrayEquals(new String[] {null, null},
                CollectionUtil.toArray(Arrays.asList(null, null), String.class));
        Number[] result = CollectionUtil.toArray(List.<Number>of(1, 2.5), Number.class);
        assertEquals(Number[].class, result.getClass());
        assertArrayEquals(new Number[] {1, 2.5}, result);
        assertThrows(NullPointerException.class, () -> CollectionUtil.toArray(List.of(), null));
        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.toArray(List.of(1), int.class));
    }

    @Test
    void findsFirstMatchWithoutEvaluatingLaterItems() {
        AtomicInteger calls = new AtomicInteger();
        assertEquals(2, CollectionUtil.findOne(List.of(1, 2, 3), value -> {
            calls.incrementAndGet();
            return value % 2 == 0;
        }));
        assertEquals(2, calls.get());
        assertNull(CollectionUtil.findOne(List.of(1, 3), value -> value % 2 == 0));
        assertNull(CollectionUtil.findOne(null, Objects::nonNull));
        assertNull(CollectionUtil.findOne(List.of(1), null));
        calls.set(0);
        assertNull(CollectionUtil.findOne(Arrays.asList(null, "a"), value -> {
            calls.incrementAndGet();
            return true;
        }));
        assertEquals(1, calls.get());
    }

    @Test
    void findsAllMatchesPreservingOrderAndDuplicates() {
        List<Integer> result = CollectionUtil.findAll(
                List.of(1, 2, 2, 3, 4), n -> n % 2 == 0);
        assertEquals(List.of(2, 2, 4), result);
        _assertImmutable(result);
        assertEquals(Arrays.asList(null, null), CollectionUtil.findAll(
                Arrays.asList(null, "a", null), Objects::isNull));
        assertTrue(CollectionUtil.findAll(null, Objects::nonNull).isEmpty());
        assertTrue(CollectionUtil.findAll(List.of(1), null).isEmpty());
        assertTrue(CollectionUtil.findAll(List.of(1), n -> false).isEmpty());
    }

    @Test
    void setOperationsFollowListAlgebraWithoutDeduplicating() {
        List<Integer> first = List.of(1, 1, 2, 3);
        List<Integer> second = List.of(2, 4, 4);
        assertEquals(List.of(1, 1, 2, 3, 4, 4), CollectionUtil.unionOf(first, second));
        assertEquals(List.of(2), CollectionUtil.intersectionOf(first, second));
        assertEquals(List.of(1, 1, 3), CollectionUtil.differenceOf(first, second));
        assertEquals(List.of(1, 1, 3, 4, 4), CollectionUtil.symmetricDifferenceOf(first, second));
        assertEquals(List.of(1, 1, 2, 3), first);
        assertEquals(List.of(2, 4, 4), second);
        assertEquals(List.of(1, 1, 2, 3), CollectionUtil.intersectionOf(first, first));
        assertTrue(CollectionUtil.differenceOf(first, first).isEmpty());
        assertTrue(CollectionUtil.symmetricDifferenceOf(first, first).isEmpty());
    }

    @Test
    void setOperationsHandleNullListsAndElements() {
        List<String> list = Arrays.asList(null, "a", "a");
        assertEquals(list, CollectionUtil.unionOf(null, list));
        assertEquals(list, CollectionUtil.unionOf(list, null));
        assertEquals(list, CollectionUtil.differenceOf(list, null));
        assertEquals(list, CollectionUtil.symmetricDifferenceOf(null, list));
        assertTrue(CollectionUtil.intersectionOf(list, null).isEmpty());
        assertTrue(CollectionUtil.differenceOf(null, list).isEmpty());
        assertTrue(CollectionUtil.unionOf(null, null).isEmpty());
        assertTrue(CollectionUtil.symmetricDifferenceOf(null, null).isEmpty());
        assertEquals(Arrays.asList((String) null), CollectionUtil.intersectionOf(list, Arrays.asList((String) null)));
        assertEquals(List.of("a", "a"), CollectionUtil.differenceOf(list, Arrays.asList((String) null)));
    }

    @Test
    void slicesWithNormalizedIndexesAsImmutableCopies() {
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        assertEquals(List.of("b", "c"), CollectionUtil.slice(list, 1));
        List<String> result = CollectionUtil.slice(list, 1, 2);
        assertEquals(List.of("b"), result);
        _assertImmutable(result);
        list.set(1, "B");
        assertEquals(List.of("b"), result);
        assertTrue(CollectionUtil.slice(null, 0).isEmpty());
        assertEquals(List.of("c"), CollectionUtil.slice(list, -1));
        assertEquals(List.of("B"), CollectionUtil.slice(list, -2, -1));
        assertEquals(List.of("a", "B", "c"), CollectionUtil.slice(list, -99, 99));
        assertTrue(CollectionUtil.slice(list, 2, 1).isEmpty());
        assertEquals(list, CollectionUtil.slice(list, 0, 4));
        assertTrue(CollectionUtil.slice(list, Integer.MAX_VALUE).isEmpty());
        assertEquals(list, CollectionUtil.slice(list, Integer.MIN_VALUE, Integer.MAX_VALUE));
        assertTrue(CollectionUtil.slice(list, 3).isEmpty());
        assertTrue(CollectionUtil.slice(list, 1, 1).isEmpty());
    }

    @Test
    void headAndTailInterpretNegativeSizes() {
        List<Integer> list = List.of(1, 2, 3);
        assertEquals(List.of(1, 2), CollectionUtil.head(list, 2));
        assertEquals(List.of(2, 3), CollectionUtil.tail(list, 2));
        assertTrue(CollectionUtil.head(null, 1).isEmpty());
        assertTrue(CollectionUtil.tail(null, 1).isEmpty());
        assertTrue(CollectionUtil.head(list, 0).isEmpty());
        assertEquals(List.of(1, 2), CollectionUtil.head(list, -1));
        assertEquals(List.of(1), CollectionUtil.head(list, -2));
        assertTrue(CollectionUtil.head(list, -3).isEmpty());
        assertTrue(CollectionUtil.head(list, -4).isEmpty());
        assertTrue(CollectionUtil.head(list, Integer.MIN_VALUE).isEmpty());
        assertTrue(CollectionUtil.tail(list, 0).isEmpty());
        assertEquals(List.of(2, 3), CollectionUtil.tail(list, -1));
        assertEquals(List.of(3), CollectionUtil.tail(list, -2));
        assertTrue(CollectionUtil.tail(list, -3).isEmpty());
        assertTrue(CollectionUtil.tail(list, -4).isEmpty());
        assertTrue(CollectionUtil.tail(list, Integer.MIN_VALUE).isEmpty());
        for (int size : new int[] {3, 4, Integer.MAX_VALUE}) {
            assertEquals(list, CollectionUtil.head(list, size));
            assertEquals(list, CollectionUtil.tail(list, size));
        }
    }

    @Test
    void indexesByKeyWithLastValueWinningAndStableKeyOrder() {
        Map<String, String> result = CollectionUtil.indexing(List.of("apple", "banana", "apricot"),
                value -> value.substring(0, 1));
        assertEquals(Map.of("a", "apricot", "b", "banana"), result);
        _assertImmutable(result);
        assertEquals(List.of("a", "b"), new ArrayList<>(result.keySet()));
        assertTrue(CollectionUtil.indexing(null, Object::toString).isEmpty());
        assertTrue(CollectionUtil.indexing(List.of("a"), null).isEmpty());
        Map<String, String> nulls = CollectionUtil.indexing(Arrays.asList("a", null), value -> null);
        assertTrue(nulls.containsKey(null));
        assertNull(nulls.get(null));
    }

    @Test
    void groupsInEncounterOrderIncludingNullKeysAndValues() {
        Map<String, List<String>> result = CollectionUtil.grouping(
                Arrays.asList("apple", "banana", "apricot", null, "apple"),
                value -> value == null ? null : value.substring(0, 1));
        assertEquals(Arrays.asList("a", "b", null), new ArrayList<>(result.keySet()));
        assertEquals(List.of("apple", "apricot", "apple"), result.get("a"));
        assertEquals(List.of("banana"), result.get("b"));
        assertEquals(Arrays.asList((String) null), result.get(null));
        _assertImmutable(result);
        result.values().forEach(CollectionUtilTest::_assertImmutable);
        assertTrue(CollectionUtil.grouping(null, Object::toString).isEmpty());
        assertTrue(CollectionUtil.grouping(List.of("a"), null).isEmpty());
    }

    @Test
    void checksEmptyMapsAndReturnsImmutableCopies() {
        assertTrue(CollectionUtil.isEmpty((Map<?, ?>) null));
        assertTrue(CollectionUtil.isEmpty(Map.of()));
        Map<String, Integer> map = new LinkedHashMap<>(Map.of("a", 1));
        assertFalse(CollectionUtil.isEmpty(map));
        Map<String, Integer> result = CollectionUtil.emptyIfNull(map);
        assertNotSame(map, result);
        assertEquals(map, result);
        _assertImmutable(result);
        map.put("b", 2);
        assertEquals(Map.of("a", 1), result);
        Map<String, Integer> empty = CollectionUtil.emptyIfNull((Map<String, Integer>) null);
        assertTrue(empty.isEmpty());
        _assertImmutable(empty);
        assertTrue(CollectionUtil.emptyIfNull((Map<String, Integer>) null).isEmpty());
    }

    @Test
    void createsUntypedMapsFromAlternatingKeysAndValues() {
        Map<Object, Object> result = CollectionUtil.asMap("foo", 42, "bar", 43, "foo", 44, null, null);
        assertEquals(Arrays.asList("foo", "bar", null), new ArrayList<>(result.keySet()));
        assertEquals(44, result.get("foo"));
        assertEquals(43, result.get("bar"));
        assertTrue(result.containsKey(null));
        assertNull(result.get(null));
        _assertImmutable(result);
        assertTrue(CollectionUtil.asMap().isEmpty());
        assertTrue(CollectionUtil.asMap((Object[]) null).isEmpty());
        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap("dangling"));
    }

    @Test
    void castsMapAsImmutableCopyWithoutConvertingValues() {
        Map<Object, Object> origin = new LinkedHashMap<>();
        origin.put("foo", 42);
        origin.put("bar", 43);
        Map<String, Integer> result = CollectionUtil.castKeyValue(origin);
        assertNotSame(origin, result);
        assertEquals(42, result.get("foo"));
        _assertImmutable(result);
        origin.put("baz", 44);
        assertFalse(result.containsKey("baz"));
        assertTrue(CollectionUtil.castKeyValue(null).isEmpty());
        Map<String, Integer> invalid = CollectionUtil.castKeyValue(CollectionUtil.asMap("a", "wrong"));
        assertThrows(ClassCastException.class, () -> {
            Integer value = invalid.get("a");
            log.info("unexpected value: {}", value);
        });
    }

    @Test
    void typedMapsValidateEveryKeyAndValue() {
        assertEquals(Map.of("foo", 42, "bar", 43),
                CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43));
        assertThrows(ClassCastException.class,
                () -> CollectionUtil.asMap(String.class, Integer.class, 1, 42));
        assertThrows(ClassCastException.class,
                () -> CollectionUtil.asMap(String.class, Integer.class, "foo", "42"));
        assertThrows(IllegalArgumentException.class,
                () -> CollectionUtil.asMap(String.class, Integer.class, "foo"));
        assertThrows(NullPointerException.class,
                () -> CollectionUtil.asMap(null, Integer.class, "foo", 42));
        assertThrows(NullPointerException.class,
                () -> CollectionUtil.asMap(String.class, null, "foo", 42));
        assertTrue(CollectionUtil.asMap(String.class, Integer.class, (Object[]) null).isEmpty());
        assertTrue(CollectionUtil.asMap(String.class, Integer.class).isEmpty());
        Map<String, Integer> nulls = CollectionUtil.asMap(String.class, Integer.class, null, null);
        assertTrue(nulls.containsKey(null));
        assertNull(nulls.get(null));
    }

    @Test
    void createsMapsFromEntries() {
        List<Map.Entry<String, Integer>> entries = Arrays.asList(
                Map.entry("foo", 42), null, Map.entry("bar", 43), Map.entry("foo", 44),
                new AbstractMap.SimpleEntry<>(null, null));
        Map<String, Integer> result = CollectionUtil.asMap(entries);
        assertEquals(44, result.get("foo"));
        assertEquals(43, result.get("bar"));
        assertEquals(Arrays.asList("foo", "bar", null), new ArrayList<>(result.keySet()));
        assertNull(result.get(null));
        _assertImmutable(result);
        assertTrue(CollectionUtil.asMap((List<Map.Entry<String, Integer>>) null).isEmpty());
        assertTrue(CollectionUtil.asMap(List.<Map.Entry<String, Integer>>of()).isEmpty());
    }

    @Test
    void loopAndStreamAlternativesProduceTheSameResults() {
        List<Map.Entry<String, Integer>> entries = List.of(
                Map.entry("a", 1), Map.entry("b", 2));
        IntStream.range(0, 100).forEach(ignored -> {
            assertEquals(List.of("a1", "b2"), CollectionUtil.zip(
                    List.of("a", "b"), List.of(1, 2), (left, right) -> left + right));
            assertEquals(2, CollectionUtil.findOne(List.of(1, 2, 3), value -> value % 2 == 0));
            assertEquals(List.of(2, 2), CollectionUtil.findAll(
                    List.of(1, 2, 2, 3), value -> value % 2 == 0));
            assertEquals(Map.of("a", "apricot", "b", "banana"), CollectionUtil.indexing(
                    List.of("apple", "banana", "apricot"), value -> value.substring(0, 1)));
            assertEquals(Map.of("a", List.of("apple", "apricot"), "b", List.of("banana")),
                    CollectionUtil.grouping(
                            List.of("apple", "banana", "apricot"),
                            value -> value.substring(0, 1)));
            assertEquals(Map.of("a", 1, "b", 2), CollectionUtil.asMap(
                    String.class, Integer.class, "a", 1, "b", 2));
            assertEquals(Map.of("a", 1, "b", 2), CollectionUtil.asMap(entries));
        });
    }

    @Test
    void allCollectionResultsAreImmutable() {
        List<List<?>> lists = List.of(
                CollectionUtil.zip(List.of("a"), List.of(1)),
                CollectionUtil.findAll(List.of(1, 2), value -> true),
                CollectionUtil.unionOf(List.of(1), List.of(2)),
                CollectionUtil.intersectionOf(List.of(1), List.of(1)),
                CollectionUtil.differenceOf(List.of(1), List.of(2)),
                CollectionUtil.symmetricDifferenceOf(List.of(1), List.of(2)),
                CollectionUtil.slice(List.of(1, 2), 0, 1),
                CollectionUtil.head(List.of(1, 2), 1),
                CollectionUtil.tail(List.of(1, 2), 1));
        lists.forEach(CollectionUtilTest::_assertImmutable);

        List<Map<?, ?>> maps = List.of(
                CollectionUtil.indexing(List.of("a"), value -> value),
                CollectionUtil.grouping(List.of("a"), value -> value),
                CollectionUtil.asMap("a", 1),
                CollectionUtil.asMap(String.class, Integer.class, "a", 1),
                CollectionUtil.asMap(List.of(Map.entry("a", 1))),
                CollectionUtil.castKeyValue(Map.of("a", 1)),
                CollectionUtil.copyOf(Map.of("a", 1)));
        maps.forEach(CollectionUtilTest::_assertImmutable);
    }

    @Test
    void copiesMapsShallowlyKeepingOrderAndNulls() {
        List<Integer> value = new ArrayList<>(List.of(1));
        Map<String, List<Integer>> origin = new LinkedHashMap<>();
        origin.put("b", value);
        origin.put("a", null);
        origin.put(null, value);
        Map<String, List<Integer>> copy = CollectionUtil.copyOf(origin);
        assertNotSame(origin, copy);
        assertEquals(origin, copy);
        assertEquals(Arrays.asList("b", "a", null), new ArrayList<>(copy.keySet()));
        assertSame(value, copy.get("b"));
        _assertImmutable(copy);
        assertTrue(origin.containsKey("a"));
        Map<String, Integer> empty = CollectionUtil.copyOf(null);
        _assertImmutable(empty);
        assertTrue(empty.isEmpty());
    }

    @Test
    void propagatesCallbackExceptions() {
        IllegalStateException failure = new IllegalStateException("callback failure");
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> CollectionUtil.zip(List.of(1), List.of(2), (a, b) -> { throw failure; })));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> CollectionUtil.findOne(List.of(1), value -> { throw failure; })));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> CollectionUtil.findAll(List.of(1), value -> { throw failure; })));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> CollectionUtil.indexing(List.of(1), value -> { throw failure; })));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> CollectionUtil.grouping(List.of(1), value -> { throw failure; })));
    }

    private static void _assertImmutable(List<?> list) {
        assertThrows(UnsupportedOperationException.class, () -> list.add(null));
    }

    private static void _assertImmutable(Map<?, ?> map) {
        assertThrows(UnsupportedOperationException.class, () -> map.put(null, null));
    }
}
