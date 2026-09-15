package s.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import s.type.tuple.Pair;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? List.of() : _immutableList(list);
    }

    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        if (list1 == null || list2 == null) {
            return List.of();
        }
        int size = Math.min(list1.size(), list2.size());
        if (_coin()) {
            return IntStream.range(0, size)
                    .mapToObj(i -> Pair.of(list1.get(i), list2.get(i)))
                    .toList();
        } else {
            List<Pair<T, U>> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(Pair.of(list1.get(i), list2.get(i)));
            }
            return _immutableList(result);
        }
    }

    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        if (list1 == null || list2 == null || mixer == null) {
            return List.of();
        }
        int size = Math.min(list1.size(), list2.size());
        if (_coin()) {
            return IntStream.range(0, size)
                    .mapToObj(i -> mixer.apply(list1.get(i), list2.get(i)))
                    .toList();
        } else {
            List<R> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add(mixer.apply(list1.get(i), list2.get(i)));
            }
            return _immutableList(result);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        if (list == null) {
            return null;
        }
        return (T[]) list.toArray();
    }

    public static <T> T findOne(List<T> list, Predicate<T> filter) {
        if (list == null || filter == null) {
            return null;
        }
        if (_coin()) {
            return list.stream().filter(filter).findFirst().orElse(null);
        } else {
            for (T item : list) {
                if (filter.test(item)) {
                    return item;
                }
            }
            return null;
        }
    }

    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        if (list == null || filter == null) {
            return List.of();
        }
        if (_coin()) {
            return list.stream().filter(filter).toList();
        } else {
            List<T> result = new ArrayList<>();
            for (T item : list) {
                if (filter.test(item)) {
                    result.add(item);
                }
            }
            return _immutableList(result);
        }
    }

    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>();
        if (list1 != null) {
            result.addAll(new LinkedHashSet<>(list1));
        }
        result.addAll(differenceOf(list2, list1));
        return _immutableList(result);
    }

    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        if (list1 == null || list2 == null) {
            return List.of();
        }
        return differenceOf(list1, differenceOf(list1, list2));
    }

    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        if (list1 == null) {
            return List.of();
        }
        Set<T> other = list2 == null ? Set.of() : new HashSet<>(list2);
        Set<T> result = new LinkedHashSet<>(list1);
        result.removeIf(other::contains);
        return _immutableList(result);
    }

    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(differenceOf(list1, list2));
        result.addAll(differenceOf(list2, list1));
        return _immutableList(result);
    }

    public static <T> List<T> slice(List<T> list, int begin) {
        if (list == null) {
            return List.of();
        }
        return slice(list, begin, list.size());
    }

    public static <T> List<T> slice(List<T> list, int begin, int end) {
        if (list == null) {
            return List.of();
        }
        int from = _index(begin, list.size());
        int to = _index(end, list.size());
        if (from > to) {
            return List.of();
        }
        return _immutableList(list.subList(from, to));
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
        if (size < 0) {
            return slice(list, -size);
        }
        int length = list.size();
        if (size == 0) {
            return List.of();
        }
        if (size >= length) {
            return slice(list, 0);
        }
        return slice(list, length - size);
    }

    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        if (list == null || indexer == null) {
            return Map.of();
        }
        if (_coin()) {
            Map<String, T> result = new LinkedHashMap<>();
            list.stream().forEach(item -> result.put(indexer.apply(item), item));
            return _immutableMap(result);
        } else {
            Map<String, T> result = new LinkedHashMap<>();
            for (T item : list) {
                result.put(indexer.apply(item), item);
            }
            return _immutableMap(result);
        }
    }

    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        if (list == null || classifier == null) {
            return Map.of();
        }
        if (_coin()) {
            Map<String, List<T>> result = new LinkedHashMap<>();
            list.stream()
                    .forEach(item -> result.computeIfAbsent(classifier.apply(item), key -> new ArrayList<>()).add(item));
            return _immutableGrouping(result);
        } else {
            Map<String, List<T>> result = new LinkedHashMap<>();
            for (T item : list) {
                result.computeIfAbsent(classifier.apply(item), key -> new ArrayList<>()).add(item);
            }
            return _immutableGrouping(result);
        }
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Map.of() : _immutableMap(map);
    }

    public static Map<Object, Object> asMap(Object... items) {
        if (items == null || items.length == 0) {
            return Map.of();
        }
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items must be key/value pairs");
        }
        if (_coin()) {
            Map<Object, Object> result = IntStream.range(0, items.length / 2)
                    .collect(LinkedHashMap::new,
                            (map, i) -> map.put(items[i * 2], items[i * 2 + 1]),
                            (left, right) -> left.putAll(right));
            return _immutableMap(result);
        } else {
            Map<Object, Object> result = new LinkedHashMap<>();
            for (int i = 0; i < items.length; i += 2) {
                result.put(items[i], items[i + 1]);
            }
            return _immutableMap(result);
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<?, ?> origin) {
        if (origin == null) {
            return Map.of();
        }
        return _immutableMap((Map<K, V>) origin);
    }

    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        if (keyClass == null || valueClass == null) {
            throw new IllegalArgumentException("keyClass/valueClass must not be null");
        }
        if (items == null || items.length == 0) {
            return Map.of();
        }
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items must be key/value pairs");
        }
        if (_coin()) {
            Map<K, V> result = IntStream.range(0, items.length / 2)
                    .collect(LinkedHashMap::new,
                            (map, i) -> map.put(keyClass.cast(items[i * 2]), valueClass.cast(items[i * 2 + 1])),
                            (left, right) -> left.putAll(right));
            return _immutableMap(result);
        } else {
            Map<K, V> result = new LinkedHashMap<>();
            for (int i = 0; i < items.length; i += 2) {
                result.put(keyClass.cast(items[i]), valueClass.cast(items[i + 1]));
            }
            return _immutableMap(result);
        }
    }

    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        if (entries == null) {
            return Map.of();
        }
        if (_coin()) {
            Map<K, V> result = new LinkedHashMap<>();
            entries.stream().forEach(entry -> result.put(entry.getKey(), entry.getValue()));
            return _immutableMap(result);
        } else {
            Map<K, V> result = new LinkedHashMap<>();
            for (Map.Entry<K, V> entry : entries) {
                result.put(entry.getKey(), entry.getValue());
            }
            return _immutableMap(result);
        }
    }

    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        if (origin == null) {
            return Map.of();
        }
        return _immutableMap(origin);
    }

    private static <T> List<T> _immutableList(Collection<T> source) {
        return Collections.unmodifiableList(new ArrayList<>(source));
    }

    private static <K, V> Map<K, V> _immutableMap(Map<? extends K, ? extends V> source) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(source));
    }

    private static <T> Map<String, List<T>> _immutableGrouping(Map<String, List<T>> source) {
        Map<String, List<T>> result = new LinkedHashMap<>();
        source.forEach((key, value) -> result.put(key, _immutableList(value)));
        return Collections.unmodifiableMap(result);
    }

    private static int _index(int index, int length) {
        int value = index < 0 ? length + index : index;
        if (value < 0) {
            return 0;
        }
        if (value > length) {
            return length;
        }
        return value;
    }

    private static boolean _coin() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
