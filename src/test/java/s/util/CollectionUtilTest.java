package s.util;

import s.type.tuple.Pair;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class CollectionUtilTest {

    @Test
    void isEmpty() {
        assertTrue(CollectionUtil.isEmpty((List<String>) null));
        assertTrue(CollectionUtil.isEmpty(Collections.emptyList()));
        assertTrue(CollectionUtil.isEmpty((Map<String, Integer>) null));
        assertTrue(CollectionUtil.isEmpty(Collections.emptyMap()));
        assertFalse(CollectionUtil.isEmpty(List.of("a")));
        assertFalse(CollectionUtil.isEmpty(Map.of("a", 1)));
        log.info("isEmpty test passed");
    }

    @Test
    void emptyIfNull() {
        assertEquals(Collections.emptyList(), CollectionUtil.emptyIfNull((List<String>) null));
        assertEquals(List.of("a"), CollectionUtil.emptyIfNull(List.of("a")));
        assertEquals(Collections.emptyMap(), CollectionUtil.emptyIfNull((Map<String, Integer>) null));
        assertEquals(Map.of("a", 1), CollectionUtil.emptyIfNull(Map.of("a", 1)));
        log.info("emptyIfNull test passed");
    }

    @Test
    void zip() {
        List<Integer> list1 = List.of(1, 2, 3);
        List<String> list2 = List.of("a", "b", "c", "d");
        List<Pair<Integer, String>> zipped = CollectionUtil.zip(list1, list2);
        assertEquals(3, zipped.size());
        assertEquals(Pair.of(1, "a"), zipped.get(0));
        assertEquals(Pair.of(2, "b"), zipped.get(1));
        assertEquals(Pair.of(3, "c"), zipped.get(2));

        // 불변 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> zipped.add(Pair.of(4, "d")));

        assertEquals(Collections.emptyList(), CollectionUtil.zip(null, list2));
        assertEquals(Collections.emptyList(), CollectionUtil.zip(list1, null));
        log.info("zip test passed: {}", zipped);
    }

    @Test
    void zipWithMixer() {
        List<Integer> list1 = List.of(1, 2, 3);
        List<String> list2 = List.of("a", "b", "c");
        BiFunction<Integer, String, String> mixer = (i, s) -> i + s;
        List<String> zipped = CollectionUtil.zip(list1, list2, mixer);
        assertEquals(3, zipped.size());
        assertEquals("1a", zipped.get(0));
        assertEquals("2b", zipped.get(1));
        assertEquals("3c", zipped.get(2));

        // 불변 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> zipped.add("4d"));

        assertEquals(Collections.emptyList(), CollectionUtil.zip(list1, list2, null));
        log.info("zip with mixer test passed: {}", zipped);
    }

    @Test
    void toArray() {
        List<String> list = List.of("a", "b", "c");
        String[] array = CollectionUtil.toArray(list, new String[0]);
        assertArrayEquals(new String[]{"a", "b", "c"}, array);

        assertArrayEquals(new String[0], CollectionUtil.toArray((List<String>) null, new String[0]));
        log.info("toArray test passed");
    }

    @Test
    void findOne() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        Predicate<Integer> even = i -> i % 2 == 0;

        assertEquals(2, CollectionUtil.findOne(list, even));
        assertNull(CollectionUtil.findOne(list, i -> i > 10));
        assertNull(CollectionUtil.findOne(null, even));
        assertNull(CollectionUtil.findOne(list, null));
        log.info("findOne test passed");
    }

    @Test
    void findAll() {
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6);
        Predicate<Integer> even = i -> i % 2 == 0;

        List<Integer> result = CollectionUtil.findAll(list, even);
        assertEquals(List.of(2, 4, 6), result);

        // 불변 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> result.add(8));

        assertEquals(Collections.emptyList(), CollectionUtil.findAll(list, i -> i > 10));
        assertEquals(Collections.emptyList(), CollectionUtil.findAll(null, even));
        assertEquals(Collections.emptyList(), CollectionUtil.findAll(list, null));
        log.info("findAll test passed");
    }

    @Test
    void unionOf() {
        List<Integer> list1 = List.of(1, 2, 3);
        List<Integer> list2 = List.of(3, 4, 5);
        List<Integer> union = CollectionUtil.unionOf(list1, list2);
        assertEquals(5, union.size());
        assertTrue(union.containsAll(List.of(1, 2, 3, 4, 5)));

        // 불변 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> union.add(6));

        assertEquals(list2, CollectionUtil.unionOf(null, list2));
        assertEquals(list1, CollectionUtil.unionOf(list1, null));
        assertEquals(Collections.emptyList(), CollectionUtil.unionOf(null, null));
        log.info("unionOf test passed: {}", union);
    }

    @Test
    void intersectionOf() {
        List<Integer> list1 = List.of(1, 2, 3, 4);
        List<Integer> list2 = List.of(3, 4, 5, 6);
        List<Integer> intersection = CollectionUtil.intersectionOf(list1, list2);
        assertEquals(List.of(3, 4), intersection);

        // 불변 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> intersection.add(5));

        assertEquals(Collections.emptyList(), CollectionUtil.intersectionOf(null, list2));
        assertEquals(Collections.emptyList(), CollectionUtil.intersectionOf(list1, null));
        log.info("intersectionOf test passed: {}", intersection);
    }

    @Test
    void differenceOf() {
        List<Integer> list1 = List.of(1, 2, 3, 4);
        List<Integer> list2 = List.of(3, 4, 5, 6);
        List<Integer> difference = CollectionUtil.differenceOf(list1, list2);
        assertEquals(List.of(1, 2), difference);

        // 불변 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> difference.add(5));

        assertEquals(Collections.emptyList(), CollectionUtil.differenceOf(null, list2));
        assertEquals(list1, CollectionUtil.differenceOf(list1, null));
        log.info("differenceOf test passed: {}", difference);
    }

    @Test
    void symmetricDifferenceOf() {
        List<Integer> list1 = List.of(1, 2, 3, 4);
        List<Integer> list2 = List.of(3, 4, 5, 6);
        List<Integer> symDiff = CollectionUtil.symmetricDifferenceOf(list1, list2);
        assertEquals(4, symDiff.size());
        assertTrue(symDiff.containsAll(List.of(1, 2, 5, 6)));

        // 불변 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> symDiff.add(7));

        assertEquals(list2, CollectionUtil.symmetricDifferenceOf(null, list2));
        assertEquals(list1, CollectionUtil.symmetricDifferenceOf(list1, null));
        log.info("symmetricDifferenceOf test passed: {}", symDiff);
    }

    @Test
    void slice() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);

        assertEquals(List.of(3, 4, 5), CollectionUtil.slice(list, 2));
        assertEquals(List.of(3, 4), CollectionUtil.slice(list, 2, 4));
        assertEquals(List.of(4, 5), CollectionUtil.slice(list, -2));
        assertEquals(List.of(2, 3, 4), CollectionUtil.slice(list, -4, -1));
        assertEquals(Collections.emptyList(), CollectionUtil.slice(list, 10));
        assertEquals(Collections.emptyList(), CollectionUtil.slice(list, 2, 2));
        assertEquals(Collections.emptyList(), CollectionUtil.slice(null, 2));

        // 불변 리스트 확인
        List<Integer> sliceResult = CollectionUtil.slice(list, 1, 3);
        assertThrows(UnsupportedOperationException.class, () -> sliceResult.add(99));
        log.info("slice test passed");
    }

    @Test
    void head() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);

        assertEquals(List.of(1, 2, 3), CollectionUtil.head(list, 3));
        assertEquals(list, CollectionUtil.head(list, 10));
        assertEquals(Collections.emptyList(), CollectionUtil.head(list, 0));
        assertEquals(Collections.emptyList(), CollectionUtil.head(null, 3));

        // 음수 size: 역방향 인덱스
        assertEquals(List.of(5), CollectionUtil.head(list, -1));
        assertEquals(List.of(4, 5), CollectionUtil.head(list, -2));
        assertEquals(list, CollectionUtil.head(list, -5));
        assertEquals(Collections.emptyList(), CollectionUtil.head(list, -10));

        // 불변 리스트 확인
        List<Integer> headResult = CollectionUtil.head(list, 3);
        assertThrows(UnsupportedOperationException.class, () -> headResult.add(99));
        log.info("head test passed");
    }

    @Test
    void tail() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);

        assertEquals(List.of(3, 4, 5), CollectionUtil.tail(list, 3));
        assertEquals(list, CollectionUtil.tail(list, 10));
        assertEquals(Collections.emptyList(), CollectionUtil.tail(list, 0));
        assertEquals(Collections.emptyList(), CollectionUtil.tail(null, 3));

        // 음수 size: 양수로 바꿔서 앞에서부터 제외
        assertEquals(List.of(2, 3, 4, 5), CollectionUtil.tail(list, -1));
        assertEquals(List.of(3, 4, 5), CollectionUtil.tail(list, -2));
        assertEquals(Collections.emptyList(), CollectionUtil.tail(list, -5));
        assertEquals(Collections.emptyList(), CollectionUtil.tail(list, -10));

        // 불변 리스트 확인
        List<Integer> tailResult = CollectionUtil.tail(list, 3);
        assertThrows(UnsupportedOperationException.class, () -> tailResult.add(99));
        log.info("tail test passed");
    }

    @Test
    void indexing() {
        List<String> list = List.of("apple", "banana", "cherry");
        Function<String, String> indexer = String::toUpperCase;
        Map<String, String> indexed = CollectionUtil.indexing(list, indexer);

        assertEquals(3, indexed.size());
        assertEquals("apple", indexed.get("APPLE"));
        assertEquals("banana", indexed.get("BANANA"));
        assertEquals("cherry", indexed.get("CHERRY"));

        // 불변 맵 확인
        assertThrows(UnsupportedOperationException.class, () -> indexed.put("DATE", "date"));

        assertEquals(Collections.emptyMap(), CollectionUtil.indexing(null, indexer));
        assertEquals(Collections.emptyMap(), CollectionUtil.indexing(list, null));
        log.info("indexing test passed: {}", indexed);
    }

    @Test
    void grouping() {
        List<String> list = List.of("apple", "apricot", "banana", "blueberry", "cherry");
        Function<String, String> classifier = s -> s.substring(0, 1);
        Map<String, List<String>> grouped = CollectionUtil.grouping(list, classifier);

        assertEquals(3, grouped.size());
        assertEquals(List.of("apple", "apricot"), grouped.get("a"));
        assertEquals(List.of("banana", "blueberry"), grouped.get("b"));
        assertEquals(List.of("cherry"), grouped.get("c"));

        // 불변 맵과 불변 내부 리스트 확인
        assertThrows(UnsupportedOperationException.class, () -> grouped.put("d", List.of("date")));
        assertThrows(UnsupportedOperationException.class, () -> grouped.get("a").add("avocado"));

        assertEquals(Collections.emptyMap(), CollectionUtil.grouping(null, classifier));
        assertEquals(Collections.emptyMap(), CollectionUtil.grouping(list, null));
        log.info("grouping test passed: {}", grouped);
    }

    @Test
    void asMapVarargs() {
        Map<Object, Object> map = CollectionUtil.asMap("foo", 42, "bar", "baz");
        assertEquals(2, map.size());
        assertEquals(42, map.get("foo"));
        assertEquals("baz", map.get("bar"));

        // 불변 맵 확인
        assertThrows(UnsupportedOperationException.class, () -> map.put("baz", "qux"));

        assertEquals(Collections.emptyMap(), CollectionUtil.asMap());

        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap("foo"));
        log.info("asMap varargs test passed: {}", map);
    }

    @Test
    void castKeyValue() {
        Map<Object, Object> map1 = CollectionUtil.asMap("foo", 42, "bar", 43);
        Map<String, Integer> map2 = CollectionUtil.castKeyValue(map1);

        assertEquals(2, map2.size());
        assertEquals(42, map2.get("foo"));
        assertEquals(43, map2.get("bar"));

        assertEquals(Collections.emptyMap(), CollectionUtil.castKeyValue(null));
        log.info("castKeyValue test passed: {}", map2);
    }

    @Test
    void asMapWithClasses() {
        Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);
        assertEquals(2, map.size());
        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));

        // 불변 맵 확인
        assertThrows(UnsupportedOperationException.class, () -> map.put("baz", 44));

        assertEquals(Collections.emptyMap(), CollectionUtil.asMap(String.class, Integer.class));

        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap(String.class, Integer.class, "foo"));
        log.info("asMap with classes test passed: {}", map);
    }

    @Test
    void asMapWithEntries() {
        List<Map.Entry<String, Integer>> entries = List.of(
                Map.entry("foo", 42),
                Map.entry("bar", 43)
        );
        Map<String, Integer> map = CollectionUtil.asMap(entries);
        assertEquals(2, map.size());
        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));

        // 불변 맵 확인
        assertThrows(UnsupportedOperationException.class, () -> map.put("baz", 44));

        assertEquals(Collections.emptyMap(), CollectionUtil.asMap((List<Map.Entry<String, Integer>>) null));
        assertEquals(Collections.emptyMap(), CollectionUtil.asMap(Collections.emptyList()));
        log.info("asMap with entries test passed: {}", map);
    }

    @Test
    void copyOf() {
        Map<String, Integer> original = new HashMap<>();
        original.put("foo", 42);
        original.put("bar", 43);

        Map<String, Integer> copy = CollectionUtil.copyOf(original);
        assertEquals(2, copy.size());
        assertEquals(42, copy.get("foo"));
        assertEquals(43, copy.get("bar"));

        // 불변 맵 확인 - 복사본은 불변이어야 함
        assertThrows(UnsupportedOperationException.class, () -> copy.put("baz", 44));
        assertThrows(UnsupportedOperationException.class, () -> copy.put("qux", 45));

        // 원본은 변경되지 않아야 함
        assertEquals(2, original.size());

        assertEquals(Collections.emptyMap(), CollectionUtil.copyOf(null));
        log.info("copyOf test passed");
    }
}