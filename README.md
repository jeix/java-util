# java-util

자바 유틸리티 클래스와 타입을 모으고 JUnit 으로 테스트하는 프로젝트.

## 요구 사항

- JDK 21 (`maven.compiler.release=21`)
- 빌드 도구는 따로 설치하지 않아도 된다. Maven Wrapper 가 Maven 3.9.16 을 받아서 쓴다.
  이미 받아 둔 배포본이 있으면(`~/.m2/wrapper/dists/apache-maven-3.9.16`) 그걸 재사용하고,
  받을 때는 `distributionSha256Sum` 으로 검증한다.
- 처음 빌드할 때는 의존성을 받기 위해 네트워크가 필요하다.

## 빌드와 테스트

```bash
./mvnw test         # 컴파일 + 테스트
./mvnw clean test   # 빌드 산출물을 지우고 처음부터
./mvnw package      # jar 만들기
```

## 구성

| 위치 | 내용 |
|---|---|
| `s.Hello` | 프로젝트 뼈대가 도는지 확인하는 예제 (lombok · Jackson · JUnit) |
| `s.util.StringUtil` | 문자열 유틸리티 — 검사, 대체값 고르기, firstNonBlank 계열, stringify, 자르기, 채우기, join/split |
| `s.util.CollectionUtil` | 리스트·맵 유틸리티 — 검사, zip, 찾기, 집합 연산, 자르기, 색인/분류, asMap 계열 |
| `s.type.tuple.Pair` · `Triplet` · `Quartet` | 값을 2·3·4개 묶는 튜플 타입. JSON 으로 바꾸면 `{"ord1": ..., "ord2": ...}` 모양 |

테스트 클래스는 `s` 패키지 아래 소스와 같은 구조로 둔다(`src/test/java/...`).

## 의존성

| 라이브러리 | 쓰임 | 스코프 |
|---|---|---|
| Lombok | getter/equals/hashCode 생성, 테스트의 `@Slf4j` | provided |
| Jackson databind | JSON 직렬화·역직렬화 | compile |
| JUnit 5 (jupiter) | 테스트 | test |
| SLF4J api · simple | 테스트 클래스의 `@Slf4j` 로그 | test |

## 문서

- `PRD.md` — 무엇을 만들려는 것인지(요구사항과 범위)
- `PLAN.md` — 어떤 순서로 진행했는지(단계별 체크리스트)
- `CLAUDE.md` — 이 저장소에서 코드를 쓸 때 지킬 규칙
- `DECISIONS.md` — 결정과 그 근거
