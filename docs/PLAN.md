# PLAN (실행 계획 및 마일스톤)

## 마일스톤

### M0. 프로젝트 초기화 — 완료

- [x] Maven Wrapper 구성 (`mvnw`, `.mvn/wrapper/maven-wrapper.properties`, Maven 3.9.16)
- [x] `pom.xml` 작성 (Java 21, lombok, jackson, junit)
- [x] 표준 디렉터리 구조 생성 (`src/main/java`, `src/test/java`)
- [x] 예제 클래스 `s.Hello`와 `s.HelloTest` 작성
- [x] `./mvnw test` 통과 (Tests run: 3, Failures: 0, Errors: 0)
- [x] 무관 파일 제외 (`.gitignore`: `target/`, `.env-claude`)

### M1. `s.util.StringUtil` 구현 — 완료

- [x] `s.util.StringUtil` 구현 (판별/기본값/선택/변환/패딩/조인·분리)
- [x] `s.util.StringUtilTest` 작성 (`@Slf4j`, 메서드별 정상·경계 테스트)
- [x] `pom.xml`에 `slf4j-api`(provided), `slf4j-simple`(test) 추가
- [x] 음수 인덱스 정규화(slice/head/tail), `repeat(String)`, 3항·switch 적용
- [x] 루프/대체 구현(Stream·정규식) 이중화 및 랜덤 분기, private 가변 인자화
- [x] `./mvnw test` 통과 (Tests run: 64, Failures: 0, Errors: 0, 5회 반복 확인)

### M2. 튜플 타입 `s.type.tuple` — 완료

- [x] `Pair<T,U>`, `Triplet<T,U,V>`, `Quartet<T,U,V,W>` 구현
- [x] Lombok `@EqualsAndHashCode`, 불변 필드, 정적 팩터리 `of`, `ordN()`(`@JsonProperty`)
- [x] `TupleTest` 작성 (of/ordN/equals·hashCode/toString/JSON 직렬화 14건)
- [x] `./mvnw test` 통과 (Tests run: 78, Failures: 0, Errors: 0)

### M3. 유틸리티 클래스 추가 확충 — 미착수

- [ ] 다음 유틸리티 클래스 후보 정의 (예: `CollectionUtil`, `NumberUtil`)
- [ ] 클래스별 정상/경계 테스트 작성
- [ ] `./mvnw test` 회귀 확인

## 다음 단계

1. M3에서 다룰 유틸리티 클래스 범위를 정한다.
2. 클래스 1개 단위로 구현 → 테스트 → 회귀 확인 순서로 진행한다.

## 확인 방법

```bash
./mvnw test
```

## 진행 기록

| 날짜 | 내용 |
| --- | --- |
| 2026-09-16 | M0 프로젝트 초기화 완료, 문서(README, docs/AGENT·PRD·PLAN·DECISION) 작성 |
| 2026-09-16 | M1 `s.util.StringUtil` 및 `StringUtilTest` 구현, 테스트 59건 통과 |
| 2026-09-16 | M1 리팩터링(음수 인덱스, Stream/정규식 랜덤 분기, 3항·switch) 후 테스트 64건 5회 통과 |
| 2026-09-16 | M1 랜덤 분기를 `if/else`로 정리(들여쓰기 균형), 테스트 64건 재확인 |
| 2026-09-16 | M2 `s.type.tuple` 튜플 타입 3종 및 `TupleTest` 구현, 테스트 78건 통과 |
