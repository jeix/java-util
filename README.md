# java-util

Java 언리틸리티 프로젝트입니다. JDK 21 + Maven을 기반으로 하며, Lombok과 Jackson을 사용합니다.

## 시작하기

### 빌드 및 테스트

```bash
# Maven wrapper로 테스트 실행 (Maven 설치 불필요)
./mvnw clean test

# JAR 빌드
./mvnw clean package
```

### 프로젝트 구조

```
java-util/
├── .gitignore
├── .mvn/wrapper/
│   ├── maven-wrapper.jar
│   └── maven-wrapper.properties
├── mvnw                    # Unix용 Maven wrapper
├── mvnw.cmd                # Windows용 Maven wrapper
├── pom.xml                 # Maven 빌드 설정
└── src/
    ├── main/java/
    │   └── s/Hello.java    # 유틸리티 클래스
    └── test/java/
        └── s/HelloTest.java # JUnit 5 테스트
```

## 의존성

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Lombok | 1.18.34 | 보일러플레이트 코드 제거 |
| Jackson Databind | 2.17.2 | JSON 처리 |
| JUnit Jupiter | 5.10.3 | 테스트 프레임워크 |

## 요구사항

- JDK 21
- 인터넷 연결 (의존성 다운로드용)
