# 결정 기록

번호를 매겨 누적하는 기록이에요. 한 번 적은 항목은 고치지 않고, 바뀌면 새 항목을 추가해요.

## 1. 브랜치 독립성 (2026-10-19)

- **결정**: 이 브랜치(`claude-claude`)는 다른 브랜치를 참조하지 않고 독자적으로 진행해요. 다른 브랜치에만 관련된 파일·디렉터리는 git에 추가하지 않아요.
- **이유**: 저장소에는 브랜치마다 같은 초기화 작업을 각자 수행한 결과가 있어요. 다른 브랜치의 내용을 가져오면 결과 비교가 의미 없어져요.
- **적용**: `.gitignore`에 `.agents/`, `.codex/`, `.env-*`, `.claude/settings.local.json`을 넣었어요. `.env-*`에는 인증 토큰이 있어서 브랜치와 관계없이 커밋하면 안 돼요.

## 2. 빌드 도구는 Maven wrapper (2026-10-19)

- **결정**: Maven을 직접 설치하지 않고 wrapper(`mvnw`)로 빌드해요. wrapper는 JAR 없는 `only-script` 방식이고, Maven 3.9.11을 써요.
- **이유**: 환경에 `mvn`이 없고, wrapper를 쓰라는 요구가 있었어요. `only-script`는 바이너리(`maven-wrapper.jar`)를 저장소에 넣지 않아도 돼요.
- **트레이드오프**: 첫 실행 때 네트워크로 Maven 배포본을 내려받아요. `mvn wrapper:wrapper`를 쓰지 못해서 Maven Central의 `maven-wrapper-distribution` 3.3.4 `only-script` 배포본에서 스크립트를 받아 넣었어요.

## 3. 의존성과 버전 (2026-10-19)

- **결정**: Java 21(`maven.compiler.release`), lombok 1.18.44, jackson-databind 2.21.1, JUnit Jupiter 5.14.1을 써요.
- **이유**: JDK 21이 설치되어 있어요. 라이브러리는 요구한 lombok·jackson·JUnit이에요.
- **lombok**: `provided` 스코프로 두고 `maven-compiler-plugin`의 `annotationProcessorPaths`에도 등록했어요. JDK 21에서는 암묵적 어노테이션 프로세서 탐색에 기대지 않고 명시해야 안정적이에요.

## 4. 초기 구조 확인용 예제 (2026-10-19)

- **결정**: `s.Hello`(유틸리티 클래스)와 `s.HelloTest`(JUnit 테스트)를 만들어 `./mvnw test`로 구조를 확인해요.
- **이유**: 빌드, lombok 처리, jackson 사용, 테스트 실행이 한 번에 검증돼요. `Hello`가 lombok(`@NoArgsConstructor`)과 jackson(`ObjectMapper`)을 모두 쓰는 이유예요.
