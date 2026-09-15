package s.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import s.type.tuple.Pair;

/**
 * 리스트와 맵을 다루는 유틸리티 메서드를 모아 둔 클래스입니다.
 *
 * <p>구현 규칙은 {@link StringUtil}과 같습니다.
 *
 * <ul>
 *   <li>파라미터를 먼저 검사하고, 정상적으로 처리할 수 없는 경우는 그대로 리턴합니다.</li>
 *   <li>같은 기능이 이미 다른 메서드에 있으면 그 메서드를 호출합니다.</li>
 *   <li>오버로드된 메서드는 실질적인 구현 메서드 하나를 호출하고, 구현 메서드가 private이면 이름 앞에 {@code _}를 붙입니다.</li>
 *   <li>단순 분기는 3항 연산자를 사용합니다.</li>
 *   <li>반복문 구현과 스트림 구현을 함께 두는 메서드는 실행 시점에 랜덤하게 분기합니다.</li>
 *   <li>새로 만든 결과 컬렉션은 불변 컬렉션으로 반환합니다. 다만 {@link #emptyIfNull(List)}, {@link #emptyIfNull(Map)},
 *       {@link #castKeyValue(Map)}는 원본을 그대로 반환합니다.</li>
 * </ul>
 */
public final class CollectionUtil {

    private CollectionUtil() {
    }

    /**
     * 리스트가 {@code null}이거나 비어 있으면 {@code true}를 반환합니다.
     *
     * @param list 검사할 리스트
     * @param <T>  요소 타입
     * @return {@code null} 또는 빈 리스트 여부
     */
    public static <T> boolean isEmpty(List<T> list) {
        return list == null ? true : list.isEmpty();
    }

    /**
     * 리스트가 {@code null}이면 불변 빈 리스트를, 아니면 원본을 그대로 반환합니다.
     *
     * @param list 대상 리스트
     * @param <T>  요소 타입
     * @return {@code null}이 아닌 리스트
     */
    public static <T> List<T> emptyIfNull(List<T> list) {
        return list == null ? List.of() : list;
    }

    /**
     * 두 리스트를 {@link Pair} 목록으로 묶습니다. 길이가 다르면 짧은 쪽에 맞춰 잘라냅니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param <T>   첫 번째 요소 타입
     * @param <U>   두 번째 요소 타입
     * @return {@link Pair} 목록
     */
    public static <T, U> List<Pair<T, U>> zip(List<T> list1, List<U> list2) {
        return zip(list1, list2, Pair::of);
    }

    /**
     * 두 리스트를 같은 인덱스끼리 믹서로 합칩니다. 길이가 다르면 짧은 쪽에 맞춰 잘라냅니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param mixer 두 값을 합치는 함수
     * @param <T>   첫 번째 요소 타입
     * @param <U>   두 번째 요소 타입
     * @param <R>   합친 결과 타입
     * @return 합친 결과 목록
     */
    public static <T, U, R> List<R> zip(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        if (isEmpty(list1) || isEmpty(list2) || mixer == null) {
            return List.of();
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _zipByLoop(list1, list2, mixer)
                : _zipByStream(list1, list2, mixer);
    }

    /**
     * {@code zip}의 반복문 구현입니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param mixer 두 값을 합치는 함수
     * @param <T>   첫 번째 요소 타입
     * @param <U>   두 번째 요소 타입
     * @param <R>   합친 결과 타입
     * @return 합친 결과 목록
     */
    private static <T, U, R> List<R> _zipByLoop(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        int size = Math.min(list1.size(), list2.size());
        List<R> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(mixer.apply(list1.get(i), list2.get(i)));
        }
        return _unmodifiable(result);
    }

