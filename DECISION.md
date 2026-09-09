# 주요 결정

- 단일 모듈 Maven 프로젝트와 공식 Maven Wrapper를 사용해 빌드 도구 사전 설치를 없애요. 간단한 유틸리티 빌드에는 별도 Gradle 스크립트보다 Maven 표준 구조로 충분해요.
- Java release를 21로 고정하고 Lombok annotation processor를 명시해요.
- Jackson 2 계열과 JUnit Jupiter 5 계열을 사용하고 의존성 버전을 고정해요.
