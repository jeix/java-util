package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import s.type.tuple.Pair;

@Slf4j
@DisplayName("CollectionUtil")
class CollectionUtilTest {

    @Nested
    @DisplayName("isEmpty / emptyIfNull")
    class Checks {

        @Test
        @DisplayName("isEmpty: null 이거나 비어 있으면 true")
        void isEmptyList() {
            assertTrue(CollectionUtil.isEmpty((List<String>) null));
            assertTrue(CollectionUtil.isEmpty(List.of()));
            assertFalse(CollectionUtil.isEmpty(List.of("a")));
        }

        @Test
        @DisplayName("isEmpty: 맵도 같은 규칙")
        void isEmptyMap() {
            assertTrue(CollectionUtil.isEmpty((Map<String, String>) null));
            assertTrue(CollectionUtil.isEmpty(Map.of()));
            assertFalse(CollectionUtil.isEmpty(Map.of("a", "b")));
        }

        @Test
        @DisplayName("emptyIfNull: null 이면 바꿔 넣을 수 있는 빈 리스트")
        void emptyIfNullList() {
            List<String> empty = CollectionUtil.emptyIfNull((List<String>) null);

            assertTrue(empty.isEmpty());
            empty.add("a");
            assertEquals(List.of("a"), empty);
        }

        @Test
        @DisplayName("emptyIfNull: null 이 아니면 그대로 돌려준다")
        void emptyIfNullKeepsSameInstance() {
            List<String> list = new ArrayList<>(List.of("a"));
            Map<String, String> map = new LinkedHashMap<>(Map.of("a", "b"));

            assertSame(list, CollectionUtil.emptyIfNull(list));
            assertSame(map, CollectionUtil.emptyIfNull(map));
        }

        @Test
        @DisplayName("emptyIfNull: null 이면 바꿔 넣을 수 있는 빈 맵")
        void emptyIfNullMap() {
            Map<String, String> empty = CollectionUtil.emptyIfNull((Map<String, String>) null);

            assertTrue(empty.isEmpty());
            empty.put("a", "b");
            assertEquals(Map.of("a", "b"), empty);
        }
    }

    @Nested
    @DisplayName("zip")
    class Zipping {

        @Test
        @DisplayName("자리 순서대로 Pair 로 짝짓는다")
        void zipPairs() {
            List<Pair<Integer, String>> zipped = CollectionUtil.zip(List.of(1, 2, 3), List.of("a", "b", "c"));

            assertEquals(List.of(Pair.of(1, "a"), Pair.of(2, "b"), Pair.of(3, "c")), zipped);
            assertEquals(Integer.valueOf(1), zipped.get(0).ord1());
            assertEquals("b", zipped.get(1).ord2());
        }

        @Test
        @DisplayName("길이가 다르면 짧은 쪽에 맞춘다")
        void zipDifferentLength() {
            assertEquals(List.of(Pair.of(1, "a"), Pair.of(2, "b")),
                    CollectionUtil.zip(List.of(1, 2, 3), List.of("a", "b")));
            assertEquals(List.of(Pair.of(1, "a")),
                    CollectionUtil.zip(List.of(1), List.of("a", "b")));
        }

        @Test
        @DisplayName("한쪽이 비어 있으면 빈 결과")
        void zipEmpty() {
            assertEquals(List.of(), CollectionUtil.zip(List.of(), List.of("a")));
            assertEquals(List.of(), CollectionUtil.zip(List.of(1), List.of()));
        }

        @Test
        @DisplayName("mixer 로 섞는다")
        void zipWithMixer() {
            List<Integer> sums = CollectionUtil.zip(List.of(1, 2), List.of(10, 20), Integer::sum);

            assertEquals(List.of(11, 22), sums);
        }

        @Test
        @DisplayName("리스트나 mixer 가 null 이면 실패")
        void zipNull() {
            assertThrows(NullPointerException.class, () -> CollectionUtil.zip(null, List.of("a")));
            assertThrows(NullPointerException.class, () -> CollectionUtil.zip(List.of(1), null));
            assertThrows(NullPointerException.class, () -> CollectionUtil.zip(List.of(1), List.of(2), null));
        }
    }

    @Nested
    @DisplayName("toArray")
    class ToArray {

        @Test
        @DisplayName("리스트를 배열로 바꾼다")
        void toArray() {
            Object[] array = CollectionUtil.toArray(List.of("a", "b", "c"));

            assertEquals(3, array.length);
            assertEquals("a", array[0]);
            assertEquals("c", array[2]);
        }

