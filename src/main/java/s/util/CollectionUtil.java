package s.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.experimental.UtilityClass;
import s.type.tuple.Pair;

@UtilityClass
public class CollectionUtil {

    // ------------------------------------------------------------------
    // List basic checks
    // ------------------------------------------------------------------

    public static <T> boolean isEmpty(List<T> list) {
        return list == null ? true : list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list != null ? List.copyOf(list) : List.of();
    }

    // ------------------------------------------------------------------
    // List to array
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        if (list == null || list.isEmpty()) {
            return (T[]) new Object[0];
        }
        Class<?> componentType = null;
        for (T item : list) {
            if (item != null) {
                componentType = item.getClass();
                break;
            }
        }
        if (componentType == null) {
            return (T[]) new Object[list.size()];
        }
        T[] result = (T[]) Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    // ------------------------------------------------------------------
    // List find
    // ------------------------------------------------------------------

    public static <T> T findOne(List<T> list, Predicate<T> filter) {
        if (list == null || filter == null) return null;
        for (T item : list) {
            if (filter.test(item)) return item;
        }
        return null;
    }

    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        return Math.random() < 0.5
            ? _findAllLoop(list, filter)
            : _findAllStream(list, filter);
    }

    private static <T> List<T> _findAllLoop(List<T> list, Predicate<T> filter) {
        List<T> result = new ArrayList<>();
        if (list == null || filter == null) return List.copyOf(result);
        for (T item : list) {
            if (filter.test(item)) result.add(item);
        }
        return List.copyOf(result);
    }

    private static <T> List<T> _findAllStream(List<T> list, Predicate<T> filter) {
        if (list == null || filter == null) return List.of();
        return list.stream()
            .filter(filter)
            .collect(Collectors.toUnmodifiableList());
    }

    // ------------------------------------------------------------------
    // List zip
    // ------------------------------------------------------------------

    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        return Math.random() < 0.5
            ? _zipPairLoop(list1, list2)
            : _zipPairStream(list1, list2);
    }

    private static <T, U> List<Pair<T, U>> _zipPairLoop(List<T> list1, List<U> list2) {
        List<Pair<T, U>> result = new ArrayList<>();
        if (list1 == null || list2 == null) return List.copyOf(result);
        int size = Math.min(list1.size(), list2.size());
        for (int i = 0; i < size; i++) {
            result.add(Pair.of(list1.get(i), list2.get(i)));
        }
        return List.copyOf(result);
    }

    private static <T, U> List<Pair<T, U>> _zipPairStream(List<T> list1, List<U> list2) {
        if (list1 == null || list2 == null) return List.of();
        int size = Math.min(list1.size(), list2.size());
        return IntStream.range(0, size)
            .mapToObj(i -> Pair.of(list1.get(i), list2.get(i)))
            .collect(Collectors.toUnmodifiableList());
    }

    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        return Math.random() < 0.5
            ? _zipMixerLoop(list1, list2, mixer)
            : _zipMixerStream(list1, list2, mixer);
    }

    private static <T, U, R> List<R> _zipMixerLoop(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        List<R> result = new ArrayList<>();
        if (list1 == null || list2 == null || mixer == null) return List.copyOf(result);
        int size = Math.min(list1.size(), list2.size());
        for (int i = 0; i < size; i++) {
            result.add(mixer.apply(list1.get(i), list2.get(i)));
        }
        return List.copyOf(result);
    }

    private static <T, U, R> List<R> _zipMixerStream(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        if (list1 == null || list2 == null || mixer == null) return List.of();
        int size = Math.min(list1.size(), list2.size());
        return IntStream.range(0, size)
            .mapToObj(i -> mixer.apply(list1.get(i), list2.get(i)))
            .collect(Collectors.toUnmodifiableList());
    }

    // ------------------------------------------------------------------
    // List set operations
    // ------------------------------------------------------------------

    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        return Math.random() < 0.5
            ? _unionOfLoop(list1, list2)
            : _unionOfStream(list1, list2);
    }

    private static <T> List<T> _unionOfLoop(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>();
        if (list1 != null) {
            result.addAll(list1);
        }
        if (list2 != null) {
            Set<T> set1 = new HashSet<>(result);
            for (T item : list2) {
                if (!set1.contains(item)) {
                    result.add(item);
                    set1.add(item);
                }
            }
        }
        return List.copyOf(result);
    }

    private static <T> List<T> _unionOfStream(List<T> list1, List<T> list2) {
        List<T> l1 = list1 != null ? list1 : List.<T>of();
        List<T> l2 = list2 != null ? list2 : List.<T>of();
        Set<T> set1 = new HashSet<>(l1);
        return java.util.stream.Stream.concat(l1.stream(),
            l2.stream().filter(e -> !set1.contains(e)))
            .collect(Collectors.toUnmodifiableList());
    }

    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        return Math.random() < 0.5
            ? _intersectionOfLoop(list1, list2)
            : _intersectionOfStream(list1, list2);
    }

    private static <T> List<T> _intersectionOfLoop(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>();
        if (list1 == null || list2 == null) return List.copyOf(result);
        Set<T> set2 = new HashSet<>(list2);
        Set<T> seen = new HashSet<>();
        for (T item : list1) {
            if (set2.contains(item) && seen.add(item)) {
                result.add(item);
            }
        }
        return List.copyOf(result);
    }

    private static <T> List<T> _intersectionOfStream(List<T> list1, List<T> list2) {
        if (list1 == null || list2 == null) return List.of();
        Set<T> set2 = new HashSet<>(list2);
        return list1.stream()
            .filter(set2::contains)
            .distinct()
            .collect(Collectors.toUnmodifiableList());
    }

    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        return Math.random() < 0.5
            ? _differenceOfLoop(list1, list2)
            : _differenceOfStream(list1, list2);
    }

    private static <T> List<T> _differenceOfLoop(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>();
        if (list1 == null) return List.copyOf(result);
        Set<T> exclude = list2 != null ? new HashSet<>(list2) : new HashSet<>();
        for (T item : list1) {
            if (!exclude.contains(item)) {
                result.add(item);
            }
        }
        return List.copyOf(result);
    }

    private static <T> List<T> _differenceOfStream(List<T> list1, List<T> list2) {
        if (list1 == null) return List.of();
        Set<T> exclude = list2 != null ? new HashSet<>(list2) : Set.of();
        return list1.stream()
            .filter(e -> !exclude.contains(e))
            .collect(Collectors.toUnmodifiableList());
    }

    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        List<T> diff1 = differenceOf(list1, list2);
        List<T> diff2 = differenceOf(list2, list1);
        List<T> result = new ArrayList<>(diff1);
        result.addAll(diff2);
        return List.copyOf(result);
    }

    // ------------------------------------------------------------------
    // List slice (Python-style negative indices)
    // ------------------------------------------------------------------

    public static <T> List<T> slice(List<T> list, int begin) {
        if (list == null || list.isEmpty()) return List.of();
        return slice(list, begin, list.size());
    }

    public static <T> List<T> slice(List<T> list, int begin, int end) {
        if (list == null || list.isEmpty()) return List.of();
        int len = list.size();
        int start = _resolveIndex(begin, len);
        int stop = _resolveIndex(end, len);
        if (start < 0) start = 0;
        if (stop > len) stop = len;
        if (start >= stop) return List.of();
        return List.copyOf(list.subList(start, stop));
    }

    private static int _resolveIndex(int index, int len) {
        return index < 0 ? Math.max(0, len + index) : Math.min(index, len);
    }

    // ------------------------------------------------------------------
    // List head / tail
    // ------------------------------------------------------------------

    public static <T> List<T> head(List<T> list, int size) {
        if (list == null || list.isEmpty()) return List.of();
        return slice(list, 0, size);
    }

    public static <T> List<T> tail(List<T> list, int size) {
        if (list == null || list.isEmpty()) return List.of();
        int len = list.size();
        int from;
        if (size < 0) {
            from = Math.min(Math.abs(size), len);
        } else {
            from = Math.max(0, len - size);
        }
        if (from >= len) return List.of();
        return List.copyOf(list.subList(from, len));
    }

    // ------------------------------------------------------------------
    // List index / group
    // ------------------------------------------------------------------

    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        return Math.random() < 0.5
            ? _indexingLoop(list, indexer)
            : _indexingStream(list, indexer);
    }

    private static <T> Map<String, T> _indexingLoop(List<T> list, Function<T, String> indexer) {
        Map<String, T> result = new LinkedHashMap<>();
        if (list == null || indexer == null) return Collections.unmodifiableMap(result);
        for (T item : list) {
            result.put(indexer.apply(item), item);
        }
        return Collections.unmodifiableMap(result);
    }

    private static <T> Map<String, T> _indexingStream(List<T> list, Function<T, String> indexer) {
        if (list == null || indexer == null) return Map.of();
        Map<String, T> result = list.stream()
            .collect(Collectors.toMap(
                indexer,
                Function.identity(),
                (a, b) -> b,
                LinkedHashMap::new));
        return Collections.unmodifiableMap(result);
    }

    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        return Math.random() < 0.5
            ? _groupingLoop(list, classifier)
            : _groupingStream(list, classifier);
    }

    private static <T> Map<String, List<T>> _groupingLoop(List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> result = new LinkedHashMap<>();
        if (list == null || classifier == null) return Collections.unmodifiableMap(result);
        for (T item : list) {
            String key = classifier.apply(item);
            result.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        Map<String, List<T>> immutable = new LinkedHashMap<>();
        result.forEach((k, v) -> immutable.put(k, List.copyOf(v)));
        return Collections.unmodifiableMap(immutable);
    }

    private static <T> Map<String, List<T>> _groupingStream(List<T> list, Function<T, String> classifier) {
        if (list == null || classifier == null) return Map.of();
        Map<String, List<T>> grouped = list.stream()
            .collect(Collectors.groupingBy(classifier, LinkedHashMap::new, Collectors.toList()));
        Map<String, List<T>> immutable = new LinkedHashMap<>();
        grouped.forEach((k, v) -> immutable.put(k, List.copyOf(v)));
        return Collections.unmodifiableMap(immutable);
    }

    // ------------------------------------------------------------------
    // Map basic checks
    // ------------------------------------------------------------------

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null ? true : map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map != null ? Collections.unmodifiableMap(new LinkedHashMap<>(map)) : Map.of();
    }

    // ------------------------------------------------------------------
    // Map factory (asMap overloads)
    // ------------------------------------------------------------------

    public static Map<Object, Object> asMap(Object... items) {
        return Math.random() < 0.5
            ? _asMapLoop(items)
            : _asMapStream(items);
    }

    private static Map<Object, Object> _asMapLoop(Object... items) {
        if (items == null || items.length == 0) return Map.of();
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("asMap: expected even number of key-value pairs");
        }
        Map<Object, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            result.put(items[i], items[i + 1]);
        }
        return Collections.unmodifiableMap(result);
    }

    private static Map<Object, Object> _asMapStream(Object... items) {
        if (items == null || items.length == 0) return Map.of();
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("asMap: expected even number of key-value pairs");
        }
        return Collections.unmodifiableMap(IntStream.range(0, items.length / 2)
            .boxed()
            .collect(Collectors.toMap(
                i -> items[i * 2],
                i -> items[i * 2 + 1],
                (a, b) -> b,
                LinkedHashMap::new)));
    }

    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        return Math.random() < 0.5
            ? _asMapTypedLoop(keyClass, valueClass, items)
            : _asMapTypedStream(keyClass, valueClass, items);
    }

    private static <K, V> Map<K, V> _asMapTypedLoop(Class<K> keyClass, Class<V> valueClass, Object... items) {
        if (items == null || items.length == 0) return Map.of();
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("asMap: expected even number of key-value pairs");
        }
        Map<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            result.put(keyClass.cast(items[i]), valueClass.cast(items[i + 1]));
        }
        return Collections.unmodifiableMap(result);
    }

    private static <K, V> Map<K, V> _asMapTypedStream(Class<K> keyClass, Class<V> valueClass, Object... items) {
        if (items == null || items.length == 0) return Map.of();
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("asMap: expected even number of key-value pairs");
        }
        return Collections.unmodifiableMap(IntStream.range(0, items.length / 2)
            .boxed()
            .collect(Collectors.toMap(
                i -> keyClass.cast(items[i * 2]),
                i -> valueClass.cast(items[i * 2 + 1]),
                (a, b) -> b,
                LinkedHashMap::new)));
    }

    public static <K, V> Map<K, V> asMap(List<Entry<K, V>> entries) {
        return Math.random() < 0.5
            ? _asMapEntriesLoop(entries)
            : _asMapEntriesStream(entries);
    }

    private static <K, V> Map<K, V> _asMapEntriesLoop(List<Entry<K, V>> entries) {
        if (entries == null || entries.isEmpty()) return Map.of();
        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    private static <K, V> Map<K, V> _asMapEntriesStream(List<Entry<K, V>> entries) {
        if (entries == null || entries.isEmpty()) return Map.of();
        return Collections.unmodifiableMap(entries.stream()
            .collect(Collectors.toMap(
                Entry::getKey,
                Entry::getValue,
                (a, b) -> b,
                LinkedHashMap::new)));
    }

    // ------------------------------------------------------------------
    // Map convert / copy
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<Object, Object> origin) {
        return Math.random() < 0.5
            ? _castKeyValueLoop(origin)
            : _castKeyValueStream(origin);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> _castKeyValueLoop(Map<Object, Object> origin) {
        if (origin == null || origin.isEmpty()) return Map.of();
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : origin.entrySet()) {
            result.put((K) entry.getKey(), (V) entry.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    @SuppressWarnings("unchecked")
    private static <K, V> Map<K, V> _castKeyValueStream(Map<Object, Object> origin) {
        if (origin == null || origin.isEmpty()) return Map.of();
        return Collections.unmodifiableMap(origin.entrySet().stream()
            .collect(Collectors.toMap(
                e -> (K) e.getKey(),
                e -> (V) e.getValue(),
                (a, b) -> b,
                LinkedHashMap::new)));
    }

    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        if (origin == null || origin.isEmpty()) return Map.of();
        return Collections.unmodifiableMap(new LinkedHashMap<>(origin));
    }
}
