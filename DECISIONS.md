# 결정 기록

"왜 이렇게 했는가"를 번호를 매겨 남긴다. 뒤에 온 결정이 앞의 것을 뒤집으면 그 자리에서 밝힌다.
코드와 Javadoc 만 봐서는 복원되지 않는 판단을 적는 곳이다. 앞으로 할 일은 여기 적지 않는다.

---

## 1. 빌드 도구는 Maven + Wrapper (2026-09-15)

빌드 도구가 설치돼 있지 않은 환경이라 `./mvnw` 로 부트스트랩했다.

- Gradle 대신 Maven: Gradle 배포본은 100MB 를 넘고 wrapper jar(바이너리)를 저장소에 둬야 한다.
  Maven Wrapper 의 `only-script` 방식은 스크립트만 두면 되고 저장소에 바이너리가 들어가지 않는다.
- Maven 3.9.16 배포본을 직접 내려받아 sha512 를 공식 값과 대조해 검증하고, 그 sha256 을
  `distributionSha256Sum` 에 넣었다. 이미 받아 둔 배포본이 있으면 그걸 재사용한다.

## 2. 의존성 스코프 (2026-09-15)

- Lombok 은 컴파일 때만 쓰이므로 `provided` 로 두되, `annotationProcessorPaths` 에 명시했다.
  JDK 21 에서 애노테이션 처리를 명시적으로 켜기 위해서다.
- 테스트 클래스의 `@Slf4j` 를 돌리려면 slf4j-api 와 바인딩이 필요해서 `slf4j-api`,
  `slf4j-simple` 을 `test` 스코프로 넣었다. main 코드는 slf4j 를 쓰지 않는다.
- 플러그인 버전은 고정한다(compiler 3.16.0, surefire 3.5.4).

## 3. 같은 일을 하는 구현이 둘 이상이면 무작위로 고른다 (2026-09-15)

`_xxxByLoop` / `_xxxByStream` 처럼 두 벌을 두고 `ThreadLocalRandom.current().nextBoolean()` 으로
고른다. 요청에 따라 도입한 방식이라, 두 구현은 **결과가 같아야** 한다. 한쪽만 고치면 테스트가
간헐적으로 깨지므로 고친 뒤에는 테스트를 여러 번 돌린다.

## 4. 실행이 보장되지 않는 중간 연산에 기대지 않는다 (2026-09-15)

처음에는 stream 구현에서 `peek` 으로 마지막 값을 모았는데, `java.util.stream` 패키지 문서가
"구현체는 결과에 영향이 없다고 판단되면 부수 효과를 생략할 수 있다"고 못박고 있어서 걷어냈다.

- `_firstNonBlankOrLast` 는 마지막 supplier 를 stream 대상에서 떼어내고 `orElseGet` 의 지연
  평가 대상으로 넘기는 방식으로 바꿨다. supplier 평가 횟수까지 for 문 방식과 같아진다.
- 부수 효과가 필요하면 종단 연산(`forEach`)을 쓴다.
- `Collectors.toMap` 은 값이 `null` 이면, `Collectors.groupingBy` 는 분류가 `null` 이면 예외를
  던져 for 문 방식과 결과가 갈린다. 그래서 `indexing`·`grouping`·`asMap` 의 stream 구현은
  수집기 대신 `forEach` 로 담는다.

## 5. StringUtil 의 규칙 (2026-09-15)

- 문자열을 받아 문자열을 돌려주는 메서드는 입력이 `null` 이면 `null` 을 돌려준다.
  (`stringify(null)` → `null`, `join` 의 `null` 원소 → `"null"` 문자열)
- 목록을 받거나 돌려주는 메서드는 `null` 과 빈 목록을 같게 다룬다(`split(null)` → 빈 목록).
- 음수 인덱스는 뒤에서부터 세는 역방향으로 본다(`slice("abcdef", -2)` → `"ef"`).
  `head` 는 그대로 역방향이고, `tail` 은 양수로 바꿔 "앞에서 제외할 글자 수"로 본다.
- `lpad`/`rpad`/`pad` 는 `len` 이 이미 문자열 길이 이상이면 원본을 그대로 돌려준다
  (Oracle 처럼 자르지 않는다). `pad` 는 가운데 정렬이고 남는 한 글자는 오른쪽에 붙인다.

## 6. CollectionUtil 의 null 정책은 StringUtil 과 다르다 (2026-09-15)

리스트·맵·함수 파라미터가 `null` 이면 `Objects.requireNonNull(param, "param")` 으로 바로
실패시킨다. 검사 메서드인 `isEmpty` 와 `emptyIfNull` 만 `null` 을 견딘다(`isEmpty(null)` → `true`).