        @Test
        @DisplayName("배열의 런타임 타입은 Object[]")
        void runtimeType() {
            Object[] array = CollectionUtil.toArray(List.of("a"));

            assertSame(Object[].class, array.getClass());
        }

        @Test
        @DisplayName("빈 리스트는 길이 0 배열")
        void toArrayEmpty() {
            assertEquals(0, CollectionUtil.toArray(List.of()).length);
        }

        @Test
        @DisplayName("리스트가 null 이면 실패")
        void toArrayNull() {
            assertThrows(NullPointerException.class, () -> CollectionUtil.toArray(null));
        }
    }

    @Nested
    @DisplayName("findOne / findAll")
    class Finding {

        @Test
        @DisplayName("findOne: 처음 통과하는 항목 하나")
        void findOne() {
            List<Integer> list = List.of(1, 2, 3, 4);

            assertEquals(Integer.valueOf(2), CollectionUtil.findOne(list, n -> n % 2 == 0));
            assertEquals(Integer.valueOf(1), CollectionUtil.findOne(list, n -> n > 0));
        }

        @Test
        @DisplayName("findOne: 없으면 null")
        void findOneNotFound() {
            assertNull(CollectionUtil.findOne(List.of(1, 3), n -> n % 2 == 0));
            assertNull(CollectionUtil.findOne(List.of(), n -> true));
        }

        @Test
        @DisplayName("findAll: 통과하는 항목 전부, 순서 그대로")
        void findAll() {
            assertEquals(List.of(2, 4), CollectionUtil.findAll(List.of(1, 2, 3, 4), n -> n % 2 == 0));
            assertEquals(List.of(), CollectionUtil.findAll(List.of(1, 3), n -> n % 2 == 0));
        }

        @Test
        @DisplayName("리스트나 filter 가 null 이면 실패")
        void nullArgs() {
            assertThrows(NullPointerException.class, () -> CollectionUtil.findOne((List<String>) null, s -> true));
            assertThrows(NullPointerException.class, () -> CollectionUtil.findAll(List.of(1), null));
        }
    }

    @Nested
    @DisplayName("집합 연산")
    class SetOperations {

        @Test
        @DisplayName("differenceOf: list1 에서 list2 에 있는 항목을 뺀다")
        void difference() {
            assertEquals(List.of(1, 3), CollectionUtil.differenceOf(List.of(1, 2, 3), List.of(2)));
            assertEquals(List.of(1, 2, 3), CollectionUtil.differenceOf(List.of(1, 2, 3), List.of(9)));
            assertEquals(List.of(), CollectionUtil.differenceOf(List.of(1, 2), List.of(1, 2)));
            assertEquals(List.of(1, 2), CollectionUtil.differenceOf(List.of(1, 2), List.of()));
        }

        @Test
        @DisplayName("differenceOf: 같은 항목이 여러 번 있으면 모두 빠진다")
        void differenceDuplicates() {
            assertEquals(List.of(1, 3), CollectionUtil.differenceOf(List.of(1, 2, 2, 3), List.of(2)));
        }

        @Test
        @DisplayName("unionOf: list1 + (list2 - list1)")
        void union() {
            assertEquals(List.of(1, 2, 3), CollectionUtil.unionOf(List.of(1, 2), List.of(2, 3)));
            assertEquals(List.of(1, 2), CollectionUtil.unionOf(List.of(1, 2), List.of()));
            assertEquals(List.of(1, 2), CollectionUtil.unionOf(List.of(), List.of(1, 2)));
            assertEquals(List.of(1, 2), CollectionUtil.unionOf(List.of(1, 2), List.of(1, 2)));
        }

        @Test
        @DisplayName("intersectionOf: list1 - (list1 - list2)")
        void intersection() {
            assertEquals(List.of(2, 3), CollectionUtil.intersectionOf(List.of(1, 2, 3), List.of(2, 3, 4)));
            assertEquals(List.of(), CollectionUtil.intersectionOf(List.of(1, 2), List.of(3, 4)));
            assertEquals(List.of(1, 1), CollectionUtil.intersectionOf(List.of(1, 1, 2), List.of(1)));
        }

        @Test
        @DisplayName("symmetricDifferenceOf: (list1 - list2) + (list2 - list1)")
        void symmetricDifference() {
            assertEquals(List.of(1, 3), CollectionUtil.symmetricDifferenceOf(List.of(1, 2), List.of(2, 3)));
            assertEquals(List.of(1, 2, 3, 4), CollectionUtil.symmetricDifferenceOf(List.of(1, 2), List.of(3, 4)));
            assertEquals(List.of(), CollectionUtil.symmetricDifferenceOf(List.of(1, 2), List.of(1, 2)));
        }

