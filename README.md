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

## 설계 결정
주요 설계 결정은 [`DECISION.md`](./DECISION.md)를 참고하세요.