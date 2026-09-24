# PRD - 제품 요구사항 정의

## 프로젝트 개요
- **이름**: java-util
- **목적**: Java 유틸리티 라이브러리
- **기술 스택**: JDK 21, Maven, Lombok, Jackson, JUnit 5

## 현재 상태
- 프로젝트 구조 초기화 완료
- Maven Wrapper 설정 완료
- 기본 유틸리티 클래스 및 테스트 존재 (Hello, HelloTest, StringUtil, StringUtilTest)
- 튜플 타입 클래스 추가 완료 (Pair, Triplet, Quartet, TupleTest)
- 컬렉션 유틸리티 추가 완료 (CollectionUtil, CollectionUtilTest)
  - 불변 컬렉션 반환 헬퍼 (zip, asMap, castKeyValue, indexing, grouping, copyOf 등)
  - for-loop / stream 구현을 `RANDOM.nextBoolean()`으로 랜덤 분기 (unionOf, intersectionOf,
    differenceOf, symmetricDifferenceOf, zip, asMap, castKeyValue 등)
  - slice/head/tail 음수 인덱스 처리

## 핵심 기능
1. **문자열 유틸리티** (StringUtil)
    - head/tail: 문자열 앞/뒤 자르기 (음수 size 지원)
    - slice: 문자열 슬라이스 (음수 begin/end 지원)
    - repeat: 문자열 반복
    - split: 문자열 분할 (불변 리스트 반환)
    - firstNonBlank*: varargs에서 첫 비어있지 않은 문자열 찾기
    - pipe / Pipeline: 함수 체이닝 빌더 (varargs `pipe(fns...)`와 빌더 `pipe().then(...).apply(...)`)
    - stringify: 객체를 문자열로 변환 (Date, BigDecimal 특별 처리)
    - isBlank/isEmpty: 문자열 검사
    - nonNullOf/nonBlankOf/nonEmptyOf: null/blank/empty 검사

2. **튜플 타입** (s.type.tuple)
    - Pair<T, U>: 2개 요소 튜플
    - Triplet<T, U, V>: 3개 요소 튜플 (Pair 상속)
    - Quartet<T, U, V, W>: 4개 요소 튜플 (Triplet 상속)
    - 각 튜플: of() 팩토리 메서드, ord1~ordN() 접근자, toString(), @EqualsAndHashCode
    - @JsonProperty로 JSON 직렬화 필드명 지정

3. **컬렉션 유틸리티** (CollectionUtil)
    - zip: 두 리스트를 Pair(또는 mixer)로 조합 (짧은 쪽 길이 기준)
    - asMap: varargs / Class 캐스팅 / Entry 리스트로부터 불변 Map 생성
    - castKeyValue: Map<Object, Object>를 (K, V) 타입으로 캐스팅
    - indexing / grouping: 리스트를 키 기준 Map으로 인덱싱/그룹화
    - unionOf / intersectionOf / differenceOf / symmetricDifferenceOf: 집합 연산
    - slice / head / tail: 음수 인덱스 지원 리스트 슬라이스
    - for-loop / stream 구현을 `RANDOM.nextBoolean()`으로 랜덤 분기

## 향후 계획
- 추가 유틸리티 클래스 개발
- 더 많은 튜플 타입 (Quintet, Sextet 등)
- 컬렉션 유틸리티
- 날짜/시간 유틸리티