    /**
     * {@code zip}의 스트림 구현입니다. 믹서가 {@code null}을 반환해도 안전하도록 {@code toList()}로 모읍니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param mixer 두 값을 합치는 함수
     * @param <T>   첫 번째 요소 타입
     * @param <U>   두 번째 요소 타입
     * @param <R>   합친 결과 타입
     * @return 합친 결과 목록
     */
    private static <T, U, R> List<R> _zipByStream(List<T> list1, List<U> list2, BiFunction<T, U, R> mixer) {
        int size = Math.min(list1.size(), list2.size());
        return _unmodifiable(IntStream.range(0, size)
                .mapToObj(index -> mixer.apply(list1.get(index), list2.get(index)))
                .toList());
    }

    /**
     * 리스트를 배열로 변환합니다.
     *
     * <p>런타임 배열 타입은 {@code Object[]}입니다. 구체 타입 배열이 필요하면 {@link List#toArray(Object[])}를 사용합니다.
     *
     * @param list 대상 리스트
     * @param <T>  요소 타입
     * @return 배열
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(List<T> list) {
        if (isEmpty(list)) {
            return (T[]) new Object[0];
        }
        return list.toArray((T[]) new Object[list.size()]);
    }

    /**
     * 조건을 만족하는 첫 번째 항목을 반환합니다. 없으면 {@code null}을 반환합니다.
     *
     * @param list   대상 리스트
     * @param filter 조건
     * @param <T>    요소 타입
     * @return 첫 번째로 조건을 만족하는 항목 또는 {@code null}
     */
    public static <T> T findOne(List<T> list, Predicate<T> filter) {
        if (isEmpty(list) || filter == null) {
            return null;
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _findOneByLoop(list, filter)
                : _findOneByStream(list, filter);
    }

    /**
     * {@code findOne}의 반복문 구현입니다.
     *
     * @param list   대상 리스트
     * @param filter 조건
     * @param <T>    요소 타입
     * @return 첫 번째로 조건을 만족하는 항목 또는 {@code null}
     */
    private static <T> T _findOneByLoop(List<T> list, Predicate<T> filter) {
        for (T item : list) {
            if (filter.test(item)) {
                return item;
            }
        }
        return null;
    }

    /**
     * {@code findOne}의 스트림 구현입니다. 조건을 만족하는 항목이 {@code null}이어도 안전하도록
     * {@link Optional}로 감싸 {@code findFirst()}의 {@code null} 예외를 피합니다.
     *
     * @param list   대상 리스트
     * @param filter 조건
     * @param <T>    요소 타입
     * @return 첫 번째로 조건을 만족하는 항목 또는 {@code null}
     */
    private static <T> T _findOneByStream(List<T> list, Predicate<T> filter) {
        Optional<T> found = list.stream()
                .filter(filter)
                .map(Optional::ofNullable)
                .findFirst()
                .orElse(Optional.empty());
        return found.orElse(null);
    }

    /**
     * 조건을 만족하는 모든 항목을 순서대로 반환합니다.
     *
     * @param list   대상 리스트
     * @param filter 조건
     * @param <T>    요소 타입
     * @return 조건을 만족하는 항목 목록
     */
    public static <T> List<T> findAll(List<T> list, Predicate<T> filter) {
        if (isEmpty(list) || filter == null) {
            return List.of();
        }
        return list.stream().filter(filter).toList();
    }

    /**
     * 합집합을 반환합니다. {@code list1 + (list2 - list1)} 순서를 따릅니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param <T>   요소 타입
     * @return 합집합
     */
    public static <T> List<T> unionOf(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return _unmodifiable(emptyIfNull(list2));
        }
        if (isEmpty(list2)) {
            return _unmodifiable(list1);
        }
        List<T> result = new ArrayList<>(list1);
        result.addAll(differenceOf(list2, list1));
        return _unmodifiable(result);
    }

    /**
     * 교집합을 반환합니다. {@code list1 - (list1 - list2)} 순서를 따릅니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param <T>   요소 타입
     * @return 교집합
     */
    public static <T> List<T> intersectionOf(List<T> list1, List<T> list2) {
        return differenceOf(list1, differenceOf(list1, list2));
    }

