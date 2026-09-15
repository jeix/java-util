# PLAN - java-util

## 현재 상태

프로젝트 구조 초기화와 `s.util.StringUtil` 구현 및 테스트 완료. 브랜치 `codex-deepseek`에서 작업 중이며, 리모트에는 아직 없는 브랜치입니다.

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

## 다음 단계

- 필요한 유틸리티 클래스와 테스트를 `s` 또는 `s.util` 패키지에 추가(예: 컬렉션 유틸리티)
- 기능이 늘어나면 `PRD.md`(요구사항)와 `PLAN.md`(단계) 갱신

## 참고

- 빌드 산출물 `target/`과 `.env*`는 `.gitignore`로 제외합니다.
- 이 브랜치 이전에 다른 브랜치에서 만들어진 `target/` 내용은 이 브랜치와 무관하므로 커밋하지 않습니다.
  검증 전에 해당 디렉터리를 `/tmp/java-util-target-leftover-20260915-211113`로 옮겨 두었습니다(복구 가능).
