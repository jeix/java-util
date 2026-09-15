# DECISION - java-util

## 1. 빌드 도구로 Maven Wrapper를 사용한다

- 결정: 커밋된 `mvnw`(script-only wrapper, Maven 3.9.16)로 빌드/테스트를 실행한다.
- 대안: Gradle Wrapper, 시스템 `mvn`/`gradle` 직접 사용.
- 근거: 빌드 도구가 설치되어 있지 않은 환경을 전제로 하므로 저장소에 Wrapper를 포함해야 합니다.
  script-only 방식을 선택해 `maven-wrapper.jar` 같은 바이너리 파일을 커밋하지 않아도 되게 했습니다.

## 2. 의존성 버전은 로컬 캐시에 존재하는 조합으로 고정한다

- 결정: Lombok 1.18.34, Jackson 2.17.1, JUnit Jupiter 5.10.2, compiler plugin 3.13.0,
  surefire plugin 3.2.5, jar plugin 3.5.0.
- 대안: 최신 버전 사용, 플러그인 버전 미지정(기본값 사용).
- 근거: 네트워크가 막힌 환경에서도 `~/.m2/repository` 캐시만으로 빌드와 테스트가 재현되어야 합니다.
  기본(default) 버전에 의존하면 캐시에 없는 플러그인을 내려받으려다 실패할 수 있어 플러그인 버전을 명시했습니다.
  jar plugin은 캐시 상태를 확인해 3.5.0을 선택했습니다. 3.4.2는 `.pom`이 없어 의존성을 해석하지 못했고,
  3.3.0은 `maven-archiver`, `plexus-utils` 등 전이 의존성이 캐시에 없어 오프라인에서 `package`가 실패했습니다.

## 3. 샘플 클래스는 Lombok과 Jackson을 함께 사용해 검증한다

- 결정: `s.Hello`에서 Lombok(`@Data`, `@Builder`)으로 접근자/빌더를 생성하고,
  Jackson(`ObjectMapper`)으로 JSON 변환 메서드를 제공하며, `s.HelloTest`가 두 기능을 모두 테스트합니다.
- 근거: 구조 초기화 여부를 한 번의 테스트 실행으로 확인할 수 있습니다.

## 4. 이 브랜치와 무관한 파일은 커밋하지 않는다

- 결정: `.gitignore`에 `target/`, `.env*`를 추가하고, 다른 브랜치에서 만들어진 `target/` 내용과
  `.env-claude.sh`는 스테이징하지 않습니다.
- 근거: 요청대로 이 브랜치와 관련 없는 파일을 저장소에 추가하지 않기 위함입니다.

## 5. StringUtil은 파라미터 검사를 먼저 하고 정상 케이스를 처리한다

- 결정: 모든 public 메서드에서 파라미터를 먼저 검사하고, 정상적으로 처리할 수 없으면 그대로 리턴합니다.
  `null`/빈 문자열은 같은 값을 그대로 반환하고, 인덱스는 `slice`/`head`/`tail`에서 0 또는 문자열 길이로 보정합니다.
- 대안: 예외를 던지거나, 값을 임의로 만들어 반환.
- 근거: 요청한 구현 스타일이며, 유틸리티 호출부에서 방어 코드를 줄일 수 있습니다.
  `substring`처럼 예외를 던지지 않으므로 테스트에서 경계값을 문서처럼 확인할 수 있습니다.

## 6. 오버로드는 구현 메서드 하나로 위임한다

- 결정: 공급자 기반 `firstNonBlankOrLast`, `firstNonBlankOrEmpty`, `firstNonBlankOrNull` 오버로드(2~5개)는
  각각 `_firstNonBlankOrLast`, `_firstNonBlankOrEmpty`, `_firstNonBlankOrNull`에 위임합니다.
  값 기반 `firstNonBlankOrEmpty`, `firstNonBlankOrNull`은 값 기반 `firstNonBlankOrLast`를 호출하고,
  `_firstNonBlankOrEmpty`/`_firstNonBlankOrNull`은 `_firstNonBlankOrLast`를 호출합니다.
