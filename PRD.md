# java-util 요구사항

## 목표와 범위

JDK 21 환경에서 자바 유틸리티 클래스와 JUnit 테스트를 쉽게 추가하고 빌드·실행하는 단일 모듈 라이브러리를 만들어요.
Maven 사전 설치 없이 Wrapper로 실행하며 Lombok과 Jackson을 사용할 수 있어야 해요.
초기 구조 확인용 `s.Hello`와 `s.HelloTest`를 포함해요. 현재 범위는 라이브러리 JAR와 테스트이며 서버·UI·실행 애플리케이션은 요구사항에 없어요.

## StringUtil API

`s.util.StringUtil`의 다음 기능은 public static으로 제공하고 `s.util.StringUtilTest`에서 검증해요.

| 기능 | 요구 메서드와 인자 |
| --- | --- |
| 판별 | `isBlank(String)`, `isEmpty(String)` |
| 기본값 | `nonNullOf`, `nonBlankOf`, `nonEmptyOf`: `(String value, String dflt)`와 `(Supplier<String>, Supplier<String>)` |
| 첫 유효값 | `firstNonBlankOrLast`, `firstNonBlankOrEmpty`, `firstNonBlankOrNull`: `(String value1, String value2, String... values)`와 Supplier 2·3·4·5개 오버로드 |
| 문자열 변환 | `stringify(Object)` |
| 부분 문자열 | `slice(String, int begin)`, `slice(String, int begin, int end)`; begin 포함, end 불포함 |
| 앞·뒤 추출 | `head(String, int size)`, `tail(String, int size)`; 원문 길이 이내 추출 |
| 기타 변환 | `trimLeadingZero(String)`, `repeat(String, int size)`, `reverse(String)` |
| 단일 문자 패딩 | `lpad`, `rpad`, `pad`: `(int len, String s, String pad)` |
| 문자열 패딩 | `lpad2`, `rpad2`, `pad2`: `(int len, String s, String pad)` |
| 결합·분할 | `join(List<T>, String delimiter)`, `split(String, String regex)` → `List<String>` |
| 함수 합성 | `pipe(Function<String,String>... fns)` → `Function<String,String>`, `pipe()` → `Pipeline` |

Supplier 선택 메서드는 각각 private static `_firstNonBlankOrLast`, `_firstNonBlankOrEmpty`, `_firstNonBlankOrNull`에 위임해요. 이 메서드들은 Supplier 2개와 나머지 Supplier 가변 인자를 받아요.
`lpad`, `rpad`, `pad`의 `pad`는 UTF-8 기준 단일 바이트 문자 하나로 제한하고, `lpad2`, `rpad2`, `pad2`는 임의 길이 문자열 패턴을 허용해요. 구현 스타일은 [AGENTS.md](AGENTS.md)를 따라요.

### StringUtil 후속 변경

- for-loop를 stream으로 바꿀 수 있는 처리는 두 구현을 유지하고 실행할 때 무작위로 선택해요.
- `nonNullOf`, `nonBlankOf`, `nonEmptyOf`, `_supply`는 삼항 연산자를 사용해요.
- `stringify`는 Java 21 pattern matching `switch`를 사용해요.
- `slice`는 음수 인덱스를 끝 기준으로 해석하고 인덱스를 `0..length`로 제한해요.
- `head`의 음수 size는 끝 기준 인덱스, `tail`의 음수 size는 절댓값만큼 앞에서 제외할 인덱스로 해석해요.
- `trimLeadingZero`는 loop와 정규식 치환 구현 중 하나를 무작위로 선택해요.
- `repeat`의 반복 대상과 `lpad`/`rpad`/`pad`의 pad는 `String`이에요. pad는 single-byte 문자 하나만 허용해요.
- `split`은 불변 리스트를 반환해요.
- 함수 배열형 `pipe`는 입력값 기반 reduce와 `Function::andThen` 합성 구현을 모두 유지하고 실행할 때 무작위로 선택해요. 인자 없는 `pipe`는 immutable `Pipeline`의 `then`·`apply` API를 제공해요.

## 후속 확정 요구사항

- `stringify`의 `java.util.Date` 입력은 연도-월-일 형식으로 출력해요.
- `BigDecimal` 입력에는 `toPlainString()`을 호출해요.
- `StringUtilTest`에 Lombok `@Slf4j`를 적용해요.

## CollectionUtil API