        @Test
        @DisplayName("결과는 원본과 따로 움직인다")
        void resultIsIndependent() {
            List<Integer> list1 = new ArrayList<>(List.of(1, 2));
            List<Integer> list2 = new ArrayList<>(List.of(2, 3));

            List<Integer> union = CollectionUtil.unionOf(list1, list2);
            list1.add(99);

            assertEquals(List.of(1, 2, 3), union);
        }

        @Test
        @DisplayName("리스트가 null 이면 실패")
        void nullArgs() {
            assertThrows(NullPointerException.class, () -> CollectionUtil.unionOf(null, List.of(1)));
            assertThrows(NullPointerException.class, () -> CollectionUtil.intersectionOf(List.of(1), null));
            assertThrows(NullPointerException.class, () -> CollectionUtil.symmetricDifferenceOf(null, null));
        }
    }

    @Nested
    @DisplayName("slice / head / tail")
    class SubLists {

        @Test
        @DisplayName("slice(list, begin): begin 부터 끝까지")
        void sliceBegin() {
            List<Integer> list = List.of(1, 2, 3, 4);

            assertEquals(List.of(3, 4), CollectionUtil.slice(list, 2));
            assertEquals(List.of(1, 2, 3, 4), CollectionUtil.slice(list, 0));
            assertEquals(List.of(), CollectionUtil.slice(list, 4));
            assertEquals(List.of(), CollectionUtil.slice(list, 10));
        }

        @Test
        @DisplayName("slice: 음수 인덱스는 뒤에서부터 센다")
        void sliceNegative() {
            List<Integer> list = List.of(1, 2, 3, 4);

            assertEquals(List.of(4), CollectionUtil.slice(list, -1));
            assertEquals(List.of(3, 4), CollectionUtil.slice(list, -2));
            assertEquals(List.of(2, 3), CollectionUtil.slice(list, 1, -1));
            assertEquals(List.of(2, 3), CollectionUtil.slice(list, -3, -1));
            assertEquals(List.of(1, 2, 3, 4), CollectionUtil.slice(list, -10));
        }

        @Test
        @DisplayName("slice: 잘라낼 구간이 없으면 빈 리스트")
        void sliceEmptyRange() {
            List<Integer> list = List.of(1, 2, 3, 4);

            assertEquals(List.of(), CollectionUtil.slice(list, 3, 3));
            assertEquals(List.of(), CollectionUtil.slice(list, 3, 1));
            assertEquals(List.of(), CollectionUtil.slice(list, 10, 20));
        }

        @Test
        @DisplayName("slice 는 원본을 보는 뷰가 아니라 새 리스트를 돌려준다")
        void sliceReturnsCopy() {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4));

            List<Integer> sliced = CollectionUtil.slice(list, 1, 3);
            list.add(99);

