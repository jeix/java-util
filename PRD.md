# PRD - 프로젝트 요구사항 정의서

## 프로젝트 개요
- **프로젝트명**: java-util
- **목적**: Java 21 기반 유틸리티 라이브러리 기초 구조 제공
- **빌드 도구**: Maven (Wrapper 포함)
- **JDK 버전**: 21

## 필수 요구사항

### 1. 프로젝트 구조
- 표준 Maven 디렉토리 레이아웃 준수
- `src/main/java` - 메인 소스 코드
- `src/test/java` - 테스트 코드

### 2. 의존성
| 라이브러리 | 용도 | 버전 |
|-----------|------|------|
| Lombok | 보일러플레이트 코드 감소 | 1.18.34 |
| Jackson (databind, annotations) | JSON 직렬화/역직렬화 | 2.17.1 |
| JUnit Jupiter (API, Engine) | 단위 테스트 프레임워크 | 5.10.2 |
| SLF4J (API, Simple) | 로깅 (테스트용) | 2.0.13 |

### 3. 기능 요구사항

#### 3.1 Hello 클래스 (초기 예제)
- 메시지를 받아 인사말 반환 (`greet()` 메서드)
- Lombok 어노테이션 활용: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`

#### 3.2 StringUtil 클래스 (문자열 유틸리티)
- **Null/Blank/Empty 체크**: `isBlank`, `isEmpty`, `nonNullOf`, `nonBlankOf`, `nonEmptyOf` (값/Supplier 오버로드)
- **첫 번째 NonBlank 선택**: `firstNonBlankOrLast`, `firstNonBlankOrEmpty`, `firstNonBlankOrNull` (가변 인자/Supplier 2~5개 오버로드)
- **문자열 변환**: `stringify` (Date → yyyy-MM-dd, BigDecimal → toPlainString)
- **슬라이싱**: `slice` (begin/end), `head`, `tail`
- **숫자/문자열 조작**: `trimLeadingZero`, `repeat` (char/String), `reverse`
- **패딩**: `lpad`/`rpad`/`pad` (char), `lpad2`/`rpad2`/`pad2` (String)
- **리스트 연산**: `join` (List<T>, delimiter), `split` (String, regex)

#### 3.3 테스트 클래스
- **HelloTest**: 정상/경계 케이스, Lombok 생성 메서드 검증 (3개 테스트)
- **StringUtilTest**: @Slf4j 적용, 모든 public 메서드 검증 (23개 테스트)

### 4. 비기능 요구사항
- Maven Wrapper 제공으로 별도 설치 없이 빌드 가능
- Java 21 컴파일 타겟
- UTF-8 인코딩 기본 적용
- 파라미터 체크 후 정상 케이스 처리 (guard clause 패턴)
- 오버로드된 메서드는 핵심 구현 메서드 호출 방식
- 프라이빗 헬퍼 메서드는 `_` 접두사 사용