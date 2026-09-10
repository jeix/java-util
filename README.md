# java-util

JDK 21 기반의 자바 유틸리티 프로젝트예요. Lombok, Jackson, JUnit Jupiter를 사용해요.

## 준비

- JDK 21과 `java` 실행 경로를 설정해요 (`JAVA_HOME` 설정 권장).
- Maven은 별도로 설치하지 않아도 돼요. Maven Wrapper가 Maven 3.9.11을 다운로드해요.
- 최초 실행에는 Maven과 의존성을 다운로드할 인터넷 연결이 필요해요.

## 빌드와 테스트

`mvnw` 뒤에는 Maven lifecycle phase나 plugin goal을 지정해요. 자주 쓰는 명령은 다음과 같아요.

| 목적 | 명령 | 동작 |
| --- | --- | --- |
| 생성물 삭제 | `./mvnw clean` | `target/`을 삭제해요. |
| main 코드만 컴파일 | `./mvnw compile` | `src/main/java`를 `target/classes`로 컴파일해요. |
| 테스트 코드까지 컴파일 | `./mvnw test-compile` | main 코드와 `src/test/java`를 컴파일하며 테스트는 실행하지 않아요. |
| 전체 테스트 | `./mvnw test` | main·테스트 코드를 컴파일하고 전체 테스트를 실행해요. |
| 특정 테스트 클래스 | `./mvnw -Dtest=s.HelloTest test` | 지정한 테스트 클래스만 실행해요. |
| JAR 빌드 | `./mvnw package` | 전체 테스트를 실행한 뒤 JAR를 만들어요. |
| 테스트 실행 없이 JAR 빌드 | `./mvnw -DskipTests package` | 테스트 코드는 컴파일하고 실행만 건너뛰어요. |
| main 코드만으로 JAR 빌드 | `./mvnw -Dmaven.test.skip=true package` | 테스트 컴파일과 실행을 모두 건너뛰어요. |
| 전체 검증 | `./mvnw clean verify` | 기존 생성물을 지우고 테스트·패키징·검증 단계를 실행해요. |
| 로컬 저장소 설치 | `./mvnw install` | 검증한 JAR와 POM을 로컬 Maven 저장소에 설치해요. |
| 의존성 확인 | `./mvnw dependency:tree` | 직접·전이 의존성 트리를 출력해요. |

캐시된 Maven과 의존성만 사용하려면 명령에 `-o`를 추가해요. 예: `./mvnw -o test`.

Windows에서는 `./mvnw` 대신 `mvnw.cmd`를 사용해요.
빌드 결과는 `target/java-util-1.0-SNAPSHOT.jar`, 테스트 결과는 `target/surefire-reports/`에 생성돼요.
JAR는 유틸리티 라이브러리이며 의존성을 포함하지 않아요.

## 구조

```text
src/main/java/s/Hello.java                  # 초기 구조 확인 예제
src/main/java/s/util/StringUtil.java        # 문자열 유틸리티
src/main/java/s/util/CollectionUtil.java    # 컬렉션 유틸리티
src/main/java/s/type/tuple/Pair.java        # 2개 값 튜플
src/main/java/s/type/tuple/Triplet.java     # 3개 값 튜플
src/main/java/s/type/tuple/Quartet.java     # 4개 값 튜플
src/test/java/s/HelloTest.java              # 초기 구조 확인 테스트
src/test/java/s/util/StringUtilTest.java     # 문자열 유틸리티 테스트
src/test/java/s/util/CollectionUtilTest.java # 컬렉션 유틸리티 테스트
src/test/java/s/type/tuple/TupleTest.java   # 튜플 타입 테스트
pom.xml                                      # Java 버전, 의존성, 빌드 설정
.mvn/wrapper/                                # Maven Wrapper 설정
mvnw / mvnw.cmd                              # Maven 실행 스크립트
```

유틸리티 클래스는 `src/main/java`, 테스트 클래스는 같은 패키지의 `src/test/java`에 추가해요.
테스트 클래스 이름은 `*Test`로 지으면 자동으로 실행돼요.
`HelloTest`는 인사말 생성과 Lombok으로 생성한 접근자를 이용한 Jackson JSON 왕복 변환을 확인해요.

## 문자열 유틸리티

`s.util.StringUtil`에 null/blank/empty 판별과 대체, 첫 유효값 선택, 자르기,
선행 0 제거, 반복·역순, 패딩, 리스트 결합·분할, 문자열 함수 합성 기능이 있어요.

```bash
./mvnw -Dtest=s.util.StringUtilTest test
```

