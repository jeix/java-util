package s.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import s.type.tuple.Pair;

/**
 * 리스트와 맵을 다루는 정적 유틸리티.
 *
 * <p>이 클래스가 지키는 규칙:
 * <ul>
 *   <li>파라미터를 먼저 검사해서 정상 처리할 수 없는 경우는 먼저 반환하고, 정상 케이스를 마지막에 처리한다.</li>
 *   <li>리스트·맵·함수 파라미터가 {@code null} 이면 {@link Objects#requireNonNull(Object, String)} 으로 바로 실패한다.
 *       {@code null} 이 들어올 수 있는 자리에서는 {@link #emptyIfNull(List)} 로 먼저 바꿔서 넘긴다.
 *       검사 메서드인 {@link #isEmpty(List)} 와 {@link #emptyIfNull(List)} 만 {@code null} 을 견딘다.</li>
 *   <li>새로 만들어 돌려주는 리스트·맵은 {@link Collections#unmodifiableList(List)} ·
 *       {@link Collections#unmodifiableMap(Map)} 로 감싼 불변 컬렉션이다. 고치려고 하면
 *       {@link UnsupportedOperationException} 이 난다. 넘어온 것을 그대로 넘기는 {@link #emptyIfNull(List)} 와
 *       {@link #castKeyValue(Map)} 는 예외이고, {@link #copyOf(Map)} 는 {@link Map#copyOf(Map)} 를 쓴다.</li>
 *   <li>이미 있는 메서드로 처리할 수 있으면 그 메서드를 호출한다.</li>
 *   <li>오버로드된 메서드는 실질적인 구현을 한 곳에 두고 나머지는 그것을 호출한다.</li>
 *   <li>같은 일을 하는 구현이 둘 이상이면(for 문 방식과 stream 방식) 실행할 때마다 무작위로 하나를 골라 쓴다.
 *       어느 쪽을 골라도 결과는 같다.</li>
 *   <li>음수 인덱스는 뒤에서부터 세는 역방향 인덱스로 본다({@code -1} 이 마지막 항목).
 *       {@link StringUtil} 의 같은 이름 메서드와 규칙이 같다.</li>
 *   <li>인덱스가 리스트 범위를 넘어가면 예외를 던지지 않고 가능한 만큼만 처리한다.</li>
 * </ul>
 */
public final class CollectionUtil {

    private CollectionUtil() {
    }

    // ------------------------------------------------------------------
    // 리스트 검사
    // ------------------------------------------------------------------

    /**
     * {@code list} 가 {@code null} 이거나 비어 있으면 {@code true}.
     *
     * <p>다른 메서드와 달리 {@code null} 을 견딘다. {@code null} 은 비어 있는 것으로 본다.
     *
     * <pre>
     * isEmpty(null)  → true
     * isEmpty([])    → true
     * isEmpty([1])   → false
     * </pre>
     */
    public static <T> boolean isEmpty(List<T> list) {
        return list == null ? true : list.isEmpty();
    }

