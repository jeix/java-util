package s.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;
import s.type.tuple.Pair;

@Slf4j
class CollectionUtilTest {

    // ---- List isEmpty / emptyIfNull ----

    @Test
    void isEmpty_list_nullReturnsTrue() {
        assertTrue(CollectionUtil.isEmpty((List<String>) null));
    }

    @Test
    void isEmpty_list_emptyReturnsTrue() {
        assertTrue(CollectionUtil.isEmpty(List.of()));
    }

    @Test
    void isEmpty_list_nonEmptyReturnsFalse() {
        assertFalse(CollectionUtil.isEmpty(List.of("hello")));
    }

    @Test
    void emptyIfNull_list_nullReturnsEmpty() {
        assertEquals(List.of(), CollectionUtil.emptyIfNull((List<String>) null));
    }

    @Test
    void emptyIfNull_list_nonNullReturnsImmutable() {
        List<String> input = List.of("hello");
        List<String> result = CollectionUtil.emptyIfNull(input);
        assertThrows(UnsupportedOperationException.class, () -> result.add("world"));
    }

    // ---- List toArray ----

    @Test
    void toArray_string() {
        String[] array = CollectionUtil.toArray(List.of("a", "b", "c"));
        assertArrayEquals(new String[]{"a", "b", "c"}, array);
    }

    @Test
    void toArray_integer() {
        Integer[] array = CollectionUtil.toArray(List.of(1, 2, 3));
        assertArrayEquals(new Integer[]{1, 2, 3}, array);
    }

    @Test
    void toArray_nullList() {
        Object[] array = CollectionUtil.toArray(null);
        assertArrayEquals(new Object[0], array);
    }

    @Test
    void toArray_emptyList() {
        Object[] array = CollectionUtil.toArray(List.of());
        assertArrayEquals(new Object[0], array);
    }

    @Test
    void toArray_containsNullElement() {
        List<String> input = new ArrayList<>();
        input.add("a");
        input.add(null);
        input.add("c");
        Object[] array = CollectionUtil.toArray(input);
        assertEquals(3, array.length);
        assertEquals("a", array[0]);
        assertNull(array[1]);
        assertEquals("c", array[2]);
    }

    // ---- List findOne / findAll ----

    @Test
    void findOne_found() {
        List<String> list = List.of("a", "b", "c", "b");
        assertEquals("b", CollectionUtil.findOne(list, s -> s.equals("b")));
    }

    @Test
    void findOne_notFound() {
        List<String> list = List.of("a", "b", "c");
        assertNull(CollectionUtil.findOne(list, s -> s.equals("x")));
    }

    @Test
    void findOne_nullList() {
        assertNull(CollectionUtil.findOne(null, s -> true));
    }

    @Test
    void findAll_multipleMatches() {
        List<String> list = List.of("a", "b", "c", "b");
        List<String> result = CollectionUtil.findAll(list, s -> s.equals("b"));
        assertEquals(List.of("b", "b"), result);
    }

    @Test
    void findAll_noMatches() {
        List<String> list = List.of("a", "b", "c");
        assertTrue(CollectionUtil.findAll(list, s -> s.equals("x")).isEmpty());
    }

    @Test
    void findAll_nullList() {
        assertTrue(CollectionUtil.findAll(null, s -> true).isEmpty());
    }

    // ---- List zip ----

    @Test
    void zip_createsPairs() {
        List<String> keys = List.of("a", "b", "c");
        List<Integer> vals = List.of(1, 2, 3);
        List<Pair<String, Integer>> result = CollectionUtil.zip(keys, vals);
        assertEquals(3, result.size());
        assertEquals(Pair.of("a", 1), result.get(0));
        assertEquals(Pair.of("b", 2), result.get(1));
        assertEquals(Pair.of("c", 3), result.get(2));
    }

    @Test
    void zip_differentLengths() {
        List<String> keys = List.of("a", "b", "c", "d");
        List<Integer> vals = List.of(1, 2);
        List<Pair<String, Integer>> result = CollectionUtil.zip(keys, vals);
        assertEquals(2, result.size());
        assertEquals(Pair.of("a", 1), result.get(0));
        assertEquals(Pair.of("b", 2), result.get(1));
    }

    @Test
    void zip_withMixer() {
        List<String> a = List.of("a", "b");
        List<Integer> b = List.of(1, 2);
        List<String> result = CollectionUtil.zip(a, b, (s, i) -> s + i);
        assertEquals(List.of("a1", "b2"), result);
    }

    @Test
    void zip_nullList1() {
        assertTrue(CollectionUtil.zip(null, List.of(1, 2)).isEmpty());
    }

    @Test
    void zip_nullList2() {
        assertTrue(CollectionUtil.zip(List.of("a", "b"), null).isEmpty());
    }

    // ---- Set operations ----

