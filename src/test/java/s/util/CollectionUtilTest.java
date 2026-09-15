package s.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import s.type.tuple.Pair;

/** {@link CollectionUtil}의 메서드들을 검증하는 테스트입니다. */
@Slf4j
class CollectionUtilTest {

    /** 랜덤 분기 검증에서 각 메서드를 반복 실행하는 횟수입니다. */
    private static final int RANDOM_REPEAT = 100;

    @BeforeAll
    static void setUp() {
        log.info("@Slf4j 로거를 사용하는 테스트 클래스입니다: {}", CollectionUtilTest.class.getName());
    }

    @Nested
    @DisplayName("리스트 기본")
    class 리스트기본 {

        @Test
        @DisplayName("isEmpty: null이거나 비어 있으면 true")
        void isEmpty() {
            List<String> nullList = null;

            assertAll(
                    () -> assertTrue(CollectionUtil.isEmpty(nullList)),
                    () -> assertTrue(CollectionUtil.isEmpty(List.of())),
                    () -> assertFalse(CollectionUtil.isEmpty(List.of("a"))));
        }

        @Test
        @DisplayName("emptyIfNull: null이면 빈 리스트, 아니면 원본을 반환한다")
        void emptyIfNull() {
            List<String> nullList = null;
            List<String> list = List.of("a");

            assertAll(
                    () -> assertEquals(List.of(), CollectionUtil.emptyIfNull(nullList)),
                    () -> assertSame(list, CollectionUtil.emptyIfNull(list)));
        }

        @Test
        @DisplayName("toArray: 리스트를 배열로 변환한다")
        void toArray() {
            List<String> list = List.of("a", "b");
            List<String> nullList = null;
            Object[] array = CollectionUtil.toArray(list);
            Object[] emptyArray = CollectionUtil.toArray(nullList);

            assertAll(
                    () -> assertArrayEquals(new Object[] {"a", "b"}, array),
                    () -> assertEquals(Object[].class, array.getClass(), "런타임 배열 타입은 Object[]이다"),
                    () -> assertEquals(0, CollectionUtil.<Object>toArray(List.of()).length),
                    () -> assertEquals(0, emptyArray.length));
        }
    }

    @Nested
    @DisplayName("zip")
    class zip {

        @Test
        @DisplayName("zip(list1, list2): 짧은 쪽 길이에 맞춰 Pair 목록을 만든다")
        void zipWithPair() {
            List<Pair<String, Integer>> result = CollectionUtil.zip(List.of("a", "b", "c"), List.of(1, 2));

            assertAll(
                    () -> assertEquals(List.of(Pair.of("a", 1), Pair.of("b", 2)), result),
                    () -> assertEquals(List.of(), CollectionUtil.zip(List.of(), List.of(1))),
                    () -> assertEquals(List.of(), CollectionUtil.zip(null, null)));
        }

        @Test
        @DisplayName("zip(list1, list2, mixer): 믹서로 합친 결과를 만든다")
        void zipWithMixer() {
            List<String> result = CollectionUtil.zip(List.of("a", "b"), List.of(1, 2), (text, number) -> text + number);

            assertAll(
                    () -> assertEquals(List.of("a1", "b2"), result),
                    () -> assertEquals(List.of(), CollectionUtil.zip(List.of("a"), List.of(), (text, number) -> text)),
                    () -> assertEquals(List.of(), CollectionUtil.zip(List.of("a"), List.of(1), null),
                            "믹서가 없으면 빈 리스트를 반환한다"));
        }
    }

    @Nested
    @DisplayName("항목 검색")
    class 항목검색 {

        @Test
        @DisplayName("findOne: 조건을 만족하는 첫 항목, 없으면 null")
        void findOne() {
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals("banana",
                            CollectionUtil.findOne(List.of("apple", "banana", "blueberry"), item -> item.startsWith("b"))),
                    () -> assertNull(CollectionUtil.findOne(List.of("apple"), item -> item.startsWith("z"))),
                    () -> assertNull(CollectionUtil.findOne(List.of(), item -> true)),
                    () -> assertNull(CollectionUtil.findOne(nullList, item -> true)),
                    () -> assertNull(CollectionUtil.findOne(List.of("apple"), null)),
                    () -> assertNull(CollectionUtil.findOne(Arrays.asList("a", null), item -> item == null),
                            "null 항목도 예외 없이 찾는다"));
        }