    /**
     * 차집합을 반환합니다. {@code list1 - list2} 순서를 따릅니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param <T>   요소 타입
     * @return 차집합
     */
    public static <T> List<T> differenceOf(List<T> list1, List<T> list2) {
        if (isEmpty(list1)) {
            return List.of();
        }
        List<T> others = emptyIfNull(list2);
        return list1.stream().filter(item -> !others.contains(item)).toList();
    }

    /**
     * 대칭차를 반환합니다. {@code (list1 - list2) + (list2 - list1)} 순서를 따릅니다.
     *
     * @param list1 첫 번째 리스트
     * @param list2 두 번째 리스트
     * @param <T>   요소 타입
     * @return 대칭차
     */
    public static <T> List<T> symmetricDifferenceOf(List<T> list1, List<T> list2) {
        List<T> result = new ArrayList<>(differenceOf(list1, list2));
        result.addAll(differenceOf(list2, list1));
        return _unmodifiable(result);
    }

    /**
     * 시작 인덱스(포함)부터 리스트 끝까지 잘라냅니다.
     * 시작 인덱스가 음수이면 뒤에서부터 세는 역방향 인덱스로 해석합니다.
     *
     * @param list  대상 리스트
     * @param begin 시작 인덱스(포함, 음수이면 역방향 인덱스)
     * @param <T>   요소 타입
     * @return 잘라낸 리스트
     */
    public static <T> List<T> slice(List<T> list, int begin) {
        if (isEmpty(list)) {
            return List.of();
        }
        return slice(list, begin, list.size());
    }

    /**
     * 시작 인덱스(포함)부터 끝 인덱스(불포함)까지 잘라냅니다.
     * 인덱스가 음수이면 뒤에서부터 세는 역방향 인덱스로 해석하고, 범위를 벗어나면 보정합니다.
     *
     * @param list  대상 리스트
     * @param begin 시작 인덱스(포함, 음수이면 역방향 인덱스)
     * @param end   끝 인덱스(불포함, 음수이면 역방향 인덱스)
     * @param <T>   요소 타입
     * @return 잘라낸 리스트
     */
    public static <T> List<T> slice(List<T> list, int begin, int end) {
        if (isEmpty(list)) {
            return List.of();
        }
        int from = _clamp(_toAbsoluteIndex(begin, list.size()), 0, list.size());
        int to = _clamp(_toAbsoluteIndex(end, list.size()), from, list.size());
        return _unmodifiable(list.subList(from, to));
    }

    /**
     * 리스트 앞에서부터 지정한 길이만큼 잘라냅니다. 길이가 리스트 길이보다 크면 전체를 반환합니다.
     * 길이가 음수이면 뒤에서부터 세는 역방향 인덱스로 해석합니다.
     *
     * @param list 대상 리스트
     * @param size 길이(음수이면 역방향 인덱스)
     * @param <T>  요소 타입
     * @return 잘라낸 리스트
     */
    public static <T> List<T> head(List<T> list, int size) {
        return slice(list, 0, size);
    }

    /**
     * 리스트 뒤에서부터 지정한 길이만큼 잘라냅니다. 길이가 리스트 길이보다 크면 전체를 반환합니다.
     * 길이가 음수이면 부호를 바꾼 값을 앞에서 제외할 인덱스로 해석합니다.
     *
     * @param list 대상 리스트
     * @param size 길이(음수이면 앞에서 제외할 항목 수)
     * @param <T>  요소 타입
     * @return 잘라낸 리스트
     */
    public static <T> List<T> tail(List<T> list, int size) {
        if (isEmpty(list)) {
            return List.of();
        }
        if (size < 0) {
            return slice(list, _clamp(-size, 0, list.size()));
        }
        return slice(list, list.size() - _clamp(size, 0, list.size()));
    }

