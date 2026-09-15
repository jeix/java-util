# PLAN - java-util

## 현재 상태

- 브랜치 `codex-deepseek`에서 작업 중이고 `origin`에 push되어 추적 중입니다(upstream: `origin/codex-deepseek`).
- `s.Hello`, `s.util.StringUtil`(문자열 유틸리티 + `pipe`/`Pipeline`), `s.util.CollectionUtil`, `s.type.tuple`(Pair/Triplet/Quartet) 구현과 테스트를 마쳤습니다.
- `./mvnw -o clean package` 기준 전체 테스트 91개가 통과합니다.

## 수행 결과

| 단계 | 내용 | 상태 |
| --- | --- | --- |
| 1 | 저장소/브랜치/untracked 파일 상태 확인 | 완료 |
| 2 | Maven Wrapper(script-only, Maven 3.9.16) 도입 | 완료 |
| 3 | `pom.xml`에 JDK 21, Lombok, Jackson, JUnit 5 의존성 구성 | 완료 |
| 4 | `s.Hello`, `s.HelloTest` 샘플 추가 | 완료 |
| 5 | `./mvnw clean test`, `./mvnw clean package`로 검증 | 완료 |
| 6 | 프로젝트 문서(README, AGENTS, PRD, PLAN, DECISION) 작성 | 완료 |
| 7 | `s.util.StringUtil` 구현(공백 판별, 기본값 선택, 첫 값 선택, 문자열 가공, 패딩, 목록 변환) | 완료 |
| 8 | `s.util.StringUtilTest` 작성(44개 테스트 실행, `@Slf4j` 로깅 확인) | 완료 |
| 9 | 문서 갱신(README 기능 목록, PRD 요구사항 R7·R8, PLAN·DECISION) | 완료 |
| 10 | `StringUtil` 스타일 변경(3항 연산자, 반복문/스트림 랜덤 분기, 음수 인덱스 역방향 해석, `repeat(String, int)`) | 완료 |
| 11 | 변경된 동작에 맞춰 `StringUtilTest` 갱신 후 10회 반복 실행(모두 성공) | 완료 |
| 12 | `s.type.tuple`에 `Pair`, `Triplet`, `Quartet` 데이터 타입 추가(스태틱 팩터리 `of`, 인스턴스 `ordN()`, `toString`, `@EqualsAndHashCode`, `@JsonProperty`) | 완료 |
| 13 | `s.type.tuple.TupleTest` 작성(10개 테스트: 값 접근, `toString`, equals/hashCode, JSON 변환, null 값) | 완료 |
| 14 | 문서 갱신(README 튜플 섹션, PRD 요구사항 R11·R12, PLAN·DECISION) | 완료 |
| 15 | `@JsonProperty`를 필드에서 `ordN()` 메서드로 이동하고, 프로퍼티 순서에 의존하지 않도록 JSON 테스트를 트리 비교로 변경 | 완료 |
| 16 | JSON 키명을 `cat`, `dog`, `elk`, `fox`에서 `ord1`~`ord4`로 변경 | 완료 |
| 17 | `s.util.CollectionUtil` 구현(리스트 기본, zip, 검색, 집합 연산, 부분 리스트, 색인·분류, 맵 생성·복사) | 완료 |
| 18 | `s.util.CollectionUtilTest` 작성(26개 테스트 실행, 전체 80개 테스트 통과) 및 문서 갱신 | 완료 |
| 19 | `CollectionUtil`의 `zip`/`findOne`/`indexing`/`grouping`/`asMap` 두 오버로드에 스트림 구현 추가 및 랜덤 분기 적용(랜덤 분기 검증 테스트 6개 추가, 전체 86개 테스트 통과) | 완료 |
| 20 | `StringUtil`에 `pipe(Function...)`, `pipe()`, `Pipeline` 클래스 추가(codelet 예제 재현 테스트 포함, 전체 90개 테스트 통과) 및 문서 갱신 | 완료 |
| 21 | 배치 정리: `pipe(Function...)`과 `pipe()`를 `Pipeline` 클래스 바로 앞으로 이동 | 완료 |
| 22 | `pipe(Function...)`에 codelet의 impl. 1(입력값 축소 구현) 추가 및 랜덤 분기 적용(랜덤 분기 검증 테스트 추가, 전체 91개 테스트 통과) | 완료 |

## 다음 단계

- 필요한 유틸리티 클래스와 테스트를 `s` 또는 `s.util` 패키지에 계속 추가
- 기능이 늘어나면 `PRD.md`(요구사항)와 `PLAN.md`(단계) 갱신
- `codex-deepseek` 브랜치를 병합할 대상(예: `main`)을 정하고 PR 생성

## 참고

- 빌드 산출물 `target/`과 `.env*`는 `.gitignore`로 제외합니다.
- 이 브랜치 이전에 다른 브랜치에서 만들어진 `target/` 내용은 이 브랜치와 무관하므로 커밋하지 않습니다.
  검증 전에 해당 디렉터리를 `/tmp/java-util-target-leftover-20260915-211113`로 옮겨 두었습니다(복구 가능).
