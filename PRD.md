# java-util 요구사항

이 저장소가 무엇을 만들려는 것인지, 무엇이 범위 안이고 무엇이 아닌지 적는다.
결정과 그 근거는 `DECISIONS.md`, 코드를 쓸 때 지킬 규칙은 `CLAUDE.md` 에 있다.

## 배경

- 자바 유틸리티 클래스를 모아 두고 테스트와 함께 관리하는 저장소다.
- 빌드 도구가 설치돼 있지 않은 환경에서도 빌드와 테스트가 돌아가야 한다.
- JDK 21 을 쓴다.

## 범위

### R1. 프로젝트 뼈대

- Maven 과 Wrapper(`./mvnw`)로 빌드·테스트가 돈다. 빌드 도구를 따로 설치하지 않아도 된다.
- Lombok, Jackson, JUnit 5 를 쓸 수 있다.
- 구조가 도는지 확인할 예제 클래스 `s.Hello` 와 `s.HelloTest` 를 둔다.
- 이 저장소와 무관한 로컬 파일은 커밋되지 않게 `.gitignore` 로 막는다.

### R2. 문자열 유틸리티 `s.util.StringUtil`

스태틱 메서드 41개. 다음과 같다.

| 묶음 | 메서드 |
|---|---|
| 검사 | `isBlank(s)`, `isEmpty(s)` |
| 대체값 | `nonNullOf`, `nonBlankOf`, `nonEmptyOf` — 각각 `(value, dflt)` 와 `(supplier, dfltSupplier)` 두 벌 |
| firstNonBlank | `firstNonBlankOrLast` · `firstNonBlankOrEmpty` · `firstNonBlankOrNull` — 각각 값 가변인자 한 벌과 supplier 2~5개 오버로드 네 벌 |
| 변환 | `stringify(obj)` — `java.util.Date` 는 `yyyy-MM-dd`, `BigDecimal` 은 `toPlainString()` |
| 자르기 | `slice(s, begin)`, `slice(s, begin, end)`, `head(s, size)`, `tail(s, size)` |
| 가공 | `trimLeadingZero(s)`, `repeat(c, size)`, `reverse(s)` |
| 채우기 | `lpad`, `rpad`, `pad`, `lpad2`, `rpad2`, `pad2` |
| 목록 | `join(list, delimiter)`, `split(s, regex)` |
| 함수 이어 붙이기 | `pipe(fns...)` → `Function<String,String>`, `pipe()` → `Pipeline`(`then` / `apply`) |

### R3. 컬렉션 유틸리티 `s.util.CollectionUtil`

스태틱 메서드 24개.

| 묶음 | 메서드 |
|---|---|
| 리스트 검사 | `isEmpty(list)`, `emptyIfNull(list)` |
| 짝짓기 | `zip(list1, list2)`, `zip(list1, list2, mixer)` |
| 배열 | `toArray(list)` |
| 찾기 | `findOne(list, filter)`, `findAll(list, filter)` |
| 집합 연산 | `unionOf` · `intersectionOf` · `differenceOf` · `symmetricDifferenceOf` |
| 자르기 | `slice(list, begin)`, `slice(list, begin, end)`, `head(list, size)`, `tail(list, size)` |
| 색인·분류 | `indexing(list, indexer)`, `grouping(list, classifier)` |
| 맵 검사 | `isEmpty(map)`, `emptyIfNull(map)` |
| 맵 만들기 | `asMap(...items)`, `asMap(keyClass, valueClass, ...items)`, `asMap(entries)`, `castKeyValue(origin)`, `copyOf(origin)` |

### R4. 튜플 타입 `s.type.tuple.*`

- `Pair<T,U>` · `Triplet<T,U,V>` · `Quartet<T,U,V,W>` 세 가지.
- `of(...)` 스태틱 팩터리로만 만든다. 생성자는 비공개.
- 값을 꺼내는 메서드는 자리 순서대로 `ord1()` ~ `ordN()` 이고, 데이터 타입이므로 인스턴스 메서드다.
- `toString()` 은 `(t, u)` / `(t, u, v)` / `(t, u, v, w)` 형식.
- 값이 같으면 같은 것으로 본다(`@EqualsAndHashCode`).
- JSON 으로 바꾸면 `{"ord1": ..., "ord2": ...}` 모양이 되고, 그 JSON 으로 다시 만들 수 있다.

### R5. 테스트

- 클래스마다 테스트 클래스를 둔다: `s.HelloTest`, `s.util.StringUtilTest`, `s.util.CollectionUtilTest`,
  `s.type.tuple.TupleTest`.
- JUnit 5 로 쓰고, 테스트 클래스에 Lombok `@Slf4j` 를 붙인다.

## 품질 기준

- `./mvnw clean test` 로 전부 통과해야 한다.
- 컴파일 경고가 없어야 한다.
- 같은 일을 하는 구현이 둘 이상이면 두 구현의 결과가 같아야 한다(무작위로 골라 쓰기 때문).
- 요청받은 시그니처를 그대로 지킨다. 이름·파라미터 순서·리턴 타입을 임의로 바꾸지 않는다.

## 이번 범위가 아닌 것

- 빌드 도구의 설치와 사용 — Wrapper 만 쓴다. Gradle 같은 다른 도구는 들이지 않는다.
- main 코드의 로깅 — slf4j 는 테스트에서만 쓴다.
