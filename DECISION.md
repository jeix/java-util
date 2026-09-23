# DECISION (Important Decisions & Rationale)

## 결정 #1: Maven Wrapper 버전 (takari 0.5.6)
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
시스템에 Maven이 설치되어 있지 않음. Maven Wrapper를 사용해야 함.

### 선택
`takari:maven-wrapper:0.5.6` 버전의 JAR을 Maven Central에서 직접 다운로드.

### 근거
- 시스템 Maven이 없으므로 `mvn` CLI로 wrapper를 생성할 수 없음
- `io.takari:maven-wrapper`는 Maven Central에 공개되어 있어 직접 다운로드 가능
- `mvnw` 스크립트( GitHub takari/maven-wrapper에서 다운로드)는 기본적으로 0.5.6 버전을 참조

### 리스크
- `mvnw` 스크립트가 wrapper JAR이 없을 경우 `wrapperUrl` 속성을 읽어 재다운로드하도록 설계됨. 현재는 JAR이 존재하므로 문제 없음.

---

## 결정 #2: maven-wrapper.properties `distributionUrl` 사용
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
Maven wrapper JAR(0.5.6)은 `distributionUrl` 속성을 기대함.

### 선택
```properties
distributionUrl=https://repo1.maven.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
```

### 근거
- `WrapperExecutor` 클래스가 `distributionUrl`을 요구함 (런타임 오류 확인됨)
- Apache Maven 3.9.9: 최신 안정화 버전

---

## 결정 #3: 패키지명 `s`
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
간결한 패키지명 필요.

### 선택
패키지명을 `s`로 지정.

### 근거
- AGENTS.md 규칙: "패키지명: 소문자 단일 단어 (예: `s`)"
- 최상위 패키지로 사용하여 하위 패키지 확장 용이 (예: `s.util`, `s.type`)

---

## 결정 #4: 의존성 스코프
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
AGENTS.md에 의존성 스코프 규칙이 명시되어 있음.

### 선택
| 라이브러리 | 스코프 |
|-----------|--------|
| Lombok | `provided` |
| Jackson Databind | `compile` |
| JUnit Jupiter | `test` |

### 근거
- AGENTS.md: "Lombok: provided, Jackson: compile, JUnit Jupiter: test"
- Lombok은 런타임에 필요 없으므로 provided 스코프
- Jackson은 런타임에 필요하므로 compile 스코프
- JUnit은 테스트만 필요하므로 test 스코프
- Lombok 어노테이션 처리를 위해 `maven-compiler-plugin`의 `annotationProcessorPaths`에 Lombok을 추가

---

## 결정 #5: .gitignore - 관련 없는 파일 제외
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
다른 브랜치에서 체크아웃된 파일이 존재 (`target/`, `.agents/`, `.codex/`, `.env-claude`)

### 선택
`.gitignore`에 다음 항목 추가:
- `target/` (빌드 출력)
- `.agents/`, `.codex/` (다른 브랜치 설정 디렉토리)
- `.env-claude` (환경 변수 파일)
- IDE 파일 (`.idea/`, `.vscode/`, `*.iml` 등)

### 근거
- "브랜치와 관련 없는 파일은 git 저장소에 추가하지 않는다"는 요구사항
- `target/`는 빌드 출력으로 언제든 재생성 가능
- `.agents/`, `.codex/`, `.env-claude`는 다른 브랜치(codex, claude 등)에서 생성된 파일

---

## 결정 #6: Hello 클래스 기능
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
초기화 검증용 유틸리티 클래스 필요.

### 선택
`s.Hello` 클래스에 3개 메서드 구현:
1. `greet(String)` - 인사 문자열 반환 (null/공백 처리 포함)
2. `toJson(String, String)` - Jackson으로 JSON 변환
3. `isPalindrome(String)` - 팰린드롬 판별

### 근거
- Lombok `@UtilityClass`로 유틸리티 패턴 적용 (AGENTS.md 규칙)
- Jackson 의존성 사용 검증
- 다양한 테스트 케이스 (정상/경계/실패)를 위한 충분한 로직

---

## 결정 #7: slf4j-api 및 slf4j-simple 의존성 추가 (test scope)
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
`StringUtilTest`에 Lombok `@Slf4j` 애노테이션을 추가했으나, `@Slf4j`는 `org.slf4j.Logger`를 참조하는 코드를 생성한다. slf4j-api가 없으면 컴파일 에러 발생.

### 선택
`pom.xml`에 `slf4j-api` (test scope)와 `slf4j-simple` (test scope) 추가.

### 근거
- Jackson 2.17.2는 slf4j-api를 전이 의존성으로 포함하지 않음
- `@Slf4j`가 생성하는 코드는 `org.slf4j.Logger`와 `org.slf4j.LoggerFactory`를 참조하므로 컴파일 타임 필요
- `slf4j-simple`은 테스트 실행 시 로깅 구현체 제공

---

## 결정 #8: StringUtil 내부 구현 - `String.isBlank()` 사용
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
`@UtilityClass` 환경에서 커스텀 `isBlank(String)` 메서드 호출 시 런타임에 false가 반환되는 현상 발생.

### 선택
private 메서드(`_firstNonBlankOrLast`, `_firstNonBlankOrEmpty`, `_firstNonBlankOrNull`)에서 커스텀 `isBlank()` 호출 대신 Java 11+의 `String.isBlank()`를 직접 사용.

