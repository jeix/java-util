# PRD - java-util

## 목표

자바 유틸리티 클래스를 작성하고, 빌드와 테스트를 즉시 실행할 수 있는 최소한의 프로젝트 기반을 제공합니다.

## 범위

- JDK 21 기준의 단일 모듈 Maven 프로젝트
- 빌드 도구가 설치되어 있지 않은 환경에서도 동작하는 Maven Wrapper
- Lombok(코드 생성), Jackson(JSON), JUnit 5(테스트) 의존성
- 구조 확인용 샘플 클래스 `s.Hello`와 테스트 `s.HelloTest`

## 요구사항

| ID | 요구사항 |
| --- | --- |
| R1 | `./mvnw test`로 컴파일과 테스트가 실행된다. |
| R2 | `./mvnw package`로 실행 가능한 jar 산출물을 만들 수 있다. |
| R3 | Lombok 애노테이션이 컴파일 시점에 처리된다. |
| R4 | Jackson으로 JSON 직렬화/역직렬화가 가능하다. |
| R5 | 테스트는 JUnit 5로 작성하고 surefire로 실행된다. |
| R6 | 이 브랜치와 관련 없는 파일(`target/`, `.env*` 등)은 커밋 대상에서 제외된다. |
| R7 | `s.util.StringUtil`에서 `null`/공백 판별, 기본값 선택, 공백이 아닌 첫 값 선택, 문자열 변환·자르기·가공·패딩·목록 변환 기능을 제공한다. |
| R8 | `StringUtil`의 모든 public 메서드를 `s.util.StringUtilTest`에서 검증하고, 테스트 클래스는 Lombok `@Slf4j`를 사용한다. |
| R9 | `slice`, `head`, `tail`은 음수 인덱스를 역방향 인덱스로 해석하고, `repeat`은 `String` 단위를 결과 길이까지 반복한다. |
| R10 | 단순 분기는 3항 연산자를 사용하고, `firstNonBlankOrLast` 계열은 반복문 구현과 스트림 구현을 두고 실행 시 랜덤하게 분기한다. |
| R11 | `s.type.tuple`에 값 2·3·4개를 묶는 `Pair`, `Triplet`, `Quartet` 데이터 타입을 제공한다. 값은 `of` 스태틱 팩터리로 만들고 `ord1()`~`ordN()` 인스턴스 메서드로 읽으며, `toString()`은 `(값1, 값2, ...)` 형식이다. |
| R12 | 튜플 타입은 Lombok `@EqualsAndHashCode`로 값 비교를 하고, `ord1()`~`ordN()` 메서드에 Jackson `@JsonProperty`를 붙여 JSON 키명을 `ord1`~`ordN`으로 지정하며, 이를 `TupleTest`에서 검증한다. |
| R13 | `s.util.CollectionUtil`에서 리스트/맵의 `null` 처리, `zip`, 항목 검색, 집합 연산, 부분 리스트, 색인·분류, 맵 생성·복사를 제공하고 `CollectionUtilTest`에서 검증한다. |
| R14 | `CollectionUtil`의 `zip(list1, list2, mixer)`, `findOne`, `indexing`, `grouping`, `asMap(items)`, `asMap(entries)`는 반복문 구현과 스트림 구현을 두고 실행 시 랜덤하게 분기하며, 두 구현은 같은 결과를 낸다. |
| R15 | `StringUtil.pipe(fns)`는 함수들을 순서대로 적용하는 `Function<String, String>`을 만들고, `StringUtil.pipe()`는 `then`으로 함수를 이어 붙이는 `Pipeline`을 만든다. 두 방식 모두 `null` 함수를 건너뛰고 빈 파이프라인은 입력을 그대로 반환하며, `pipe(fns)`는 함수 합성 구현과 입력값 축소 구현을 두고 실행 시 랜덤하게 분기한다(두 구현은 같은 결과). |

## 범위 밖

- 이후 추가되는 유틸리티 클래스의 기능 목록(추가될 때마다 `PLAN.md`에 기록)
- 배포, CI 파이프라인, 멀티 모듈 분리
