# java-util

자바 유틸리티 클래스를 만들고 테스트하기 위한 최소 프로젝트 구조입니다.

## 요구 사항

- JDK 21
- 별도 빌드 도구 설치는 불필요합니다. Maven Wrapper(`mvnw`)가 Maven 배포본을 사용합니다.

## 의존성

| 용도 | 라이브러리 | 버전 |
| --- | --- | --- |
| 보일러플레이트 코드 생성 | Lombok | 1.18.34 (`provided`) |
| JSON 직렬화/역직렬화 | Jackson Databind | 2.17.1 |
| 테스트 | JUnit 5 (Jupiter) | 5.10.2 (`test`) |

## 빌드와 테스트

```bash
./mvnw clean test    # 컴파일 후 테스트 실행
./mvnw clean package # 테스트 실행 후 jar 생성 (target/java-util-1.0-SNAPSHOT.jar)
```

네트워크가 없는 환경에서는 로컬 캐시만 사용하도록 오프라인 모드로 실행할 수 있습니다.

```bash
./mvnw -o test
```

## 구조

```
├── mvnw, mvnw.cmd                     # Maven Wrapper (script-only)
├── .mvn/wrapper/maven-wrapper.properties
├── pom.xml
└── src
    ├── main/java/s/Hello.java         # Lombok + Jackson 사용 예시
    └── test/java/s/HelloTest.java     # JUnit 5 테스트 예시
```

유틸리티 클래스는 `src/main/java/s` 아래에, 테스트 클래스는 `src/test/java/s` 아래에 같은 패키지 구조로 추가합니다.

## 문서

- [PRD.md](PRD.md): 무엇을 만드는지, 목표와 범위
- [PLAN.md](PLAN.md): 계획, 수행 결과, 현재 상태와 다음 단계
- [DECISION.md](DECISION.md): 주요 결정과 근거
