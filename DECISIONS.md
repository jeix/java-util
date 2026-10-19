# 결정 기록

번호를 매겨 누적하는 기록이에요. 한 번 적은 항목은 고치지 않고, 바뀌면 새 항목을 추가해요.

## 1. 브랜치 독립성 (2026-10-19)

- **결정**: 이 브랜치(`claude-claude`)는 다른 브랜치를 참조하지 않고 독자적으로 진행해요. 다른 브랜치에만 관련된 파일·디렉터리는 git에 추가하지 않아요.
- **이유**: 저장소에는 브랜치마다 같은 초기화 작업을 각자 수행한 결과가 있어요. 다른 브랜치의 내용을 가져오면 결과 비교가 의미 없어져요.
- **적용**: `.gitignore`에 `.agents/`, `.codex/`, `.env-*`, `.claude/settings.local.json`을 넣었어요. `.env-*`에는 인증 토큰이 있어서 브랜치와 관계없이 커밋하면 안 돼요.

## 2. 빌드 도구는 Maven wrapper (2026-10-19)

- **결정**: Maven을 직접 설치하지 않고 wrapper(`mvnw`)로 빌드해요. wrapper는 JAR 없는 `only-script` 방식이고, Maven 3.9.11을 써요.
- **이유**: 환경에 `mvn`이 없고, wrapper를 쓰라는 요구가 있었어요. `only-script`는 바이너리(`maven-wrapper.jar`)를 저장소에 넣지 않아도 돼요.
- **트레이드오프**: 첫 실행 때 네트워크로 Maven 배포본을 내려받아요. `mvn wrapper:wrapper`를 쓰지 못해서 Maven Central의 `maven-wrapper-distribution` 3.3.4 `only-script` 배포본에서 스크립트를 받아 넣었어요.

## 3. 의존성과 버전 (2026-10-19)

- **결정**: Java 21(`maven.compiler.release`), lombok 1.18.44, jackson-databind 2.21.1, JUnit Jupiter 5.14.1을 써요.
- **이유**: JDK 21이 설치되어 있어요. 라이브러리는 요구한 lombok·jackson·JUnit이에요.
- **lombok**: `provided` 스코프로 두고 `maven-compiler-plugin`의 `annotationProcessorPaths`에도 등록했어요. JDK 21에서는 암묵적 어노테이션 프로세서 탐색에 기대지 않고 명시해야 안정적이에요.

## 4. 초기 구조 확인용 예제 (2026-10-19)

- **결정**: `s.Hello`(유틸리티 클래스)와 `s.HelloTest`(JUnit 테스트)를 만들어 `./mvnw test`로 구조를 확인해요.
- **이유**: 빌드, lombok 처리, jackson 사용, 테스트 실행이 한 번에 검증돼요. `Hello`가 lombok(`@NoArgsConstructor`)과 jackson(`ObjectMapper`)을 모두 쓰는 이유예요.

## 5. StringUtil 동작 규칙 (2026-10-19)

- **결정**: `s.util.StringUtil`은 null 문자열을 ""로 취급하고, 처리할 수 없는 입력은 예외 없이 `s`(또는 "")를 돌려줘요. 구현은 오버로드 중 하나(supplier 버전, `_firstNonBlankOr*`)에 모으고 나머지는 위임해요.
- **세부 규칙**: `head`/`tail`은 size가 음수이거나 길이보다 크면 "". `slice`는 end를 길이로 보정하고 begin이 음수이거나 end 이상이면 "". `lpad`/`rpad`/`pad`는 패딩 불가(len ≤ 길이, 싱글바이트 아님, pad 길이 ≠ 1)이면 `s` 그대로이고 잘라내지 않아요. `pad`/`pad2`는 남는 칸이 홀수면 오른쪽이 하나 더. `split`은 뒤쪽 빈 문자열을 남기고(limit -1), `stringify(null)`은 "", `Date`는 시스템 기본 시간대의 `yyyy-MM-dd`.
- **이유**: 요구 사항에 정해지지 않은 경계를 위와 같이 정했어요. 바꾸려면 새 항목을 추가해요.
- **의존성**: `@Slf4j`를 쓰려고 `slf4j-api` 2.0.19(compile)와 `slf4j-simple`(test)를 추가했어요.

## 6. StringUtil 수정: 무작위 분기와 인덱스 규칙 (2026-10-19)

- **결정**: 5번 항목의 규칙 중 아래를 바꿔요(5번은 고치지 않고 이 항목이 우선해요).
  - `stringify(null)`은 null. `join`은 null 원소를 ""로 이어요.
  - `slice`는 음수 인덱스를 뒤에서부터 센 위치로 보고 범위를 [0, 길이]로 보정해요. `head(s, size)`는 `slice(s, 0, size)`와 같고(길이보다 크면 전체), `tail`의 음수 size는 앞에서 -size개를 뺀 나머지예요. 더는 "범위 밖이면 빈 문자열"이 아니에요.
  - `repeat`의 첫 파라미터는 `String`. `lpad`/`rpad`/`pad`의 pad는 싱글바이트 한 글자(`"가"`, `"do"`는 불가). `split`은 불변 리스트를 돌려줘요.
- **무작위 분기**: 반복문 구현과 stream 구현(`firstNonBlankOr*`, `join`, varargs → supplier 변환), 반복문과 정규식 구현(`trimLeadingZero`)을 두고 호출 때마다 `_useStream()`으로 무작위로 골라요. 두 구현은 결과가 같아야 해요.
- **테스트**: 분기가 있는 메서드의 테스트는 `@RepeatedTest(20)`으로 두 구현을 모두 거치게 했어요(한쪽만 20번 연속 걸릴 확률 약 2⁻¹⁹).