    /**
     * 리스트를 색인합니다. 색인이 키이고 항목이 값인 맵을 반환합니다.
     * 같은 색인이 여러 번 나오면 먼저 나온 항목을 유지합니다.
     *
     * @param list    대상 리스트
     * @param indexer 색인 함수
     * @param <T>     요소 타입
     * @return 색인 맵
     */
    public static <T> Map<String, T> indexing(List<T> list, Function<T, String> indexer) {
        if (isEmpty(list) || indexer == null) {
            return Map.of();
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _indexingByLoop(list, indexer)
                : _indexingByStream(list, indexer);
    }

    /**
     * {@code indexing}의 반복문 구현입니다.
     *
     * @param list    대상 리스트
     * @param indexer 색인 함수
     * @param <T>     요소 타입
     * @return 색인 맵
     */
    private static <T> Map<String, T> _indexingByLoop(List<T> list, Function<T, String> indexer) {
        Map<String, T> result = new LinkedHashMap<>();
        for (T item : list) {
            _indexFirst(result, indexer.apply(item), item);
        }
        return _unmodifiable(result);
    }

    /**
     * {@code indexing}의 스트림 구현입니다. {@link java.util.stream.Collectors#toMap}은 값이 {@code null}이면
     * 예외가 나므로 반복문 구현과 같은 규칙을 쓰는 수집기로 모읍니다.
     *
     * @param list    대상 리스트
     * @param indexer 색인 함수
     * @param <T>     요소 타입
     * @return 색인 맵
     */
    private static <T> Map<String, T> _indexingByStream(List<T> list, Function<T, String> indexer) {
        Map<String, T> result = list.stream()
                .collect(LinkedHashMap::new, (indexed, item) -> _indexFirst(indexed, indexer.apply(item), item),
                        LinkedHashMap::putAll);
        return _unmodifiable(result);
    }

    /**
     * 색인 맵에 아직 없는 색인이면 항목을 넣습니다.
     *
     * @param indexed 색인 맵
     * @param index   색인
     * @param item    항목
     * @param <T>     요소 타입
     */
    private static <T> void _indexFirst(Map<String, T> indexed, String index, T item) {
        if (!indexed.containsKey(index)) {
            indexed.put(index, item);
        }
    }

    /**
     * 리스트를 분류합니다. 분류가 키이고 분류에 해당하는 항목 목록이 값인 맵을 반환합니다.
     *
     * @param list       대상 리스트
     * @param classifier 분류 함수
     * @param <T>        요소 타입
     * @return 분류 맵
     */
    public static <T> Map<String, List<T>> grouping(List<T> list, Function<T, String> classifier) {
        if (isEmpty(list) || classifier == null) {
            return Map.of();
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _groupingByLoop(list, classifier)
                : _groupingByStream(list, classifier);
    }

    /**
     * {@code grouping}의 반복문 구현입니다.
     *
     * @param list       대상 리스트
     * @param classifier 분류 함수
     * @param <T>        요소 타입
     * @return 분류 맵
     */
    private static <T> Map<String, List<T>> _groupingByLoop(List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> groups = new LinkedHashMap<>();
        for (T item : list) {
            _addGroup(groups, classifier.apply(item), item);
        }
        return _unmodifiableGroups(groups);
    }

    /**
     * {@code grouping}의 스트림 구현입니다. 분류가 {@code null}이어도 예외가 나지 않도록
     * {@link java.util.stream.Collectors#groupingBy} 대신 반복문 구현과 같은 규칙의 수집기를 씁니다.
     *
     * @param list       대상 리스트
     * @param classifier 분류 함수
     * @param <T>        요소 타입
     * @return 분류 맵
     */
    private static <T> Map<String, List<T>> _groupingByStream(List<T> list, Function<T, String> classifier) {
        Map<String, List<T>> groups = list.stream()
                .collect(LinkedHashMap::new,
                        (grouped, item) -> _addGroup(grouped, classifier.apply(item), item),
                        (left, right) -> _mergeGroups(left, right));
        return _unmodifiableGroups(groups);
    }

    /**
     * 분류 맵에 항목을 추가합니다.
     *
     * @param groups 분류 맵
     * @param group  분류
     * @param item   항목
     * @param <T>    요소 타입
     */
    private static <T> void _addGroup(Map<String, List<T>> groups, String group, T item) {
        groups.computeIfAbsent(group, key -> new ArrayList<>()).add(item);
    }

    /**
     * 분류 맵 두 개를 합칩니다.
     *
     * @param target 합칠 대상
     * @param source 합칠 원본
     * @param <T>    요소 타입
     */
    private static <T> void _mergeGroups(Map<String, List<T>> target, Map<String, List<T>> source) {
        source.forEach((group, items) -> target.computeIfAbsent(group, key -> new ArrayList<>()).addAll(items));
    }

    /**
     * 분류 맵의 값 목록과 맵 자체를 불변으로 반환합니다.
     *
     * @param groups 분류 맵
     * @param <T>    요소 타입
     * @return 불변 분류 맵
     */
    private static <T> Map<String, List<T>> _unmodifiableGroups(Map<String, List<T>> groups) {
        Map<String, List<T>> result = new LinkedHashMap<>();
        groups.forEach((key, value) -> result.put(key, _unmodifiable(value)));
        return _unmodifiable(result);
    }

    /**
     * 맵이 {@code null}이거나 비어 있으면 {@code true}를 반환합니다.
     *
     * @param map 검사할 맵
     * @param <K> 키 타입
     * @param <V> 값 타입
     * @return {@code null} 또는 빈 맵 여부
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null ? true : map.isEmpty();
    }

    /**
     * 맵이 {@code null}이면 불변 빈 맵을, 아니면 원본을 그대로 반환합니다.
     *
     * @param map 대상 맵
     * @param <K> 키 타입
     * @param <V> 값 타입
     * @return {@code null}이 아닌 맵
     */
    public static <K, V> Map<K, V> emptyIfNull(Map<K, V> map) {
        return map == null ? Map.of() : map;
    }

    /**
     * {@code key1, value1, key2, value2, ...} 형태의 항목들로 맵을 만듭니다.
     * 같은 키가 여러 번 나오면 뒤에 나온 값이 이깁니다.
     *
     * @param items 키와 값이 번갈아 나오는 항목들
     * @return 만들어진 맵
     * @throws IllegalArgumentException 항목 수가 홀수인 경우
     */
    public static Map<Object, Object> asMap(Object... items) {
        if (items == null || items.length == 0) {
            return Map.of();
        }
        if (items.length % 2 != 0) {
            throw new IllegalArgumentException("items는 key, value 쌍이어야 합니다: " + items.length);
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _asMapByLoop(items)
                : _asMapByStream(items);
    }

    /**
     * {@code asMap(items)}의 반복문 구현입니다.
     *
     * @param items 키와 값이 번갈아 나오는 항목들
     * @return 만들어진 맵
     */
    private static Map<Object, Object> _asMapByLoop(Object[] items) {
        Map<Object, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < items.length; i += 2) {
            result.put(items[i], items[i + 1]);
        }
        return _unmodifiable(result);
    }

    /**
     * {@code asMap(items)}의 스트림 구현입니다. 값이 {@code null}이어도 안전하도록 쌍의 인덱스로 모읍니다.
     *
     * @param items 키와 값이 번갈아 나오는 항목들
     * @return 만들어진 맵
     */
    private static Map<Object, Object> _asMapByStream(Object[] items) {
        Map<Object, Object> result = IntStream.range(0, items.length / 2)
                .boxed()
                .collect(LinkedHashMap::new, (map, index) -> map.put(items[index * 2], items[index * 2 + 1]),
                        LinkedHashMap::putAll);
        return _unmodifiable(result);
    }

    /**
     * {@code Map<Object, Object>}를 원하는 키/값 타입의 맵으로 캐스팅합니다.
     * 원본을 그대로 반환하므로 타입만 바꾼 뷰입니다.
     *
     * @param origin 원본 맵
     * @param <K>    키 타입
     * @param <V>    값 타입
     * @return 캐스팅된 맵
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> castKeyValue(Map<Object, Object> origin) {
        if (origin == null) {
            return Map.of();
        }
        return (Map<K, V>) (Map<?, ?>) origin;
    }

    /**
     * {@code key1, value1, key2, value2, ...} 형태의 항목들로 맵을 만들면서 키와 값의 타입을 검사합니다.
     *
     * @param keyClass   키 타입
     * @param valueClass 값 타입
     * @param items      키와 값이 번갈아 나오는 항목들
     * @param <K>        키 타입
     * @param <V>        값 타입
     * @return 만들어진 맵
     * @throws IllegalArgumentException 항목 수가 홀수인 경우
     * @throws ClassCastException       키나 값이 지정한 타입이 아닌 경우
     */
    public static <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass, Object... items) {
        if (keyClass == null || valueClass == null) {
            return Map.of();
        }
        Map<Object, Object> origin = asMap(items);
        Map<K, V> result = new LinkedHashMap<>();
        origin.forEach((key, value) -> result.put(keyClass.cast(key), valueClass.cast(value)));
        return _unmodifiable(result);
    }

    /**
     * {@link Map.Entry} 목록으로 맵을 만듭니다. 같은 키가 여러 번 나오면 뒤에 나온 값이 이깁니다.
     *
     * @param entries 항목 목록
     * @param <K>     키 타입
     * @param <V>     값 타입
     * @return 만들어진 맵
     */
    public static <K, V> Map<K, V> asMap(List<Map.Entry<K, V>> entries) {
        if (isEmpty(entries)) {
            return Map.of();
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _asMapEntriesByLoop(entries)
                : _asMapEntriesByStream(entries);
    }

    /**
     * {@code asMap(entries)}의 반복문 구현입니다.
     *
     * @param entries 항목 목록
     * @param <K>     키 타입
     * @param <V>     값 타입
     * @return 만들어진 맵
     */
    private static <K, V> Map<K, V> _asMapEntriesByLoop(List<Map.Entry<K, V>> entries) {
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            if (entry == null) {
                continue;
            }
            result.put(entry.getKey(), entry.getValue());
        }
        return _unmodifiable(result);
    }

    /**
     * {@code asMap(entries)}의 스트림 구현입니다. 반복문 구현과 같게 {@code null} 항목은 건너뜁니다.
     *
     * @param entries 항목 목록
     * @param <K>     키 타입
     * @param <V>     값 타입
     * @return 만들어진 맵
     */
    private static <K, V> Map<K, V> _asMapEntriesByStream(List<Map.Entry<K, V>> entries) {
        Map<K, V> result = entries.stream()
                .filter(entry -> entry != null)
                .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll);
        return _unmodifiable(result);
    }

