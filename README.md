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
src/main/java/s/Hello.java                  # 초기 구조 확인 예제
src/main/java/s/util/StringUtil.java        # 문자열 유틸리티
src/main/java/s/util/CollectionUtil.java    # 컬렉션 유틸리티
src/main/java/s/util/Pair.java              # zip 결과 record
src/test/java/s/HelloTest.java              # 초기 구조 확인 테스트
src/test/java/s/util/StringUtilTest.java     # 문자열 유틸리티 테스트
src/test/java/s/util/CollectionUtilTest.java # 컬렉션 유틸리티 테스트
pom.xml                                      # Java 버전, 의존성, 빌드 설정
.mvn/wrapper/                                # Maven Wrapper 설정
mvnw / mvnw.cmd                              # Maven 실행 스크립트
```

유틸리티 클래스는 `src/main/java`, 테스트 클래스는 같은 패키지의 `src/test/java`에 추가해요.
테스트 클래스 이름은 `*Test`로 지으면 자동으로 실행돼요.
`HelloTest`는 인사말 생성과 Lombok으로 생성한 접근자를 이용한 Jackson JSON 왕복 변환을 확인해요.

## 문자열 유틸리티

`s.util.StringUtil`에 null/blank/empty 판별과 대체, 첫 유효값 선택, 자르기,
선행 0 제거, 반복·역순, 패딩, 리스트 결합·분할 기능이 있어요.

```bash
./mvnw -Dtest=s.util.StringUtilTest test
```

- `isBlank`는 null 또는 Java `String.isBlank()` 기준 공백을, `isEmpty`는 null 또는 빈 문자열을 판별해요.
- Supplier는 필요한 순서대로 한 번씩 호출해요. null Supplier는 null 값으로 취급하고 내부 예외는 전달해요.
- `firstNonBlankOrLast`는 유효값이 없으면 마지막 값을 그대로 반환해요. `OrEmpty`/`OrNull`은 각각 `""`/null을 반환해요.
- `stringify(null)`은 `""`, `Date`는 시스템 기본 시간대 기준 `yyyy-MM-dd`, `BigDecimal`은 `toPlainString()`, 다른 객체는 `toString()` 결과를 반환해요.
- `StringUtilTest`는 Lombok `@Slf4j`를 사용해요. 테스트 범위의 `slf4j-api`와 `slf4j-simple`로 로그를 출력해요 ([SLF4J 설정 참고](https://www.slf4j.org/manual.html)).
- `slice`/`head`/`tail`/`trimLeadingZero`/`reverse`/패딩은 null 입력을 유지해요. `slice`의 잘못된 범위는 `""`, `head`/`tail`의 0 이하 크기는 `""`, 원문보다 큰 크기는 원문을 반환해요.
- `trimLeadingZero`는 부호 있는 정수·소수를 지원하고 정수부에 0 하나는 남겨요. 숫자가 아닌 값은 원문을 유지해요.
- `repeat`는 `char`를 받아요. 0 이하 횟수는 `""`를 반환해요.
- `lpad`/`rpad`/`pad`는 `char`, 이름이 `2`로 끝나는 메서드는 문자열 패턴을 받아요. 단일 바이트 문자열을 전제로 하며 길이는 UTF-16 단위예요.
- `pad`/`pad2`는 가운데 정렬하고 남는 한 칸은 오른쪽에 채워요. 각 방향에서 패턴을 처음부터 반복하고 필요한 길이만 사용해요. 원문이 이미 충분히 길거나 패턴이 null/빈 문자열이면 원문을 유지해요.
- `join`은 null 리스트를 `""`, null 요소·구분자를 `""`로 취급해요.
- `split`은 끝의 빈 항목을 유지하는 수정 가능한 리스트를 반환해요. null 문자열은 빈 리스트, null 정규식은 원문 하나를 반환하고 잘못된 정규식은 예외를 전달해요.

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
String first = pairs.getFirst().first();
Map<String, Integer> map = CollectionUtil.asMap(String.class, Integer.class, "foo", 42, "bar", 43);
String[] array = CollectionUtil.toArray(List.of(), String.class);
```

- null 리스트·맵은 빈 컬렉션으로 취급해요. `emptyIfNull`은 non-null 입력을 그대로 반환하고 null이면 새 수정 가능한 컬렉션을 반환해요.
- `zip`은 짧은 리스트 길이까지만 묶어요. `Pair<T,U>`는 `s.util.Pair` record이며 `first()`·`second()`로 값을 읽어요.
- `toArray(list)`는 첫 non-null 항목의 런타임 클래스를 사용해요. null/빈 리스트/모두 null이면 `IllegalArgumentException`, 나머지 항목의 타입이 호환되지 않으면 `ArrayStoreException`이 발생해요. 빈 리스트·혼합 하위 타입에는 `toArray(list, Class<T>)`로 배열 타입을 지정해요.
- `findOne`은 첫 일치 항목 또는 null을, `findAll`은 순서와 중복을 유지한 새 리스트를 반환해요. null 항목도 콜백에 전달해요. 일치 항목이 null이면 미발견과 구분되지 않아요.
- null 콜백은 결과 없음으로 취급해요. 콜백 내부 예외는 전달해요.
- `unionOf`는 `list1 + (list2 - list1)`, `intersectionOf`는 list2에 포함된 list1 항목, `differenceOf`는 list2에 없는 list1 항목을 반환해요. `symmetricDifferenceOf`는 양방향 차집합을 연결해요. `equals`/`hashCode`로 비교하며 입력 순서와 남는 항목의 중복을 유지해요.
- `slice`는 begin 포함/end 불포함의 `subList` 뷰를 반환해요. `head`/`tail`도 이 뷰를 사용하며 길이를 0~원문 크기로 제한해요. 뷰의 수정 가능성과 변경 반영은 원본 리스트에 따라요. null이나 잘못된 slice 범위는 새 빈 리스트를 반환해요.
- `indexing`/`grouping`은 키 최초 등장 순서를 유지해요. 중복 키의 색인은 마지막 항목을 사용하고 그룹은 항목 순서·중복을 유지해요. null 키도 허용해요.
- `asMap`은 키/값 교대 인자를 받아요. 홀수 개면 `IllegalArgumentException`, Class 지정 시 타입이 맞지 않으면 `ClassCastException`이 발생해요. 타입 변환은 하지 않아요. null Class는 허용하지 않아요.
- `asMap(entries)`는 null entry를 건너뛰어요. 모든 맵 생성은 null 키/값을 허용하고 중복 키는 마지막 값으로 덮어쓰며 키 최초 등장 순서를 유지해요.
- `castKeyValue`는 원본 맵을 복사하지 않는 unchecked cast예요. Class 인자가 없어 실제 키/값 타입은 검사하지 않으며 호출자가 보장해야 해요. null이면 새 빈 맵을 반환해요.
- `copyOf`는 원본 순회 순서를 유지하는 수정 가능한 얕은 복사예요. 키·값 객체는 공유해요.

## 프로젝트 컨텍스트

- [AGENTS.md](AGENTS.md): 작업 지침
- [PRD.md](PRD.md): 목표·범위·요구사항
- [PLAN.md](PLAN.md): 완료·대기 상태와 검증 결과
- [DECISION.md](DECISION.md): 결정과 구현 가정