## 7. 튜플 타입 (2026-10-19)

- **결정**: `s.type.tuple.Pair`/`Triplet`/`Quartet`은 불변 final 클래스예요. 생성자는 private이고 `of(...)` 스태틱 팩터리로만 만들어요. 값은 `ord1()`~`ordN()`으로 읽고, `toString()`은 `(a, b, c)` 형식이에요(null은 `null`). `@EqualsAndHashCode`를 붙였어요.
- **JSON**: 접근자가 getter 이름(`getXxx`)이 아니라서, 필드 `ord1`~`ordN`에 `@JsonProperty`를 붙여 직렬화 대상으로 했어요. 직렬화만 다루고 역직렬화는 지원하지 않아요(요구 사항에 없어서).

## 8. 튜플 역직렬화 추가 (2026-10-19)

- **결정**: 7번의 "역직렬화 미지원"을 뒤집어요. private 생성자에 `@JsonCreator`와 `@JsonProperty("ordN")` 파라미터를 붙여 `{"ord1":..,"ord2":..}`를 읽어요. 제네릭 타입은 `TypeReference`로 지정해야 해요. 없는 키는 null이 돼요.

## 9. CollectionUtil 동작 규칙 (2026-10-19)

- **결정**: `s.util.CollectionUtil`은 `StringUtil`(5·6번)과 같은 방식이에요. null 리스트·맵은 빈 컬렉션으로 취급하고, 처리할 수 없는 입력은 예외 없이 빈 컬렉션을 돌려줘요. 새로 만드는 리스트·맵은 수정할 수 없고 맵은 삽입 순서를 유지해요. `emptyIfNull`은 null이 아니면 원본을 그대로 돌려줘요.
- **세부 규칙**:
  - `slice`/`head`/`tail`은 `StringUtil`과 같은 인덱스 규칙(음수는 뒤에서부터, 범위 보정)이고 `subList`의 뷰가 아니라 복사본을 돌려줘요.
  - 집합 연산은 중복을 그대로 두고 상대 리스트에 들어 있는지만 봐요(`unionOf`는 list1 + (list2 − list1), `intersectionOf`는 list1 − (list1 − list2)).
  - `zip`은 짧은 쪽 길이에 맞춰요. `indexing`은 같은 색인이면 뒤의 항목이 이겨요.
  - `asMap(items...)`는 개수가 홀수이면 빈 맵이고, 같은 키는 뒤의 것이 이겨요. `asMap(keyClass, valueClass, items...)`가 실제 구현이고 타입이 맞지 않으면 `ClassCastException`을 던져요(`asMap(items...)`는 `Object.class`로 위임). `castKeyValue`는 복사 없이 타입 인자만 바꾸고 검사하지 않아요.
  - `toArray(list)`는 `Class` 인자가 없어서 원소들의 공통 상위 클래스를 배열 타입으로 써요. 비었거나 전부 null이면 `Object[]`라서 `String[]` 변수에 받으면 `ClassCastException`이 나요(그런 경우 `list.toArray(new String[0])`).
- **stream**: 이 클래스는 반복문 대신 stream을 쓰고, `StringUtil`처럼 무작위로 분기하지는 않아요(요청에 없어서).

## 10. DateTimeUtil 동작 규칙 (2026-10-19)

- **결정**: `s.util.DateTimeUtil`의 날짜·시간 파라미터와 반환값은 `eval`·`eom`에서 `String`이고(`yyyyMMdd` 또는 `yyyy-MM-dd`), `StringUtil`·`CollectionUtil`과 같게 처리할 수 없는 입력은 예외 없이 null(boolean은 false)을 돌려줘요. `stringify*(null)`도 null.
- **세부 규칙**:
  - 파싱은 `ResolverStyle.STRICT`라서 없는 날짜("20240230")나 24시는 null이에요. 구분자 없는 형식과 있는 형식은 문자열 길이로 가려요(8/10, 6/8, 14/19). 앞뒤 공백은 허용하지 않아요.
  - `eval(date, op, n, unit)`의 결과는 입력과 같은 형식이에요. 달·년 계산에서 해당 날짜가 없으면 그 달의 마지막 날로 맞춰요(`20240131` + 1 M → `20240229`). `op`·`unit`은 대소문자를 구분하고(`"d"`는 null), 범위를 넘는 계산도 null이에요. `n`이 음수이면 방향이 반대예요.
  - `eom`도 입력과 같은 형식으로 돌려줘요. `toTimestamp(LocalDate)`는 그날 0시예요.
  - `today()`/`date()`/`time()` 등은 시스템 기본 시간대의 현재 시각이에요. 시계를 주입하지 않아서 테스트는 형식과 전후 시각 범위만 확인해요.

## 11. StringUtil.pipe (2026-10-19)

- **결정**: `StringUtil`에 함수 합성 두 가지를 추가했어요. `pipe(fns...)`는 `Function<String,String>`을 차례로 이은 하나의 함수를 돌려주고(`Function.identity()`에서 `Function::andThen`으로 `reduce`), `pipe()`는 `.then(f).then(g).apply(s)` 형태의 불변 `StringUtil.Pipeline`을 돌려줘요.
- **이유**: 제시된 codelet 두 가지를 따랐어요. 병렬 stream이 아니라서 combiner가 필요한 `reduce` 구현은 쓰지 않고 `andThen` 방식을 골랐어요.
- **세부 규칙**: `fns`가 null이거나 비었으면 입력 그대로이고 null 함수는 건너뛰어요. `Pipeline.then(null)`은 자기 자신을 돌려줘요. 입력 null은 첫 함수가 처리해요. 이 메서드들은 `_useStream()` 무작위 분기를 쓰지 않아요.
