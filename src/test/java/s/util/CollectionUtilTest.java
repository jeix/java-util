package s.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import s.type.tuple.Pair;

@Slf4j
class CollectionUtilTest {

    @BeforeAll
    static void beforeAll() {
        log.info("CollectionUtilTest start");
    }

    @Test
    void isEmptyHandlesList() {
        assertTrue(CollectionUtil.isEmpty((List<Object>) null));
        assertTrue(CollectionUtil.isEmpty(List.of()));
        assertFalse(CollectionUtil.isEmpty(List.of(1)));
    }

    @Test
    void isEmptyHandlesMap() {
        assertTrue(CollectionUtil.isEmpty((Map<Object, Object>) null));
        assertTrue(CollectionUtil.isEmpty(Map.of()));
        assertFalse(CollectionUtil.isEmpty(Map.of("a", 1)));
    }

    @Test
    void emptyIfNullHandlesList() {
        assertEquals(List.of(), CollectionUtil.emptyIfNull((List<Object>) null));
        List<Integer> list = List.of(1);

        assertEquals(list, CollectionUtil.emptyIfNull(list));
        assertThrows(UnsupportedOperationException.class, () -> CollectionUtil.emptyIfNull(list).add(2));
    }

    @Test
    void emptyIfNullHandlesMap() {
        assertEquals(Map.of(), CollectionUtil.emptyIfNull((Map<Object, Object>) null));
        Map<String, Integer> map = Map.of("a", 1);

        assertEquals(map, CollectionUtil.emptyIfNull(map));
        assertThrows(UnsupportedOperationException.class, () -> CollectionUtil.emptyIfNull(map).put("b", 2));
    }

    @Test
    void zipReturnsPairsUpToShorterSize() {
        List<Pair<Integer, String>> zipped = CollectionUtil.zip(List.of(1, 2, 3), List.of("a", "b"));

        assertEquals(List.of(Pair.of(1, "a"), Pair.of(2, "b")), zipped);
    }

    @Test
    void zipAppliesMixer() {
        List<Integer> sums = CollectionUtil.zip(List.of(1, 2), List.of(10, 20), (a, b) -> a + b);

        assertEquals(List.of(11, 22), sums);
    }

    @Test
    void zipHandlesNull() {
        assertEquals(List.of(), CollectionUtil.zip(null, List.of(1)));
        assertEquals(List.of(), CollectionUtil.zip(List.of(1), null, (a, b) -> a));
    }

    @Test
    void toArrayReturnsArray() {
        assertArrayEquals(new Object[] { "a", "b" }, CollectionUtil.toArray(List.of("a", "b")));
        assertNull(CollectionUtil.toArray(null));
    }

    @Test
    void findOneReturnsFirstMatch() {
        assertEquals(2, CollectionUtil.findOne(List.of(1, 2, 3, 4), i -> i % 2 == 0));
    }

    @Test
    void findOneReturnsNullWhenNoMatch() {
        assertNull(CollectionUtil.findOne(List.of(1, 3), i -> i % 2 == 0));
        assertNull(CollectionUtil.findOne(null, i -> true));
        assertNull(CollectionUtil.findOne(List.of(1), null));
    }

    @Test
    void findAllReturnsMatches() {
        assertEquals(List.of(2, 4), CollectionUtil.findAll(List.of(1, 2, 3, 4), i -> i % 2 == 0));
    }

    @Test
    void findAllReturnsEmptyWhenNoMatch() {
        assertEquals(List.of(), CollectionUtil.findAll(List.of(1, 3), i -> i % 2 == 0));
        assertEquals(List.of(), CollectionUtil.findAll(null, i -> true));
    }

    @Test
    void unionOfMergesDistinct() {
        assertEquals(List.of(1, 2, 3, 4), CollectionUtil.unionOf(List.of(1, 2, 3), List.of(3, 4)));
        assertEquals(List.of(1, 2, 3), CollectionUtil.unionOf(List.of(1, 1, 2), List.of(2, 3)));
    }

    @Test
    void unionOfHandlesNull() {
        assertEquals(List.of(1), CollectionUtil.unionOf(null, List.of(1)));
        assertEquals(List.of(1), CollectionUtil.unionOf(List.of(1), null));
        assertEquals(List.of(), CollectionUtil.unionOf(null, null));
    }