클래스마다 규칙이 다르므로 새 메서드를 넣기 전에 그 클래스의 Javadoc 을 먼저 본다.

## 7. CollectionUtil 이 돌려주는 컬렉션은 불변 (2026-09-15)

새로 만들어 돌려주는 리스트·맵은 `Collections.unmodifiableList` / `unmodifiableMap` 로 감싼다.
`grouping` 은 값으로 들어 있는 리스트까지 감싼다. 빈 결과는 `Collections.emptyList()`.

예외 셋: `emptyIfNull` 은 넘어온 것을 그대로 넘기고(새로 만드는 빈 컬렉션은 바꿔 넣을 수 있다),
`castKeyValue` 는 캐스팅만 하고, `copyOf` 는 `Map.copyOf` 를 쓴다.

`List.copyOf`/`Map.copyOf` 로 통일하지 않은 이유는 그쪽이 `null` 원소를 거부해서
`findAll` 로 `null` 이 섞이거나 `asMap("k", null)` 을 하면 예외가 나기 때문이다.
`copyOf` 는 이름과 짝이 맞고 기존 동작을 유지하기로 해서 `Map.copyOf` 그대로 둔다
(키·값 `null` 거부, 항목 순서 미보장).

## 8. CollectionUtil 의 집합 연산과 자르기 (2026-09-15)

- 집합 연산은 `differenceOf` 한 곳에만 구현을 두고 나머지는 제시된 수식 그대로 조합한다.
  `unionOf = list1 + differenceOf(list2, list1)`,
  `intersectionOf = differenceOf(list1, differenceOf(list1, list2))`,
  `symmetricDifferenceOf = differenceOf(list1, list2) + differenceOf(list2, list1)`.
  같은 항목이 여러 번 있으면 모두 빠지고, `list1` 의 중복은 보존된다.
- `zip` 은 길이가 다르면 짧은 쪽에 맞춘다(Python·Guava 방식). mixer 에 `null` 이 안 들어간다.
- `slice`/`head`/`tail` 은 `subList` 뷰가 아니라 새 리스트를 돌려준다. 뷰를 넘기면 원본을 고칠 때
  깨지기 때문이다.

## 9. trimLeadingZero 의 정규식은 `\p{Nd}` (2026-09-15)

for 문 방식은 `Character.isDigit`(유니코드 Nd)을 쓰는데 자바 정규식의 `\d` 는 기본이 아스키
숫자뿐이라 `"0٠"` 같은 입력에서 두 구현이 갈렸다. 정규식을 `^0+(?=\p{Nd})` 로 맞춰 범위를
일치시켰다. 무작위 분기에서는 이렇게 두 구현의 관찰 가능한 동작이 같아야 한다.

## 10. toArray 가 돌려주는 배열의 런타임 타입은 Object[] (2026-09-15)

`List<T>` 만 받아서는 `T[]` 를 만들 수 없어 `(T[]) list.toArray()` 로 둔다. 정적 타입은 `T[]` 지만
실제로는 `Object[]` 라서, `String[]` 로 받으면 `ClassCastException` 이 난다. Javadoc 에 적어 두고
테스트로 `Object[].class` 인지 확인한다. 원소 타입 배열이 필요하면 `list.toArray(T[]::new)`.

## 11. 튜플은 스태틱 팩터리 + `ordN()` 접근자 (2026-09-15)

- 생성자는 `private`, 만드는 통로는 `of(...)` 하나.
- 값을 꺼내는 메서드 이름은 자리 순서대로 `ord1()` ~ `ordN()`. 데이터 타입이라 인스턴스 메서드다.
- `@EqualsAndHashCode` 로 값 비교. 접근자 이름과 `toString` 형식이 지정돼 있어
  `@Getter` / `@ToString` 은 쓰지 않고 직접 구현한다.
- `ordN()` 에 `@JsonProperty("ordN")`, `of(...)` 에 `@JsonCreator` 를 붙여 JSON 키가 `ord1`~`ord4` 가
  되게 했다. JSON 변환 대상 필드가 `ordN()` 이라는 요청을 그대로 옮긴 것이다.

## 12. 무작위 분기를 넣은 뒤에는 두 구현을 맞붙여 확인한다 (2026-09-15)

테스트를 여러 번 돌리는 것만으로는 두 구현이 정말 같은지 확신하기 어려워서, 리플렉션으로
`_xxxByLoop` 과 `_xxxByStream` 을 직접 불러 결과와 supplier 평가 횟수를 비교했다. 이 방식으로
`peek` 문제와 `\p{Nd}` 문제를 찾았다. 확인용 코드는 저장소에 넣지 않는다(비공개 메서드를
리플렉션으로 부르는 코드라 테스트로 남기기엔 부담이 크다).
