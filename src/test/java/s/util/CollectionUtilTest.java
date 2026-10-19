package s.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import s.type.tuple.Pair;

@Slf4j
class CollectionUtilTest {

    private static final List<String> NULL_LIST = null;
    private static final Map<String, Integer> NULL_MAP = null;
    private static final List<String> ABCDE = List.of("a", "b", "c", "d", "e");

    @Test
    void isEmptyList() {
        log.info("isEmpty(list)");
        assertTrue(CollectionUtil.isEmpty(NULL_LIST));
        assertTrue(CollectionUtil.isEmpty(List.of()));
        assertFalse(CollectionUtil.isEmpty(List.of("a")));
        assertFalse(CollectionUtil.isEmpty(Arrays.asList((String) null)));
    }

    @Test
    void emptyIfNullList() {
        assertEquals(List.of(), CollectionUtil.emptyIfNull(NULL_LIST));
        List<String> list = List.of("a");
        assertSame(list, CollectionUtil.emptyIfNull(list));
    }

    @Test
    void zip() {
        assertEquals(List.of(Pair.of(1, "a"), Pair.of(2, "b")), CollectionUtil.zip(List.of(1, 2), List.of("a", "b")));
        assertEquals(List.of(Pair.of(1, "a")), CollectionUtil.zip(List.of(1, 2), List.of("a")));
        assertEquals(List.of(Pair.of(1, "a")), CollectionUtil.zip(List.of(1), List.of("a", "b")));
        assertEquals(List.of(), CollectionUtil.zip(List.of(1), List.<String>of()));
        assertEquals(List.of(), CollectionUtil.zip(null, List.of("a")));
        assertEquals(List.of(Pair.of(null, "a")), CollectionUtil.zip(Arrays.asList((Integer) null), List.of("a")));
    }

    @Test
    void zipMixer() {
        assertEquals(List.of("1a", "2b"), CollectionUtil.zip(List.of(1, 2), List.of("a", "b"), (n, s) -> n + s));
        assertEquals(List.of("1a"), CollectionUtil.zip(List.of(1, 2), List.of("a"), (n, s) -> n + s));
        assertEquals(List.of(), CollectionUtil.zip(List.of(1), NULL_LIST, (n, s) -> n + s));
        assertEquals(List.of(), CollectionUtil.zip(List.of(1), List.of("a"), null));
    }

    @Test
    void toArray() {
        String[] strings = CollectionUtil.toArray(List.of("a", "b"));
        assertArrayEquals(new String[] {"a", "b"}, strings);
        Number[] numbers = CollectionUtil.toArray(List.<Number>of(1, 2.5));
        assertArrayEquals(new Number[] {1, 2.5}, numbers);
        assertEquals(Number.class, numbers.getClass().getComponentType());
        String[] withNull = CollectionUtil.toArray(Arrays.asList("a", null));
        assertArrayEquals(new String[] {"a", null}, withNull);
        assertEquals(0, CollectionUtil.<Object>toArray(null).length);
        assertEquals(Object[].class, CollectionUtil.toArray(List.of()).getClass());
        assertEquals(Object[].class, CollectionUtil.toArray(List.<Object>of(1, "a")).getClass());
    }

    @Test
    void findOne() {
        assertEquals("b", CollectionUtil.findOne(List.of("a", "b", "bb"), s -> s.startsWith("b")));
        assertNull(CollectionUtil.findOne(List.of("a"), s -> s.startsWith("b")));
        assertNull(CollectionUtil.findOne(NULL_LIST, s -> true));
        assertNull(CollectionUtil.findOne(List.of("a"), null));
        assertNull(CollectionUtil.findOne(Arrays.asList("a", null), s -> s == null));
    }

    @Test
    void findAll() {
        assertEquals(List.of("b", "bb"), CollectionUtil.findAll(List.of("a", "b", "bb"), s -> s.startsWith("b")));
        assertEquals(List.of(), CollectionUtil.findAll(List.of("a"), s -> s.startsWith("b")));
        assertEquals(List.of(), CollectionUtil.findAll(NULL_LIST, s -> true));
        assertEquals(List.of(), CollectionUtil.findAll(List.of("a"), null));
    }