    @Test
    void intersectionOfKeepsCommonDistinct() {
        assertEquals(List.of(2, 3), CollectionUtil.intersectionOf(List.of(1, 2, 3), List.of(2, 3, 4)));
        assertEquals(List.of(), CollectionUtil.intersectionOf(List.of(1, 2), List.of(3, 4)));
        assertEquals(List.of(2), CollectionUtil.intersectionOf(List.of(2, 2), List.of(2)));
    }

    @Test
    void intersectionOfHandlesNull() {
        assertEquals(List.of(), CollectionUtil.intersectionOf(null, List.of(1)));
        assertEquals(List.of(), CollectionUtil.intersectionOf(List.of(1), null));
    }

    @Test
    void differenceOfRemovesSecondDistinct() {
        assertEquals(List.of(1, 3), CollectionUtil.differenceOf(List.of(1, 2, 3), List.of(2)));
        assertEquals(List.of(1), CollectionUtil.differenceOf(List.of(1, 1, 2), List.of(2)));
    }

    @Test
    void differenceOfHandlesNull() {
        assertEquals(List.of(), CollectionUtil.differenceOf(null, List.of(1)));
        assertEquals(List.of(1), CollectionUtil.differenceOf(List.of(1), null));
    }

    @Test
    void symmetricDifferenceOfCombinesBothDifferences() {
        assertEquals(List.of(1, 2, 4), CollectionUtil.symmetricDifferenceOf(List.of(1, 2, 3), List.of(3, 4)));
        assertEquals(List.of(1, 2, 3, 4),
                CollectionUtil.symmetricDifferenceOf(List.of(1, 2), List.of(3, 4)));
        assertEquals(List.of(), CollectionUtil.symmetricDifferenceOf(List.of(1, 2), List.of(1, 2)));
    }

    @Test
    void sliceReturnsFromBegin() {
        assertEquals(List.of(2, 3, 4, 5), CollectionUtil.slice(List.of(1, 2, 3, 4, 5), 1));
    }

    @Test
    void sliceReturnsBetweenBeginAndEnd() {
        assertEquals(List.of(2, 3), CollectionUtil.slice(List.of(1, 2, 3, 4, 5), 1, 3));
    }

