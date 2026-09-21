# PLAN (Execution Plan)

## 현재 상태: 단계 0 (초기화) 완료

### 진행 상황
| 단계 | 설명 | 상태 | 완료일 |
|------|------|------|--------|
| 0 | 프로젝트 구조 초기화 | ✅ 완료 | 2026-09-21 |

## 실행 단계

### 0단계: 프로젝트 구조 초기화 [완료]
**목표:** Java 프로젝트의 기본 골격을 마련하고 검증 가능한 상태로 만든다.

**완료된 작업:**
- `.gitignore` 작성 (`.agents/`, `.codex/`, `.env-claude`, `target/` 제외)
- Maven wrapper 설치 (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`)
- `pom.xml` 작성 (Lombok 1.18.34, Jackson 2.17.2, JUnit 5.10.3, JDK 21)
- `s.Hello.java` 작성 (유틸리티 클래스: greet, toJson, isPalindrome)
- `s.HelloTest.java` 작성 (7개 JUnit 5 테스트)
- `./mvnw clean test` 실행 → BUILD SUCCESS, 7/7 테스트 통과
- Git 커밋 (`opencode-kilo` 브랜치, 커밋 7ac5dca)

**완료 기준:**
- [x] `./mvnw clean test` 성공
- [x] 테스트 7개 통과, 0 실패
- [x] Lombok @UtilityClass 정상 동작
- [x] Jackson JSON 변환 정상 동작

### 1단계: 문자열 유틸리티 확장 [예정]
**목표:** `s.util.StringUtil`에 문자열 처리 메서드 추가

**계획:**
- `s.util.StringUtil` 클래스 작성
  - `isBlank(String)` - 문자열이 null이거나 공백인지 확인
  - `truncate(String, int)` - 문자열을 지정 길이로 자르기
  - `abbreviate(String, int)` - 문자열 축약 (말줄임표 포함)
- `s.util.StringUtilTest` 작성 (정상/경계/실패 케이스)
- `./mvnw test` 검증

**입력:** 없음
**출력:** `src/main/java/s/util/StringUtil.java`, `src/test/java/s/util/StringUtilTest.java`
**완료 기준:** 테스트 통과, 커밋 완료

### 2단계: 컬렉션 유틸리티 [예정]
**목표:** `s.util.CollectionUtil`에 컬렉션 처리 메서드 추가

**계획:**
- `s.util.CollectionUtil` 클래스 작성
  - `isNullOrEmpty(Collection)` - 컬렉션이 null/비어있는지 확인
  - `join(Collection, String)` - 컬렉션을 구분자로 연결
- `s.util.CollectionUtilTest` 작성
- `./mvnw test` 검증

### 3단계: 튜플 타입 [예정]
**목표:** `s.type.Tuple` 등 커스텀 데이터 타입 제공

**계획:**
- `s.type.Tuple` 클래스 작성 (이중/삼중 튜플)
- 테스트 작성
- `./mvnw test` 검증

## 마일스톤

| 마일스톤 | 목표 | 목표일 | 상태 |
|----------|------|--------|------|
| M0 | 프로젝트 초기화 | 2026-09-21 | ✅ 완료 |
| M1 | 문자열 유틸리티 | - | 예정 |
| M2 | 컬렉션 유틸리티 | - | 예정 |
| M3 | 튜플 타입 제공 | - | 예정 |
