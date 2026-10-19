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

## 5. StringUtil 동작 규칙 (2026-10-19)

- **결정**: `s.util.StringUtil`은 null 문자열을 ""로 취급하고, 처리할 수 없는 입력은 예외 없이 `s`(또는 "")를 돌려줘요. 구현은 오버로드 중 하나(supplier 버전, `_firstNonBlankOr*`)에 모으고 나머지는 위임해요.
- **세부 규칙**: `head`/`tail`은 size가 음수이거나 길이보다 크면 "". `slice`는 end를 길이로 보정하고 begin이 음수이거나 end 이상이면 "". `lpad`/`rpad`/`pad`는 패딩 불가(len ≤ 길이, 싱글바이트 아님, pad 길이 ≠ 1)이면 `s` 그대로이고 잘라내지 않아요. `pad`/`pad2`는 남는 칸이 홀수면 오른쪽이 하나 더. `split`은 뒤쪽 빈 문자열을 남기고(limit -1), `stringify(null)`은 "", `Date`는 시스템 기본 시간대의 `yyyy-MM-dd`.
- **이유**: 요구 사항에 정해지지 않은 경계를 위와 같이 정했어요. 바꾸려면 새 항목을 추가해요.
- **의존성**: `@Slf4j`를 쓰려고 `slf4j-api` 2.0.19(compile)와 `slf4j-simple`(test)를 추가했어요.

## 6. StringUtil 수정: 무작위 분기와 인덱스 규칙 (2026-10-19)

- **결정**: 5번 항목의 규칙 중 아래를 바꿔요(5번은 고치지 않고 이 항목이 우선해요).
  - `stringify(null)`은 null. `join`은 null 원소를 ""로 이어요.
  - `slice`는 음수 인덱스를 뒤에서부터 센 위치로 보고 범위를 [0, 길이]로 보정해요. `head(s, size)`는 `slice(s, 0, size)`와 같고(길이보다 크면 전체), `tail`의 음수 size는 앞에서 -size개를 뺀 나머지예요. 더는 "범위 밖이면 빈 문자열"이 아니에요.
  - `repeat`의 첫 파라미터는 `String`. `lpad`/`rpad`/`pad`의 pad는 싱글바이트 한 글자(`"가"`, `"do"`는 불가). `split`은 불변 리스트를 돌려줘요.
- **무작위 분기**: 반복문 구현과 stream 구현(`firstNonBlankOr*`, `join`, varargs → supplier 변환), 반복문과 정규식 구현(`trimLeadingZero`)을 두고 호출 때마다 `_useStream()`으로 무작위로 골라요. 두 구현은 결과가 같아야 해요.
- **테스트**: 분기가 있는 메서드의 테스트는 `@RepeatedTest(20)`으로 두 구현을 모두 거치게 했어요(한쪽만 20번 연속 걸릴 확률 약 2⁻¹⁹).
