# DECISION (주요 결정 사항 및 근거)

## D-1. 빌드 도구로 Maven Wrapper를 사용한다

- 배경: 전역 Maven/Gradle이 설치되어 있지 않다.
- 결정: Maven Wrapper(only-script 방식, Maven 3.9.16)를 저장소에 포함한다.
- 근거: 표준 디렉터리 관례와 의존성 관리가 명확하고, 별도 jar 없이 스크립트만으로 부트스트랩할 수 있다.
  로컬 `~/.m2` 캐시를 재사용하므로 최초 실행 시 재다운로드를 피할 수 있다.

## D-2. Java 21을 대상으로 한다

- 배경: 설치된 JDK가 21이다.
- 결정: `maven.compiler.release=21`, 소스 인코딩 UTF-8.
- 근거: 환경에서 바로 컴파일·테스트 가능하며, 최신 언어 기능을 사용할 수 있다.

## D-3. 패키지 `s`, 예제 클래스 `Hello`

- 결정: 패키지는 `s`, 구조 확인용 클래스는 `s.Hello`와 `s.HelloTest`.
- 근거: 요구된 예제 명세를 그대로 따르며, 유틸리티 클래스의 최소 단위 예시로 적합하다.

## D-4. 의존성 버전

- lombok 1.18.48 (provided, `annotationProcessorPaths`로 애노테이션 처리)
- jackson-databind 2.17.1
- junit-jupiter 5.14.1 (test)
- maven-compiler-plugin 3.13.0, maven-surefire-plugin 3.5.4
- 근거: 로컬 `~/.m2` 저장소에 캐시되어 있어 오프라인에서도 빌드 가능하며, Java 21과 호환된다.

## D-5. 무관 파일을 git에 추가하지 않는다

- 결정: `.gitignore`에 `target/`, `.env-claude`를 등록한다.
- 근거: 두 항목은 이 브랜치의 작업과 무관한 파일·산출물이므로 커밋 대상에서 제외한다.
  `.agents/`, `.codex/`는 빈 디렉터리라 git이 추적하지 않는다.

## D-6. 문서 위치

- 결정: 개요는 루트 `README.md`, 운영 문서는 `docs/`에 둔다.
- 근거: 루트는 진입점 역할만 하고, 세션 연속성용 문서(AGENT/PRD/PLAN/DECISION)는 한곳에 모아 관리한다.