        @Test
        @DisplayName("findAll: 조건을 만족하는 항목을 순서대로 반환한다")
        void findAll() {
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("apple", "avocado"),
                            CollectionUtil.findAll(List.of("apple", "banana", "avocado"), item -> item.startsWith("a"))),
                    () -> assertEquals(List.of(), CollectionUtil.findAll(List.of("apple"), item -> false)),
                    () -> assertEquals(List.of(), CollectionUtil.findAll(nullList, item -> true)),
                    () -> assertEquals(List.of(), CollectionUtil.findAll(List.of("apple"), null)));
        }
    }

    @Nested
    @DisplayName("집합 연산")
    class 집합연산 {

        @Test
        @DisplayName("unionOf: list1 + (list2 - list1)")
        void unionOf() {
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("a", "b", "c", "d"),
                            CollectionUtil.unionOf(List.of("a", "b", "c"), List.of("c", "d"))),
                    () -> assertEquals(List.of("a", "a", "b"),
                            CollectionUtil.unionOf(List.of("a", "a"), List.of("a", "b")), "list1의 중복은 유지한다"),
                    () -> assertEquals(List.of("a"), CollectionUtil.unionOf(List.of("a"), nullList)),
                    () -> assertEquals(List.of("a"), CollectionUtil.unionOf(nullList, List.of("a"))),
                    () -> assertEquals(List.of(), CollectionUtil.unionOf(nullList, nullList)));
        }

        @Test
        @DisplayName("intersectionOf: list1 - (list1 - list2)")
        void intersectionOf() {
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("c"),
                            CollectionUtil.intersectionOf(List.of("a", "b", "c"), List.of("c", "d"))),
                    () -> assertEquals(List.of("a", "a"),
                            CollectionUtil.intersectionOf(List.of("a", "a", "b"), List.of("a")), "list1의 중복은 유지한다"),
                    () -> assertEquals(List.of(), CollectionUtil.intersectionOf(List.of("a"), nullList)),
                    () -> assertEquals(List.of(), CollectionUtil.intersectionOf(nullList, List.of("a"))));
        }

        @Test
        @DisplayName("differenceOf: list1 - list2")
        void differenceOf() {
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("a", "b"),
                            CollectionUtil.differenceOf(List.of("a", "b", "c"), List.of("c", "d"))),
                    () -> assertEquals(List.of("a", "a"),
                            CollectionUtil.differenceOf(List.of("a", "a"), List.of("b"))),
                    () -> assertEquals(List.of("a"), CollectionUtil.differenceOf(List.of("a"), nullList)),
                    () -> assertEquals(List.of(), CollectionUtil.differenceOf(nullList, List.of("a"))));
        }

        @Test
        @DisplayName("symmetricDifferenceOf: (list1 - list2) + (list2 - list1)")
        void symmetricDifferenceOf() {
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("a", "b", "d"),
                            CollectionUtil.symmetricDifferenceOf(List.of("a", "b", "c"), List.of("c", "d"))),
                    () -> assertEquals(List.of("a", "b"), CollectionUtil.symmetricDifferenceOf(nullList, List.of("a", "b"))),
                    () -> assertEquals(List.of("a", "b"), CollectionUtil.symmetricDifferenceOf(List.of("a", "b"), nullList)),
                    () -> assertEquals(List.of(), CollectionUtil.symmetricDifferenceOf(nullList, nullList)));
        }

        @Test
        @DisplayName("집합 연산 결과는 불변 리스트다")
        void unmodifiable() {
            List<String> result = CollectionUtil.unionOf(List.of("a"), List.of("b"));

            assertThrows(UnsupportedOperationException.class, () -> result.add("c"));
        }
    }

    @Nested
    @DisplayName("부분 리스트")
    class 부분리스트 {

        @Test
        @DisplayName("slice(list, begin): 시작 인덱스부터 끝까지, 음수는 역방향 인덱스")
        void sliceWithBegin() {
            List<String> list = List.of("a", "b", "c", "d");
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("c", "d"), CollectionUtil.slice(list, 2)),
                    () -> assertEquals(List.of("d"), CollectionUtil.slice(list, -1), "음수는 역방향 인덱스다"),
                    () -> assertEquals(List.of("a", "b", "c", "d"), CollectionUtil.slice(list, -4)),
                    () -> assertEquals(List.of(), CollectionUtil.slice(list, 10)),
                    () -> assertEquals(List.of(), CollectionUtil.slice(nullList, 0)));
        }

        @Test
        @DisplayName("slice(list, begin, end): 시작 인덱스(포함)부터 끝 인덱스(불포함)까지")
        void sliceWithBeginAndEnd() {
            List<String> list = List.of("a", "b", "c", "d");

            assertAll(
                    () -> assertEquals(List.of("b", "c"), CollectionUtil.slice(list, 1, 3)),
                    () -> assertEquals(List.of("c"), CollectionUtil.slice(list, -2, -1)),
                    () -> assertEquals(List.of(), CollectionUtil.slice(list, 3, 1)),
                    () -> assertEquals(List.of("a", "b", "c", "d"), CollectionUtil.slice(list, -10, 10)));
        }

        @Test
        @DisplayName("head(list, size): 앞에서부터 size만큼, 음수 size는 역방향 인덱스")
        void head() {
            List<String> list = List.of("a", "b", "c", "d");
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("a", "b"), CollectionUtil.head(list, 2)),
                    () -> assertEquals(list, CollectionUtil.head(list, 100)),
                    () -> assertEquals(List.of("a", "b", "c"), CollectionUtil.head(list, -1)),
                    () -> assertEquals(List.of(), CollectionUtil.head(list, 0)),
                    () -> assertEquals(List.of(), CollectionUtil.head(nullList, 2)));
        }

        @Test
        @DisplayName("tail(list, size): 뒤에서부터 size만큼, 음수 size는 앞에서 제외할 인덱스")
        void tail() {
            List<String> list = List.of("a", "b", "c", "d");
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(List.of("c", "d"), CollectionUtil.tail(list, 2)),
                    () -> assertEquals(list, CollectionUtil.tail(list, 100)),
                    () -> assertEquals(List.of("b", "c", "d"), CollectionUtil.tail(list, -1)),
                    () -> assertEquals(List.of(), CollectionUtil.tail(list, -4)),
                    () -> assertEquals(List.of(), CollectionUtil.tail(nullList, 2)));
        }

        @Test
        @DisplayName("slice 결과는 원본과 분리된 불변 리스트다")
        void sliceIsCopied() {
            List<String> origin = new ArrayList<>(List.of("a", "b", "c"));
            List<String> sliced = CollectionUtil.slice(origin, 1);

            origin.set(1, "x");

            assertAll(
                    () -> assertEquals(List.of("b", "c"), sliced),
                    () -> assertThrows(UnsupportedOperationException.class, () -> sliced.add("d")));
        }
    }

    @Nested
    @DisplayName("색인과 분류")
    class 색인과분류 {

        @Test
        @DisplayName("indexing: 색인이 키, 항목이 값인 맵을 만든다")
        void indexing() {
            List<String> words = List.of("foo", "bar", "baz");
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(Map.of("f", "foo", "b", "bar"),
                            CollectionUtil.indexing(words, item -> item.substring(0, 1))),
                    () -> assertEquals("bar",
                            CollectionUtil.indexing(words, item -> item.substring(0, 1)).get("b"),
                            "같은 색인은 먼저 나온 항목을 유지한다"),
                    () -> assertEquals(Map.of(), CollectionUtil.indexing(nullList, item -> item)),
                    () -> assertEquals(Map.of(), CollectionUtil.indexing(words, null)));
        }

        @Test
        @DisplayName("grouping: 분류가 키, 분류에 해당하는 목록이 값인 맵을 만든다")
        void grouping() {
            List<String> words = List.of("apple", "avocado", "banana");
            List<String> nullList = null;

            assertAll(
                    () -> assertEquals(Map.of("a", List.of("apple", "avocado"), "b", List.of("banana")),
                            CollectionUtil.grouping(words, item -> item.substring(0, 1))),
                    () -> assertEquals(List.of("apple", "avocado"),
                            CollectionUtil.grouping(words, item -> item.substring(0, 1)).get("a"), "순서를 유지한다"),
                    () -> assertEquals(Map.of(), CollectionUtil.grouping(nullList, item -> item)),
                    () -> assertEquals(Map.of(), CollectionUtil.grouping(words, null)));
        }

        @Test
        @DisplayName("grouping의 값 목록은 불변 리스트다")
        void groupingValuesUnmodifiable() {
            Map<String, List<String>> grouped = CollectionUtil.grouping(List.of("apple"), item -> item.substring(0, 1));

            assertThrows(UnsupportedOperationException.class, () -> grouped.get("a").add("avocado"));
        }
    }

    @Nested
    @DisplayName("맵")
    class 맵 {

        @Test
        @DisplayName("isEmpty, emptyIfNull")
        void isEmptyAndEmptyIfNull() {
            Map<String, Integer> nullMap = null;
            Map<String, Integer> map = Map.of("foo", 42);

            assertAll(
                    () -> assertTrue(CollectionUtil.isEmpty(nullMap)),
                    () -> assertTrue(CollectionUtil.isEmpty(Map.of())),
                    () -> assertFalse(CollectionUtil.isEmpty(map)),
                    () -> assertEquals(Map.of(), CollectionUtil.emptyIfNull(nullMap)),
                    () -> assertSame(map, CollectionUtil.emptyIfNull(map)));
        }

        @Test
        @DisplayName("asMap(items): key, value 쌍으로 맵을 만든다")
        void asMapWithItems() {
            assertAll(
                    () -> assertEquals(Map.of("foo", 42, "bar", 43), CollectionUtil.asMap("foo", 42, "bar", 43)),
                    () -> assertEquals(Map.of(), CollectionUtil.asMap()),
                    () -> assertEquals(Map.of("foo", 42), CollectionUtil.asMap("foo", 42, "foo", 42)),
                    () -> assertThrows(IllegalArgumentException.class, () -> CollectionUtil.asMap("foo", 42, "bar")));
        }

        @Test
        @DisplayName("castKeyValue: 키와 값 타입만 바꾼 맵을 반환한다")
        void castKeyValue() {
            Map<Object, Object> origin = CollectionUtil.asMap("foo", 42, "bar", 43);
            Map<String, Integer> casted = CollectionUtil.castKeyValue(origin);
            Map<Object, Object> nullMap = null;

            assertAll(
                    () -> assertEquals(42, casted.get("foo").intValue()),
                    () -> assertEquals(43, casted.get("bar").intValue()),
                    () -> assertSame(origin, casted, "원본을 그대로 반환한다"),
                    () -> assertEquals(Map.of(), CollectionUtil.castKeyValue(nullMap)));
        }

        @Test
        @DisplayName("asMap(keyClass, valueClass, items): 키와 값 타입을 검사해 맵을 만든다")
        void asMapWithClasses() {
            Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);
            Class<String> nullKeyClass = null;
            Class<Integer> nullValueClass = null;

            assertAll(
                    () -> assertEquals(Map.of("foo", 42, "bar", 43), map),
                    () -> assertThrows(ClassCastException.class,
                            () -> CollectionUtil.asMap(String.class, Integer.class, "foo", "not-integer")),
                    () -> assertEquals(Map.of(), CollectionUtil.asMap(nullKeyClass, nullValueClass, "foo", 42)));
        }

        @Test
        @DisplayName("asMap(entries): Map.Entry 목록으로 맵을 만든다")
        void asMapWithEntries() {
            List<Map.Entry<String, Integer>> entries = List.of(Map.entry("foo", 42), Map.entry("bar", 43));
            List<Map.Entry<String, Integer>> nullEntries = null;

            assertAll(
                    () -> assertEquals(Map.of("foo", 42, "bar", 43), CollectionUtil.asMap(entries)),
                    () -> assertEquals(Map.of(), CollectionUtil.asMap(nullEntries)));
        }

        @Test
        @DisplayName("copyOf: 맵을 복사해 불변 맵으로 반환한다")
        void copyOf() {
            Map<Object, Object> origin = CollectionUtil.asMap("foo", 42, "bar", 43);
            Map<Object, Object> copied = CollectionUtil.copyOf(origin);
            Map<Object, Object> nullMap = null;

            assertAll(
                    () -> assertEquals(origin, copied),
                    () -> assertNotSame(origin, copied),
                    () -> assertEquals(Map.of(), CollectionUtil.copyOf(nullMap)),
                    () -> assertThrows(UnsupportedOperationException.class, () -> copied.put("baz", 44)));
        }
    }

    @Nested
    @DisplayName("랜덤 분기")
    class 랜덤분기 {

        @Test
        @DisplayName("zip: 반복문 구현과 스트림 구현이 같은 결과를 반환한다")
        void zip() {
            List<List<String>> results = new ArrayList<>();
            for (int i = 0; i < RANDOM_REPEAT; i++) {
                results.add(CollectionUtil.zip(List.of("a", "b"), List.of(1, 2), (text, number) -> text + number));
            }

            assertAll(
                    () -> assertEquals(1, results.stream().distinct().count(), "항상 같은 결과여야 한다"),
                    () -> assertEquals(List.of("a1", "b2"), results.get(0)));
        }

        @Test
        @DisplayName("findOne: null 항목을 찾아도 예외 없이 같은 결과를 반환한다")
        void findOne() {
            List<String> results = new ArrayList<>();
            for (int i = 0; i < RANDOM_REPEAT; i++) {
                results.add(CollectionUtil.findOne(Arrays.asList("a", null), item -> item == null));
                results.add(CollectionUtil.findOne(List.of("apple", "banana"), item -> item.startsWith("b")));
            }

            assertAll(
                    () -> assertEquals(2, results.stream().distinct().count(), "null과 banana 두 결과만 나온다"),
                    () -> assertTrue(results.contains(null)),
                    () -> assertTrue(results.contains("banana")));
        }

        @Test
        @DisplayName("indexing: 같은 색인 맵을 반환한다")
        void indexing() {
            List<Map<String, String>> results = new ArrayList<>();
            for (int i = 0; i < RANDOM_REPEAT; i++) {
                results.add(CollectionUtil.indexing(List.of("foo", "bar", "baz"), item -> item.substring(0, 1)));
            }

            assertAll(
                    () -> assertEquals(1, results.stream().distinct().count(), "항상 같은 결과여야 한다"),
                    () -> assertEquals(Map.of("f", "foo", "b", "bar"), results.get(0)));
        }

        @Test
        @DisplayName("grouping: 같은 분류 맵을 반환한다")
        void grouping() {
            List<Map<String, List<String>>> results = new ArrayList<>();
            for (int i = 0; i < RANDOM_REPEAT; i++) {
                results.add(CollectionUtil.grouping(List.of("apple", "avocado", "banana"), item -> item.substring(0, 1)));
            }

            assertAll(
                    () -> assertEquals(1, results.stream().distinct().count(), "항상 같은 결과여야 한다"),
                    () -> assertEquals(Map.of("a", List.of("apple", "avocado"), "b", List.of("banana")), results.get(0)));
        }

        @Test
        @DisplayName("asMap(items): 같은 맵을 반환한다")
        void asMapItems() {
            List<Map<Object, Object>> results = new ArrayList<>();
            for (int i = 0; i < RANDOM_REPEAT; i++) {
                results.add(CollectionUtil.asMap("foo", 42, "bar", 43));
            }

            assertAll(
                    () -> assertEquals(1, results.stream().distinct().count(), "항상 같은 결과여야 한다"),
                    () -> assertEquals(Map.of("foo", 42, "bar", 43), results.get(0)));
        }

        @Test
        @DisplayName("asMap(entries): 같은 맵을 반환한다")
        void asMapEntries() {
            List<Map.Entry<String, Integer>> entries = List.of(Map.entry("foo", 42), Map.entry("bar", 43));
            List<Map<String, Integer>> results = new ArrayList<>();
            for (int i = 0; i < RANDOM_REPEAT; i++) {
                results.add(CollectionUtil.asMap(entries));
            }

            assertAll(
                    () -> assertEquals(1, results.stream().distinct().count(), "항상 같은 결과여야 한다"),
                    () -> assertEquals(Map.of("foo", 42, "bar", 43), results.get(0)));
        }
    }
}