    /**
     * 맵을 복사해 불변 맵으로 반환합니다.
     *
     * @param origin 원본 맵
     * @param <K>    키 타입
     * @param <V>    값 타입
     * @return 복사된 불변 맵
     */
    public static <K, V> Map<K, V> copyOf(Map<K, V> origin) {
        if (isEmpty(origin)) {
            return Map.of();
        }
        return _unmodifiable(origin);
    }

    /**
     * 리스트를 복사해 불변 리스트로 반환합니다.
     *
     * @param list 대상 리스트
     * @param <T>  요소 타입
     * @return 불변 리스트
     */
    private static <T> List<T> _unmodifiable(List<T> list) {
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    /**
     * 맵을 복사해 불변 맵으로 반환합니다.
     *
     * @param map 대상 맵
     * @param <K> 키 타입
     * @param <V> 값 타입
     * @return 불변 맵
     */
    private static <K, V> Map<K, V> _unmodifiable(Map<K, V> map) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    /**
     * 값을 지정한 범위 안으로 보정합니다.
     *
     * @param value 값
     * @param min   최솟값
     * @param max   최댓값
     * @return 보정된 값
     */
    private static int _clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * 음수 인덱스를 뒤에서부터 세는 역방향 인덱스로 해석해 절대 인덱스로 변환합니다.
     *
     * @param index  인덱스
     * @param length 리스트 길이
     * @return 절대 인덱스
     */
    private static int _toAbsoluteIndex(int index, int length) {
        return index < 0 ? length + index : index;
    }
}
