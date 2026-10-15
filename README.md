# java-util

Java 유틸리티 라이브러리. JDK 21 기반으로 작성되었습니다.

## 기술 스택
- **JDK 21**, **Maven Wrapper** (`./mvnw`)
- **Lombok**, **Jackson**, **JUnit 5**

## 빌드 & 테스트
```bash
./mvnw test          # 단위 테스트 실행
```

## 모듈

### 1. 문자열 유틸리티 (`s.util.StringUtil`)
- `slice` / `head` / `tail` / `repeat` / `split` / `pad*` / `lpad2` / `rpad2` / `reverse` / `trimLeadingZero` 등
- `firstNonBlankOrLast`: varargs에서 첫 비어있지 않은 문자열 찾기
- `pipe` / `Pipeline`: 함수 체이닝 빌더 (varargs `pipe(fns...)`와 빌더 `pipe().then(...).apply(...)`), for-loop/stream 랜덤 분기

### 2. 튜플 타입 (`s.type.tuple`)
- `Pair<T, U>` → `Triplet<T, U, V>` → `Quartet<T, U, V, W>` (상속 구조)
- `of()`, `ord1~ordN()`, `toString()`, `@EqualsAndHashCode`
- `@JsonProperty`로 JSON 직렬화 필드명 지정

### 3. 컬렉션 유틸리티 (`s.util.CollectionUtil`)
- `zip`: 두 리스트를 `Pair`(또는 mixer 함수)로 조합 (짧은 쪽 길이 기준)
- `asMap`: varargs / `Class` 캐스팅 / `Entry` 리스트로부터 불변 `Map` 생성
- `castKeyValue`: `Map<Object, Object>`를 `(K, V)` 타입으로 캐스팅
- `indexing` / `grouping`: 리스트를 키 기준으로 인덱싱 / 그룹화
- `unionOf` / `intersectionOf` / `differenceOf` / `symmetricDifferenceOf`: 집합 연산
- `slice` / `head` / `tail`: 음수 인덱스 지원 리스트 슬라이스
- `copyOf`: 불변 Map 복사
- for-loop / stream 구현을 `RANDOM.nextBoolean()`으로 랜덤 분기

### 4. 날짜/시간 유틸리티 (`s.util.DateTimeUtil`)
- **현재 날짜/시간 조회**: `today()` / `date()` / `date2()` / `time()` / `time2()` / `datetime()` / `datetime2()`
  - compact 포맷 (`yyyyMMdd`, `HHmmss`, `yyyyMMddHHmmss`)과 구분자 포맷 (`yyyy-MM-dd`, `HH:mm:ss`, `yyyy-MM-dd HH:mm:ss`) 지원
- **문자열 변환**: `stringify(LocalDate/LocalTime/LocalDateTime)` / `stringify2(...)`
- **문자열 파싱**: `localDateOf` / `localTimeOf` / `localDateTimeOf` (포맷 자동 인식: `-` 또는 `:` 포함 여부로 구분)
- **Timestamp 변환**: `toTimestamp(LocalDate)` / `toTimestamp(LocalDateTime)`
- **날짜 비교**: `eval(date1, op, date2)` — `=` `!=` `>=` `>` `<=` `<`
- **날짜 연산**: `eval(date, op, n, unit)` — `+` `-` / `D` `W` `M` `Y`
- **월말 계산**: `eom(date)` — 해당 월의 마지막 날짜 반환

```java
// 사용 예제
DateTimeUtil.today();                            // "20250115"
DateTimeUtil.date2();                            // "2025-01-15"
DateTimeUtil.stringify(LocalDate.of(2024, 1, 15)); // "20240115"
DateTimeUtil.localDateOf("2024-01-15");        // LocalDate
DateTimeUtil.eval("20240115", ">", "20240110"); // true
DateTimeUtil.eval("20240115", "+", 1, "M");    // "20240215"
DateTimeUtil.eom("20240115");                  // "20240131"
DateTimeUtil.toTimestamp(LocalDate.now());      // java.sql.Timestamp
```

## 설계 결정
주요 설계 결정은 [`DECISION.md`](./DECISION.md)를 참고하세요.