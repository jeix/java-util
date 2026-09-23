# PLAN (Execution Plan)

## 현재 상태: 1단계 완료 (문자열 유틸리티)

### 진행 상황
| 단계 | 설명 | 상태 | 완료일 |
|------|------|------|--------|
| 0 | 프로젝트 구조 초기화 | ✅ 완료 | 2026-09-21 |
| 1 | 문자열 유틸리티 (StringUtil) | ✅ 완료 | 2026-09-21 |

## 실행 단계

### 0단계: 프로젝트 구조 초기화 [완료]
**목표:** Java 프로젝트의 기본 골격을 마령하고 검증 가능한 상태로 만든다.

**완료된 작업:**
- `.gitignore` 작성 (`.agents/`, `.codex/`, `.env-claude`, `target/` 제외)
- Maven wrapper 설치 (`mvnw`, `mvnw.cmd`, `.mvn/wrapper/`)
- `pom.xml` 작성 (Lombok 1.18.34, Jackson 2.17.2, JUnit 5.10.3, JDK 21)
- `s.Hello.java` 작성 (유틸리티 클래스: greet, toJson, isPalindrome)
- `s.HelloTest.java` 작성 (7개 JUnit 5 테스트)
- `./mvnw clean test` 실행 → BUILD SUCCESS, 7/7 테스트 통과
- Git 커밋 (`opencode-kilo-laguna` 브랜치, 커밋 7ac5dca)

**완료 기준:**
- [x] `./mvnw clean test` 성공
- [x] 테스트 7개 통과, 0 실패
- [x] Lombok @UtilityClass 정상 동작
- [x] Jackson JSON 변환 정상 동작

### 1단계: 문자열 유틸리티 (StringUtil) [완료]
**목표:** `s.util.StringUtil`에 다양한 문자열 처리 메서드 제공

**완료된 작업:**
- `s.util.StringUtil` 클래스 작성 (25개 메서드)
  - Boolean 체크: `isBlank`, `isEmpty` (3항 연산자)
  - 기본값 제공: `nonNullOf`, `nonBlankOf`, `nonEmptyOf` (value + supplier 오버로드)
  - 다중 값 처리: `firstNonBlankOrLast`, `firstNonBlankOrEmpty`, `firstNonBlankOrNull` (value + supplier 오버로드, private `_firstNonBlankOr*Loop/Stream`, 랜덤 분기)
  - 변환: `stringify` (switch expression)
  - 슬라이스: `slice` (Python-style 음수 인덱스)
  - 부분 문자열: `head` (음수 size 역방향), `tail` (음수 size front 제외)
  - 숫자 처리: `trimLeadingZero` (loop + regex, 랜덤 분기)
  - 반복/역순: `repeat(String)`, `reverse`
  - 패딩: `lpad`, `rpad`, `pad` (단일 문자), `lpad2`, `rpad2`, `pad2` (다중 문자, 스트림/loof 랜덤 분기)
  - 컬렉션: `join` (스트림/loof 랜덤 분기), `split` (immutable list 반환)
- `s.util.StringUtilTest` 작성 (65개 JUnit 5 테스트, Lombok @Slf4j)
- `pom.xml`에 slf4j-api, slf4j-simple (test scope) 의존성 추가
- `./mvnw clean test` 실행 → BUILD SUCCESS, 72/72 테스트 통과

**완료 기준:**
- [x] `./mvnw clean test` 성공
- [x] 테스트 72개 통과, 0 실패
- [x] 모든 메서드 오버로드 정상 동작

### 2단계: 컬렉션 유틸리티 [예정]
**목표:** `s.util.CollectionUtil`에 컬렉션 처리 메서드 추가

**계획:**
- `s.util.CollectionUtil` 클래스 작성
  - `isNullOrEmpty(List)` - 리스트가 null/비어있는지 확인
  - `join(List, String)` - 리스트를 구분자로 연결 (StringUtil.join의 확장)
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
| M1 | 문자열 유틸리티 | 2026-09-21 | ✅ 완료 (재커밋 예정) |
| M2 | 컬렉션 유틸리티 | - | 예정 |
| M3 | 튜플 타입 제공 | - | 예정 |