    @Test
    void unionOf_mergesLists() {
        List<Integer> a = List.of(1, 2, 3);
        List<Integer> b = List.of(3, 4, 5);
        List<Integer> result = CollectionUtil.unionOf(a, b);
        assertEquals(List.of(1, 2, 3, 4, 5), result);
    }

    @Test
    void unionOf_preservesDuplicatesInList1() {
        List<Integer> a = List.of(1, 2, 2, 3);
        List<Integer> b = List.of(3, 4);
        List<Integer> result = CollectionUtil.unionOf(a, b);
        assertEquals(List.of(1, 2, 2, 3, 4), result);
    }

    @Test
    void intersectionOf_commonElements() {
        List<Integer> a = List.of(1, 2, 3, 4);
        List<Integer> b = List.of(3, 4, 5, 6);
        List<Integer> result = CollectionUtil.intersectionOf(a, b);
        assertEquals(List.of(3, 4), result);
    }

    @Test
    void intersectionOf_noOverlap() {
        List<Integer> a = List.of(1, 2);
        List<Integer> b = List.of(3, 4);
        assertTrue(CollectionUtil.intersectionOf(a, b).isEmpty());
    }

    @Test
    void differenceOf_aMinusB() {
        List<Integer> a = List.of(1, 2, 3, 4);
        List<Integer> b = List.of(3, 4, 5);
        List<Integer> result = CollectionUtil.differenceOf(a, b);
        assertEquals(List.of(1, 2), result);
    }

    @Test
    void differenceOf_allRemoved() {
        List<Integer> a = List.of(1, 2);
        List<Integer> b = List.of(1, 2, 3);
        assertTrue(CollectionUtil.differenceOf(a, b).isEmpty());
    }

    @Test
    void symmetricDifferenceOf_bothDirections() {
        List<Integer> a = List.of(1, 2, 3);
        List<Integer> b = List.of(2, 3, 4);
        List<Integer> result = CollectionUtil.symmetricDifferenceOf(a, b);
        assertEquals(List.of(1, 4), result);
    }

    @Test
    void symmetricDifferenceOf_noOverlap() {
        List<Integer> a = List.of(1, 2);
        List<Integer> b = List.of(3, 4);
        List<Integer> result = CollectionUtil.symmetricDifferenceOf(a, b);
        assertEquals(List.of(1, 2, 3, 4), result);
    }

    // ---- List slice ----

