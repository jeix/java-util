# DECISION (주요 결정 사항 및 근거)

## D-1. 빌드 도구로 Maven Wrapper를 사용한다

- 배경: 전역 Maven/Gradle이 설치되어 있지 않다.
- 결정: Maven Wrapper(only-script 방식, Maven 3.9.16)를 저장소에 포함한다.
- 근거: 표준 디렉터리 관례와 의존성 관리가 명확하고, 별도 jar 없이 스크립트만으로 부트스트랩할 수 있다.
  로컬 `~/.m2` 캐시를 재사용하므로 최초 실행 시 재다운로드를 피할 수 있다.

## D-2. Java 21을 대상으로 한다

- 배경: 설치된 JDK가 21이다.
- 결정: `maven.compiler.release=21`, 소스 인코딩 UTF-8.
- 근거: 환경에서 바로 컴파일·테스트 가능하며, 최신 언어 기능을 사용할 수 있다.

## D-3. 패키지 `s`, 예제 클래스 `Hello`

- 결정: 패키지는 `s`, 구조 확인용 클래스는 `s.Hello`와 `s.HelloTest`.
- 근거: 요구된 예제 명세를 그대로 따르며, 유틸리티 클래스의 최소 단위 예시로 적합하다.

## D-4. 의존성 버전

- lombok 1.18.48 (provided, `annotationProcessorPaths`로 애노테이션 처리)
- jackson-databind 2.17.1
- junit-jupiter 5.14.1 (test)
- maven-compiler-plugin 3.13.0, maven-surefire-plugin 3.5.4
- 근거: 로컬 `~/.m2` 저장소에 캐시되어 있어 오프라인에서도 빌드 가능하며, Java 21과 호환된다.

## D-5. 무관 파일을 git에 추가하지 않는다

- 결정: `.gitignore`에 `target/`, `.env-claude`를 등록한다.
- 근거: 두 항목은 이 브랜치의 작업과 무관한 파일·산출물이므로 커밋 대상에서 제외한다.
  `.agents/`, `.codex/`는 빈 디렉터리라 git이 추적하지 않는다.

## D-6. 문서 위치

- 결정: 개요는 루트 `README.md`, 운영 문서는 `docs/`에 둔다.
- 근거: 루트는 진입점 역할만 하고, 세션 연속성용 문서(AGENT/PRD/PLAN/DECISION)는 한곳에 모아 관리한다.

## D-7. `StringUtil` 메서드 동작 규약

- 파라미터를 먼저 검사하고 정상 처리할 수 없으면 조기 반환한다. 문자열 입력이 `null`이면 결과도 `null`.
- 오버로드의 실질 구현은 supplier 버전 한 곳에 두고 value 버전이 위임한다. 추가 private 메서드는 `_` 접두사를 붙인다.
- `nonBlankOf`는 `firstNonBlankOrLast`에, `lpad/rpad/pad`는 각각 `lpad2/rpad2/pad2`에, `lpad2/rpad2/pad2`는 `_pad`에 위임한다.
- `firstNonBlankOr*`: 첫 non-blank 반환, 전부 공백이면 `OrLast`=마지막 인자, `OrEmpty`=`""`, `OrNull`=`null`.
- `pad`/`pad2`는 양쪽 중앙 정렬(부족분은 좌=몫, 우=나머지)로 해석한다.
- `slice`: 음수 인덱스는 뒤에서부터 센다(`-1`=마지막). `-length`보다 작으면 0, `length`보다 크면 `length`로 보정하고, 정규화 후 `begin>end`이면 `null`.
- `head`: `size`를 인덱스로 보정해 앞에서부터 반환한다. 음수 `size`는 뒤에서부터 센 위치까지.
- `tail`: 양수 `size`는 뒤에서 `size`개, 음수 `size`는 `-size`를 앞에서 제외할 인덱스로 해석한다.
- `repeat(String c, int size)`: `size<=0`이거나 `c`가 null/빈 문자열이면 `""`.
- `trimLeadingZero`는 `"000"`→`"0"`처럼 최소 한 자리를 유지한다(부호 미처리).
- `stringify`: `switch` 패턴 매칭으로 `java.util.Date`→`yyyy-MM-dd`, `BigDecimal`→`toPlainString()`, 그 외 `String.valueOf`, `null`→`null`.
- `join`은 null/빈 리스트→`""`, 원소는 `stringify` 사용, null 원소는 `"null"`, delimiter null은 `""`. `split`은 `null` 입력 시 빈 리스트.
- `isBlank`/`isEmpty`/`nonNullOf(Supplier,Supplier)`/`nonEmptyOf(Supplier,Supplier)`는 3항 연산자로 구현한다.
- `_firstNonBlankOrLast`/`_firstNonBlankOrEmpty`/`_firstNonBlankOrNull`는 `List` 대신 `Supplier<String>...` 가변 인자를 받는다.

## D-8. 테스트 로깅에 `@Slf4j` 사용

- 결정: `StringUtilTest`에 Lombok `@Slf4j`를 적용하고 `pom.xml`에 `slf4j-api`(provided), `slf4j-simple`(test)를 추가한다.
- 근거: Lombok 로거 생성에는 `slf4j-api`가 필요하고, 테스트 실행 시 provider 경고를 막기 위해 `slf4j-simple`을 test 스코프로 둔다.
  본 소스에는 로깅이 필요 없어 `provided` 스코프로 유지해 런타임 의존을 늘리지 않는다.

## D-9. 루프/대체 구현 이중화와 랜덤 분기

- 결정: `_firstNonBlankOrLast`, `_firstNonBlankOrNull`, `join`, `_pad`, `_suppliers`, `trimLeadingZero`에 기존 루프 구현과 대체 구현(Stream/`String.repeat`/정규식)을 모두 두고, 호출 시 `_coin()`(`ThreadLocalRandom.nextBoolean()`)으로 무작위 분기한다.
- 구조: 모든 분기 지점은 `if (_coin()) { ... } else { ... }` 형태로 작성해 두 구현의 들여쓰기 깊이를 동일하게 맞춘다.
- 근거: 대체 구현을 추가하면서도 기존 동작을 유지하기 위함이다. 두 분기의 결과가 같은지 `randomBranchesProduceConsistentResults` 테스트에서 100회 반복 호출로 검증한다.

## D-10. 튜플 타입 `s.type.tuple` 설계

- 결정: `Pair<T,U>`, `Triplet<T,U,V>`, `Quartet<T,U,V,W>`를 만든다. 필드는 불변 `private final`(`cat`, `dog`, `elk`, `fox` 순), 생성자는 `private`, 생성은 정적 팩터리 `of`만 사용한다.
- 접근자는 데이터 타입이므로 인스턴스 메서드 `ord1()`~`ordN()`으로 제공하고, 각 메서드에 `@JsonProperty("ordN")`을 붙여 JSON 키를 `ord1`~`ordN`으로 직렬화한다.
- `toString()`은 `(cat, dog, ...)` 형식으로 직접 구현한다.
- Lombok `@EqualsAndHashCode`로 값 동등성/해시를 생성한다.
- 근거: 순서가 있는 값 묶음을 간결하게 표현하고, Jackson 직렬화 키를 명시적으로 `ordN`으로 고정하기 위함이다. 불변·private 생성자로 값 객체 성격을 유지한다.
