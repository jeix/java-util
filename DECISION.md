# DECISION - 주요 결정 사항 및 근거

## 1. 빌드 도구: Maven + Wrapper
**결정**: Maven Wrapper(mvnw) 사용, 별도 Maven 설치 불필요
**근거**: 
- 실행 환경 일관성 보장 (CI/CD, 팀원 간 버전 차이 방지)
- JDK 21만 있으면 즉시 빌드 가능
- 프로젝트 이식성 최대화

## 2. Java 버전: 21 (LTS)
**결정**: Java 21 컴파일 타겟 및 소스 레벨 설정
**근거**: 
- 현재 LTS 버전으로 장기 지원 보장
- 가상 스레드, 패턴 매칭 등 최신 기능 활용 가능
- 기업 환경 표준 버전

## 3. Lombok 적용 범위: provided 스코프
**결정**: `<scope>provided</scope>`로 설정, 컴파일러 플러그인에 annotationProcessorPaths 명시
**근거**: 
- 런타임 의존성에서 제외 (jar 크기 최소화)
- 컴파일 시 어노테이션 프로세서로만 동작
- IDE 지원 시 별도 플러그인 필요하지만 빌드는 독립적

## 4. Jackson 의존성: databind + annotations 분리
**결정**: jackson-databind, jackson-annotations 별도 선언 (jackson-core는 전이적 의존)
**근거**: 
- annotations만 필요한 모듈은 databind 의존 없이 사용 가능
- 버전 관리를 properties로 일원화
- 최소 의존성 원칙 준수

## 5. 테스트 프레임워크: JUnit 5 (Jupiter)
**결정**: JUnit 4가 아닌 JUnit 5 사용, surefire-plugin 3.2.5 적용
**근거**: 
- JUnit 5는 모듈화된 아키텍처 (Platform, Jupiter, Vintage)
- 확장 모델, 파라미터화 테스트, 동적 테스트 등 현대적 기능
- Maven Surefire 3.x는 JUnit Platform 네이티브 지원

## 6. 패키지 구조: 단일 패키지 `s`
**결정**: 기능별 패키지 분리 없이 단일 패키지 `s` 사용
**근거**: 
- 초기화 단계에서는 최소 구조로 시작
- 향후 도메인/계층별 분리 시 리팩토링 용이
- 예제 클래스 확인 목적에 부합

## 7. 인코딩: UTF-8 강제
**결정**: `project.build.sourceEncoding=UTF-8`, 컴파일러 인코딩 명시
**근거**: 
- 한글 주석/문자열 깨짐 방지
- 크로스 플랫폼 빌드 일관성
- Maven 기본값이 플랫폼 종속적일 수 있음

## 8. 플러그인 버전 고정
**결정**: maven-compiler-plugin 3.13.0, maven-surefire-plugin 3.2.5 명시
**근거**: 
- 재현 가능한 빌드 보장
- 슈퍼 POM 기본값 변경에 영향 받지 않음
- 보안 취약점 대응 시 버전 업그레이드 용이