    @Test
    void sliceInterpretsNegativeIndexFromEnd() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);

        assertEquals(List.of(4, 5), CollectionUtil.slice(list, -2));
        assertEquals(List.of(2, 3, 4), CollectionUtil.slice(list, -4, -1));
    }

    @Test
    void sliceClampsAndHandlesEmptyRange() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);

        assertEquals(List.of(), CollectionUtil.slice(list, 100));
        assertEquals(list, CollectionUtil.slice(list, -100));
        assertEquals(List.of(), CollectionUtil.slice(list, 3, 2));
        assertEquals(List.of(), CollectionUtil.slice(null, 1));
    }

    @Test
    void headReturnsLeadingPart() {
        assertEquals(List.of(1, 2), CollectionUtil.head(List.of(1, 2, 3, 4), 2));
        assertEquals(List.of(), CollectionUtil.head(List.of(1, 2, 3, 4), 0));
        assertEquals(List.of(1, 2, 3, 4), CollectionUtil.head(List.of(1, 2, 3, 4), 10));
    }

    @Test
    void headInterpretsNegativeSizeFromEnd() {
        assertEquals(List.of(1, 2, 3), CollectionUtil.head(List.of(1, 2, 3, 4), -1));
        assertEquals(List.of(), CollectionUtil.head(List.of(1, 2, 3, 4), -100));
        assertEquals(List.of(), CollectionUtil.head(null, 2));
    }

    @Test
    void tailReturnsTrailingPart() {
        assertEquals(List.of(3, 4), CollectionUtil.tail(List.of(1, 2, 3, 4), 2));
        assertEquals(List.of(), CollectionUtil.tail(List.of(1, 2, 3, 4), 0));
        assertEquals(List.of(1, 2, 3, 4), CollectionUtil.tail(List.of(1, 2, 3, 4), 10));
    }

    @Test
    void tailTreatsNegativeSizeAsFrontExcludeIndex() {
        assertEquals(List.of(2, 3, 4), CollectionUtil.tail(List.of(1, 2, 3, 4), -1));
        assertEquals(List.of(), CollectionUtil.tail(List.of(1, 2, 3, 4), -100));
        assertEquals(List.of(), CollectionUtil.tail(null, 2));
    }

    @Test
    void indexingMapsKeyToItem() {
        assertEquals(Map.of("k1", 1, "k2", 2), CollectionUtil.indexing(List.of(1, 2), i -> "k" + i));
    }

    @Test
    void indexingKeepsLastForDuplicateKey() {
        Map<String, Integer> indexed = CollectionUtil.indexing(List.of(1, 2), i -> "k");

        assertEquals(1, indexed.size());
        assertEquals(2, indexed.get("k"));
    }

    @Test
    void indexingHandlesNull() {
        assertEquals(Map.of(), CollectionUtil.indexing(null, i -> "k"));
    }

    @Test
    void groupingClassifiesItems() {
        Map<String, List<Integer>> grouped =
                CollectionUtil.grouping(List.of(1, 2, 3, 4), i -> i % 2 == 0 ? "even" : "odd");

        assertEquals(List.of(1, 3), grouped.get("odd"));
        assertEquals(List.of(2, 4), grouped.get("even"));
        assertEquals(List.of("odd", "even"), List.copyOf(grouped.keySet()));
    }

    @Test
    void groupingHandlesNull() {
        assertEquals(Map.of(), CollectionUtil.grouping(null, i -> "k"));
    }

    @Test
    void asMapBuildsFromPairs() {
        Map<Object, Object> map = CollectionUtil.asMap("foo", 42, "bar", 43);

        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));
        assertEquals(List.of("foo", "bar"), List.copyOf(map.keySet()));
    }

    @Test
    void asMapRejectsOddItems() {
        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap("foo", 42, "bar"));
    }

    @Test
    void asMapHandlesEmpty() {
        assertEquals(Map.of(), CollectionUtil.asMap());
    }

    @Test
    void castKeyValueCastsMap() {
        Map<Object, Object> origin = CollectionUtil.asMap("foo", 42, "bar", 43);

        Map<String, Integer> typed = CollectionUtil.castKeyValue(origin);

        assertEquals(42, typed.get("foo"));
        assertEquals(43, typed.get("bar"));
        assertEquals(Map.of(), CollectionUtil.castKeyValue(null));
    }

    @Test
    void asMapWithClassesCastsItems() {
        Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);

        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));
    }

    @Test
    void asMapWithClassesRejectsWrongType() {
        assertThrows(ClassCastException.class,
                () -> CollectionUtil.asMap(String.class, Integer.class, "foo", "notInteger"));
    }

    @Test
    void asMapWithClassesRejectsOddItems() {
        assertThrows(IllegalArgumentException.class,
                () -> CollectionUtil.asMap(String.class, Integer.class, "foo"));
    }

    @Test
    void asMapBuildsFromEntries() {
        Map<String, Integer> map = CollectionUtil.asMap(
                List.of(Map.entry("foo", 42), Map.entry("bar", 43)));

        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));
        assertEquals(List.of("foo", "bar"), List.copyOf(map.keySet()));
    }

    @Test
    void asMapHandlesNullEntries() {
        assertEquals(Map.of(), CollectionUtil.asMap((List<Map.Entry<String, Integer>>) null));
    }

    @Test
    void copyOfCopiesMap() {
        Map<String, Integer> origin = Map.of("foo", 42);

        Map<String, Integer> copy = CollectionUtil.copyOf(origin);

        assertEquals(origin, copy);
        assertNotSame(origin, copy);
        assertEquals(Map.of(), CollectionUtil.copyOf(null));
    }

    @Test
    void returnedCollectionsAreImmutable() {
        List<Integer> union = CollectionUtil.unionOf(List.of(1), List.of(2));
        assertThrows(UnsupportedOperationException.class, () -> union.add(3));

        List<Integer> sliced = CollectionUtil.slice(List.of(1, 2, 3), 1);
        assertThrows(UnsupportedOperationException.class, () -> sliced.add(9));

        List<Pair<Integer, String>> zipped = CollectionUtil.zip(List.of(1), List.of("a"));
        assertThrows(UnsupportedOperationException.class, () -> zipped.add(Pair.of(2, "b")));

        Map<Object, Object> map = CollectionUtil.asMap("a", 1);
        assertThrows(UnsupportedOperationException.class, () -> map.put("b", 2));

        List<Integer> items = List.of(1);
        Map<String, Integer> indexed = CollectionUtil.indexing(items, i -> "k");
        assertThrows(UnsupportedOperationException.class, () -> indexed.put("x", 1));

        Map<String, List<Integer>> grouped = CollectionUtil.grouping(List.of(1, 2), i -> "k");
        assertThrows(UnsupportedOperationException.class, () -> grouped.get("k").add(3));
    }
}