    @Test
    void unionOf() {
        assertEquals(List.of(1, 2, 3, 4), CollectionUtil.unionOf(List.of(1, 2, 3), List.of(2, 3, 4)));
        assertEquals(List.of(1, 1, 2, 5, 5), CollectionUtil.unionOf(List.of(1, 1, 2), List.of(2, 5, 5)));
        assertEquals(List.of(2, 3), CollectionUtil.unionOf(null, List.of(2, 3)));
        assertEquals(List.of(1), CollectionUtil.unionOf(List.of(1), null));
        assertEquals(List.of(), CollectionUtil.unionOf(null, null));
    }

    @Test
    void intersectionOf() {
        assertEquals(List.of(2, 3), CollectionUtil.intersectionOf(List.of(1, 2, 3), List.of(2, 3, 4)));
        assertEquals(List.of(2, 2), CollectionUtil.intersectionOf(List.of(1, 2, 2), List.of(2)));
        assertEquals(List.of(), CollectionUtil.intersectionOf(List.of(1), List.of(2)));
        assertEquals(List.of(), CollectionUtil.intersectionOf(List.of(1), null));
        assertEquals(List.of(), CollectionUtil.intersectionOf(null, List.of(1)));
    }

    @Test
    void differenceOf() {
        assertEquals(List.of(1), CollectionUtil.differenceOf(List.of(1, 2, 3), List.of(2, 3, 4)));
        assertEquals(List.of(1, 1), CollectionUtil.differenceOf(List.of(1, 1, 2), List.of(2)));
        assertEquals(List.of(1, 2), CollectionUtil.differenceOf(List.of(1, 2), null));
        assertEquals(List.of(), CollectionUtil.differenceOf(null, List.of(1)));
        assertEquals(List.of("a"), CollectionUtil.differenceOf(Arrays.asList("a", null), Arrays.asList((String) null)));
    }

    @Test
    void symmetricDifferenceOf() {
        assertEquals(List.of(1, 4), CollectionUtil.symmetricDifferenceOf(List.of(1, 2, 3), List.of(2, 3, 4)));
        assertEquals(List.of(), CollectionUtil.symmetricDifferenceOf(List.of(1, 2), List.of(2, 1)));
        assertEquals(List.of(1), CollectionUtil.symmetricDifferenceOf(List.of(1), null));
        assertEquals(List.of(1), CollectionUtil.symmetricDifferenceOf(null, List.of(1)));
    }

    @Test
    void slice() {
        assertEquals(List.of("c", "d", "e"), CollectionUtil.slice(ABCDE, 2));
        assertEquals(ABCDE, CollectionUtil.slice(ABCDE, 0));
        assertEquals(List.of(), CollectionUtil.slice(ABCDE, 5));
        assertEquals(List.of(), CollectionUtil.slice(ABCDE, 10));
        assertEquals(List.of("d", "e"), CollectionUtil.slice(ABCDE, -2));
        assertEquals(ABCDE, CollectionUtil.slice(ABCDE, -10));
        assertEquals(List.of(), CollectionUtil.slice(NULL_LIST, 0));
    }

    @Test
    void sliceBeginEnd() {
        assertEquals(List.of("b", "c"), CollectionUtil.slice(ABCDE, 1, 3));
        assertEquals(ABCDE, CollectionUtil.slice(ABCDE, 0, 5));
        assertEquals(List.of("d", "e"), CollectionUtil.slice(ABCDE, 3, 100));
        assertEquals(List.of(), CollectionUtil.slice(ABCDE, 3, 3));
        assertEquals(List.of(), CollectionUtil.slice(ABCDE, 3, 1));
        assertEquals(List.of("c", "d"), CollectionUtil.slice(ABCDE, -3, -1));
        assertEquals(List.of("a", "b"), CollectionUtil.slice(ABCDE, -10, 2));
        assertEquals(List.of("b", "c", "d"), CollectionUtil.slice(ABCDE, 1, -1));
        assertEquals(List.of(), CollectionUtil.slice(ABCDE, 0, -10));
        assertEquals(List.of(), CollectionUtil.slice(NULL_LIST, 0, 3));
    }

