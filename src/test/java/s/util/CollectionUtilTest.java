package s.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import s.type.tuple.Pair;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilTest {

    // isEmpty(List)

    @Test
    void isEmpty_list_null_returnsTrue() {
        assertTrue(CollectionUtil.isEmpty((List<String>) null));
    }

    @Test
    void isEmpty_list_empty_returnsTrue() {
        assertTrue(CollectionUtil.isEmpty(List.of()));
    }

    @Test
    void isEmpty_list_nonEmpty_returnsFalse() {
        assertFalse(CollectionUtil.isEmpty(List.of("a")));
    }

    // emptyIfNull(List)

    @Test
    void emptyIfNull_list_null_returnsEmptyList() {
        List<String> result = CollectionUtil.<String>emptyIfNull(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void emptyIfNull_list_nonNull_returnsUnmodifiableList() {
        List<String> input = List.of("a", "b");
        List<String> result = CollectionUtil.<String>emptyIfNull(input);
        assertEquals(input, result);
        assertThrows(UnsupportedOperationException.class, () -> result.add("c"));
    }

    // zip

    @Test
    void zip_createsPairs() {
        List<String> list1 = List.of("a", "b", "c");
        List<Integer> list2 = List.of(1, 2, 3);
        List<Pair<String, Integer>> result = CollectionUtil.zip(list1, list2);
        assertEquals(3, result.size());
        assertEquals(Pair.of("a", 1), result.get(0));
        assertEquals(Pair.of("b", 2), result.get(1));
        assertEquals(Pair.of("c", 3), result.get(2));
    }

    @Test
    void zip_differentSizes_usesMinSize() {
        List<String> list1 = List.of("a", "b", "c");
        List<Integer> list2 = List.of(1, 2);
        List<Pair<String, Integer>> result = CollectionUtil.zip(list1, list2);
        assertEquals(2, result.size());
    }

    @Test
    void zip_withMixer_createsMixedResults() {
        List<String> list1 = List.of("a", "b");
        List<Integer> list2 = List.of(1, 2);
        List<String> result = CollectionUtil.zip(list1, list2, (s, i) -> s + i);
        assertEquals(List.of("a1", "b2"), result);
    }

    @Test
    void zip_returnsUnmodifiableList() {
        List<String> list1 = List.of("a", "b");
        List<Integer> list2 = List.of(1, 2);
        List<Pair<String, Integer>> result = CollectionUtil.zip(list1, list2);
        assertThrows(UnsupportedOperationException.class, () -> result.add(Pair.of("c", 3)));
    }

    // toArray

    @Test
    void toArray_convertsListToArray() {
        List<String> list = List.of("a", "b", "c");
        String[] array = CollectionUtil.toArray(list);
        assertArrayEquals(new String[]{"a", "b", "c"}, array);
    }

    // findOne

    @Test
    void findOne_findsFirstMatch() {
        List<String> list = List.of("a", "b", "c", "b");
        String result = CollectionUtil.findOne(list, (String s) -> s.equals("b"));
        assertEquals("b", result);
    }

    @Test
    void findOne_noMatch_returnsNull() {
        List<String> list = List.of("a", "b", "c");
        String result = CollectionUtil.findOne(list, (String s) -> s.equals("z"));
        assertNull(result);
    }

    // findAll

    @Test
    void findAll_findsAllMatches() {
        List<String> list = List.of("a", "b", "c", "b");
        List<String> result = CollectionUtil.findAll(list, (String s) -> s.equals("b"));
        assertEquals(List.of("b", "b"), result);
    }

    @Test
    void findAll_noMatch_returnsEmptyList() {
        List<String> list = List.of("a", "b", "c");
        List<String> result = CollectionUtil.findAll(list, (String s) -> s.equals("z"));
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_returnsUnmodifiableList() {
        List<String> list = List.of("a", "b", "c");
        List<String> result = CollectionUtil.findAll(list, (String s) -> s.equals("b"));
        assertThrows(UnsupportedOperationException.class, () -> result.add("d"));
    }

    // unionOf

    @Test
    void unionOf_combinesListsWithoutDuplicates() {
        List<String> list1 = List.of("a", "b", "c");
        List<String> list2 = List.of("b", "c", "d");
        List<String> result = CollectionUtil.unionOf(list1, list2);
        assertEquals(List.of("a", "b", "c", "d"), result);
    }

    @Test
    void unionOf_returnsUnmodifiableList() {
        List<String> list1 = List.of("a", "b");
        List<String> list2 = List.of("c", "d");
        List<String> result = CollectionUtil.unionOf(list1, list2);
        assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    }

    // intersectionOf

    @Test
    void intersectionOf_findsCommonElements() {
        List<String> list1 = List.of("a", "b", "c");
        List<String> list2 = List.of("b", "c", "d");
        List<String> result = CollectionUtil.intersectionOf(list1, list2);
        assertEquals(List.of("b", "c"), result);
    }

    @Test
    void intersectionOf_returnsUnmodifiableList() {
        List<String> list1 = List.of("a", "b");
        List<String> list2 = List.of("a", "b");
        List<String> result = CollectionUtil.intersectionOf(list1, list2);
        assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    }

    // differenceOf

    @Test
    void differenceOf_findsElementsInFirstNotSecond() {
        List<String> list1 = List.of("a", "b", "c");
        List<String> list2 = List.of("b", "c", "d");
        List<String> result = CollectionUtil.differenceOf(list1, list2);
        assertEquals(List.of("a"), result);
    }

    @Test
    void differenceOf_returnsUnmodifiableList() {
        List<String> list1 = List.of("a", "b");
        List<String> list2 = List.of("c", "d");
        List<String> result = CollectionUtil.differenceOf(list1, list2);
        assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    }

    // symmetricDifferenceOf

    @Test
    void symmetricDifferenceOf_findsElementsInEitherButNotBoth() {
        List<String> list1 = List.of("a", "b", "c");
        List<String> list2 = List.of("b", "c", "d");
        List<String> result = CollectionUtil.symmetricDifferenceOf(list1, list2);
        assertEquals(List.of("a", "d"), result);
    }

    @Test
    void symmetricDifferenceOf_returnsUnmodifiableList() {
        List<String> list1 = List.of("a", "b");
        List<String> list2 = List.of("c", "d");
        List<String> result = CollectionUtil.symmetricDifferenceOf(list1, list2);
        assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    }

    // slice

    @Test
    void slice_withBegin_returnsFromBeginToEnd() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.slice(list, 1);
        assertEquals(List.of("b", "c", "d"), result);
    }

    @Test
    void slice_withBeginAndEnd_returnsSublist() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.slice(list, 1, 3);
        assertEquals(List.of("b", "c"), result);
    }

    @Test
    void slice_negativeBegin_interpretsAsReverseIndex() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.slice(list, -2);
        assertEquals(List.of("c", "d"), result);
    }

    @Test
    void slice_negativeBeginAndEnd_interpretsAsReverseIndex() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.slice(list, -3, -1);
        assertEquals(List.of("b", "c"), result);
    }

    @Test
    void slice_beginLessThanNegativeLen_clampsToZero() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.slice(list, -10);
        assertEquals(List.of("a", "b", "c", "d"), result);
    }

    @Test
    void slice_beginGreaterThanLen_clampsToLen() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.slice(list, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void slice_returnsUnmodifiableList() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.slice(list, 1, 3);
        assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    }

    // head

    @Test
    void head_returnsFirstNElements() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.head(list, 2);
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void head_negativeSize_interpretsAsReverseIndex() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.head(list, -2);
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void head_negativeSizeLessThanNegativeLen_clampsToZero() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.head(list, -10);
        assertTrue(result.isEmpty());
    }

    @Test
    void head_sizeGreaterThanLen_clampsToLen() {
        List<String> list = List.of("a", "b");
        List<String> result = CollectionUtil.head(list, 10);
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void head_returnsUnmodifiableList() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.head(list, 2);
        assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    }

    // tail

    @Test
    void tail_returnsLastNElements() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.tail(list, 2);
        assertEquals(List.of("c", "d"), result);
    }

    @Test
    void tail_negativeSize_interpretsAsExcludingFromFront() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.tail(list, -2);
        assertEquals(List.of("c", "d"), result);
    }

    @Test
    void tail_negativeSizeLessThanNegativeLen_clampsToZero() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.tail(list, -10);
        assertTrue(result.isEmpty());
    }

    @Test
    void tail_sizeGreaterThanLen_clampsToLen() {
        List<String> list = List.of("a", "b");
        List<String> result = CollectionUtil.tail(list, 10);
        assertEquals(List.of("a", "b"), result);
    }

    @Test
    void tail_returnsUnmodifiableList() {
        List<String> list = List.of("a", "b", "c", "d");
        List<String> result = CollectionUtil.tail(list, 2);
        assertThrows(UnsupportedOperationException.class, () -> result.add("e"));
    }

    // indexing

    @Test
    void indexing_createsMapFromList() {
        List<String> list = List.of("a", "b", "c");
        Map<String, String> result = CollectionUtil.indexing(list, s -> s.toUpperCase());
        assertEquals("a", result.get("A"));
        assertEquals("b", result.get("B"));
        assertEquals("c", result.get("C"));
    }

    @Test
    void indexing_returnsUnmodifiableMap() {
        List<String> list = List.of("a", "b", "c");
        Map<String, String> result = CollectionUtil.indexing(list, s -> s.toUpperCase());
        assertThrows(UnsupportedOperationException.class, () -> result.put("D", "d"));
    }

    // grouping

    @Test
    void grouping_groupsElementsByClassifier() {
        List<String> list = List.of("apple", "avocado", "banana", "blueberry");
        Map<String, List<String>> result = CollectionUtil.grouping(list, s -> s.substring(0, 1));
        assertEquals(List.of("apple", "avocado"), result.get("a"));
        assertEquals(List.of("banana", "blueberry"), result.get("b"));
    }

    @Test
    void grouping_returnsUnmodifiableMapAndValues() {
        List<String> list = List.of("apple", "avocado", "banana");
        Map<String, List<String>> result = CollectionUtil.grouping(list, s -> s.substring(0, 1));
        assertThrows(UnsupportedOperationException.class, () -> result.put("c", List.of()));
        assertThrows(UnsupportedOperationException.class, () -> result.get("a").add("cherry"));
    }

    // isEmpty(Map)

    @Test
    void isEmpty_map_null_returnsTrue() {
        assertTrue(CollectionUtil.isEmpty((Map<String, String>) null));
    }

    @Test
    void isEmpty_map_empty_returnsTrue() {
        assertTrue(CollectionUtil.isEmpty(Map.of()));
    }

    @Test
    void isEmpty_map_nonEmpty_returnsFalse() {
        assertFalse(CollectionUtil.isEmpty(Map.of("a", 1)));
    }

    // emptyIfNull(Map)

    @Test
    void emptyIfNull_map_null_returnsEmptyMap() {
        Map<String, String> result = CollectionUtil.<String, String>emptyIfNull(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void emptyIfNull_map_nonNull_returnsUnmodifiableMap() {
        Map<String, String> input = Map.of("a", "1");
        Map<String, String> result = CollectionUtil.<String, String>emptyIfNull(input);
        assertEquals(input, result);
        assertThrows(UnsupportedOperationException.class, () -> result.put("b", "2"));
    }

    // asMap(Object...)

    @Test
    void asMap_createsMapFromKeyValuePairs() {
        Map<Object, Object> result = CollectionUtil.asMap("foo", 42, "bar", 43);
        assertEquals(42, result.get("foo"));
        assertEquals(43, result.get("bar"));
    }

    @Test
    void asMap_oddLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap("foo", 42, "bar"));
    }

    @Test
    void asMap_returnsUnmodifiableMap() {
        Map<Object, Object> result = CollectionUtil.asMap("foo", 42);
        assertThrows(UnsupportedOperationException.class, () -> result.put("bar", 43));
    }

    // castKeyValue

    @Test
    void castKeyValue_castsMapTypes() {
        Map<Object, Object> origin = CollectionUtil.asMap("foo", 42, "bar", 43);
        Map<String, Integer> result = CollectionUtil.castKeyValue(origin);
        assertEquals(42, result.get("foo"));
        assertEquals(43, result.get("bar"));
    }

    @Test
    void castKeyValue_returnsUnmodifiableMap() {
        Map<Object, Object> origin = CollectionUtil.asMap("foo", 42);
        Map<String, Integer> result = CollectionUtil.castKeyValue(origin);
        assertThrows(UnsupportedOperationException.class, () -> result.put("bar", 43));
    }

    // asMap(Class, Class, Object...)

    @Test
    void asMap_withClasses_createsTypedMap() {
        Map<String, Integer> result = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);
        assertEquals(42, result.get("foo"));
        assertEquals(43, result.get("bar"));
    }

    @Test
    void asMap_withClasses_oddLength_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar"));
    }

    @Test
    void asMap_withClasses_returnsUnmodifiableMap() {
        Map<String, Integer> result = CollectionUtil.asMap(String.class, Integer.class, "foo", 42);
        assertThrows(UnsupportedOperationException.class, () -> result.put("bar", 43));
    }

    // asMap(List<Map.Entry>)

    @Test
    void asMap_fromEntries_createsMap() {
        List<Map.Entry<String, Integer>> entries = List.of(
                Map.entry("foo", 42),
                Map.entry("bar", 43)
        );
        Map<String, Integer> result = CollectionUtil.asMap(entries);
        assertEquals(42, result.get("foo"));
        assertEquals(43, result.get("bar"));
    }

    @Test
    void asMap_fromEntries_returnsUnmodifiableMap() {
        List<Map.Entry<String, Integer>> entries = List.of(Map.entry("foo", 42));
        Map<String, Integer> result = CollectionUtil.asMap(entries);
        assertThrows(UnsupportedOperationException.class, () -> result.put("bar", 43));
    }

    // copyOf

    @Test
    void copyOf_createsCopyOfMap() {
        Map<String, Integer> origin = new LinkedHashMap<>();
        origin.put("foo", 42);
        origin.put("bar", 43);
        Map<String, Integer> result = CollectionUtil.copyOf(origin);
        assertEquals(origin, result);
        assertNotSame(origin, result);
    }

    @Test
    void copyOf_returnsUnmodifiableMap() {
        Map<String, Integer> origin = new LinkedHashMap<>();
        origin.put("foo", 42);
        Map<String, Integer> result = CollectionUtil.copyOf(origin);
        assertThrows(UnsupportedOperationException.class, () -> result.put("bar", 43));
    }
}
