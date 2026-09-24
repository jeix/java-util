package s.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import s.type.tuple.Pair;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    // List operations

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        int size = Math.min(list1.size(), list2.size());
        List<Pair<T, U>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(Pair.of(list1.get(i), list2.get(i)));
        }
        return result;
    }

    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        int size = Math.min(list1.size(), list2.size());
        List<R> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(mixer.apply(list1.get(i), list2.get(i)));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        if (list.isEmpty()) {
            return (T[]) new Object[0];
        }
        T first = list.get(0);
        if (first == null) {
            return (T[]) new Object[0];
        }
        Class<?> componentType = first.getClass();
        T[] array = (T[]) java.lang.reflect.Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    public static <T> T findOne(List<T> list, Predicate<T> filter) {
        return list.stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        return list.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(list1);
        for (T item : list2) {
            if (!list1.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        return list1.stream()
                .filter(list2::contains)
                .collect(Collectors.toList());
    }

    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        return list1.stream()
                .filter(item -> !list2.contains(item))
                .collect(Collectors.toList());
    }

    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>();
        result.addAll(differenceOf(list1, list2));
        result.addAll(differenceOf(list2, list1));
        return result;
    }

    public static <T> List<T> slice(List<T> list, int begin) {
        return slice(list, begin, list.size());
    }

    public static <T> List<T> slice(List<T> list, int begin, int end) {
        return list.subList(begin, end);
    }

    public static <T> List<T> head(List<T> list, int size) {
        if (size > list.size()) {
            throw new IllegalArgumentException("size must not be greater than list size");
        }
        return list.subList(0, size);
    }

    public static <T> List<T> tail(List<T> list, int size) {
        if (size > list.size()) {
            throw new IllegalArgumentException("size must not be greater than list size");
        }
        return list.subList(list.size() - size, list.size());
    }

    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        Map<String, T> result = new LinkedHashMap<>();
        for (T item : list) {
            result.put(indexer.apply(item), item);
        }
        return result;
    }

    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        return list.stream()
                .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));
    }

    // Map operations

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : map;
    }

    public static Map<Object, Object> asMap(Object... items) {
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items length must be even");
        }
        Map<Object, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            map.put(items[i], items[i + 1]);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<Object, Object> origin) {
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : origin.entrySet()) {
            result.put((K) entry.getKey(), (V) entry.getValue());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items length must be even");
        }
        Map<K, V> map = new LinkedHashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            map.put(keyClass.cast(items[i]), valueClass.cast(items[i + 1]));
        }
        return map;
    }

    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        Map<K, V> map = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        return new LinkedHashMap<>(origin);
    }
}