    @Test
    void sliceReturnsCopy() {
        List<String> source = new ArrayList<>(ABCDE);
        List<String> sliced = CollectionUtil.slice(source, 1, 3);
        source.set(1, "x");
        assertEquals(List.of("b", "c"), sliced);
        assertThrows(UnsupportedOperationException.class, () -> sliced.add("z"));
    }

    @Test
    void head() {
        assertEquals(List.of("a", "b"), CollectionUtil.head(ABCDE, 2));
        assertEquals(List.of(), CollectionUtil.head(ABCDE, 0));
        assertEquals(ABCDE, CollectionUtil.head(ABCDE, 5));
        assertEquals(ABCDE, CollectionUtil.head(ABCDE, 6));
        assertEquals(List.of("a", "b", "c", "d"), CollectionUtil.head(ABCDE, -1));
        assertEquals(List.of(), CollectionUtil.head(ABCDE, -5));
        assertEquals(List.of(), CollectionUtil.head(ABCDE, -10));
        assertEquals(List.of(), CollectionUtil.head(NULL_LIST, 1));
    }

    @Test
    void tail() {
        assertEquals(List.of("d", "e"), CollectionUtil.tail(ABCDE, 2));
        assertEquals(List.of(), CollectionUtil.tail(ABCDE, 0));
        assertEquals(ABCDE, CollectionUtil.tail(ABCDE, 5));
        assertEquals(ABCDE, CollectionUtil.tail(ABCDE, 6));
        assertEquals(List.of("b", "c", "d", "e"), CollectionUtil.tail(ABCDE, -1));
        assertEquals(List.of(), CollectionUtil.tail(ABCDE, -5));
        assertEquals(List.of(), CollectionUtil.tail(ABCDE, -10));
        assertEquals(List.of(), CollectionUtil.tail(ABCDE, Integer.MIN_VALUE));
        assertEquals(List.of(), CollectionUtil.tail(NULL_LIST, 1));
    }

    @Test
    void indexing() {
        Map<String, String> map = CollectionUtil.indexing(List.of("apple", "banana", "avocado"), s -> s.substring(0, 2));
        assertEquals(List.of("ap", "ba", "av"), List.copyOf(map.keySet()));
        assertEquals("apple", map.get("ap"));
        assertEquals("banana", map.get("ba"));
        // 같은 색인이면 뒤의 것이 이김
        assertEquals("avocado", CollectionUtil.indexing(List.of("apple", "avocado"), s -> s.substring(0, 1)).get("a"));
        assertEquals(Map.of(), CollectionUtil.indexing(NULL_LIST, s -> s));
        assertEquals(Map.of(), CollectionUtil.indexing(List.of("a"), null));
        assertEquals("a", CollectionUtil.indexing(List.of("a"), s -> null).get(null));
    }

    @Test
    void grouping() {
        Map<String, List<String>> map =
                CollectionUtil.grouping(List.of("apple", "banana", "avocado", "blueberry", "cherry"), s -> s.substring(0, 1));
        assertEquals(List.of("a", "b", "c"), List.copyOf(map.keySet()));
        assertEquals(List.of("apple", "avocado"), map.get("a"));
        assertEquals(List.of("banana", "blueberry"), map.get("b"));
        assertEquals(List.of("cherry"), map.get("c"));
        assertThrows(UnsupportedOperationException.class, () -> map.get("a").add("x"));
        assertThrows(UnsupportedOperationException.class, () -> map.put("z", List.of()));
        assertEquals(Map.of(), CollectionUtil.grouping(NULL_LIST, s -> s));
        assertEquals(Map.of(), CollectionUtil.grouping(List.of("a"), null));
    }

