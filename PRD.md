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
- **문자열 변환**: `stringify` (Date → yyyy-MM-dd, BigDecimal → toPlainString) - switch expression 사용
- **슬라이싱**: `slice` (begin/end), `head`, `tail` - 음수 인덱스 지원 (head: 역방향, tail: 앞에서 제외)
- **숫자/문자열 조작**: `trimLeadingZero` (정규식/루프 랜덤 분기), `repeat` (String만, 스트림/루프 랜덤 분기), `reverse`
- **패딩**: `lpad`/`rpad`/`pad` (String pad, 길이 1 검증, 스트림/루프 랜덤 분기), `lpad2`/`rpad2`/`pad2` (멀티바이트 pad 지원, 스트림/루프 랜덤 분기)
- **리스트 연산**: `join` (List<T>, delimiter, 스트림/루프 랜덤 분기), `split` (String, regex, 불변 리스트 반환)

#### 3.3 튜플 타입 클래스 (s.type.tuple 패키지)
- **Pair<T, U>**: 2개 요소 튜플
- **Triplet<T, U, V>**: 3개 요소 튜플
- **Quartet<T, U, V, W>**: 4개 요소 튜플
- 공통 특징:
  - `@EqualsAndHashCode` 적용 (Lombok)
  - private 생성자 + static factory method `of()`
  - 불변 객체 (final 필드)
  - JSON 직렬화용 getter: `ord1()`, `ord2()`, `ord3()`, `ord4()` (`@JsonProperty` 적용)
  - `toString()`: `(t, u, v, w)` 형식

#### 3.4 CollectionUtil 클래스 (컬렉션/맵 유틸리티)
- **빈 체크/기본값**: `isEmpty` (Collection/Map), `emptyIfNull` (List/Map)
- **리스트 결합/변환**: `zip` (Pair/함수, 불변 리스트, 스트림/루프 랜덤 분기), `toArray` (타입 안전한 배열)
- **필터링/검색**: `findOne` (스트림/루프 랜덤 분기), `findAll` (불변 리스트, 스트림/루프 랜덤 분기)
- **집합 연산**: `unionOf`, `intersectionOf`, `differenceOf`, `symmetricDifferenceOf` (모두 불변 리스트, 스트림/루프 랜덤 분기)
- **슬라이싱**: `slice` (begin/end, 불변 리스트), `head`, `tail` (음수 인덱스 지원, 불변 리스트)
- **색인화/분류**: `indexing` (Function→불변 Map, 스트림/루프 랜덤 분기), `grouping` (Function→불변 Map<불변 List>, 스트림/루프 랜덤 분기)
- **맵 생성/변환**: `asMap` (가변인자/클래스/Entry리스트, 불변 맵, 스트림/루프 랜덤 분기), `castKeyValue`, `copyOf` (불변 맵)

#### 3.5 테스트 클래스
- **HelloTest**: 정상/경계 케이스, Lombok 생성 메서드 검증 (3개 테스트)
- **StringUtilTest**: @Slf4j 적용, 모든 public 메서드 검증 (20개 테스트)
- **TupleTest**: @Slf4j 적용, 생성/조회, equals/hashCode, null 값, 불변성, JSON 직렬화 검증 (9개 테스트)
- **CollectionUtilTest**: @Slf4j 적용, 빈 체크, 결합, 필터링, 집합 연산, 슬라이싱, 색인화, 맵 변환, 불변성 검증 (21개 테스트)

### 4. 비기능 요구사항
- Maven Wrapper 제공으로 별도 설치 없이 빌드 가능
- Java 21 컴파일 타겟
- UTF-8 인코딩 기본 적용
- 파라미터 체크 후 정상 케이스 처리 (guard clause 패턴)
- 오버로드된 메서드는 핵심 구현 메서드 호출 방식
- 프라이빗 헬퍼 메서드는 `_` 접두사 사용
- **스트림/루프 구현 병행 및 실행 시 랜덤 분기**
- **불변 컬렉션/맵 반환**