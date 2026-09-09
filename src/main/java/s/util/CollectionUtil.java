package s.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** null 컬렉션을 빈 컬렉션으로 취급하는 유틸리티예요. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CollectionUtil {

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static <T> List<T> emptyIfNull(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        return zip(list1, list2, Pair::new);
    }

    /** 두 리스트 중 짧은 길이만큼 순서대로 묶어요. */
    public static <T, U, R> List<R> zip(
            List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        List<R> result = new ArrayList<>();
        if (isEmpty(list1) || isEmpty(list2) || mixer == null) {
            return result;
        }
        Iterator<T> first = list1.iterator();
        Iterator<U> second = list2.iterator();
        while (first.hasNext() && second.hasNext()) {
            result.add(mixer.apply(first.next(), second.next()));
        }
        return result;
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
        for (T value : list) {
            if (filter.test(value)) {
                return value;
            }
        }
        return null;
    }

    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        List<T> result = new ArrayList<>();
        if (isEmpty(list) || filter == null) {
            return result;
        }
        for (T value : list) {
            if (filter.test(value)) {
                result.add(value);
            }
        }
        return result;
    }

    /** list1 + (list2 - list1). 입력의 순서와 중복을 유지해요. */
    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(emptyIfNull(list1));
        result.addAll(differenceOf(list2, list1));
        return result;
    }

    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        if (isEmpty(list1) || isEmpty(list2)) {
            return new ArrayList<>();
        }
        Set<T> members = new HashSet<>(list2);
        return findAll(list1, members::contains);
    }

    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return new ArrayList<>();
        }
        if (isEmpty(list2)) {
            return new ArrayList<>(list1);
        }
        Set<T> members = new HashSet<>(list2);
        return findAll(list1, value -> !members.contains(value));
    }

    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        List<T> result = differenceOf(list1, list2);
        result.addAll(differenceOf(list2, list1));
        return result;
    }

    public static <T> List<T> slice(List<T> list, int begin) {
        return slice(list, begin, list == null ? 0 : list.size());
    }

    /** 정상 범위는 subList 뷰, null 또는 잘못된 범위는 새 빈 리스트를 반환해요. */
    public static <T> List<T> slice(List<T> list, int begin, int end) {
        if (list == null || begin < 0 || end < begin || end > list.size()) {
            return new ArrayList<>();
        }
        return list.subList(begin, end);
    }

    public static <T> List<T> head(List<T> list, int size) {
        if (list == null) {
            return new ArrayList<>();
        }
        return slice(list, 0, Math.min(Math.max(size, 0), list.size()));
    }

    public static <T> List<T> tail(List<T> list, int size) {
        if (list == null) {
            return new ArrayList<>();
        }
        return slice(list, list.size() - Math.min(Math.max(size, 0), list.size()));
    }

    /** 키의 최초 등장 순서를 유지하며 같은 키는 마지막 항목으로 덮어써요. */
    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        Map<String, T> result = new LinkedHashMap<>();
        if (isEmpty(list) || indexer == null) {
            return result;
        }
        for (T value : list) {
            result.put(indexer.apply(value), value);
        }
        return result;
    }

    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> result = new LinkedHashMap<>();
        if (isEmpty(list) || classifier == null) {
            return result;
        }
        for (T value : list) {
            String key = classifier.apply(value);
            List<T> group = result.get(key);
            if (group == null) {
                group = new ArrayList<>();
                result.put(key, group);
            }
            group.add(value);
        }
        return result;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        if (map == null) {
            return new LinkedHashMap<>();
        }
        return map;
    }

    public static Map<Object, Object> asMap(Object... items) {
        return asMap(Object.class, Object.class, items);
    }

    /** 타입 변환 없이 원본 맵을 unchecked cast해요. 실제 타입 보장은 호출자의 책임이에요. */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<?, ?> origin) {
        if (origin == null) {
            return new LinkedHashMap<>();
        }
        return (Map<K, V>) origin;
    }

    /** 홀수 인자 수는 IllegalArgumentException, 타입 불일치는 ClassCastException이에요. */
    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        Objects.requireNonNull(keyClass, "keyClass");
        Objects.requireNonNull(valueClass, "valueClass");
        Map<K, V> result = new LinkedHashMap<>();
        if (items == null || items.length == 0) {
            return result;
        }
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items must contain key/value pairs");
        }
        for (int i = 0; i < items.length; i += 2) {
            result.put(keyClass.cast(items[i]), valueClass.cast(items[i + 1]));
        }
        return result;
    }

    /** null entry는 건너뛰고 중복 키에는 마지막 값을 사용해요. */
    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        Map<K, V> result = new LinkedHashMap<>();
        if (isEmpty(entries)) {
            return result;
        }
        for (Map.Entry<K, V> entry : entries) {
            if (entry != null) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /** 원본의 순회 순서와 키·값 참조를 유지하는 수정 가능한 얕은 복사예요. */
    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        return new LinkedHashMap<>(emptyIfNull(origin));
    }
}
