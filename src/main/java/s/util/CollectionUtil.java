package s.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import s.type.tuple.Pair;

public final class CollectionUtil {

    private static final Random RANDOM = new Random();

    private CollectionUtil() {
    }

    // List operations

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? Collections.emptyList() : Collections.unmodifiableList(list);
    }

    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        int size = Math.min(list1.size(), list2.size());
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            List<Pair<T, U>> result = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                result.add(Pair.of(list1.get(i), list2.get(i)));
            }
            return Collections.unmodifiableList(result);
        } else {
            // stream implementation
            return Collections.unmodifiableList(IntStream.range(0, size)
                    .mapToObj(i -> Pair.of(list1.get(i), list2.get(i)))
                    .collect(Collectors.toList()));
        }
    }

    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        int size = Math.min(list1.size(), list2.size());
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            List<R> result = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                result.add(mixer.apply(list1.get(i), list2.get(i)));
            }
            return Collections.unmodifiableList(result);
        } else {
            // stream implementation
            return Collections.unmodifiableList(IntStream.range(0, size)
                    .mapToObj(i -> mixer.apply(list1.get(i), list2.get(i)))
                    .collect(Collectors.toList()));
        }
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
        return Collections.unmodifiableList(list.stream()
                .filter(filter)
                .collect(Collectors.toList()));
    }

    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            List<T> result = new ArrayList<>(list1);
            for (T item : list2) {
                if (!list1.contains(item)) {
                    result.add(item);
                }
            }
            return Collections.unmodifiableList(result);
        } else {
            // stream implementation
            List<T> result = new ArrayList<>(list1);
            list2.stream()
                    .filter(item -> !list1.contains(item))
                    .forEach(result::add);
            return Collections.unmodifiableList(result);
        }
    }

    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            List<T> result = new ArrayList<>();
            for (T item : list1) {
                if (list2.contains(item)) {
                    result.add(item);
                }
            }
            return Collections.unmodifiableList(result);
        } else {
            // stream implementation
            return Collections.unmodifiableList(list1.stream()
                    .filter(list2::contains)
                    .collect(Collectors.toList()));
        }
    }

    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            List<T> result = new ArrayList<>();
            for (T item : list1) {
                if (!list2.contains(item)) {
                    result.add(item);
                }
            }
            return Collections.unmodifiableList(result);
        } else {
            // stream implementation
            return Collections.unmodifiableList(list1.stream()
                    .filter(item -> !list2.contains(item))
                    .collect(Collectors.toList()));
        }
    }

    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            List<T> result = new ArrayList<>();
            for (T item : list1) {
                if (!list2.contains(item)) {
                    result.add(item);
                }
            }
            for (T item : list2) {
                if (!list1.contains(item)) {
                    result.add(item);
                }
            }
            return Collections.unmodifiableList(result);
        } else {
            // stream implementation
            List<T> result = new ArrayList<>();
            result.addAll(differenceOf(list1, list2));
            result.addAll(differenceOf(list2, list1));
            return Collections.unmodifiableList(result);
        }
    }

    public static <T> List<T> slice(List<T> list, int begin) {
        return slice(list, begin, list.size());
    }

    public static <T> List<T> slice(List<T> list, int begin, int end) {
        int len = list.size();
        // 음수 인덱스를 역방향 인덱스로 해석
        if (begin < 0) {
            begin = len + begin;
        }
        if (end < 0) {
            end = len + end;
        }
        // 길이의 음수값보다 작으면 0으로
        if (begin < -len) {
            begin = 0;
        }
        if (end < -len) {
            end = 0;
        }
        // 길이보다 큰 인덱스는 길이로
        if (begin > len) {
            begin = len;
        }
        if (end > len) {
            end = len;
        }
        if (begin > end) {
            begin = end;
        }
        return Collections.unmodifiableList(list.subList(begin, end));
    }

    public static <T> List<T> head(List<T> list, int size) {
        int len = list.size();
        // 음수 size를 역방향 인덱스로 해석
        if (size < 0) {
            size = len + size;
        }
        // 길이의 음수값보다 작으면 0으로
        if (size < -len) {
            size = 0;
        }
        // 길이보다 크면 길이로
        if (size > len) {
            size = len;
        }
        return Collections.unmodifiableList(list.subList(0, size));
    }

    public static <T> List<T> tail(List<T> list, int size) {
        int len = list.size();
        // 음수 size를 양수로 바꾸고 앞에서부터 제외할 인덱스로 해석
        if (size < 0) {
            size = len + size; // len - |size|
        }
        // 길이의 음수값보다 작으면 0으로
        if (size < -len) {
            size = 0;
        }
        // 길이보다 크면 길이로
        if (size > len) {
            size = len;
        }
        return Collections.unmodifiableList(list.subList(len - size, len));
    }

    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        Map<String, T> result = new LinkedHashMap<>();
        for (T item : list) {
            result.put(indexer.apply(item), item);
        }
        return Collections.unmodifiableMap(result);
    }

    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> result = list.stream()
                .collect(Collectors.groupingBy(classifier, LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)));
        return Collections.unmodifiableMap(result);
    }

    // Map operations

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Collections.emptyMap() : Collections.unmodifiableMap(map);
    }

    public static Map<Object, Object> asMap(Object... items) {
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items length must be even");
        }
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            Map<Object, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < items.length; i += 2) {
                map.put(items[i], items[i + 1]);
            }
            return Collections.unmodifiableMap(map);
        } else {
            // stream implementation
            return Collections.unmodifiableMap(IntStream.range(0, items.length / 2)
                    .boxed()
                    .collect(Collectors.toMap(
                            i -> items[2 * i],
                            i -> items[2 * i + 1],
                            (a, b) -> b,
                            LinkedHashMap::new)));
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<Object, Object> origin) {
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            Map<K, V> result = new LinkedHashMap<>();
            for (Map.Entry<Object, Object> entry : origin.entrySet()) {
                result.put((K) entry.getKey(), (V) entry.getValue());
            }
            return Collections.unmodifiableMap(result);
        } else {
            // stream implementation
            return Collections.unmodifiableMap(origin.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> (K) e.getKey(),
                            e -> (V) e.getValue(),
                            (v1, v2) -> v2,
                            LinkedHashMap::new)));
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items length must be even");
        }
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            Map<K, V> map = new LinkedHashMap<>();
            for (int i = 0; i < items.length; i += 2) {
                map.put(keyClass.cast(items[i]), valueClass.cast(items[i + 1]));
            }
            return Collections.unmodifiableMap(map);
        } else {
            // stream implementation
            return Collections.unmodifiableMap(IntStream.range(0, items.length / 2)
                    .boxed()
                    .collect(Collectors.toMap(
                            i -> keyClass.cast(items[2 * i]),
                            i -> valueClass.cast(items[2 * i + 1]),
                            (a, b) -> b,
                            LinkedHashMap::new)));
        }
    }

    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        if (RANDOM.nextBoolean()) {
            // for-loop implementation
            Map<K, V> map = new LinkedHashMap<>();
            for (Map.Entry<K, V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            return Collections.unmodifiableMap(map);
        } else {
            // stream implementation
            return Collections.unmodifiableMap(entries.stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> b,
                            LinkedHashMap::new)));
        }
    }

    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(origin));
    }
}