- 대안: 오버로드마다 구현을 중복.
- 근거: 요청한 스타일이며, 판정 규칙이 한 곳에만 존재해 수정 시 누락 위험이 줄어듭니다.

## 7. 공급자 오버로드는 지연 평가를 유지한다

- 결정: 공급자 기반 메서드는 유효한 값을 찾은 뒤의 공급자를 호출하지 않습니다.
  값 기반 메서드에 위임하지 않고 `_get`으로 값을 꺼내 비교하는 이유입니다.
- 대안: 값 기반 메서드에 위임(기본값을 항상 평가).
- 근거: 기본값 계산 비용이나 부수 효과를 피하려는 것이 공급자 오버로드의 목적이기 때문입니다.
  테스트(`nonNullOfSupplier`, `firstNonBlankOrLastSupplierLazy`)로 호출 횟수를 검증합니다.

## 8. 패딩 계열도 위임 구조로 구현한다

- 결정: `lpad`/`rpad`/`pad`는 각각 `lpad2`/`rpad2`/`pad2`에 단일 문자를 넘겨 호출하고,
  `pad2`는 `lpad2`와 `rpad2`를 조합합니다. 남는 길이가 홀수이면 오른쪽을 1글자 길게 채웁니다.
- 대안: 단일 문자 패딩을 별도로 구현.
- 근거: 여러 문자 패딩 구현 하나만 유지하면 되고, 홀수 처리 규칙도 한 곳에서 결정됩니다.

## 9. 단순 분기는 3항 연산자로 표현한다

- 결정: `isBlank`, `isEmpty`, `nonNullOf`, `nonBlankOf`, `nonEmptyOf`, `_get`, 값 기반 `firstNonBlankOrEmpty`,
  `firstNonBlankOrNull`과 각 private 구현은 `if` 대신 3항 연산자로 결과를 한 줄에 반환합니다.
- 대안: `if`로 분기하고 각 분기에서 `return`.
- 근거: 요청한 스타일이며, 값 선택 로직이 짧아 한 줄로 읽는 편이 분기 흐름을 파악하기 쉽습니다.
  공급자 오버로드에서 기본값 호출을 미루기 위해 지역 변수에 값을 담은 뒤 3항 연산자로 선택합니다.

## 10. firstNonBlankOrLast는 반복문과 스트림 구현을 두고 랜덤 분기한다

- 결정: 값 기반과 공급자 기반 모두 `_firstNonBlankOrLastByLoop`(반복문)와 스트림 구현
  (`_firstNonBlankOrLastByStream`, `_firstNonBlankOrLastSupplierByStream`)을 두고,
  `ThreadLocalRandom.current().nextBoolean()`으로 실행 시점에 하나를 선택합니다.
- 대안: 반복문만 유지, 스트림만 사용, 랜덤 대신 고정 분기.
- 근거: 두 구현이 같은 결과를 내는지 비교할 수 있고, 요청한 스타일에 맞춰 실행마다 다른 구현이 선택됩니다.
  스트림 구현은 `findFirst()`의 단락 평가를 이용해 공급자의 지연 평가를 그대로 유지하고, 모두 공백일 때의
  마지막 값은 스트림을 다시 돌지 않고 위치로 꺼내 `null` 요소도 안전하게 처리합니다.
  테스트는 100회 반복해 두 구현이 같은 값을 반환하는지 확인합니다(`firstNonBlankOrLastRandomBranch`).

## 11. 음수 인덱스는 역방향 인덱스로 해석한다

- 결정: `slice`의 시작/끝 인덱스와 `head`의 `size`가 음수이면 뒤에서부터 세는 역방향 인덱스로 해석합니다
  (`_toAbsoluteIndex`로 `length + index` 변환 후 범위 보정). `tail`의 음수 `size`는 부호를 바꿔 앞에서 제외할
  문자 수로 해석합니다.