            assertEquals(List.of(2, 3), sliced);
        }

        @Test
        @DisplayName("head: 앞에서 size 개, 음수는 뒤에서부터 세는 역방향 인덱스")
        void head() {
            List<Integer> list = List.of(1, 2, 3, 4);

            assertEquals(List.of(1, 2), CollectionUtil.head(list, 2));
            assertEquals(List.of(1, 2, 3, 4), CollectionUtil.head(list, 4));
            assertEquals(List.of(1, 2, 3, 4), CollectionUtil.head(list, 10));
            assertEquals(List.of(), CollectionUtil.head(list, 0));
            assertEquals(List.of(1, 2), CollectionUtil.head(list, -2));
            assertEquals(List.of(), CollectionUtil.head(list, -4));
        }

        @Test
        @DisplayName("tail: 뒤에서 size 개, 음수는 앞에서 제외할 개수")
        void tail() {
            List<Integer> list = List.of(1, 2, 3, 4);

            assertEquals(List.of(3, 4), CollectionUtil.tail(list, 2));
            assertEquals(List.of(1, 2, 3, 4), CollectionUtil.tail(list, 4));
            assertEquals(List.of(1, 2, 3, 4), CollectionUtil.tail(list, 10));
            assertEquals(List.of(), CollectionUtil.tail(list, 0));
            assertEquals(List.of(3, 4), CollectionUtil.tail(list, -2));
            assertEquals(List.of(), CollectionUtil.tail(list, -10));
        }

        @Test
        @DisplayName("리스트가 null 이면 실패")
        void nullList() {
            assertThrows(NullPointerException.class, () -> CollectionUtil.slice(null, 1));
            assertThrows(NullPointerException.class, () -> CollectionUtil.head(null, 1));
            assertThrows(NullPointerException.class, () -> CollectionUtil.tail(null, 1));
        }
    }

    @Nested
    @DisplayName("indexing / grouping")
    class Indexing {

        @Test
        @DisplayName("indexing: 색인이 키, 항목이 값")
        void indexing() {
            Map<String, String> indexed = CollectionUtil.indexing(List.of("ko", "en"), s -> s);

            assertEquals(Map.of("ko", "ko", "en", "en"), indexed);
            assertEquals(List.of("ko", "en"), new ArrayList<>(indexed.keySet()));
        }

        @Test
        @DisplayName("indexing: 색인이 겹치면 뒤에 나온 항목이 남는다")
        void indexingDuplicate() {
            Map<String, String> indexed = CollectionUtil.indexing(List.of("apple", "avocado", "banana"),
                    s -> s.substring(0, 1));

            assertEquals(2, indexed.size());
            assertEquals("avocado", indexed.get("a"));
            assertEquals("banana", indexed.get("b"));
        }

        @Test
        @DisplayName("grouping: 분류가 키, 그 분류의 항목 리스트가 값")
        void grouping() {
            Map<String, List<Integer>> grouped = CollectionUtil.grouping(List.of(1, 2, 3, 4, 5),
                    n -> n % 2 == 0 ? "짝" : "홀");

            log.info("grouping 결과: {}", grouped);

            assertEquals(Map.of("홀", List.of(1, 3, 5), "짝", List.of(2, 4)), grouped);
            assertEquals(List.of("홀", "짝"), new ArrayList<>(grouped.keySet()));
        }

        @Test
        @DisplayName("grouping: 빈 리스트면 빈 맵")
        void groupingEmpty() {
            assertEquals(Map.of(), CollectionUtil.grouping(List.of(), n -> "x"));
        }

        @Test
        @DisplayName("리스트나 함수가 null 이면 실패")
        void nullArgs() {
            assertThrows(NullPointerException.class, () -> CollectionUtil.indexing(List.of("a"), null));
            assertThrows(NullPointerException.class, () -> CollectionUtil.grouping(List.of("a"), null));
        }
    }

    @Nested
    @DisplayName("asMap / castKeyValue / copyOf")
    class MapBuilding {

        @Test
        @DisplayName("asMap: 키와 값을 번갈아 넘겨 만든다")
        void asMapItems() {
            Map<Object, Object> map = CollectionUtil.asMap("foo", 42, "bar", 43);

            log.info("asMap 결과: {}", map);

            assertEquals(2, map.size());
            assertEquals(42, map.get("foo"));
            assertEquals(43, map.get("bar"));
            assertEquals(List.of("foo", "bar"), new ArrayList<>(map.keySet()));
        }

        @Test
        @DisplayName("asMap: 키와 값이 짝을 이루지 않으면 예외")
        void asMapOddItems() {
            assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap("foo", 42, "bar"));
        }

        @Test
        @DisplayName("asMap: 빈 맵도 만들 수 있다")
        void asMapEmpty() {
            assertTrue(CollectionUtil.asMap().isEmpty());
        }

        @Test
        @DisplayName("castKeyValue: 타입만 바꿔서 본다")
        void castKeyValue() {
            Map<Object, Object> origin = CollectionUtil.asMap("foo", 42, "bar", 43);
            Map<String, Integer> typed = CollectionUtil.castKeyValue(origin);

            assertEquals(Integer.valueOf(42), typed.get("foo"));
            assertSame(origin, typed);
        }

        @Test
        @DisplayName("asMap(keyClass, valueClass, ...): 클래스로 검사하면서 만든다")
        void asMapTyped() {
            Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);

            assertEquals(2, map.size());
            assertEquals(Integer.valueOf(42), map.get("foo"));
            assertEquals(List.of("foo", "bar"), new ArrayList<>(map.keySet()));
        }

        @Test
        @DisplayName("asMap(keyClass, valueClass, ...): 타입이 안 맞으면 예외")
        void asMapTypedMismatch() {
            assertThrows(ClassCastException.class,
                    () -> CollectionUtil.asMap(String.class, Integer.class, "foo", "42"));
            assertThrows(ClassCastException.class,
                    () -> CollectionUtil.asMap(String.class, Integer.class, 42, 42));
            assertThrows(IllegalArgumentException.class,
                    () -> CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar"));
        }

        @Test
        @DisplayName("asMap(entries): Map.Entry 목록에서 만든다")
        void asMapEntries() {
            List<Map.Entry<String, Integer>> entries = List.of(Map.entry("foo", 42), Map.entry("bar", 43));
            Map<String, Integer> map = CollectionUtil.asMap(entries);

            assertEquals(2, map.size());
            assertEquals(Integer.valueOf(42), map.get("foo"));
            assertEquals(Integer.valueOf(43), map.get("bar"));
            assertEquals(List.of("foo", "bar"), new ArrayList<>(map.keySet()));
        }

        @Test
        @DisplayName("copyOf: 내용이 같은 불변 맵")
        void copyOf() {
            Map<String, Integer> origin = new LinkedHashMap<>();
            origin.put("foo", 42);
            origin.put("bar", 43);

            Map<String, Integer> copied = CollectionUtil.copyOf(origin);

            assertEquals(origin, copied);
            assertThrows(UnsupportedOperationException.class, () -> copied.put("baz", 44));
        }

        @Test
        @DisplayName("copyOf: 키나 값이 null 이면 예외")
        void copyOfNullValue() {
            Map<String, Integer> origin = new LinkedHashMap<>();
            origin.put("foo", null);

            assertThrows(NullPointerException.class, () -> CollectionUtil.copyOf(origin));
        }

        @Test
        @DisplayName("맵이나 목록이 null 이면 실패")
        void nullArgs() {
            assertThrows(NullPointerException.class, () -> CollectionUtil.copyOf(null));
            assertThrows(NullPointerException.class, () -> CollectionUtil.castKeyValue(null));
            assertThrows(NullPointerException.class,
                    () -> CollectionUtil.asMap((List<Map.Entry<String, Integer>>) null));
        }
    }

    @Nested
    @DisplayName("돌려주는 컬렉션의 불변 여부")
    class Immutability {

        @Test
        @DisplayName("리스트를 돌려주는 메서드들은 고칠 수 없다")
        void listsAreUnmodifiable() {
            List<Integer> list1 = List.of(1, 2);
            List<Integer> list2 = List.of(2, 3);

            assertUnmodifiable(CollectionUtil.zip(list1, list2));
            assertUnmodifiable(CollectionUtil.zip(list1, list2, Integer::sum));
            assertUnmodifiable(CollectionUtil.findAll(list1, n -> true));
            assertUnmodifiable(CollectionUtil.unionOf(list1, list2));
            assertUnmodifiable(CollectionUtil.intersectionOf(list1, list2));
            assertUnmodifiable(CollectionUtil.differenceOf(list1, list2));
            assertUnmodifiable(CollectionUtil.symmetricDifferenceOf(list1, list2));
            assertUnmodifiable(CollectionUtil.slice(list1, 0, 1));
            assertUnmodifiable(CollectionUtil.slice(list1, 5, 6));
            assertUnmodifiable(CollectionUtil.head(list1, 1));
            assertUnmodifiable(CollectionUtil.tail(list1, 1));
        }

        @Test
        @DisplayName("맵을 돌려주는 메서드들은 고칠 수 없다")
        void mapsAreUnmodifiable() {
            List<String> list = List.of("a", "bb");

            assertUnmodifiableMap(CollectionUtil.indexing(list, s -> s));
            assertUnmodifiableMap(CollectionUtil.grouping(list, s -> s.substring(0, 1)));
            assertUnmodifiableMap(CollectionUtil.asMap("foo", 42));
            assertUnmodifiableMap(CollectionUtil.asMap(String.class, Integer.class, "foo", 42));
            assertUnmodifiableMap(CollectionUtil.asMap(List.of(Map.entry("foo", 42))));
        }

        @Test
        @DisplayName("grouping 이 돌려주는 값 리스트도 고칠 수 없다")
        void groupingValuesAreUnmodifiable() {
            Map<String, List<Integer>> grouped = CollectionUtil.grouping(List.of(1, 2, 3),
                    n -> n % 2 == 0 ? "짝" : "홀");

            assertThrows(UnsupportedOperationException.class, () -> grouped.get("홀").add(99));
        }

        @Test
        @DisplayName("emptyIfNull 로 만든 빈 컬렉션은 바꿔 넣을 수 있다")
        void emptyIfNullIsMutable() {
            List<String> list = CollectionUtil.emptyIfNull((List<String>) null);
            Map<String, String> map = CollectionUtil.emptyIfNull((Map<String, String>) null);

            list.add("a");
            map.put("a", "b");

            assertEquals(List.of("a"), list);
            assertEquals(Map.of("a", "b"), map);
        }

        private <T> void assertUnmodifiable(List<T> list) {
            assertThrows(UnsupportedOperationException.class, () -> list.add(null));
        }

        private <K, V> void assertUnmodifiableMap(Map<K, V> map) {
            assertThrows(UnsupportedOperationException.class, () -> map.put(null, null));
        }
    }
}
