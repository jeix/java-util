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
    ├── main/java/s/util/CollectionUtil.java # 리스트/맵 유틸리티
    ├── test/java/s/HelloTest.java          # JUnit 5 테스트 예시
    ├── test/java/s/type/tuple/TupleTest.java
    ├── test/java/s/util/StringUtilTest.java
    └── test/java/s/util/CollectionUtilTest.java
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
| `pipe(fns)` | 함수들을 순서대로 적용하는 `Function<String, String>`을 만듭니다. |
| `pipe()` | `then`으로 함수를 이어 붙이는 `Pipeline`을 만듭니다. |

구현 규칙은 다음과 같습니다.

- 파라미터를 먼저 검사하고, 정상적으로 처리할 수 없는 경우는 그대로 리턴합니다.
- 같은 기능이 이미 다른 메서드에 있으면 그 메서드를 호출합니다.
- 오버로드된 메서드는 실질적인 구현 메서드 하나로 위임하고, 구현 메서드가 private이면 이름 앞에 `_`를 붙입니다.
- 단순 분기는 3항 연산자를 사용하고, 반복문 구현과 스트림 구현을 함께 두는 메서드는 실행 시 랜덤하게 분기합니다.

`pipe`는 같은 작업을 두 가지 스타일로 표현할 수 있습니다.

```java
String input = "2025-03-19 12:26:41.012345000";

Function<String, String> piped = StringUtil.pipe(
        s -> StringUtil.slice(s, 20),
        StringUtil::reverse,
        StringUtil::trimLeadingZero,
        StringUtil::reverse);
String a = piped.apply(input); // "012345"

String b = StringUtil.pipe()
        .then(s -> StringUtil.slice(s, 20))
        .then(StringUtil::reverse)
        .then(StringUtil::trimLeadingZero)
        .then(StringUtil::reverse)
        .apply(input); // "012345"
```

`pipe(fns)`는 함수 합성 구현(`_pipeByCompose`)과 입력값 축소 구현(`_pipeByReduce`)을 두고 실행 시 랜덤하게 분기합니다.
두 구현은 같은 결과를 냅니다.

## s.util.CollectionUtil

| 메서드 | 설명 |
| --- | --- |
| `isEmpty(list)`, `emptyIfNull(list)` | `null`/빈 리스트 판별과 `null` 대체 |
| `zip(list1, list2)`, `zip(list1, list2, mixer)` | 짧은 쪽 길이에 맞춰 `Pair` 목록 또는 믹서 결과 생성 |
| `toArray(list)` | 배열로 변환(런타임 배열 타입은 `Object[]`) |
| `findOne(list, filter)`, `findAll(list, filter)` | 조건에 맞는 첫 항목/모든 항목 |
| `unionOf`, `intersectionOf`, `differenceOf`, `symmetricDifferenceOf` | 집합 연산(요청한 수식 순서 유지, `list1`의 중복 유지) |
| `slice`, `head`, `tail` | 음수 인덱스를 역방향으로 해석하는 부분 리스트 |
| `indexing(list, indexer)`, `grouping(list, classifier)` | 색인 맵, 분류 맵 |
| `isEmpty(map)`, `emptyIfNull(map)` | `null`/빈 맵 판별과 `null` 대체 |
| `asMap(items)`, `asMap(keyClass, valueClass, items)`, `asMap(entries)` | `key, value` 쌍·타입 검사·`Map.Entry` 목록으로 맵 생성 |
| `castKeyValue(origin)`, `copyOf(origin)` | 키/값 타입 캐스팅, 불변 복사 |

- 새로 만든 결과 컬렉션은 불변 컬렉션입니다. `emptyIfNull`과 `castKeyValue`는 원본을 그대로 반환합니다.
- `asMap`에 홀수 개 항목을 넘기면 `IllegalArgumentException`, `asMap(keyClass, valueClass, ...)`에서 타입이 다르면
  `ClassCastException`이 발생합니다.
- `indexing`은 같은 색인에 대해 먼저 나온 항목을 유지하고, `grouping`은 항목 순서를 유지합니다.
- `zip(list1, list2, mixer)`, `findOne`, `indexing`, `grouping`, `asMap(items)`, `asMap(entries)`는 반복문 구현과
  스트림 구현을 함께 두고 실행 시 랜덤하게 분기합니다. 두 구현은 같은 결과를 냅니다.

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