    @Test
    void slice_fromBeginToEnd() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertEquals(List.of("b", "c", "d"), CollectionUtil.slice(list, 1, 4));
    }

    @Test
    void slice_negativeBegin() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertEquals(List.of("d", "e"), CollectionUtil.slice(list, -2));
    }

    @Test
    void slice_negativeBeginAndEnd() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertEquals(List.of("c", "d"), CollectionUtil.slice(list, -3, -1));
    }

    @Test
    void slice_emptyResult() {
        List<String> list = List.of("a", "b", "c");
        assertTrue(CollectionUtil.slice(list, 2, 1).isEmpty());
    }

    @Test
    void slice_nullList() {
        assertTrue(CollectionUtil.slice(null, 0, 1).isEmpty());
    }

    // ---- head / tail ----

    @Test
    void head_firstNElements() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertEquals(List.of("a", "b", "c"), CollectionUtil.head(list, 3));
    }

    @Test
    void head_sizeExceedsList() {
        List<String> list = List.of("a", "b");
        assertEquals(List.of("a", "b"), CollectionUtil.head(list, 10));
    }

    @Test
    void head_zeroReturnsEmpty() {
        List<String> list = List.of("a", "b", "c");
        assertTrue(CollectionUtil.head(list, 0).isEmpty());
    }

    @Test
    void head_negativeSize() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertEquals(List.of("a", "b", "c"), CollectionUtil.head(list, -2));
    }

    @Test
    void head_negativeSizeBeyondRange() {
        List<String> list = List.of("a", "b", "c");
        assertTrue(CollectionUtil.head(list, -5).isEmpty());
    }

    @Test
    void tail_lastNElements() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertEquals(List.of("c", "d", "e"), CollectionUtil.tail(list, 3));
    }

    @Test
    void tail_sizeExceedsList() {
        List<String> list = List.of("a", "b");
        assertEquals(List.of("a", "b"), CollectionUtil.tail(list, 10));
    }

    @Test
    void tail_zeroReturnsEmpty() {
        List<String> list = List.of("a", "b", "c");
        assertTrue(CollectionUtil.tail(list, 0).isEmpty());
    }

    @Test
    void tail_negativeSize() {
        List<String> list = List.of("a", "b", "c", "d", "e");
        assertEquals(List.of("c", "d", "e"), CollectionUtil.tail(list, -2));
    }

    @Test
    void tail_negativeSizeBeyondRange() {
        List<String> list = List.of("a", "b", "c");
        assertTrue(CollectionUtil.tail(list, -5).isEmpty());
    }

    // ---- indexing / grouping ----

    @Test
    void indexing_createsMap() {
        List<String> list = List.of("apple", "banana", "cherry");
        Map<String, String> result = CollectionUtil.indexing(list, s -> s.substring(0, 1));
        assertEquals("apple", result.get("a"));
        assertEquals("banana", result.get("b"));
        assertEquals("cherry", result.get("c"));
    }

    @Test
    void indexing_overwritesDuplicateKeys() {
        List<String> list = List.of("apple", "apricot");
        Map<String, String> result = CollectionUtil.indexing(list, s -> s.substring(0, 1));
        assertEquals("apricot", result.get("a"));
    }

    @Test
    void grouping_createsGroups() {
        List<String> list = List.of("apple", "apricot", "banana", "blueberry");
        Map<String, List<String>> result = CollectionUtil.grouping(list, s -> s.substring(0, 1));
        assertEquals(List.of("apple", "apricot"), result.get("a"));
        assertEquals(List.of("banana", "blueberry"), result.get("b"));
    }

    @Test
    void grouping_preservesOrder() {
        List<String> list = List.of("apple", "banana", "cherry");
        Map<String, List<String>> result = CollectionUtil.grouping(list, s -> s.substring(0, 1));
        assertEquals(List.of("a", "b", "c"), new ArrayList<>(result.keySet()));
    }

    // ---- Map isEmpty / emptyIfNull ----

    @Test
    void isEmpty_map_nullReturnsTrue() {
        assertTrue(CollectionUtil.isEmpty((Map<String, Integer>) null));
    }

    @Test
    void isEmpty_map_emptyReturnsTrue() {
        assertTrue(CollectionUtil.isEmpty(Map.of()));
    }

    @Test
    void emptyIfNull_map_nullReturnsEmpty() {
        assertEquals(Map.of(), CollectionUtil.emptyIfNull((Map<String, Integer>) null));
    }

    @Test
    void emptyIfNull_map_nonNullReturnsEqual() {
        Map<String, Integer> input = Map.of("foo", 42);
        assertEquals(input, CollectionUtil.emptyIfNull(input));
    }

    @Test
    void emptyIfNull_map_nonNullReturnsImmutable() {
        Map<String, Integer> input = new LinkedHashMap<>();
        input.put("foo", 42);
        Map<String, Integer> result = CollectionUtil.emptyIfNull(input);
        assertThrows(UnsupportedOperationException.class, () -> result.put("bar", 43));
    }

    // ---- Map asMap ----

    @Test
    void asMap_varargs() {
        Map<Object, Object> map = CollectionUtil.asMap("foo", 42, "bar", 43);
        assertEquals(2, map.size());
        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));
    }

    @Test
    void asMap_varargs_oddItemsThrows() {
        assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap("foo", 42, "bar"));
    }

    @Test
    void asMap_varargs_empty() {
        assertEquals(Map.of(), CollectionUtil.asMap());
    }

    @Test
    void asMap_typed() {
        Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);
        assertEquals(2, map.size());
        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));
    }

    @Test
    void asMap_typed_classCastException() {
        assertThrows(ClassCastException.class,
            () -> CollectionUtil.asMap(String.class, Integer.class, "foo", "not-an-int"));
    }

    @Test
    void asMap_fromEntries() {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>();
        entries.add(Map.entry("foo", 42));
        entries.add(Map.entry("bar", 43));
        Map<String, Integer> map = CollectionUtil.asMap(entries);
        assertEquals(2, map.size());
        assertEquals(42, map.get("foo"));
        assertEquals(43, map.get("bar"));
    }

    // ---- castKeyValue ----

    @Test
    void castKeyValue_converts() {
        Map<Object, Object> origin = CollectionUtil.asMap("foo", 42, "bar", 43);
        Map<String, Integer> result = CollectionUtil.castKeyValue(origin);
        assertEquals(42, result.get("foo"));
        assertEquals(43, result.get("bar"));
    }

    @Test
    void castKeyValue_nullOrigin() {
        assertEquals(Map.of(), CollectionUtil.castKeyValue(null));
    }

    // ---- copyOf ----

    @Test
    void copyOf_createsImmutableCopy() {
        Map<String, Integer> origin = new LinkedHashMap<>();
        origin.put("foo", 42);
        origin.put("bar", 43);
        Map<String, Integer> copy = CollectionUtil.copyOf(origin);
        assertEquals(origin, copy);
        assertThrows(UnsupportedOperationException.class, () -> copy.put("baz", 44));
    }

    @Test
    void copyOf_nullOrigin() {
        assertEquals(Map.of(), CollectionUtil.copyOf(null));
    }
}
