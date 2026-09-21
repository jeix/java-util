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
