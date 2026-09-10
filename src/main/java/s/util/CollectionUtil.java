package s.util;

import s.type.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CollectionUtil {

    private CollectionUtil() {
    }

    public static <T> boolean isEmpty(final Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <T> List<T> emptyIfNull(final List<T> list) {
        return list != null ? list : Collections.emptyList();
    }

    public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> map) {
        return map != null ? map : Collections.emptyMap();
    }

    public static <T, U> List<Pair<T, U>> zip(final List<T> list1, final List<U> list2) {
        if (list1 == null || list2 == null) {
            return Collections.emptyList();
        }
        final int size = Math.min(list1.size(), list2.size());
        final List<Pair<T, U>> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(Pair.of(list1.get(i), list2.get(i)));
        }
        return result;
    }

    public static <T, U, R> List<R> zip(final List<T> list1, final List<U> list2, final BiFunction<T, U, R> mixer) {
        if (list1 == null || list2 == null || mixer == null) {
            return Collections.emptyList();
        }
        final int size = Math.min(list1.size(), list2.size());
        final List<R> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(mixer.apply(list1.get(i), list2.get(i)));
        }
        return result;
    }

    public static <T> T[] toArray(final List<T> list, final T[] array) {
        if (list == null) {
            return array;
        }
        return list.toArray(array);
    }

    public static <T> T[] toArray(final List<T> list) {
        if (list == null) {
            return (T[]) new Object[0];
        }
        return list.toArray((T[]) new Object[list.size()]);
    }

    public static <T> T findOne(final List<T> list, final Predicate<T> filter) {
        if (list == null || filter == null) {
            return null;
        }
        for (final T item : list) {
            if (filter.test(item)) {
                return item;
            }
        }
        return null;
    }

    public static <T> List<T> findAll(final List<T> list, final Predicate<T> filter) {
        if (list == null || filter == null) {
            return Collections.emptyList();
        }
        return list.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static <T> List<T> unionOf(final List<T> list1, final List<T> list2) {
        if (isEmpty(list1)) {
            return emptyIfNull(list2);
        }
        if (isEmpty(list2)) {
            return new ArrayList<>(list1);
        }
        final Set<T> set = new HashSet<>(list1);
        set.addAll(list2);
        return new ArrayList<>(set);
    }

    public static <T> List<T> intersectionOf(final List<T> list1, final List<T> list2) {
        if (isEmpty(list1) || isEmpty(list2)) {
            return Collections.emptyList();
        }
        final Set<T> set2 = new HashSet<>(list2);
        final List<T> result = new ArrayList<>();
        for (final T item : list1) {
            if (set2.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> List<T> differenceOf(final List<T> list1, final List<T> list2) {
        if (isEmpty(list1)) {
            return Collections.emptyList();
        }
        if (isEmpty(list2)) {
            return new ArrayList<>(list1);
        }
        final Set<T> set2 = new HashSet<>(list2);
        final List<T> result = new ArrayList<>();
        for (final T item : list1) {
            if (!set2.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public static <T> List<T> symmetricDifferenceOf(final List<T> list1, final List<T> list2) {
        if (isEmpty(list1)) {
            return emptyIfNull(list2);
        }
        if (isEmpty(list2)) {
            return new ArrayList<>(list1);
        }
        final List<T> diff1 = differenceOf(list1, list2);
        final List<T> diff2 = differenceOf(list2, list1);
        final List<T> result = new ArrayList<>(diff1.size() + diff2.size());
        result.addAll(diff1);
        result.addAll(diff2);
        return result;
    }

    public static <T> List<T> slice(final List<T> list, final int begin) {
        if (list == null) {
            return Collections.emptyList();
        }
        if (begin >= list.size()) {
            return Collections.emptyList();
        }
        int b = begin;
        if (b < 0) {
            b = list.size() + b;
            if (b < 0) {
                b = 0;
            }
        }
        return list.subList(b, list.size());
    }

    public static <T> List<T> slice(final List<T> list, final int begin, final int end) {
        if (list == null) {
            return Collections.emptyList();
        }
        int b = begin;
        int e = end;
        final int size = list.size();
        if (b < 0) {
            b = size + b;
            if (b < 0) {
                b = 0;
            }
        }
        if (e < 0) {
            e = size + e;
        }
        if (b >= size) {
            return Collections.emptyList();
        }
        if (e > size) {
            e = size;
        }
        if (b >= e) {
            return Collections.emptyList();
        }
        return list.subList(b, e);
    }

    public static <T> List<T> head(final List<T> list, final int size) {
        if (list == null || size <= 0) {
            return Collections.emptyList();
        }
        if (size >= list.size()) {
            return new ArrayList<>(list);
        }
        return list.subList(0, size);
    }

    public static <T> List<T> tail(final List<T> list, final int size) {
        if (list == null || size <= 0) {
            return Collections.emptyList();
        }
        if (size >= list.size()) {
            return new ArrayList<>(list);
        }
        return list.subList(list.size() - size, list.size());
    }

    public static <T> Map<String, T> indexing(final List<T> list, final Function<T, String> indexer) {
        if (list == null || indexer == null) {
            return Collections.emptyMap();
        }
        final Map<String, T> result = new HashMap<>();
        for (final T item : list) {
            final String key = indexer.apply(item);
            if (key != null) {
                result.put(key, item);
            }
        }
        return result;
    }

    public static <T> Map<String, List<T>> grouping(final List<T> list, final Function<T, String> classifier) {
        if (list == null || classifier == null) {
            return Collections.emptyMap();
        }
        return list.stream()
                .collect(Collectors.groupingBy(classifier));
    }

    @SafeVarargs
    public static Map<Object, Object> asMap(final Object... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyMap();
        }
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("Number of items must be even (key, value pairs)");
        }
        final Map<Object, Object> result = new HashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            result.put(items[i], items[i + 1]);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(final Map<Object, Object> origin) {
        if (origin == null) {
            return Collections.emptyMap();
        }
        return (Map<K, V>) origin;
    }

    public static <K, V> Map<K, V> asMap(final Class<K> keyClass, final Class<V> valueClass, final Object... items) {
        if (items == null || items.length == 0) {
            return Collections.emptyMap();
        }
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("Number of items must be even (key, value pairs)");
        }
        final Map<K, V> result = new HashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            final K key = keyClass.cast(items[i]);
            final V value = valueClass.cast(items[i + 1]);
            result.put(key, value);
        }
        return result;
    }

    public static <K, V> Map<K, V> asMap(final List<Map.Entry<K, V>> entries) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<K, V> result = new HashMap<>();
        for (final Map.Entry<K, V> entry : entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <K, V> Map<K, V> copyOf(final Map<K, V> origin) {
        if (origin == null) {
            return Collections.emptyMap();
        }
        return new HashMap<>(origin);
    }
}