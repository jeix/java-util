# java-util

간단한 자바 유틸리티 프로젝트예요. Maven wrapper로 빌드하고 JUnit 5로 테스트해요.

## 요구 사항

- JDK 21
- Maven 설치는 필요 없어요. 저장소에 포함된 Maven wrapper(`mvnw`)를 써요.

## 구성

| 항목 | 내용 |
| --- | --- |
| 빌드 도구 | Maven 3.9.11 (Maven wrapper, `only-script` 방식) |
| Java | 21 (`maven.compiler.release`) |
| lombok | 1.18.44 (`provided`, 컴파일러 애노테이션 프로세서로 등록) |
| jackson-databind | 2.21.1 |
| slf4j-api | 2.0.19 (`@Slf4j` 로깅) |
| slf4j-simple | 2.0.19 (`test`, 테스트 로그 출력) |
| JUnit Jupiter | 5.14.1 (`test`) |

`only-script` 방식이라 wrapper용 JAR가 저장소에 들어 있지 않아요. 첫 실행 때 `mvnw`가 Maven 배포본을 내려받아 `~/.m2/wrapper`에 캐시해요.

## 디렉터리 구조

```
pom.xml
mvnw, mvnw.cmd, .mvn/wrapper/   Maven wrapper
src/main/java/s/Hello.java      유틸리티 클래스 (lombok, jackson 사용 예)
src/test/java/s/HelloTest.java  Hello의 JUnit 테스트
src/main/java/s/util/StringUtil.java      문자열 유틸리티 (static 메서드)
src/test/java/s/util/StringUtilTest.java  StringUtil의 JUnit 테스트
```

`s.Hello`와 `s.HelloTest`는 초기 구조가 제대로 동작하는지 확인하려고 둔 예제예요.

## StringUtil

`s.util.StringUtil`은 null 안전한 문자열 유틸리티예요. 기본값 선택(`nonNullOf`, `nonBlankOf`, `nonEmptyOf`), 첫 유효값 선택(`firstNonBlankOrLast`/`OrEmpty`/`OrNull`, 값·Supplier 버전), 변환·부분 문자열(`stringify`, `slice`, `head`, `tail`, `trimLeadingZero`, `repeat`, `reverse`), 패딩(`lpad`/`rpad`/`pad`, `lpad2`/`rpad2`/`pad2`), `join`, `split`을 제공해요. 경계 동작과 근거는 `DECISIONS.md` 5·6번에 있어요.

## 빌드와 테스트

```bash
./mvnw test      # 컴파일 + 테스트 실행
./mvnw package   # 테스트 후 JAR 생성 (target/)
```

Windows에서는 `mvnw.cmd`를 써요.

## git에 포함하지 않는 것

`.gitignore`로 아래 항목을 제외해요.

- `target/`: 빌드 산출물
- `.env-*`: 인증 정보가 들어 있는 로컬 환경 파일
- `.agents/`, `.codex/`, `.claude/settings.local.json`: 이 브랜치와 무관하거나 로컬 전용인 도구 설정
