package s.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import s.type.tuple.Pair;

/**
 * 컬렉션 유틸리티.
 *
 * <p>null 리스트·맵은 빈 컬렉션으로 취급하고, 처리할 수 없는 입력은 예외 없이 빈 컬렉션을 돌려줘요.
 * 새로 만들어 돌려주는 리스트와 맵은 수정할 수 없고, 맵은 삽입 순서를 유지해요.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtil {

    // ---- List ----

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? List.of() : list;
    }

    /** 같은 위치끼리 짝지어요. 길이가 다르면 짧은 쪽에 맞춰요. */
    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        return zip(list1, list2, Pair::of);
    }

    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        if (isEmpty(list1) || isEmpty(list2) || mixer == null) {
            return List.of();
        }
        int size = Math.min(list1.size(), list2.size());
        return IntStream.range(0, size)
                .mapToObj(i -> mixer.apply(list1.get(i), list2.get(i)))
                .toList();
    }

    /**
     * 원소들의 공통 상위 클래스를 배열 타입으로 쓰는 배열. 비었거나 원소가 전부 null이면 {@code Object[]}라서
     * {@code String[]} 같은 변수에 받으려면 {@code list.toArray(new String[0])}을 써야 해요.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        List<T> items = emptyIfNull(list);
        T[] array = (T[]) Array.newInstance(_componentTypeOf(items), items.size());
        return items.toArray(array);
    }

    private static Class<?> _componentTypeOf(List<?> list) {
        List<?> nonNulls = list.stream().filter(Objects::nonNull).toList();
        if (nonNulls.isEmpty()) {
            return Object.class;
        }
        return Stream.<Class<?>>iterate(nonNulls.get(0).getClass(), c -> c != null, c -> c.getSuperclass())
                .filter(c -> nonNulls.stream().allMatch(c::isInstance))
                .findFirst()
                .orElse(Object.class);
    }

    /** filter를 통과하는 첫 원소. 없으면 null. */
    public static <T> T findOne(List<T> list, Predicate<T> filter) {
        if (isEmpty(list) || filter == null) {
            return null;
        }
        List<T> found = list.stream().filter(filter).limit(1).toList();
        return found.isEmpty() ? null : found.get(0);
    }

    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        if (isEmpty(list) || filter == null) {
            return List.of();
        }
        return list.stream().filter(filter).toList();
    }

    // ---- 집합 연산 (중복은 그대로 두고, 상대 목록에 들어 있는지만 봐요) ----

    /** list1 + (list2 - list1) */
    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        return Stream.concat(emptyIfNull(list1).stream(), differenceOf(list2, list1).stream())
                .toList();
    }

    /** list1 - (list1 - list2) */
    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        return differenceOf(list1, differenceOf(list1, list2));
    }

    /** list1 - list2 */
    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return List.of();
        }
        Set<T> excluded = new HashSet<>(emptyIfNull(list2));
        return list1.stream().filter(e -> !excluded.contains(e)).toList();
    }

    /** (list1 - list2) + (list2 - list1) */
    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        return Stream.concat(differenceOf(list1, list2).stream(), differenceOf(list2, list1).stream())
                .toList();
    }

    // ---- 부분 리스트 ----

    /** begin부터 끝까지. */
    public static <T> List<T> slice(List<T> list, int begin) {
        if (list == null) {
            return List.of();
        }
        return slice(list, begin, list.size());
    }

    /**
     * begin(포함)부터 end(불포함)까지. 음수 인덱스는 뒤에서부터 센 위치로 해석하고(-1은 마지막 원소),
     * 길이의 음수값보다 작으면 0, 길이보다 크면 길이로 보정해요. 보정 후 begin이 end 이상이면 빈 리스트.
     */
    public static <T> List<T> slice(List<T> list, int begin, int end) {
        if (list == null) {
            return List.of();
        }
        int from = _index(begin, list.size());
        int to = _index(end, list.size());
        if (from >= to) {
            return List.of();
        }
        return list.subList(from, to).stream().toList();
    }

    private static int _index(int i, int len) {
        return i < 0 ? Math.max(0, len + i) : Math.min(i, len);
    }

    /** 앞에서 size개. 음수 size는 뒤에서 -size개를 뺀 앞부분이고, 길이보다 크면 전체. */
    public static <T> List<T> head(List<T> list, int size) {
        if (list == null) {
            return List.of();
        }
        return slice(list, 0, size);
    }

    /** 뒤에서 size개. 음수 size는 앞에서 -size개를 뺀 나머지이고, 길이보다 크면 전체. */
    public static <T> List<T> tail(List<T> list, int size) {
        if (list == null) {
            return List.of();
        }
        if (size >= 0) {
            return slice(list, Math.max(0, list.size() - size));
        }
        return slice(list, (int) Math.min(-(long) size, list.size()));
    }

    // ---- 색인·분류 ----

    /** 색인이 같은 항목이 여럿이면 뒤의 것이 이겨요. */
    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        if (isEmpty(list) || indexer == null) {
            return Map.of();
        }
        Map<String, T> map = new LinkedHashMap<>();
        list.forEach(e -> map.put(indexer.apply(e), e));
        return Collections.unmodifiableMap(map);
    }

    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        if (isEmpty(list) || classifier == null) {
            return Map.of();
        }
        Map<String, List<T>> map = new LinkedHashMap<>();
        list.forEach(e -> map.computeIfAbsent(classifier.apply(e), k -> new ArrayList<>()).add(e));
        map.replaceAll((k, v) -> Collections.unmodifiableList(v));
        return Collections.unmodifiableMap(map);
    }

    // ---- Map ----

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Map.of() : map;
    }

    /** key1, value1, key2, value2, ... 순서로 받아요. 개수가 홀수이면 빈 맵. 같은 키는 뒤의 것이 이겨요. */
    public static Map<Object, Object> asMap(Object... items) {
        return asMap(Object.class, Object.class, items);
    }

    /** 키와 값을 각각 keyClass, valueClass로 검사해요(맞지 않으면 {@link ClassCastException}). */
    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        if (keyClass == null || valueClass == null || items == null || items.length % 2 != 0) {
            return Map.of();
        }
        Map<K, V> map = new LinkedHashMap<>();
        IntStream.range(0, items.length / 2)
                .forEach(i -> map.put(keyClass.cast(items[2 * i]), valueClass.cast(items[2 * i + 1])));
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        if (isEmpty(entries)) {
            return Map.of();
        }
        Map<K, V> map = new LinkedHashMap<>();
        entries.stream().filter(Objects::nonNull).forEach(e -> map.put(e.getKey(), e.getValue()));
        return Collections.unmodifiableMap(map);
    }

    /** 복사 없이 타입 인자만 바꿔요. 실제 키·값 타입이 맞는지는 검사하지 않아요. */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<?, ?> origin) {
        if (origin == null) {
            return Map.of();
        }
        return (Map<K, V>) origin;
    }

    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        if (isEmpty(origin)) {
            return Map.of();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(origin));
    }
}