- 대안: 음수를 0으로 처리(이전 동작), 잘라내지 않고 전체 반환.
- 근거: 파이썬 슬라이스처럼 "뒤에서부터"를 표현할 수 있어 호출부가 길이를 직접 계산하지 않아도 됩니다.
  `head`는 `slice`에 위임하므로 음수 처리 로직을 중복해서 갖지 않습니다.

## 12. repeat의 반복 단위는 String으로 받는다

- 결정: `repeat(String c, int size)`로 선언하고 기존 `_repeat(String unit, int size)`에 위임합니다.
- 대안: `char`를 받는 오버로드를 함께 유지.
- 근거: 여러 문자 단위를 반복하는 경우까지 같은 메서드로 처리할 수 있고, 결과 길이를 넘으면 잘라냅니다
  (`repeat("ab", 3)` → `"aba"`).

## 13. 튜플은 데이터 타입으로 구현한다

- 결정: `Pair`, `Triplet`, `Quartet`은 `private final` 필드(`cat`, `dog`, `elk`, `fox`)와 private 생성자를 두고,
  `of(...)` 스태틱 팩터리로만 인스턴스를 만듭니다. 값은 인스턴스 메서드 `ord1()`~`ordN()`으로 읽고,
  `toString()`은 `(값1, 값2, ...)` 형식으로 직접 구현합니다.
- 대안: `record` 사용, Lombok `@Value`/`@Getter`/`@AllArgsConstructor` 사용, public 생성자 제공.
- 근거: 유틸리티가 아닌 데이터 타입이므로 접근자를 인스턴스 메서드로 두라는 요청을 따랐고,
  생성자를 private으로 막아 `of`로만 만들도록 통일했습니다. `toString`은 Lombok 기본 형식
  (`Quartet(cat=..., dog=...)`)이 아니라 요청한 `(t, u, v, w)` 형식이 필요해 직접 구현했습니다.
  값 비교는 Lombok `@EqualsAndHashCode`가 필드 전체를 사용합니다.

## 14. ordN() 메서드를 Jackson 변환 대상으로 지정한다

- 결정: `ord1()`, `ord2()`, `ord3()`, `ord4()` 메서드에 각각 `@JsonProperty("ord1")`, `@JsonProperty("ord2")`,
  `@JsonProperty("ord3")`, `@JsonProperty("ord4")`를 붙입니다. 필드에는 애노테이션을 두지 않습니다.
- 대안: 필드에 `@JsonProperty` 지정(이전 구현), getter 이름을 `getCat()` 형태로 변경, 애노테이션 없이 두고
  호출부에서 필드 가시성을 열어 처리.
- 근거: 접근자 이름이 `ordN()`이라 Jackson 기본 규칙(getter/공개 필드)으로는 값을 찾지 못해 직렬화에 실패합니다.
  요청대로 `ordN()`을 JSON 변환 대상으로 지정하고, `private final` 필드는 값 저장 용도로만 남겼습니다.
  JSON 키명을 필드명(`cat`, `dog`, `elk`, `fox`)이 아니라 메서드명과 같은 `ordN`으로 두어 값의 위치가 키에 드러나고,
  다른 튜플 타입과도 키 규칙이 일치합니다.
  private 생성자만 있어 역직렬화는 지원하지 않으며, 필요해지면 `@JsonCreator`를 추가합니다.

## 15. JSON 테스트는 프로퍼티 순서에 의존하지 않게 작성한다

- 결정: `TupleTest`의 JSON 검증은 직렬화 문자열을 그대로 비교하지 않고 `ObjectMapper.readTree` 결과를 비교합니다.
- 대안: 문자열 비교 유지, `@JsonPropertyOrder`로 순서 고정.
- 근거: 메서드 애노테이션으로 직렬화하면 Jackson이 리플렉션에서 발견한 순서를 사용해 선언 순서와 다를 수 있고
  (`{"dog":2,"cat":"cat"}`), JSON 객체의 프로퍼티 순서는 의미가 없습니다. 순서를 고정하고 싶다면
  `@JsonPropertyOrder`를 추가하면 됩니다.
