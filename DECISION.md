# 주요 결정

## 빌드와 의존성

- 단일 모듈 Maven과 공식 only-script Maven Wrapper를 선택했어요. 간단한 유틸리티 프로젝트에는 Gradle 별도 스크립트 없이 표준 디렉터리 구조로 충분하고, 빌드 도구 사전 설치도 필요 없어요.
- Java release 21과 Lombok annotation processor를 명시해요. Jackson 2 계열과 JUnit Jupiter 5 계열을 사용하며 정확한 의존성·플러그인 버전은 `pom.xml`에서 관리해요.
- `@Slf4j` 컴파일과 테스트 로그 출력을 위해 SLF4J API와 Simple 구현체를 test scope로 추가했어요. 라이브러리 소비자에게 로깅 구현체를 강제하지 않아요.

## StringUtil 구현 가정

아래는 최초 명세에 없어서 구현 시 선택한 동작이에요. 경계값 제안에 대한 별도 사용자 확정 응답은 없었어요. 현재 공개 동작의 전체 목록은 [README.md](README.md#문자열-유틸리티)를 기준으로 해요.

- null은 조기 반환해요. `slice`는 음수 인덱스를 끝 기준으로 변환한 뒤 `0..length`로 제한하며, 변환한 end가 begin보다 작으면 빈 문자열을 반환해요.
- `head`의 음수 size는 끝 기준 end 인덱스로, `tail`의 음수 size는 절댓값만큼 앞에서 제외하는 begin 인덱스로 해석해요. 모든 size는 유효 범위로 제한해요.
- `pad`/`pad2`는 가운데 정렬하며 홀수 여백의 한 칸은 오른쪽에 둬요. 길이가 이미 충분하면 원문을 자르지 않아요. 패턴은 양쪽에서 각각 처음부터 반복해요.
- Supplier는 순서대로 필요한 후보만 한 번씩 평가하고 null Supplier는 null 값으로 취급해요. 내부 예외는 기본값으로 숨기지 않고 전달해요.
- `split`은 마지막 빈 필드를 유지해 데이터 손실을 피하고 불변 리스트로 반환해요. 잘못된 정규식은 예외로 전달해요.
- `trimLeadingZero`는 부호 있는 정수·소수까지 지원해요. 입력의 부호와 소수부는 유지하고 정수부가 모두 0이면 하나를 남겨요.
- 비교 가능한 구현을 함께 실행할 수 있도록 첫 유효값 탐색과 `join`에 for-loop·stream 구현을 두고 `ThreadLocalRandom`으로 선택해요. Supplier stream 구현은 인덱스와 값을 결과 요소로 묶어 첫 유효값 또는 마지막 값을 선택하므로 `peek` 부수효과에 의존하지 않으며, loop와 같은 평가 순서로 첫 유효값에서 중단해요.
- `trimLeadingZero`도 loop·정규식 치환 구현을 무작위로 선택해요. 두 구현 전의 숫자 형식 검증은 공통으로 수행해요.
- single-byte pad는 UTF-8 바이트 길이가 1이고 Java 문자열 길이도 1인 값으로 판정해요. 조건에 맞지 않으면 패딩하지 않고 원문을 반환해요.
- 함수 배열형 `pipe`는 입력값을 reduce identity로 사용하는 구현과 `Arrays.stream(fns).reduce(Function.identity(), Function::andThen)` 합성 구현을 두고 `ThreadLocalRandom`으로 선택해요. 입력값 기반 3인자 `reduce`의 combiner는 병렬 수행에서만 사용되며 현재 순차 stream에서는 호출되지 않아요. 두 구현은 null 함수를 건너뛰고 같은 순서로 적용해요. fluent `Pipeline`은 `then`마다 새 인스턴스를 반환해 기존 pipeline을 보존하며 함수 내부 예외는 전달해요.

## stringify 후속 요구사항 반영

- 사용자가 표현한 `YYYY-MM-DD`는 달력 날짜로 해석하여 Java 패턴 `yyyy-MM-dd`를 사용해요. 대문자 `Y`/`D`의 주 기준 연도·연중 일수 의미를 피하고 연말·연초 테스트로 확인해요.
- 시간대는 별도 지정이 없어 시스템 기본 시간대를 사용해요. `SimpleDateFormat`은 호출마다 생성해 공유 가변 상태를 피하고 `java.sql.Date`도 지원해요.
- BigDecimal은 사용자 요구대로 `toPlainString()`을 사용해 지수 표기를 없애고 소수점 뒤 0을 유지해요. `join`도 기존 `stringify` 호출을 통해 같은 변환을 적용받아요.

## CollectionUtil

- 동일 시그니처의 합집합·대칭차 `unionOf`를 함께 선언할 수 없어 대칭차를 영어 용어 symmetric difference에 맞춘 `symmetricDifferenceOf`로 구분했어요. 이 이름은 구현 가정이며 별도 확정 응답은 없었어요.
- CollectionUtil 구현 당시에는 추가 라이브러리 대신 `s.util.Pair<T,U>` record를 사용했어요. 이후 튜플 타입 요구사항에 따라 이를 제거하고 `s.type.tuple.Pair<T,U>` 불변 클래스로 대체했어요.
- 타입 소거 때문에 `toArray(list)`만으로 빈 리스트의 배열 타입을 알 수 없어요. `Object[]`를 `T[]`로 가장하는 대신 첫 non-null 요소의 클래스로 추론하고, 추론 불가 시 예외를 발생시켜요. 안전하게 타입을 지정하는 Class 오버로드도 제공해요. 혼합 하위 타입은 Class 지정이 필요할 수 있어요.
- 집합 연산은 Set 반환이나 완전 중복 제거 대신 요청한 리스트 수식을 따라 순서·중복을 유지해요. 포함 여부는 HashSet으로 판별해요.
- 컬렉션 반환값은 null 요소와 null 키·값을 허용하면서 불변이어야 하므로 `List.copyOf`·`Map.copyOf` 대신 방어적 복사 후 불변 래퍼를 사용해요. `grouping`은 내부 리스트까지 불변으로 만들어요.
- `slice`는 정규화한 범위의 `subList()`를 방어적으로 복사해 원본과 분리해요. 음수 인덱스와 `head`·`tail`의 음수 size는 StringUtil과 같은 규칙을 적용해요.
- 기존 반복문이 있는 `zip`, `findOne`, `findAll`, `indexing`, `grouping`, `asMap`은 loop·stream 구현을 두고 `ThreadLocalRandom`으로 선택해요. stream은 순차 실행해 loop와 같은 순서와 단락 평가를 유지해요.
- Map 결과는 `LinkedHashMap` 기반 복사로 순서를 유지하며 중복 키는 마지막 값으로 정했어요. `castKeyValue`는 Class 정보가 없는 unchecked cast 후 불변 복사하며 검증·변환은 하지 않아요. 런타임 검증이 필요하면 Class를 받는 `asMap`을 사용해요.
- null 처리, 짧은 길이 zip, 중복 키, 홀수 맵 인자 예외 등 미지정 경계값은 README에 명시한 구현 가정이에요.

## Tuple

- `Pair`, `Triplet`, `Quartet`은 데이터 타입이므로 public 동작을 static `of(...)`를 제외한 instance 메서드로 제공해요. 생성자를 private으로 두어 생성 경로를 factory로 통일해요.
- 각 순서 값은 private final field에 보관해 불변으로 만들고 Lombok `@EqualsAndHashCode`로 모든 field 기반 동등성을 구현해요.
- Jackson이 `ord1`~`ord4`를 속성으로 처리하도록 접근자에 `@JsonProperty`를 붙이고, 같은 속성으로 복원하도록 factory에 `@JsonCreator`를 붙였어요.
- 사용자가 반환 타입 설명에 적은 `Quartlet`은 요청한 클래스 이름과 일반적인 용어에 맞춰 `Quartet`으로 구현했어요.