    @Test
    void isEmptyMap() {
        assertTrue(CollectionUtil.isEmpty(NULL_MAP));
        assertTrue(CollectionUtil.isEmpty(Map.of()));
        assertFalse(CollectionUtil.isEmpty(Map.of("a", 1)));
    }

    @Test
    void emptyIfNullMap() {
        assertEquals(Map.of(), CollectionUtil.emptyIfNull(NULL_MAP));
        Map<String, Integer> map = Map.of("a", 1);
        assertSame(map, CollectionUtil.emptyIfNull(map));
    }

    @Test
    void asMap() {
        Map<Object, Object> map = CollectionUtil.asMap("foo", 42, "bar", 43);
        assertEquals(2, map.size());
        assertEquals(42, map.get("foo"));
        assertEquals(List.of("foo", "bar"), List.copyOf(map.keySet()));
        assertEquals(Map.of(), CollectionUtil.asMap());
        assertEquals(Map.of(), CollectionUtil.asMap("foo", 42, "bar"));
        assertEquals(44, CollectionUtil.asMap("foo", 42, "foo", 44).get("foo"));
        assertNull(CollectionUtil.asMap("foo", null).get("foo"));
        assertTrue(CollectionUtil.asMap("foo", null).containsKey("foo"));
        assertThrows(UnsupportedOperationException.class, () -> map.put("x", 1));
    }

    @Test
    void castKeyValue() {
        Map<Object, Object> map1 = CollectionUtil.asMap("foo", 42, "bar", 43);
        Map<String, Integer> map2 = CollectionUtil.castKeyValue(map1);
        assertEquals(42, map2.get("foo"));
        assertEquals(43, map2.get("bar"));
        assertSame(map1, map2);
        assertEquals(Map.of(), CollectionUtil.castKeyValue(null));
    }

    @Test
    void asMapWithClasses() {
        Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);
        assertEquals(Map.of("foo", 42, "bar", 43), map);
        assertEquals(List.of("foo", "bar"), List.copyOf(map.keySet()));
        assertEquals(Map.of(), CollectionUtil.asMap(String.class, Integer.class));
        assertEquals(Map.of(), CollectionUtil.asMap(String.class, Integer.class, "foo"));
        assertEquals(Map.of(), CollectionUtil.asMap(null, Integer.class, "foo", 42));
        assertThrows(ClassCastException.class, () -> CollectionUtil.asMap(String.class, Integer.class, "foo", "x"));
        assertThrows(ClassCastException.class, () -> CollectionUtil.asMap(String.class, Integer.class, 1, 2));
    }

    @Test
    void asMapWithEntries() {
        List<Map.Entry<String, Integer>> entries =
                List.of(Map.<String, Integer>entry("foo", 42), Map.<String, Integer>entry("bar", 43));
        Map<String, Integer> map = CollectionUtil.asMap(entries);
        assertEquals(Map.of("foo", 42, "bar", 43), map);
        assertEquals(List.of("foo", "bar"), List.copyOf(map.keySet()));
        assertEquals(Map.of(), CollectionUtil.asMap(List.<Map.Entry<String, Integer>>of()));
        assertEquals(Map.of(), CollectionUtil.asMap((List<Map.Entry<String, Integer>>) null));
        assertEquals(
                Map.of("foo", 1),
                CollectionUtil.asMap(Arrays.asList(Map.<String, Integer>entry("foo", 1), null)));
    }

    @Test
    void copyOf() {
        Map<String, Integer> origin = new java.util.LinkedHashMap<>();
        origin.put("b", 2);
        origin.put("a", 1);
        Map<String, Integer> copy = CollectionUtil.copyOf(origin);
        assertEquals(origin, copy);
        assertEquals(List.of("b", "a"), List.copyOf(copy.keySet()));
        origin.put("c", 3);
        assertEquals(2, copy.size());
        assertThrows(UnsupportedOperationException.class, () -> copy.put("x", 1));
        assertEquals(Map.of(), CollectionUtil.copyOf(NULL_MAP));
        assertInstanceOf(Map.class, CollectionUtil.copyOf(Map.<String, Integer>of()));
    }
}
