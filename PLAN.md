# 작업 상태

- 목표: JDK 21에서 유틸리티 클래스와 JUnit 테스트를 빌드하고 실행하는 최소 프로젝트예요.
- 구현: Maven 설정, Lombok/Jackson/JUnit 의존성, `s.Hello`, `s.HelloTest`, 실행 안내를 추가했어요.
- 검증 완료: JDK 21.0.11에서 Maven Wrapper 최초 다운로드, `clean verify`, `-Dtest=s.HelloTest test`가 성공했어요. 테스트 2개가 통과했고 JAR가 생성됐어요.
- 검증 환경: 다운로드 캐시와 Maven 로컬 저장소는 `/tmp` 하위 경로를 사용했어요. 개별 테스트는 캐시를 사용하는 오프라인 모드로도 통과했어요.
- 정적 확인: `git diff --check`와 `sh -n mvnw`가 통과했어요. 별도 formatter/lint는 설정하지 않았어요.
- 사용자 승인: 초기화 변경 사항을 커밋하도록 요청받았어요.
- 다음 단계: 현재 브랜치에서 필요한 유틸리티와 테스트를 추가해요. push는 명시적으로 요청할 때만 해요.
