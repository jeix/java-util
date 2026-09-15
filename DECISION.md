# DECISION - java-util

## 1. 빌드 도구로 Maven Wrapper를 사용한다

- 결정: 커밋된 `mvnw`(script-only wrapper, Maven 3.9.16)로 빌드/테스트를 실행한다.
- 대안: Gradle Wrapper, 시스템 `mvn`/`gradle` 직접 사용.
- 근거: 빌드 도구가 설치되어 있지 않은 환경을 전제로 하므로 저장소에 Wrapper를 포함해야 합니다.
  script-only 방식을 선택해 `maven-wrapper.jar` 같은 바이너리 파일을 커밋하지 않아도 되게 했습니다.

## 2. 의존성 버전은 로컬 캐시에 존재하는 조합으로 고정한다

- 결정: Lombok 1.18.34, Jackson 2.17.1, JUnit Jupiter 5.10.2, compiler plugin 3.13.0,
  surefire plugin 3.2.5, jar plugin 3.5.0.
- 대안: 최신 버전 사용, 플러그인 버전 미지정(기본값 사용).
- 근거: 네트워크가 막힌 환경에서도 `~/.m2/repository` 캐시만으로 빌드와 테스트가 재현되어야 합니다.
  기본(default) 버전에 의존하면 캐시에 없는 플러그인을 내려받으려다 실패할 수 있어 플러그인 버전을 명시했습니다.
  jar plugin은 캐시 상태를 확인해 3.5.0을 선택했습니다. 3.4.2는 `.pom`이 없어 의존성을 해석하지 못했고,
  3.3.0은 `maven-archiver`, `plexus-utils` 등 전이 의존성이 캐시에 없어 오프라인에서 `package`가 실패했습니다.

## 3. 샘플 클래스는 Lombok과 Jackson을 함께 사용해 검증한다

- 결정: `s.Hello`에서 Lombok(`@Data`, `@Builder`)으로 접근자/빌더를 생성하고,
  Jackson(`ObjectMapper`)으로 JSON 변환 메서드를 제공하며, `s.HelloTest`가 두 기능을 모두 테스트합니다.
- 근거: 구조 초기화 여부를 한 번의 테스트 실행으로 확인할 수 있습니다.

## 4. 이 브랜치와 무관한 파일은 커밋하지 않는다

- 결정: `.gitignore`에 `target/`, `.env*`를 추가하고, 다른 브랜치에서 만들어진 `target/` 내용과
  `.env-claude.sh`는 스테이징하지 않습니다.
- 근거: 요청대로 이 브랜치와 관련 없는 파일을 저장소에 추가하지 않기 위함입니다.
