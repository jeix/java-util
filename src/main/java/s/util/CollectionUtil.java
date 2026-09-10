package s.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import s.type.tuple.Pair;

/** null 컬렉션을 빈 컬렉션으로 취급하는 유틸리티예요. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtil {

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? List.of() : _immutableList(list);
    }

    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        return zip(list1, list2, Pair::of);
    }

    /** 두 리스트 중 짧은 길이만큼 순서대로 묶어요. */
    public static <T, U, R> List<R> zip(
            List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        if (isEmpty(list1) || isEmpty(list2) || mixer == null) {
            return List.of();
        }
        List<R> result = _chooseAlternative()
                ? _zipWithStream(list1, list2, mixer)
                : _zipWithLoop(list1, list2, mixer);
        return _immutableList(result);
    }

    private static <T, U, R> List<R> _zipWithLoop(
            List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        List<R> result = new ArrayList<>();
        Iterator<T> first = list1.iterator();
        Iterator<U> second = list2.iterator();
        while (first.hasNext() && second.hasNext()) {
            result.add(mixer.apply(first.next(), second.next()));
        }
        return result;
    }

    private static <T, U, R> List<R> _zipWithStream(
            List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        return IntStream.range(0, Math.min(list1.size(), list2.size()))
                .mapToObj(index -> mixer.apply(list1.get(index), list2.get(index)))
                .toList();
    }

    /**
     * 첫 non-null 요소의 런타임 클래스로 배열을 만들어요.
     * 타입을 추론할 수 없으면 IllegalArgumentException, 호환되지 않는 요소는 ArrayStoreException이에요.
     * 빈 리스트나 서로 다른 하위 타입에는 Class를 받는 오버로드를 사용해요.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        T first = findOne(list, Objects::nonNull);
        if (first == null) {
            throw new IllegalArgumentException("array component type cannot be inferred; provide a Class");
        }
        return toArray(list, (Class<T>) first.getClass());
    }

    public static <T> T[] toArray(List<T> list, Class<T> componentType) {
        Objects.requireNonNull(componentType, "componentType");
        if (componentType.isPrimitive()) {
            throw new IllegalArgumentException("componentType must be a reference type");
        }
        return emptyIfNull(list).toArray(_newArray(componentType, list == null ? 0 : list.size()));
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] _newArray(Class<T> componentType, int size) {
        return (T[]) Array.newInstance(componentType, size);
    }

    public static <T> T findOne(List<T> list, Predicate<T> filter) {
        if (isEmpty(list) || filter == null) {
            return null;
        }
        return _chooseAlternative()
                ? _findOneWithStream(list, filter)
                : _findOneWithLoop(list, filter);
    }

    private static <T> T _findOneWithLoop(List<T> list, Predicate<T> filter) {
        for (T value : list) {
            if (filter.test(value)) {
                return value;
            }
        }
        return null;
    }

    private static <T> T _findOneWithStream(List<T> list, Predicate<T> filter) {
        return list.stream()
                .map(_Value::new)
                .filter(candidate -> filter.test(candidate.value()))
                .findFirst()
                .map(_Value::value)
                .orElse(null);
    }

    /**
     * Stream.findFirst()는 선택한 요소가 null이면 NullPointerException을 발생시켜요.
     * null 후보를 non-null 객체로 감싸 기존 findOne의 null 처리와 단락 평가를 유지해요.
     */
    private record _Value<T>(T value) {
    }

    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        if (isEmpty(list) || filter == null) {
            return List.of();
        }
        List<T> result = _chooseAlternative()
                ? _findAllWithStream(list, filter)
                : _findAllWithLoop(list, filter);
        return _immutableList(result);
    }

    private static <T> List<T> _findAllWithLoop(List<T> list, Predicate<T> filter) {
        List<T> result = new ArrayList<>();
        for (T value : list) {
            if (filter.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    private static <T> List<T> _findAllWithStream(List<T> list, Predicate<T> filter) {
        return list.stream().filter(filter).toList();
    }

    /** list1 + (list2 - list1). 입력의 순서와 중복을 유지해요. */
    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(emptyIfNull(list1));
        result.addAll(differenceOf(list2, list1));
        return _immutableList(result);
    }

    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        if (isEmpty(list1) || isEmpty(list2)) {
            return List.of();
        }
        Set<T> members = new HashSet<>(list2);
        return findAll(list1, members::contains);
    }

    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return List.of();
        }
        if (isEmpty(list2)) {
            return _immutableList(list1);
        }
        Set<T> members = new HashSet<>(list2);
        return findAll(list1, value -> !members.contains(value));
    }

    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(differenceOf(list1, list2));
        result.addAll(differenceOf(list2, list1));
        return _immutableList(result);
    }

    public static <T> List<T> slice(List<T> list, int begin) {
        return slice(list, begin, list == null ? 0 : list.size());
    }

    /** 음수 인덱스는 뒤에서부터 세고, 변환한 인덱스는 0부터 리스트 길이 사이로 제한해요. */
    public static <T> List<T> slice(List<T> list, int begin, int end) {
        if (list == null) {
            return List.of();
        }
        int normalizedBegin = _normalizeIndex(list.size(), begin);
        int normalizedEnd = _normalizeIndex(list.size(), end);
        if (normalizedEnd < normalizedBegin) {
            return List.of();
        }
        return _immutableList(list.subList(normalizedBegin, normalizedEnd));
    }

    public static <T> List<T> head(List<T> list, int size) {
        if (list == null) {
            return List.of();
        }
        return slice(list, 0, size);
    }

    public static <T> List<T> tail(List<T> list, int size) {
        if (list == null) {
            return List.of();
        }
        long begin = size < 0 ? -(long) size : (long) list.size() - size;
        return slice(list, _clampIndex(list.size(), begin));
    }

    private static int _normalizeIndex(int length, int index) {
        long normalized = index < 0 ? (long) length + index : index;
        return _clampIndex(length, normalized);
    }

    private static int _clampIndex(int length, long index) {
        return (int) Math.min(Math.max(index, 0), length);
    }

    /** 키의 최초 등장 순서를 유지하며 같은 키는 마지막 항목으로 덮어써요. */
    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        if (isEmpty(list) || indexer == null) {
            return Map.of();
        }
        Map<String, T> result = _chooseAlternative()
                ? _indexingWithStream(list, indexer)
                : _indexingWithLoop(list, indexer);
        return _immutableMap(result);
    }

    private static <T> Map<String, T> _indexingWithLoop(
            List<T> list, Function<T, String> indexer) {
        Map<String, T> result = new LinkedHashMap<>();
        for (T value : list) {
            result.put(indexer.apply(value), value);
        }
        return result;
    }

    private static <T> Map<String, T> _indexingWithStream(
            List<T> list, Function<T, String> indexer) {
        return list.stream().collect(
                LinkedHashMap::new,
                (result, value) -> result.put(indexer.apply(value), value),
                Map::putAll);
    }

    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        if (isEmpty(list) || classifier == null) {
            return Map.of();
        }
        Map<String, List<T>> result = _chooseAlternative()
                ? _groupingWithStream(list, classifier)
                : _groupingWithLoop(list, classifier);
        return _immutableGroups(result);
    }

    private static <T> Map<String, List<T>> _groupingWithLoop(
            List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> result = new LinkedHashMap<>();
        for (T value : list) {
            String key = classifier.apply(value);
            result.computeIfAbsent(key, ignored -> new ArrayList<>()).add(value);
        }
        return result;
    }

    private static <T> Map<String, List<T>> _groupingWithStream(
            List<T> list, Function<T, String> classifier) {
        return list.stream().collect(
                LinkedHashMap::new,
                (result, value) -> result
                        .computeIfAbsent(classifier.apply(value), ignored -> new ArrayList<>())
                        .add(value),
                (result, addition) -> addition.forEach((key, values) -> result
                        .computeIfAbsent(key, ignored -> new ArrayList<>())
                        .addAll(values)));
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Map.of() : _immutableMap(map);
    }

    public static Map<Object, Object> asMap(Object... items) {
        return asMap(Object.class, Object.class, items);
    }

    /** 타입 변환 없이 원본 맵을 unchecked cast해요. 실제 타입 보장은 호출자의 책임이에요. */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<?, ?> origin) {
        if (origin == null) {
            return Map.of();
        }
        return _immutableMap((Map<K, V>) origin);
    }

    /** 홀수 인자 수는 IllegalArgumentException, 타입 불일치는 ClassCastException이에요. */
    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        Objects.requireNonNull(keyClass, "keyClass");
        Objects.requireNonNull(valueClass, "valueClass");
        if (items == null || items.length == 0) {
            return Map.of();
        }
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items must contain key/value pairs");
        }
        Map<K, V> result = _chooseAlternative()
                ? _asMapWithStream(keyClass, valueClass, items)
                : _asMapWithLoop(keyClass, valueClass, items);
        return _immutableMap(result);
    }

    private static <K, V> Map<K, V> _asMapWithLoop(
            Class<K> keyClass, Class<V> valueClass, Object[] items) {
        Map<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            result.put(keyClass.cast(items[i]), valueClass.cast(items[i + 1]));
        }
        return result;
    }

    private static <K, V> Map<K, V> _asMapWithStream(
            Class<K> keyClass, Class<V> valueClass, Object[] items) {
        return IntStream.range(0, items.length / 2).collect(
                LinkedHashMap::new,
                (result, index) -> result.put(
                        keyClass.cast(items[index * 2]),
                        valueClass.cast(items[index * 2 + 1])),
                Map::putAll);
    }

    /** null entry는 건너뛰고 중복 키에는 마지막 값을 사용해요. */
    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        if (isEmpty(entries)) {
            return Map.of();
        }
        Map<K, V> result = _chooseAlternative()
                ? _asMapWithStream(entries)
                : _asMapWithLoop(entries);
        return _immutableMap(result);
    }

    private static <K, V> Map<K, V> _asMapWithLoop(List<Map.Entry<K, V>> entries) {
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            if (entry != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private static <K, V> Map<K, V> _asMapWithStream(List<Map.Entry<K, V>> entries) {
        return entries.stream()
                .filter(Objects::nonNull)
                .collect(
                        LinkedHashMap::new,
                        (result, entry) -> result.put(entry.getKey(), entry.getValue()),
                        Map::putAll);
    }

    /** 원본의 순회 순서와 키·값 참조를 유지하는 불변 얕은 복사예요. */
    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        return emptyIfNull(origin);
    }

    private static boolean _chooseAlternative() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    private static <T> List<T> _immutableList(Collection<? extends T> values) {
        return Collections.unmodifiableList(new ArrayList<>(values));
    }

    private static <K, V> Map<K, V> _immutableMap(Map<? extends K, ? extends V> values) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    private static <T> Map<String, List<T>> _immutableGroups(Map<String, List<T>> groups) {
        Map<String, List<T>> result = new LinkedHashMap<>();
        groups.forEach((key, values) -> result.put(key, _immutableList(values)));
        return _immutableMap(result);
    }
}
