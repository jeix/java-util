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
    ├── main/java/s/Hello.java              # Lombok + Jackson 사용 예시
    ├── main/java/s/type/tuple/Pair.java    # 튜플 데이터 타입
    ├── main/java/s/type/tuple/Triplet.java
    ├── main/java/s/type/tuple/Quartet.java
    ├── main/java/s/util/StringUtil.java    # 문자열 유틸리티
    ├── test/java/s/HelloTest.java          # JUnit 5 테스트 예시
    ├── test/java/s/type/tuple/TupleTest.java
    └── test/java/s/util/StringUtilTest.java
```

유틸리티 클래스는 `src/main/java/s` 아래에, 테스트 클래스는 `src/test/java/s` 아래에 같은 패키지 구조로 추가합니다.

## s.util.StringUtil

| 메서드 | 설명 |
| --- | --- |
| `isBlank`, `isEmpty` | `null`을 포함한 공백/빈 문자열 판별 |
| `nonNullOf`, `nonBlankOf`, `nonEmptyOf` | 값이 없을 때 기본값 선택 (값/공급자 오버로드) |
| `firstNonBlankOrLast`, `firstNonBlankOrEmpty`, `firstNonBlankOrNull` | 공백이 아닌 첫 번째 값 선택 (값 2개 + 가변 인자, 공급자 2~5개 오버로드) |
| `stringify` | `null`은 `null`, `Date`는 `yyyy-MM-dd`, `BigDecimal`은 `toPlainString()`으로 변환 |
| `slice`, `head`, `tail` | 음수 인덱스는 역방향으로 해석하고, 범위를 벗어나면 보정해 잘라내기 |
| `trimLeadingZero`, `repeat`, `reverse` | 문자열 가공(`repeat`은 `String` 단위를 결과 길이까지 반복) |
| `lpad`, `rpad`, `pad` | 단일 문자 패딩 |
| `lpad2`, `rpad2`, `pad2` | 여러 문자 패딩 |
| `join`, `split` | 목록 연결과 분리 |

구현 규칙은 다음과 같습니다.

- 파라미터를 먼저 검사하고, 정상적으로 처리할 수 없는 경우는 그대로 리턴합니다.
- 같은 기능이 이미 다른 메서드에 있으면 그 메서드를 호출합니다.
- 오버로드된 메서드는 실질적인 구현 메서드 하나로 위임하고, 구현 메서드가 private이면 이름 앞에 `_`를 붙입니다.
- 단순 분기는 3항 연산자를 사용하고, 반복문 구현과 스트림 구현을 함께 두는 메서드는 실행 시 랜덤하게 분기합니다.

## s.type.tuple

| 타입 | 필드 | 메서드 |
| --- | --- | --- |
| `Pair<T, U>` | `cat`, `dog` | `of`, `ord1`, `ord2`, `toString` |
| `Triplet<T, U, V>` | `cat`, `dog`, `elk` | `of`, `ord1`~`ord3`, `toString` |
| `Quartet<T, U, V, W>` | `cat`, `dog`, `elk`, `fox` | `of`, `ord1`~`ord4`, `toString` |

- 유틸리티가 아니라 데이터 타입이므로, 값은 `of(...)` 스태틱 팩터리로 만들고 `ord1()`~`ordN()` 인스턴스 메서드로 읽습니다.
- `toString()`은 `(값1, 값2, ...)` 형식입니다.
- 값 비교는 Lombok `@EqualsAndHashCode`를 사용하고, `ord1()`~`ordN()` 메서드에 Jackson `@JsonProperty`를 붙여
  JSON 변환 대상으로 지정합니다. JSON 키명은 `ord1`~`ord4`이므로 `Quartet.of("cat", 2, true, "fox")`는
  `{"ord1":"cat","ord2":2,"ord3":true,"ord4":"fox"}`로 변환됩니다(프로퍼티 순서는 보장하지 않습니다).

## 문서

- [PRD.md](PRD.md): 무엇을 만드는지, 목표와 범위
- [PLAN.md](PLAN.md): 계획, 수행 결과, 현재 상태와 다음 단계
- [DECISION.md](DECISION.md): 주요 결정과 근거
