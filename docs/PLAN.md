# PLAN (실행 계획 및 마일스톤)

## 마일스톤

### M0. 프로젝트 초기화 — 완료

- [x] Maven Wrapper 구성 (`mvnw`, `.mvn/wrapper/maven-wrapper.properties`, Maven 3.9.16)
- [x] `pom.xml` 작성 (Java 21, lombok, jackson, junit)
- [x] 표준 디렉터리 구조 생성 (`src/main/java`, `src/test/java`)
- [x] 예제 클래스 `s.Hello`와 `s.HelloTest` 작성
- [x] `./mvnw test` 통과 (Tests run: 3, Failures: 0, Errors: 0)
- [x] 무관 파일 제외 (`.gitignore`: `target/`, `.env-claude`)

### M1. 유틸리티 클래스 확충 — 미착수

- [ ] 유틸리티 클래스 후보 정의
- [ ] 클래스별 정상/경계 테스트 작성
- [ ] `./mvnw test` 회귀 확인

## 다음 단계

1. `docs/PLAN.md`의 M1에서 다룰 유틸리티 클래스 범위를 정한다.
2. 클래스 1개 단위로 구현 → 테스트 → 회귀 확인 순서로 진행한다.

## 확인 방법

```bash
./mvnw test
```

## 진행 기록

| 날짜 | 내용 |
| --- | --- |
| 2026-09-16 | M0 프로젝트 초기화 완료, 문서(README, docs/AGENT·PRD·PLAN·DECISION) 작성 |
