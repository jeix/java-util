# java-util

Java 21 기반 유틸리티 라이브러리. 문자열 처리, 컬렉션/맵 조작, 튜플 타입을 제공합니다.

## 요구사항

- **JDK 21** 이상
- 별도 Maven 설치 불필요 (Maven Wrapper 포함)

## 빌드 및 테스트

```bash
# 컴파일
./mvnw compile

# 테스트 실행
./mvnw test

# 전체 빌드 (컴파일 + 테스트)
./mvnw clean test
```

## 모듈 구조

```
src/main/java
├── s/
│   ├── Hello.java                    # 예제 클래스
│   ├── util/
│   │   ├── StringUtil.java           # 문자열 유틸리티
│   │   └── CollectionUtil.java       # 컬렉션/맵 유틸리티
│   └── type/
│       └── tuple/
│           ├── Pair.java             # 2-튜플
│           ├── Triplet.java          # 3-튜플
│           └── Quartet.java          # 4-튜플
```

## 주요 기능

### StringUtil (`s.util.StringUtil`)

문자열 검사, 변환, 조작, 패딩, 분할/결합 등 30여 개 메서드 제공.

**주요 메서드:**

| 카테고리 | 메서드 |
|----------|--------|
| **검사** | `isBlank(s)`, `isEmpty(s)` |
| **기본값** | `nonNullOf(v, dflt)`, `nonBlankOf(v, dflt)`, `nonEmptyOf(v, dflt)` (Supplier 오버로드 포함) |
| **우선순위 선택** | `firstNonBlankOrLast`, `firstNonBlankOrEmpty`, `firstNonBlankOrNull` (가변인자/Supplier 2~5개) |
| **변환** | `stringify(obj)` - Date→yyyy-MM-dd, BigDecimal→toPlainString |
| **슬라이싱** | `slice(s, begin)`, `slice(s, begin, end)`, `head(s, size)`, `tail(s, size)` |
| **조작** | `trimLeadingZero(s)`, `repeat(s, n)`, `reverse(s)` |
| **패딩** | `lpad(len, s, pad)`, `rpad(len, s, pad)`, `pad(len, s, pad)` (pad는 길이 1 문자열), `lpad2(len, s, pad)`, `rpad2(len, s, pad)`, `pad2(len, s, pad)` (멀티바이트 pad 지원) |
| **분할/결합** | `join(list, delim)`, `split(s, regex)` (불변 리스트 반환) |

**특징:**
- `head`/`tail`: 음수 size 지원 (head: 역방향 인덱스, tail: 앞에서 제외)
- `trimLeadingZero`: 정규식/루프 구현 랜덤 분기
- `isBlank`, `repeat`, `join`, `lpad/rpad/pad`: 스트림/루프 구현 랜덤 분기
- `lpad2`/`rpad2`/`pad2`: 멀티바이트 pad 지원 (한글, 이모지 등)
- `split`: 불변 리스트(`List.of`) 반환

```java
StringUtil.head("hello", 2)        // "he"
StringUtil.head("hello", -2)       // "lo" (역방향)
StringUtil.tail("hello", -1)       // "ello" (앞에서 1개 제외)
StringUtil.trimLeadingZero("00.123") // "0.123"
StringUtil.repeat("ab", 3)         // "ababab"
StringUtil.lpad(7, "hi", "0")      // "00000hi"
StringUtil.lpad2(7, "hi", "00")    // "00000hi" (멀티바이트 pad)
StringUtil.pad2(9, "hi", "xy")     // "xyxyhixyx"
StringUtil.split("a,b,c", ",")     // List.of("a", "b", "c") - 불변
```

### CollectionUtil (`s.util.CollectionUtil`)

컬렉션/맵 빈 체크, 결합, 필터링, 집합 연산, 슬라이싱, 색인화, 분류, 맵 생성 등 25개 메서드 제공.

**주요 메서드:**

