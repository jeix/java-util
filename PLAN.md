# 실행 계획

지금까지 진행한 순서를 남긴다. 각 단계는 "구현 → 테스트 작성 → 빌드로 확인 → 커밋" 순서를 지켰다.
결정의 근거는 `DECISIONS.md`, 코드를 쓸 때 지킬 규칙은 `CLAUDE.md` 를 본다.

## 1. 프로젝트 뼈대

- [x] Maven Wrapper 부트스트랩 (only-script, Maven 3.9.16, 배포본 sha256 검증)
- [x] `pom.xml` — JDK 21, lombok(provided) · jackson-databind · junit-jupiter(test)
- [x] `.gitignore` — `target/` 과 이 브랜치와 무관한 로컬 파일 제외
- [x] `s.Hello` / `s.HelloTest` 로 뼈대가 도는지 확인
- [x] `./mvnw clean test` 통과(3개) 후 커밋 `c457382`

## 2. `s.util.StringUtil`

- [x] 39개 메서드 구현 — 가드 먼저, 오버로드는 구현 한 곳에 모으고, 비공개 메서드에 `_` 접두사
- [x] 값 버전이 supplier 버전을 호출하도록 위임 구조 잡기 (supplier 는 필요할 때만 평가)
- [x] `StringUtilTest` — `@Slf4j`, `@Nested` 9그룹
- [x] 요청 반영: 3항 연산자, `Supplier<String>...` 가변인자, 패턴 switch, 음수 인덱스 역방향,
      `repeat(String, int)`, `pad` 가운데 정렬
- [x] loop/stream 무작위 분기 추가 (`_firstNonBlankOrLast`, `join`, `trimLeadingZero`)
- [x] `peek` 제거 — 마지막 supplier 를 검색 대상에서 떼어 `orElseGet` 으로 넘기는 방식
- [x] `trimLeadingZero` 정규식을 `\p{Nd}` 로 맞춰 두 구현의 동작 일치
- [x] 리플렉션으로 두 구현의 결과·supplier 평가 횟수 비교, 테스트 10회 반복 실행
- [x] 커밋 `502100e`

## 3. `s.type.tuple.*`

- [x] `Pair` · `Triplet` · `Quartet` — `of(...)`, `ordN()`, `toString()`, `@EqualsAndHashCode`
- [x] `ordN()` 에 `@JsonProperty`, `of(...)` 에 `@JsonCreator` → JSON 키가 `ord1`~`ord4`
- [x] `TupleTest` — 값 꺼내기, `toString` 형식, equals, JSON 왕복, `null` 값
- [x] 커밋 `4559dc3`

## 4. `s.util.CollectionUtil`

- [x] 24개 메서드 구현 — `requireNonNull` 로 null 단언, 집합 연산은 `differenceOf` 한 곳에
- [x] 돌려주는 컬렉션을 불변으로 (`Collections.unmodifiable*`), `grouping` 의 값 리스트까지
- [x] zip · findOne · findAll · differenceOf · indexing · grouping · asMap 에 무작위 분기 추가
- [x] `Collectors.toMap` · `groupingBy` 대신 `forEach` — null 에서 두 구현이 갈리는 것 방지
- [x] `CollectionUtilTest` — `@Slf4j`, `@Nested` 9그룹(불변 여부 그룹 포함)
- [x] 리플렉션으로 loop/stream 11쌍 비교, 테스트 8회 반복 실행
- [x] 커밋 `3f9d055`

## 5. 문서

- [x] `README.md` — 요구사항, 빌드·테스트 명령, 구성, 의존성
- [x] `CLAUDE.md` — 코드를 쓸 때 지킬 규칙
- [x] `DECISIONS.md` — 결정 12개와 근거
- [x] `PRD.md` · `PLAN.md`

## 6. `s.util.StringUtil.pipe` 추가

- [x] `pipe(Function...)` — 코드릿의 두 구현(적용 방식 / 합성 방식)을 무작위로 분기
- [x] `pipe()` → `Pipeline` 빌더 (`then` / `apply`)
- [x] null 함수 검사를 붙이는 자리로 앞당겨 두 구현의 실패 시점을 맞춤
- [x] 가변인자 배열을 `List` 에 옮겨 담아 `[varargs]` 경고 제거
- [x] `StringUtilTest` 에 `pipe` 그룹 7개 추가, 리플렉션으로 두 구현 비교, 테스트 5회 반복

## 남은 것

- [ ] 랜덤 분기 두 구현의 동등성 검사를 테스트로 남길지 검토 — 지금은 리플렉션으로 한 번씩 확인만 한다