- `isBlank`는 null 또는 Java `String.isBlank()` 기준 공백을, `isEmpty`는 null 또는 빈 문자열을 판별해요.
- Supplier는 필요한 순서대로 한 번씩 호출해요. null Supplier는 null 값으로 취급하고 내부 예외는 전달해요.
- `firstNonBlankOrLast`는 유효값이 없으면 마지막 값을 그대로 반환해요. `OrEmpty`/`OrNull`은 각각 `""`/null을 반환해요.
- 반복 처리가 필요한 첫 유효값 탐색과 `join`은 for-loop와 stream 구현 중 하나를 실행 시 무작위로 선택해요. 두 구현의 반환값과 Supplier 평가 순서는 같아요.
- Supplier stream 구현은 `peek`의 부수효과에 의존하지 않고 후보 자체를 최종 결과로 선택해요. 자세한 배경은 [Java Stream의 부수효과 안내](https://docs.oracle.com/javase/jp/21/docs/api/java.base/java/util/stream/package-summary.html#SideEffects)를 참고해요.
- `stringify(null)`은 `""`, `Date`는 시스템 기본 시간대 기준 `yyyy-MM-dd`, `BigDecimal`은 `toPlainString()`, 다른 객체는 `toString()` 결과를 반환해요.
- `StringUtilTest`는 Lombok `@Slf4j`를 사용해요. 테스트 범위의 `slf4j-api`와 `slf4j-simple`로 로그를 출력해요 ([SLF4J 설정 참고](https://www.slf4j.org/manual.html)).
- `slice`의 음수 인덱스는 문자열 끝에서부터 세며 모든 인덱스를 `0..length`로 제한해요. 변환한 end가 begin보다 작으면 `""`를 반환해요.
- `head(s, -n)`은 뒤에서 `n`자를 제외하고, `tail(s, -n)`은 앞에서 `n`자를 제외해요. 범위를 벗어난 size는 문자열 길이에 맞춰 제한해요.
- `slice`/`head`/`tail`/`trimLeadingZero`/`reverse`/패딩은 null 입력을 유지해요.
- `trimLeadingZero`는 부호 있는 정수·소수를 지원하고 정수부에 0 하나는 남겨요. loop와 정규식 치환 구현을 실행 시 무작위로 선택하며 숫자가 아닌 값은 원문을 유지해요.
- `repeat`는 `String`을 받아요. null은 null, 0 이하 횟수는 `""`를 반환해요.
- `lpad`/`rpad`/`pad`는 UTF-8 기준 single-byte 문자 하나로 구성된 `String`만 받아요. null, `""`, `"가"`, `"do"` 같은 pad는 원문을 반환해요. 이름이 `2`로 끝나는 메서드는 임의의 문자열 패턴을 받아요.
- `pad`/`pad2`는 가운데 정렬하고 남는 한 칸은 오른쪽에 채워요. 각 방향에서 패턴을 처음부터 반복하고 필요한 길이만 사용해요. 원문이 이미 충분히 길거나 패턴이 null/빈 문자열이면 원문을 유지해요.
- `join`은 null 리스트를 `""`, null 요소·구분자를 `""`로 취급해요.
- `split`은 끝의 빈 항목을 유지하는 불변 리스트를 반환해요. null 문자열은 빈 리스트, null 정규식은 원문 하나를 반환하고 잘못된 정규식은 예외를 전달해요.
- `pipe(Function<String,String>...)`는 입력값을 identity로 삼는 stream reduce 구현과 `Function::andThen` 합성 구현 중 하나를 호출 시 무작위로 선택해요. 두 구현 모두 함수를 전달된 순서대로 적용해 하나의 `Function`을 반환해요. `pipe()`는 `then`으로 함수를 추가하고 `apply`로 실행하는 불변 `Pipeline`을 반환해요. null 함수는 건너뛰며 함수 내부 예외는 전달해요.

```java
String input = "2025-03-19 12:26:41.012345000";

String fraction1 = StringUtil.pipe(
        value -> StringUtil.slice(value, 20),
        StringUtil::reverse,
        StringUtil::trimLeadingZero,
        StringUtil::reverse
).apply(input); // "012345"

String fraction2 = StringUtil.pipe()
        .then(value -> StringUtil.slice(value, 20))
        .then(StringUtil::reverse)
        .then(StringUtil::trimLeadingZero)
        .then(StringUtil::reverse)
        .apply(input); // "012345"
```

설정 참고: [Maven Wrapper](https://maven.apache.org/tools/wrapper/index.html),
[Lombok Maven 설정](https://projectlombok.org/setup/maven),
[Jackson 2.21](https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.21),
[JUnit 5](https://docs.junit.org/5.14.1/user-guide/).

## 컬렉션 유틸리티

`s.util.CollectionUtil`은 리스트 판별·대체, zip, 배열 변환, 검색, 집합 연산,
부분 리스트, 색인·그룹 생성, 맵 생성·형변환·복사를 제공해요.
`CollectionUtilTest`도 Lombok `@Slf4j`를 사용해요.

```bash
./mvnw -Dtest=s.util.CollectionUtilTest test
```

```java
List<Pair<String, Integer>> pairs = CollectionUtil.zip(List.of("a", "b"), List.of(1, 2));
String first = pairs.getFirst().ord1();
Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);
String[] array = CollectionUtil.toArray(List.of(), String.class);
```

- null 리스트·맵은 빈 불변 컬렉션으로 취급해요. `emptyIfNull`은 삼항 연산자를 사용하며 non-null 입력도 방어적으로 복사한 불변 컬렉션으로 반환해요.
- 컬렉션을 반환하는 모든 메서드는 불변 리스트 또는 불변 맵을 반환해요. `grouping`의 각 그룹 리스트도 불변이며, null 요소와 null 키·값은 유지해요.
- 기존 반복문이 있는 `zip`, `findOne`, `findAll`, `indexing`, `grouping`, `asMap`은 loop와 stream 구현 중 하나를 실행 시 무작위로 선택해요. 두 구현의 결과와 콜백 평가 순서는 같아요.
- `zip`은 짧은 리스트 길이까지만 묶어요. 결과는 `s.type.tuple.Pair<T,U>`이며 `ord1()`·`ord2()`로 값을 읽어요.
- `toArray(list)`는 첫 non-null 항목의 런타임 클래스를 사용해요. null/빈 리스트/모두 null이면 `IllegalArgumentException`, 나머지 항목의 타입이 호환되지 않으면 `ArrayStoreException`이 발생해요. 빈 리스트·혼합 하위 타입에는 `toArray(list, Class<T>)`로 배열 타입을 지정해요.
- `findOne`은 첫 일치 항목 또는 null을, `findAll`은 순서와 중복을 유지한 불변 리스트를 반환해요. null 항목도 콜백에 전달해요. 일치 항목이 null이면 미발견과 구분되지 않아요.
- null 콜백은 결과 없음으로 취급해요. 콜백 내부 예외는 전달해요.
- `unionOf`는 `list1 + (list2 - list1)`, `intersectionOf`는 list2에 포함된 list1 항목, `differenceOf`는 list2에 없는 list1 항목을 반환해요. `symmetricDifferenceOf`는 양방향 차집합을 연결해요. `equals`/`hashCode`로 비교하며 입력 순서와 남는 항목의 중복을 유지해요.
- `slice`의 음수 인덱스는 리스트 끝에서부터 세며 모든 인덱스를 `0..size`로 제한해요. 변환한 end가 begin보다 작으면 빈 리스트를 반환해요. 결과는 원본과 분리된 불변 리스트예요.
- `head(list, -n)`은 뒤에서 `n`개를 제외하고, `tail(list, -n)`은 앞에서 `n`개를 제외해요. 범위를 벗어난 size는 리스트 길이에 맞춰 제한해요.
- `indexing`/`grouping`은 키 최초 등장 순서를 유지해요. 중복 키의 색인은 마지막 항목을 사용하고 그룹은 항목 순서·중복을 유지해요. null 키도 허용해요.
- `asMap`은 키/값 교대 인자를 받아요. 홀수 개면 `IllegalArgumentException`, Class 지정 시 타입이 맞지 않으면 `ClassCastException`이 발생해요. 타입 변환은 하지 않아요. null Class는 허용하지 않아요.
- `asMap(entries)`는 null entry를 건너뛰어요. 모든 맵 생성은 null 키/값을 허용하고 중복 키는 마지막 값으로 덮어쓰며 키 최초 등장 순서를 유지해요.
- `castKeyValue`는 실제 키/값 타입을 검사하거나 변환하지 않는 unchecked cast 후 불변 복사본을 반환해요. 타입 안전성은 호출자가 보장해야 해요.
- `copyOf`는 원본 순회 순서를 유지하는 불변 얕은 복사예요. 키·값 객체는 공유해요.

## 튜플 타입

`s.type.tuple` 패키지는 두 값의 `Pair<T,U>`, 세 값의 `Triplet<T,U,V>`,
네 값의 `Quartet<T,U,V,W>`를 제공해요.

```java
Pair<String, Integer> pair = Pair.of("cat", 2);
Triplet<String, Integer, Boolean> triplet = Triplet.of("cat", 2, true);
Quartet<String, Integer, Boolean, Double> quartet = Quartet.of("cat", 2, true, 4.5);
```

- 생성은 static factory `of(...)`를 사용하고 값은 순서대로 `ord1()`~`ord4()`에서 읽어요.
- `toString()`은 `(cat, 2, true, 4.5)`처럼 괄호 안에 값을 쉼표와 공백으로 구분해요.
- Lombok `@EqualsAndHashCode`가 모든 순서 값을 기준으로 동등성과 해시 코드를 생성해요.
- `ordN`은 Jackson JSON 속성 이름이에요. `of(...)`를 creator로 사용하므로 같은 속성 이름으로 역직렬화할 수 있어요.
- 모든 값은 null을 허용해요.

```bash
./mvnw -Dtest=s.type.tuple.TupleTest test
```

## 프로젝트 컨텍스트

- [AGENTS.md](AGENTS.md): 작업 지침
- [PRD.md](PRD.md): 목표·범위·요구사항
- [PLAN.md](PLAN.md): 완료·대기 상태와 검증 결과
- [DECISION.md](DECISION.md): 결정과 구현 가정