### 근거
- 커스텀 `isBlank` 공개 테스트에서는 정상 동작 확인
- private 메서드 내부에서 동일한 `isBlank` 호출 시 런타임에 다르게 동작하는 이상 현상 발생
- `val != null && !val.isBlank()` 형태로 null-safe하게 처리

---

## 결정 #9: Stream 기반 구현 및 랜덤 분기
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
요구사항: for문 대신 stream을 사용할 수 있으면 구현을 추가하고 실행 시 랜덤으로 분기.

### 선택
다음 메서드에 stream 기반 구현 추가 및 `Math.random() < 0.5` 랜덤 분기:
1. `_firstNonBlankOrLast` → `_firstNonBlankOrLastLoop` + `_firstNonBlankOrLastStream`
2. `_firstNonBlankOrEmpty` → `_firstNonBlankOrEmptyLoop` + `_firstNonBlankOrEmptyStream`
3. `_firstNonBlankOrNull` → `_firstNonBlankOrNullLoop` + `_firstNonBlankOrNullStream`
4. `join` → `_joinLoop` + `_joinStream`
5. `_repeatPad` → `_repeatPadLoop` + `_repeatPadStream`
6. value-to-supplier 변환 → `_toSuppliersLoop` + `_toSuppliersStream`

### 근거
- Stream API는 함수형 스타일로 코드 가독성 향상
- 랜덤 분기를 통해 두 구현 모두 검증 가능 (테스트에서 검증)
- Loop 버전은 lazy evaluation, Stream 버전은 eager evaluation (트레이드오프 명확히)

---

## 결정 #10: 3항 연산자 사용
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
요구사항: isBlank, isEmpty, nonNullOf(Supplier, Supplier), nonBlankOf(Supplier, Supplier), nonEmptyOf(Supplier, Supplier)에 3항 연산자 사용.

### 선택
- `isBlank`: `return s == null ? true : s.isEmpty() || s.trim().isEmpty();`
- `isEmpty`: `return s == null ? true : s.isEmpty();`
- `nonNullOf(Supplier, Supplier)`: `String val = supplier != null ? supplier.get() : null; return val != null ? val : _dflt(dfltSupplier);`
- `nonBlankOf(Supplier, Supplier)`: `String val = supplier != null ? supplier.get() : null; return (val != null && !val.isBlank()) ? val : _dflt(dfltSupplier);`
- `nonEmptyOf(Supplier, Supplier)`: 동일 패턴

### 근거
- 3항 연산자는 단일 표현식으로 값 반환 → 코드 간결화
- null 체크와 값 처리를 한 줄로 표현 가능

---

## 결정 #11: stringify switch expression 사용
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
요구사항: stringify에 switch 사용.

### 선택
```java
return switch (obj) {
    case null -> "";
    case Date d -> new SimpleDateFormat("yyyy-MM-dd").format(d);
    case BigDecimal bd -> bd.toPlainString();
    default -> obj.toString();
};
```

### 근거
- Java 21의 switch 패턴 매칭으로 타입 검사와 캐스팅을 동시 처리
- `case null` 처리 가능 (Java 21)
- if-else 체인보다 가독성 우수

---

## 결정 #12: slice/head/tail 음수 인덱스 (Python-style)
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
요구사항: 음수 인덱스를 역방향 인덱스로 해석.

### 선택
- `slice(s, begin)`: `begin < 0` → `len + begin`, clamp to 0 if < -len, clamp to len if > len
- `slice(s, begin, end)`: same logic for both begin and end
- `head(s, size)`: `size < 0` → `len + size`, if `size < -len` → return ""
- `tail(s, size)`: `size < 0` → `-size`로 변환 후 front에서 제외 (`s.substring(abs(size))`)

### 근거
- Python의 슬라이싱 규칙과 동일한 직관적 동작
- 음수 인덱스는 문자열 끝에서부터 셈

---

## 결정 #13: trimLeadingZero 정규식 구현
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
요구사항: 정규식으로 찾아 바꾸기 방식의 구현 추가, 랜덤 분기.

### 선택
- `_trimLeadingZeroLoop`: 기존 while 루프 방식
- `_trimLeadingZeroRegex`: `s.replaceFirst("^0+(?!$)", "")`
  - `^0+` : 시작부터 하나 이상의 0
  - `(?!$)` : 끝이 아닌 위치에서만 매칭 (전체가 0인 문자열 "0000" → "0" 유지)
- `trimLeadingZero`: `Math.random() < 0.5`로 두 구현 랜덤 분기

### 근거
- 정규식은 짧고 선언적, 루프는 더 명확한 제어 흐름
- 두 구현 모두 동일한 결과를 보장

---

## 결정 #14: repeat 파라미터 String 변경
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
요구사항: repeat의 c 파라미터 타입을 String으로 변경.

### 선택
```java
public static String repeat(String c, int size) {
    if (c == null || size <= 0) return "";
    return c.repeat(size);
}
```

### 근거
- Java 11+의 `String.repeat(int)` 활용 가능
- String 파라미터로 다중 문자 반복도 지원 (`"ab".repeat(3)` = "ababab")
- null-safe 처리

---

## 결정 #15: split immutable list 반환
**일시:** 2026-09-21
**상태:** 승인됨

### 배경
요구사항: split이 불변 리스트를 반환.

### 선택
```java
return List.of(s.split(regex));
```

### 근거
- `List.of()`는 Java 9+ 불변 리스트 생성
- `Collections.singletonList`이나 `Collections.emptyList()`도 불변이지만 `List.of()` 더 일관됨