| 카테고리 | 메서드 |
|----------|--------|
| **빈 체크/기본값** | `isEmpty(coll)`, `isEmpty(map)`, `emptyIfNull(list)`, `emptyIfNull(map)` |
| **결합** | `zip(list1, list2)` → `List<Pair>`, `zip(list1, list2, mixer)` → `List<R>` |
| **배열 변환** | `toArray(list, array)`, `toArray(list)` |
| **필터링/검색** | `findOne(list, pred)`, `findAll(list, pred)` (불변 리스트) |
| **집합 연산** | `unionOf`, `intersectionOf`, `differenceOf`, `symmetricDifferenceOf` (모두 불변 리스트) |
| **슬라이싱** | `slice(list, begin)`, `slice(list, begin, end)`, `head(list, size)`, `tail(list, size)` (불변 리스트) |
| **색인화/분류** | `indexing(list, indexer)` → `Map<K,V>` (불변), `grouping(list, classifier)` → `Map<K,List<V>>` (불변 맵+불변 리스트) |
| **맵 생성** | `asMap(k1,v1,...)`, `asMap(K.class, V.class, k1,v1,...)`, `asMap(entries)`, `copyOf(map)`, `castKeyValue(map)` (모두 불변 맵) |

**특징:**
- **모든 반환 리스트/맵 불변** (`Collections.unmodifiableList/Map`) - 수정 시 `UnsupportedOperationException`
- 스트림/루프 구현 병행, `useStream()`으로 실행 시 랜덤 분기
- `head`/`tail`: 음수 size 지원 (StringUtil과 동일 시맨틱)
- 스트림 구현 시 `HashMap::new` 등 팩토리 명시 후 `unmodifiableMap` 래핑
- `grouping`: 내부 리스트도 불변화

```java
List<Integer> list = List.of(1, 2, 3, 4, 5);

CollectionUtil.head(list, 2)       // [1, 2]
CollectionUtil.head(list, -2)      // [4, 5] (역방향)
CollectionUtil.tail(list, -1)      // [2, 3, 4, 5] (앞에서 1개 제외)

CollectionUtil.zip(List.of(1,2), List.of("a","b")) 
// [(1, a), (2, b)] - 불변 리스트

CollectionUtil.grouping(List.of("apple", "banana", "cherry"), s -> s.substring(0, 1))
// {a=[apple], b=[banana], c=[cherry]} - 불변 맵 + 불변 내부 리스트

Map<String, Integer> map = CollectionUtil.asMap("foo", 42, "bar", 43);
// 불변 맵 - map.put("baz", 1) 시 UnsupportedOperationException
```

### 튜플 타입 (`s.type.tuple`)

불변 튜플 클래스. JSON 직렬화 지원.

| 클래스 | 요소 수 | 생성 |
|--------|---------|------|
| `Pair<T,U>` | 2 | `Pair.of(t, u)` |
| `Triplet<T,U,V>` | 3 | `Triplet.of(t, u, v)` |
| `Quartet<T,U,V,W>` | 4 | `Quartet.of(t, u, v, w)` |

**공통 특징:**
- `@EqualsAndHashCode` (Lombok) - 값 기반 동등성
- 불변 객체 (final 필드, setter 없음)
- Getter: `ord1()`~`ord4()` (`@JsonProperty("1")`~`("4")` 적용)
- `toString()`: `(t, u, v, w)` 형식

```java
Pair.of("hello", 42)                          // (hello, 42)
Triplet.of("a", 1, 3.14)                      // (a, 1, 3.14)
Quartet.of("x", 10, true, "y")                // (x, 10, true, y)

// JSON 직렬화 (Jackson)
new ObjectMapper().writeValueAsString(Pair.of("hello", 42))
// {"1":"hello","2":42}
```

## 테스트

총 **53개 테스트** 통과:

| 테스트 클래스 | 테스트 수 |
|--------------|----------|
| `HelloTest` | 3 |
| `StringUtilTest` | 20 |
| `TupleTest` | 9 |
| `CollectionUtilTest` | 21 |

```bash
./mvnw test
```

## 라이선스

MIT License