    /**
     * {@code list} 가 {@code null} 이면 빈 리스트, 아니면 {@code list} 그대로.
     *
     * <p>새로 만드는 쪽(빈 리스트)은 바꿔 넣을 수 있다. 넘어온 리스트는 복사하지 않고 그대로 넘긴다.
     *
     * <pre>
     * emptyIfNull(null)  → []
     * </pre>
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    // ------------------------------------------------------------------
    // 짝짓기
    // ------------------------------------------------------------------

    /**
     * 두 리스트를 자리 순서대로 짝지어 {@link Pair} 목록으로 만든다. 길이가 다르면 짧은 쪽에 맞춘다.
     *
     * <pre>
     * zip([1, 2], ["a", "b", "c"])  → [(1, a), (2, b)]
     * </pre>
     */
    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        return zip(list1, list2, Pair::of);
    }

    /**
     * 두 리스트를 자리 순서대로 짝지어 {@code mixer} 로 섞은 결과 목록을 만든다.
     * 길이가 다르면 짧은 쪽에 맞춘다.
     *
     * <pre>
     * zip([1, 2], [10, 20], (t, u) -&gt; t + u)  → [11, 22]
     * </pre>
     */
    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        Objects.requireNonNull(list1, "list1");
        Objects.requireNonNull(list2, "list2");
        Objects.requireNonNull(mixer, "mixer");

        int size = Math.min(list1.size(), list2.size());
        List<R> zipped = ThreadLocalRandom.current().nextBoolean()
                ? _zipByLoop(list1, list2, mixer, size)
                : _zipByStream(list1, list2, mixer, size);

        return Collections.unmodifiableList(zipped);
    }

    /** for 문으로 짝짓는다. */
    private static <T, U, R> List<R> _zipByLoop(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer, int size) {
        List<R> zipped = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            zipped.add(mixer.apply(list1.get(i), list2.get(i)));
        }
        return zipped;
    }

    /** stream 으로 짝짓는다. {@code mixer} 가 {@code null} 을 돌려줘도 for 문 방식과 같게 담긴다. */
    private static <T, U, R> List<R> _zipByStream(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer, int size) {
        return IntStream.range(0, size)
                .mapToObj(i -> mixer.apply(list1.get(i), list2.get(i)))
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // 배열로 바꾸기
    // ------------------------------------------------------------------

    /**
     * 리스트를 배열로 바꾼다.
     *
     * <p>돌려주는 배열의 런타임 타입은 {@code Object[]} 다. 원소 타입 배열이 필요하면
     * {@code list.toArray(T[]::new)} 를 써야 한다.
     *
     * <pre>
     * Object[] array = toArray(["a", "b"]);
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        Objects.requireNonNull(list, "list");

        return (T[]) list.toArray();
    }

    // ------------------------------------------------------------------
    // 찾기
    // ------------------------------------------------------------------

    /**
     * {@code filter} 를 처음으로 통과하는 항목 하나를 돌려준다. 없으면 {@code null}.
     *
     * <pre>
     * findOne([1, 2, 3, 4], n -&gt; n % 2 == 0)  → 2
     * </pre>
     */
    public static <T> T findOne(List<T> list, Predicate<T> filter) {
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(filter, "filter");

        return ThreadLocalRandom.current().nextBoolean()
                ? _findOneByLoop(list, filter)
                : _findOneByStream(list, filter);
    }

    /** for 문으로 처음 통과하는 항목을 찾는다. 찾으면 거기서 멈춘다. */
    private static <T> T _findOneByLoop(List<T> list, Predicate<T> filter) {
        for (T item : list) {
            if (filter.test(item)) {
                return item;
            }
        }
        return null;
    }

    /** stream 으로 처음 통과하는 항목을 찾는다. {@code findFirst} 가 거기서 멈춘다. */
    private static <T> T _findOneByStream(List<T> list, Predicate<T> filter) {
        return list.stream()
                .filter(filter)
                .findFirst()
                .orElse(null);
    }

    /**
     * {@code filter} 를 통과하는 항목 전부를 돌려준다. 순서는 원래 리스트 그대로.
     *
     * <pre>
     * findAll([1, 2, 3, 4], n -&gt; n % 2 == 0)  → [2, 4]
     * </pre>
     */
    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(filter, "filter");

        List<T> found = ThreadLocalRandom.current().nextBoolean()
                ? _findAllByLoop(list, filter)
                : _findAllByStream(list, filter);

        return Collections.unmodifiableList(found);
    }

    /** for 문으로 통과하는 항목을 모은다. */
    private static <T> List<T> _findAllByLoop(List<T> list, Predicate<T> filter) {
        List<T> found = new ArrayList<>();

        for (T item : list) {
            if (filter.test(item)) {
                found.add(item);
            }
        }
        return found;
    }

    /** stream 으로 통과하는 항목을 모은다. {@code null} 항목도 for 문 방식과 같게 담긴다. */
    private static <T> List<T> _findAllByStream(List<T> list, Predicate<T> filter) {
        return list.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------
    // 집합 연산 (리스트 순서를 지킨다)
    // ------------------------------------------------------------------

    /**
     * 합집합. {@code list1} 에 {@code list2} 의 항목 중 {@code list1} 에 없는 것을 뒤에 붙인다.
     *
     * <pre>
     * unionOf([1, 2], [2, 3])  → [1, 2, 3]
     * </pre>
     */
    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        Objects.requireNonNull(list1, "list1");
        Objects.requireNonNull(list2, "list2");

        List<T> union = new ArrayList<>(list1);
        union.addAll(differenceOf(list2, list1));

        return Collections.unmodifiableList(union);
    }

    /**
     * 교집합. {@code list1 - (list1 - list2)} 와 같다.
     *
     * <pre>
     * intersectionOf([1, 2, 3], [2, 3, 4])  → [2, 3]
     * </pre>
     */
    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        Objects.requireNonNull(list1, "list1");
        Objects.requireNonNull(list2, "list2");

        return differenceOf(list1, differenceOf(list1, list2));
    }

    /**
     * 차집합. {@code list1} 에서 {@code list2} 에 있는 항목을 뺀다.
     * 같은 항목이 여러 번 있으면 그 항목은 모두 빠진다.
     *
     * <pre>
     * differenceOf([1, 2, 2, 3], [2])  → [1, 3]
     * </pre>
     */
    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        Objects.requireNonNull(list1, "list1");
        Objects.requireNonNull(list2, "list2");

        Set<T> excluded = new HashSet<>(list2);
        List<T> difference = ThreadLocalRandom.current().nextBoolean()
                ? _differenceByLoop(list1, excluded)
                : _differenceByStream(list1, excluded);

        return Collections.unmodifiableList(difference);
    }

    /** for 문으로 {@code excluded} 에 없는 항목을 모은다. */
    private static <T> List<T> _differenceByLoop(List<T> list1, Set<T> excluded) {
        List<T> difference = new ArrayList<>();

        for (T item : list1) {
            if (!excluded.contains(item)) {
                difference.add(item);
            }
        }
        return difference;
    }

    /** stream 으로 {@code excluded} 에 없는 항목을 모은다. */
    private static <T> List<T> _differenceByStream(List<T> list1, Set<T> excluded) {
        return list1.stream()
                .filter(item -> !excluded.contains(item))
                .collect(Collectors.toList());
    }

    /**
     * 대칭차. {@code (list1 - list2)} 뒤에 {@code (list2 - list1)} 을 붙인다.
     *
     * <pre>
     * symmetricDifferenceOf([1, 2], [2, 3])  → [1, 3]
     * </pre>
     */
    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        Objects.requireNonNull(list1, "list1");
        Objects.requireNonNull(list2, "list2");

        List<T> symmetric = new ArrayList<>(differenceOf(list1, list2));
        symmetric.addAll(differenceOf(list2, list1));

        return Collections.unmodifiableList(symmetric);
    }

    // ------------------------------------------------------------------
    // 자르기
    // ------------------------------------------------------------------

    /**
     * {@code begin} 부터 끝까지 자른다. {@code begin} 은 포함한다.
     *
     * <pre>
     * slice([1, 2, 3, 4], 2)   → [3, 4]
     * slice([1, 2, 3, 4], -2)  → [3, 4]   (뒤에서 2개)
     * </pre>
     */
    public static <T> List<T> slice(List<T> list, int begin) {
        Objects.requireNonNull(list, "list");

        return slice(list, begin, list.size());
    }

    /**
     * {@code begin} 부터 {@code end} 전까지 자른다. {@code begin} 은 포함하고 {@code end} 는 포함하지 않는다.
     *
     * <p>음수 인덱스는 뒤에서부터 세는 역방향 인덱스로 본다. 범위를 넘어가는 인덱스는 가능한 범위로 맞추고,
     * 잘라낼 구간이 없으면 빈 리스트.
     *
     * <p>돌려주는 리스트는 새로 만든 것이라 원본과 따로 움직인다({@code subList} 뷰가 아니다).
     *
     * <pre>
     * slice([1, 2, 3, 4], 1, 3)   → [2, 3]
     * slice([1, 2, 3, 4], 1, -1)  → [2, 3]
     * </pre>
     */
    public static <T> List<T> slice(List<T> list, int begin, int end) {
        Objects.requireNonNull(list, "list");

        int from = _clamp(begin, list.size());
        int to = _clamp(end, list.size());

        if (from >= to) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(list.subList(from, to)));
    }

    /** 인덱스를 {@code 0 ~ length} 범위로 맞춘다. 음수는 뒤에서부터 세는 역방향 인덱스로 본다. */
    private static int _clamp(int index, int length) {
        return index < 0 ? Math.max(length + index, 0) : Math.min(index, length);
    }

    /**
     * 앞에서 {@code size} 개. {@code size} 가 리스트 길이보다 크면 리스트 전체를 돌려준다.
     * 음수 {@code size} 는 뒤에서부터 세는 역방향 인덱스로 보고 그 앞까지만 자른다.
     *
     * <pre>
     * head([1, 2, 3, 4], 2)   → [1, 2]
     * head([1, 2, 3, 4], -2)  → [1, 2]   (뒤에서 2개 앞까지)
     * </pre>
     */
    public static <T> List<T> head(List<T> list, int size) {
        Objects.requireNonNull(list, "list");

        return slice(list, 0, size);
    }

    /**
     * 뒤에서 {@code size} 개. {@code size} 가 리스트 길이보다 크면 리스트 전체를 돌려준다.
     * 음수 {@code size} 는 양수로 바꿔 "앞에서 제외할 개수"로 본다.
     *
     * <pre>
     * tail([1, 2, 3, 4], 2)   → [3, 4]
     * tail([1, 2, 3, 4], -2)  → [3, 4]   (앞 2개를 뺀 나머지)
     * </pre>
     */
    public static <T> List<T> tail(List<T> list, int size) {
        Objects.requireNonNull(list, "list");

        if (size < 0) {
            return slice(list, Math.abs(size));
        }
        if (size >= list.size()) {
            return Collections.unmodifiableList(new ArrayList<>(list));
        }
        return slice(list, list.size() - size);
    }

    // ------------------------------------------------------------------
    // 색인화 / 분류
    // ------------------------------------------------------------------

    /**
     * 리스트를 색인화한다. 색인이 키이고 항목이 값이다. 색인이 겹치면 뒤에 나온 항목이 남는다.
     * 넣은 순서를 지킨다.
     *
     * <pre>
     * indexing([1, 2], n -&gt; "n" + n)  → {n1: 1, n2: 2}
     * </pre>
     */
    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(indexer, "indexer");

        Map<String, T> indexed = ThreadLocalRandom.current().nextBoolean()
                ? _indexByLoop(list, indexer)
                : _indexByStream(list, indexer);

        return Collections.unmodifiableMap(indexed);
    }

    /** for 문으로 색인을 채운다. */
    private static <T> Map<String, T> _indexByLoop(List<T> list, Function<T, String> indexer) {
        Map<String, T> indexed = new LinkedHashMap<>();

        for (T item : list) {
            indexed.put(indexer.apply(item), item);
        }
        return indexed;
    }

    /**
     * stream 으로 색인을 채운다.
     *
     * <p>{@link Collectors#toMap} 을 쓰지 않는다. 그쪽은 값이 {@code null} 이면 예외를 던져서
     * for 문 방식과 결과가 갈리기 때문이다. {@code forEach} 는 stream 의 종단 연산이라 모든 항목에 대해 실행된다.
     */
    private static <T> Map<String, T> _indexByStream(List<T> list, Function<T, String> indexer) {
        Map<String, T> indexed = new LinkedHashMap<>();

        list.forEach(item -> indexed.put(indexer.apply(item), item));

        return indexed;
    }

    /**
     * 리스트를 분류한다. 분류가 키이고 그 분류에 해당하는 항목 리스트가 값이다. 넣은 순서를 지킨다.
     * 값으로 들어 있는 리스트도 불변이다.
     *
     * <pre>
     * grouping([1, 2, 3, 4], n -&gt; n % 2 == 0 ? "짝" : "홀")  → {홀: [1, 3], 짝: [2, 4]}
     * </pre>
     */
    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        Objects.requireNonNull(list, "list");
        Objects.requireNonNull(classifier, "classifier");

        Map<String, List<T>> grouped = ThreadLocalRandom.current().nextBoolean()
                ? _groupByLoop(list, classifier)
                : _groupByStream(list, classifier);
        grouped.replaceAll((key, value) -> Collections.unmodifiableList(value));

        return Collections.unmodifiableMap(grouped);
    }

    /** for 문으로 분류한다. */
    private static <T> Map<String, List<T>> _groupByLoop(List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> grouped = new LinkedHashMap<>();

        for (T item : list) {
            grouped.computeIfAbsent(classifier.apply(item), key -> new ArrayList<>()).add(item);
        }
        return grouped;
    }

    /**
     * stream 으로 분류한다.
     *
     * <p>{@link Collectors#groupingBy} 를 쓰지 않는다. 그쪽은 분류가 {@code null} 이면 예외를 던져서
     * for 문 방식과 결과가 갈리기 때문이다.
     */
    private static <T> Map<String, List<T>> _groupByStream(List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> grouped = new LinkedHashMap<>();

        list.forEach(item -> grouped.computeIfAbsent(classifier.apply(item), key -> new ArrayList<>()).add(item));

        return grouped;
    }

    // ------------------------------------------------------------------
    // 맵 검사
    // ------------------------------------------------------------------

    /**
     * {@code map} 이 {@code null} 이거나 비어 있으면 {@code true}.
     *
     * <p>다른 메서드와 달리 {@code null} 을 견딘다. {@code null} 은 비어 있는 것으로 본다.
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null ? true : map.isEmpty();
    }

    /**
     * {@code map} 이 {@code null} 이면 빈 맵, 아니면 {@code map} 그대로.
     *
     * <p>새로 만드는 쪽(빈 맵)은 바꿔 넣을 수 있다. 넘어온 맵은 복사하지 않고 그대로 넘긴다.
     */
    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? new LinkedHashMap<>() : map;
    }

    // ------------------------------------------------------------------
    // 맵 만들기
    // ------------------------------------------------------------------

    /**
     * {@code key1, value1, key2, value2, ...} 순서로 짝지어 맵을 만든다. 넣은 순서를 지킨다.
     *
     * <pre>
     * asMap("foo", 42, "bar", 43)  → {foo: 42, bar: 43}
     * </pre>
     *
     * @throws IllegalArgumentException 키와 값이 짝을 이루지 못한(홀수 개인) 경우
     */
    public static Map<Object, Object> asMap(Object... items) {
        Objects.requireNonNull(items, "items");

        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("키와 값이 짝을 이뤄야 한다. 넘어온 개수: " + items.length);
        }
        Map<Object, Object> map = ThreadLocalRandom.current().nextBoolean()
                ? _asMapByLoop(items)
                : _asMapByStream(items);

        return Collections.unmodifiableMap(map);
    }

    /** for 문으로 키와 값을 짝지어 담는다. */
    private static Map<Object, Object> _asMapByLoop(Object[] items) {
        Map<Object, Object> map = new LinkedHashMap<>(items.length / 2);

        for (int i = 0; i < items.length; i += 2) {
            map.put(items[i], items[i + 1]);
        }
        return map;
    }

    /**
     * stream 으로 키와 값을 짝지어 담는다.
     *
     * <p>{@link Collectors#toMap} 을 쓰지 않는다. 그쪽은 값이 {@code null} 이면 예외를 던져서
     * for 문 방식과 결과가 갈리기 때문이다.
     */
    private static Map<Object, Object> _asMapByStream(Object[] items) {
        Map<Object, Object> map = new LinkedHashMap<>(items.length / 2);

        IntStream.range(0, items.length / 2).forEach(i -> map.put(items[i * 2], items[i * 2 + 1]));

        return map;
    }

    /**
     * {@link #asMap(Object...)} 로 만든 맵을 원하는 키·값 타입으로 보고 쓴다.
     * 값을 실제로 바꾸지는 않으므로 잘못된 타입으로 꺼낼 때 {@link ClassCastException} 이 난다.
     *
     * <p>넘어온 맵을 복사하지 않고 그대로 넘긴다.
     *
     * <pre>
     * Map&lt;Object, Object&gt; map1 = asMap("foo", 42);
     * Map&lt;String, Integer&gt; map2 = castKeyValue(map1);
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<Object, Object> origin) {
        Objects.requireNonNull(origin, "origin");

        return (Map<K, V>) origin;
    }

    /**
     * {@link #asMap(Object...)} 로 만든 값을 키·값 클래스로 검사하면서 원하는 타입의 맵으로 만든다.
     * 넣은 순서를 지킨다.
     *
     * <pre>
     * Map&lt;String, Integer&gt; map = asMap(String.class, Integer.class, "foo", 42, "bar", 43);
     * </pre>
     *
     * @throws IllegalArgumentException 키와 값이 짝을 이루지 못한(홀수 개인) 경우
     * @throws ClassCastException       키나 값이 넘긴 클래스의 인스턴스가 아닌 경우
     */
    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        Objects.requireNonNull(keyClass, "keyClass");
        Objects.requireNonNull(valueClass, "valueClass");

        Map<K, V> typed = new LinkedHashMap<>();

        for (Map.Entry<Object, Object> entry : asMap(items).entrySet()) {
            typed.put(keyClass.cast(entry.getKey()), valueClass.cast(entry.getValue()));
        }
        return Collections.unmodifiableMap(typed);
    }

    /**
     * {@link Map.Entry} 목록을 맵으로 만든다. 넣은 순서를 지킨다.
     *
     * <pre>
     * asMap(List.of(Map.entry("foo", 42), Map.entry("bar", 43)))  → {foo: 42, bar: 43}
     * </pre>
     */
    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        Objects.requireNonNull(entries, "entries");

        Map<K, V> map = ThreadLocalRandom.current().nextBoolean()
                ? _asMapEntriesByLoop(entries)
                : _asMapEntriesByStream(entries);

        return Collections.unmodifiableMap(map);
    }

    /** for 문으로 항목을 담는다. */
    private static <K, V> Map<K, V> _asMapEntriesByLoop(List<Map.Entry<K, V>> entries) {
        Map<K, V> map = new LinkedHashMap<>();

        for (Map.Entry<K, V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    /**
     * stream 으로 항목을 담는다.
     *
     * <p>{@link Collectors#toMap} 을 쓰지 않는다. 그쪽은 값이 {@code null} 이면 예외를 던져서
     * for 문 방식과 결과가 갈리기 때문이다({@link Map.Entry} 는 {@code null} 값을 담을 수 있다).
     */
    private static <K, V> Map<K, V> _asMapEntriesByStream(List<Map.Entry<K, V>> entries) {
        Map<K, V> map = new LinkedHashMap<>();

        entries.forEach(entry -> map.put(entry.getKey(), entry.getValue()));

        return map;
    }

    /**
     * {@code origin} 을 바꿀 수 없는 맵으로 복사한다.
     *
     * <p>{@link Map#copyOf(Map)} 를 쓰므로 키나 값에 {@code null} 이 있으면 안 되고, 항목 순서는 보장하지 않는다.
     *
     * @throws NullPointerException 키나 값이 {@code null} 인 경우
     */
    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        Objects.requireNonNull(origin, "origin");

        return Map.copyOf(origin);
    }
}
