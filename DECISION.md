# DECISION - 설계 결정 기록

## 1. 빌드 도구: Maven Wrapper
- **결정**: Maven Wrapper(`./mvnw`) 사용
- **근거**: 빌드 도구가 설치되어 있지 않을 수 있어, wrapper를 통해 빌드 환경을 표준화

## 2. Java 버전: JDK 21
- **결정**: JDK 21 사용
- **근거**: 사용자가 JDK 21이 설치되어 있다고 명시

## 3. 튜플 타입 상속 구조
- **결정**: Pair → Triplet → Quartet 상속 구조
- **근거**: 코드 재사용을 통해 중복 최소화, 각 하위 클래스는 추가 필드와 메서드만 구현

## 4. @EqualsAndHashCode(callSuper = false)
- **결정**: Triplet, Quartet에 `@EqualsAndHashCode(callSuper = false)` 적용
- **근거**: Lombok warning 회피, 각 튜플 타입의 equals/hashCode가 자신의 필드만 고려하도록 함

## 5. JSON 직렬화: @JsonProperty
- **결정**: 각 필드에 `@JsonProperty("ord1")`, `@JsonProperty("ord2")` 등 적용
- **근거**: JSON 변환 시 필드명을 ord1, ord2, ord3, ord4로 일관되게 매핑

## 6. toString 형식
- **결정**: `(t, u, v, w)` 형식 사용
- **근거**: 튜플의 수학적 표현과 일치, 가독성 높음

## 7. head/tail 음수 size 처리
- **결정**: 
  - head: 음수 size = `len + size` (역방향 인덱스)
  - tail: 음수 size = `len - size` (뒤에서부터 제외할 개수)
- **근거**: 직관적인 음수 인덱스 동작, 길이 클램핑으로 안정성 확보

## 8. split 불변 리스트 반환
- **결정**: `Collections.unmodifiableList()`로 래핑하여 반환
- **근거**: 반환된 리스트의 불변성 보장, 예상치 못한 수정 방지

## 9. repeat 파라미터 타입
- **결정**: `char` → `String`으로 변경
- **근거**: 더 유연한 사용 (단일 문자뿐만 아니라 문자열 반복 가능)

## 10. firstNonBlank* varargs 처리
- **결정**: stream과 for문을 `RANDOM.nextBoolean()`으로 랜덤 분기
- **근거**: 두 구현 모두 테스트 커버리지 확보

## 11. CollectionUtil 랜덤 분기 (stream 구현 추가)
- **결정**: `unionOf`, `intersectionOf`, `differenceOf`, `symmetricDifferenceOf`뿐 아니라
  `zip(Pair)`, `zip(BiFunction)`, `asMap(Object...)`, `castKeyValue`,
  `asMap(Class, Class, Object...)`, `asMap(List<Map.Entry>)`에도 stream 구현을 추가하고
  `RANDOM.nextBoolean()`으로 for-loop / stream 구현을 랜덤 분기
- **근거**: 코드베이스 전반에 걸친 구현 이중화·커버리지 확보, 기존 `firstNonBlank*` 패턴과 일관성 유지

## 12. StringUtil.pipe / Pipeline (함수 체이닝 빌더)
- **결정**: `pipe(Function<String,String>... fns)` (varargs 합성)와
  `pipe().then(...).apply(input)` (빌더) 두 형태를 제공하며, varargs 형태는
  impl 1 (`Stream.of(fns).reduce(input, accumulator, combiner)`)과 impl 2
  (`Arrays.stream(fns).reduce(Function.identity(), Function::andThen)`) 사이를
  `RANDOM.nextBoolean()`으로 랜덤 분기
- **근거**: 두 구현 모두 동일한 좌→우 함수 적용 결과를 보장, 커버리지 확보
- **참고**: `pipe()` 무인자 호출은 빌더 `Pipeline` 반환( varargs 우선순위 낮음)임에 유의

## 13. DateTimeUtil: @UtilityClass 패턴
- **결정**: `StringUtil`과 동일하게 Lombok `@UtilityClass` 사용
- **근거**: 일관성 유지, 메서드가 암묵적으로 `public static`, 인스턴스화 방지

## 14. DateTimeUtil: 이중 포맷 자동 파싱
- **결정**: `localDateOf` / `localTimeOf` / `localDateTimeOf`에서 포맷 자동 인식
  - `date.contains("-")` → 구분자 포맷 (`yyyy-MM-dd`), `date.contains(":")` → 구분자 포맷 (`HH:mm:ss`, `yyyy-MM-dd HH:mm:ss`)
  - 그 외 → compact 포맷 (`yyyyMMdd`, `HHmmss`, `yyyyMMddHHmmss`)
- **근거**: 호출 코드에서 포맷을 명시할 필요 없이 자연스럽게 양식을 섞어 사용할 수 있음

## 15. DateTimeUtil: DateTimeFormatter 상수 재사용
- **결정**: `DateTimeFormatter.ofPattern(...)`을 `private static final` 상수로 선언
- **근거**: `DateTimeFormatter`는 스레드 안전하므로 재사용 가능, 매번 `ofPattern` 호출 비용 회피

## 16. DateTimeUtil: eval 메서드 오버로드
- **결정**: `eval(String, String, String)` (비교, boolean 반환)과 `eval(String, String, int, String)` (산술, String 반환)으로 오버로드
- **근거**: 인터페이스 시그니처(`eval(date1, op, date2)` vs `eval(date, op, n, unit)`)를 자연스럽게 표현, 파라미터 수가 달라 충돌 없음

## 17. DateTimeUtil: 날짜 비교 연산자 구현
- **결정**: `LocalDate.isEqual` / `isBefore` / `isAfter`를 기반으로 6개 연산자 구현
  - `>=` = `!isBefore`, `<=` = `!isAfter` (Java의 `ChronoLocalDate` 비교 계약에 정확히 부합)
- **근거**: `compareTo` 기반이라서 시간대나 초단위 차이 없이 날짜만으로 정확히 비교

## 18. DateTimeUtil: eom 반환 포맷
- **결정**: `eom(date)`는 `yyyyMMdd` (compact format)으로 반환
- **근거**: `date()` / `datetime()` 등 기본 날짜 조회 메서드의 기본 포맷과 일관성 유지