`s.util.CollectionUtil`과 `s.util.CollectionUtilTest`를 추가해요. 아래 API는 public static이에요.

| 기능 | 요구 API |
| --- | --- |
| 판별·대체 | List/Map 각각 `isEmpty`, `emptyIfNull` |
| 묶기 | `zip(list1, list2)` → `List<Pair<T,U>>`, `zip(list1, list2, BiFunction<T,U,R>)` → `List<R>` |
| 배열 | `toArray(List<T>)` → `T[]` |
| 검색 | `findOne(List<T>, Predicate<T>)` → `T` 또는 null, `findAll` → `List<T>` |
| 집합 연산 | `unionOf`, `intersectionOf`, `differenceOf`, 대칭차 (모두 리스트 2개 입력) |
| 추출 | `slice(list, begin)`, `slice(list, begin, end)`, `head(list, size)`, `tail(list, size)` |
| 색인·분류 | `indexing(List<T>, Function<T,String>)` → `Map<String,T>`, `grouping` → `Map<String,List<T>>` |
| 맵 생성 | `asMap(Object... items)`, `asMap(Class<K>, Class<V>, Object... items)`, `asMap(List<Map.Entry<K,V>>)` |
| 맵 변환·복사 | `castKeyValue(origin)` → `Map<K,V>`, `copyOf(origin)` → `Map<K,V>` |

목록에 중복 기재된 대칭차 `unionOf`는 영어 용어인 symmetric difference에 맞춰 `symmetricDifferenceOf`로 구분했어요.
배열 타입을 명시하는 `toArray(list, Class<T>)`도 제공해요. 이 두 선택은 제안한 구현 기준이며 별도 확정 응답은 없었어요.
구체적인 경계값과 반환 컬렉션의 공유 여부는 README에 기록해요.

### CollectionUtil 후속 변경

- 컬렉션을 반환하는 모든 메서드는 불변 리스트 또는 불변 맵을 반환해요. 중첩된 그룹 리스트도 불변이에요.
- for-loop나 while-loop를 stream으로 바꿀 수 있는 처리는 두 구현을 유지하고 실행할 때 무작위로 선택해요.
- List/Map `emptyIfNull`은 삼항 연산자를 사용해요.
- `slice`는 음수 인덱스를 끝 기준으로 해석하고 인덱스를 `0..size`로 제한해요.
- `head`의 음수 size는 끝 기준 인덱스, `tail`의 음수 size는 절댓값만큼 앞에서 제외할 인덱스로 해석해요.

## Tuple API

`s.type.tuple.Pair<T,U>`, `Triplet<T,U,V>`, `Quartet<T,U,V,W>`와
JUnit 기반 `s.type.tuple.TupleTest`를 제공해요. 기존 `s.util.Pair`는 새 Pair로 대체해요.

- 각 타입은 값 개수에 맞는 public static `of(...)` factory를 제공해요.
- 값은 JSON 변환 대상인 instance 메서드 `ord1()`~`ord4()`로 노출해요.
- `toString()`은 값을 `(t, u)`, `(t, u, v)`, `(t, u, v, w)` 형식으로 반환해요.
- Lombok `@EqualsAndHashCode`를 적용해 모든 순서 값으로 동등성과 해시 코드를 계산해요.
- CollectionUtil의 `zip(list1, list2)`는 새 `s.type.tuple.Pair`를 반환해요.

## 검증 기준

전체 빌드와 JUnit 테스트가 통과해야 해요. StringUtil은 정상값·경계값, 음수·범위 밖 인덱스,
Supplier 지연 평가와 호출 순서, for-loop·stream 구현의 동등성, 불변 `split` 결과,
날짜·숫자 변환, 두 `pipe` 구현의 적용 순서와 동등성을 검증해요. CollectionUtil은 null/빈 입력, 순서·중복, 콜백 평가와 예외,
배열 타입, 음수·범위 밖 인덱스, loop·stream 구현의 동등성, 반환 컬렉션의 불변성,
맵 생성·형변환·복사를 검증해요.
Tuple은 factory와 순서별 접근자, null, 문자열 표현, 동등성·해시 코드, Jackson JSON 왕복 변환을 검증해요.
현재 세부 동작은 README의 [문자열 유틸리티](README.md#문자열-유틸리티)와
[컬렉션 유틸리티](README.md#컬렉션-유틸리티), 명세에 없는 동작의 선택 근거는 [DECISION.md](DECISION.md)에 기록해요.
