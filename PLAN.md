# PLAN - java-util

## 현재 상태

프로젝트 구조 초기화 완료. 브랜치 `codex-deepseek`에서 작업 중이며, 리모트에는 아직 없는 브랜치입니다.

## 수행 결과

| 단계 | 내용 | 상태 |
| --- | --- | --- |
| 1 | 저장소/브랜치/untracked 파일 상태 확인 | 완료 |
| 2 | Maven Wrapper(script-only, Maven 3.9.16) 도입 | 완료 |
| 3 | `pom.xml`에 JDK 21, Lombok, Jackson, JUnit 5 의존성 구성 | 완료 |
| 4 | `s.Hello`, `s.HelloTest` 샘플 추가 | 완료 |
| 5 | `./mvnw clean test`, `./mvnw clean package`로 검증 | 완료 |
| 6 | 프로젝트 문서(README, AGENTS, PRD, PLAN, DECISION) 작성 | 완료 |

## 다음 단계

- 필요한 유틸리티 클래스와 테스트를 `s` 또는 `s.util` 패키지에 추가
- 기능이 늘어나면 `PRD.md`(요구사항)와 `PLAN.md`(단계) 갱신

## 참고

- 빌드 산출물 `target/`과 `.env*`는 `.gitignore`로 제외합니다.
- 이 브랜치 이전에 다른 브랜치에서 만들어진 `target/` 내용은 이 브랜치와 무관하므로 커밋하지 않습니다.
  검증 전에 해당 디렉터리를 `/tmp/java-util-target-leftover-20260915-211113`로 옮겨 두었습니다(복구 가능).
