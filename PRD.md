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
| 기타 변환 | `trimLeadingZero(String)`, `repeat(char, int size)`, `reverse(String)` |
| 단일 문자 패딩 | `lpad`, `rpad`, `pad`: `(int len, String s, char pad)` |
| 문자열 패딩 | `lpad2`, `rpad2`, `pad2`: `(int len, String s, String pad)` |
| 결합·분할 | `join(List<T>, String delimiter)`, `split(String, String regex)` → `List<String>` |

Supplier 선택 메서드는 각각 private static `_firstNonBlankOrLast`, `_firstNonBlankOrEmpty`, `_firstNonBlankOrNull`에 위임해요. 이 메서드들은 Supplier 2개와 나머지 Supplier 가변 인자를 받아요.
패딩 입력은 단일 바이트 문자열을 전제로 해요. 구현 스타일은 [AGENTS.md](AGENTS.md)를 따라요.

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

## 검증 기준

전체 빌드와 JUnit 테스트가 통과해야 해요. StringUtil은 정상값·경계값, Supplier 지연 평가와 호출 순서,
날짜·숫자 변환을 검증해요. CollectionUtil은 null/빈 입력, 순서·중복, 콜백 평가와 예외,
배열 타입, 부분 리스트 뷰, 맵 생성·형변환·복사를 검증해요.
현재 세부 동작은 README의 [문자열 유틸리티](README.md#문자열-유틸리티)와
[컬렉션 유틸리티](README.md#컬렉션-유틸리티), 명세에 없는 동작의 선택 근거는 [DECISION.md](DECISION.md)에 기록해요.
