# java-util

JDK 21 기반의 자바 유틸리티 프로젝트예요. Lombok, Jackson, JUnit Jupiter를 사용해요.

## 준비

- JDK 21과 `java` 실행 경로를 설정해요 (`JAVA_HOME` 설정 권장).
- Maven은 별도로 설치하지 않아도 돼요. Maven Wrapper가 Maven 3.9.11을 다운로드해요.
- 최초 실행에는 Maven과 의존성을 다운로드할 인터넷 연결이 필요해요.

## 빌드와 테스트

```bash
./mvnw test                      # 전체 테스트
./mvnw -Dtest=s.HelloTest test    # 특정 테스트 클래스
./mvnw clean verify              # 클린 빌드, 테스트, JAR 생성
```

Windows에서는 `./mvnw` 대신 `mvnw.cmd`를 사용해요.
빌드 결과는 `target/java-util-1.0-SNAPSHOT.jar`, 테스트 결과는 `target/surefire-reports/`에 생성돼요.
JAR는 유틸리티 라이브러리이며 의존성을 포함하지 않아요.

## 구조

```text
src/main/java/s/Hello.java      # 유틸리티 클래스
src/test/java/s/HelloTest.java  # JUnit 테스트 클래스
pom.xml                        # Java 버전, 의존성, 빌드 설정
.mvn/wrapper/                  # Maven Wrapper 설정
mvnw / mvnw.cmd                # Maven 실행 스크립트
```

유틸리티 클래스는 `src/main/java`, 테스트 클래스는 같은 패키지의 `src/test/java`에 추가해요.
테스트 클래스 이름은 `*Test`로 지으면 자동으로 실행돼요.
`HelloTest`는 인사말 생성과 Lombok으로 생성한 접근자를 이용한 Jackson JSON 왕복 변환을 확인해요.

설정 참고: [Maven Wrapper](https://maven.apache.org/tools/wrapper/index.html),
[Lombok Maven 설정](https://projectlombok.org/setup/maven),
[Jackson 2.21](https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.21),
[JUnit 5](https://docs.junit.org/5.14.1/user-guide/).
