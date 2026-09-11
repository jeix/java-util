# DECISION - 주요 결정 사항 및 근거

## 1. 빌드 도구: Maven + Wrapper
**결정**: Maven Wrapper(mvnw) 사용, 별도 Maven 설치 불필요
**근거**: 
- 실행 환경 일관성 보장 (CI/CD, 팀원 간 버전 차이 방지)
- JDK 21만 있으면 즉시 빌드 가능
- 프로젝트 이식성 최대화

## 2. Java 버전: 21 (LTS)
**결정**: Java 21 컴파일 타겟 및 소스 레벨 설정
**근거**: 
- 현재 LTS 버전으로 장기 지원 보장
- 가상 스레드, 패턴 매칭 등 최신 기능 활용 가능
- 기업 환경 표준 버전

## 3. Lombok 적용 범위: provided 스코프
**결정**: `<scope>provided</scope>`로 설정, 컴파일러 플러그인에 annotationProcessorPaths 명시
**근거**: 
- 런타임 의존성에서 제외 (jar 크기 최소화)
- 컴파일 시 어노테이션 프로세서로만 동작
- IDE 지원 시 별도 플러그인 필요하지만 빌드는 독립적

## 4. Jackson 의존성: databind + annotations 분리
**결정**: jackson-databind, jackson-annotations 별도 선언 (jackson-core는 전이적 의존)
**근거**: 
- annotations만 필요한 모듈은 databind 의존 없이 사용 가능
- 버전 관리를 properties로 일원화
- 최소 의존성 원칙 준수

## 5. 테스트 프레임워크: JUnit 5 (Jupiter)
**결정**: JUnit 4가 아닌 JUnit 5 사용, surefire-plugin 3.2.5 적용
**근거**: 
- JUnit 5는 모듈화된 아키텍처 (Platform, Jupiter, Vintage)
- 확장 모델, 파라미터화 테스트, 동적 테스트 등 현대적 기능
- Maven Surefire 3.x는 JUnit Platform 네이티브 지원

## 6. 패키지 구조: 단일 패키지 `s` + 기능별 서브패키지
**결정**: 초기에는 단일 패키지 `s` 사용, 유틸리티는 `s.util` 서브패키지로 분리, 튜플은 `s.type.tuple` 서브패키지로 분리
**근거**: 
- 초기화 단계에서는 최소 구조로 시작
- 기능별 분리 시 서브패키지로 자연스럽게 확장 가능
- `s.util`은 유틸리티 클래스들의 네임스페이스 역할
- `s.type.tuple`은 데이터 타입(튜플)들의 네임스페이스 역할

## 7. 인코딩: UTF-8 강제
**결정**: `project.build.sourceEncoding=UTF-8`, 컴파일러 인코딩 명시
**근거**: 
- 한글 주석/문자열 깨짐 방지
- 크로스 플랫폼 빌드 일관성
- Maven 기본값이 플랫폼 종속적일 수 있음

## 8. 플러그인 버전 고정
**결정**: maven-compiler-plugin 3.13.0, maven-surefire-plugin 3.2.5 명시
**근거**: 
- 재현 가능한 빌드 보장
- 슈퍼 POM 기본값 변경에 영향 받지 않음
- 보안 취약점 대응 시 버전 업그레이드 용이

## 9. SLF4J 테스트 의존성 추가
**결정**: slf4j-api + slf4j-simple을 test 스코프로 추가
**근거**: 
- StringUtilTest, TupleTest, CollectionUtilTest에서 @Slf4j 사용 위해 필요
- 테스트 실행 시 콘솔 로그 출력용 simple 바인딩
- 운영 코드에는 로깅 구현체 미포함 (호출자 결정)

## 10. StringUtil 메서드 설계 원칙
**결정**: 
- 파라미터 검증 후 조기 리턴 (guard clause 패턴)
- 오버로드 메서드는 핵심 구현 메서드 위임
- 프라이빗 헬퍼 메서드는 `_` 접두사 사용
- null 안전성 보장 (null 입력 시 null 또는 빈 문자열 반환)
**근거**: 
- 가독성과 유지보수성 향상
- 중복 코드 제거
- 내부 구현 은닉
- NullPointerException 방지

## 11. StringUtil 주요 메서드 그룹화
**결정**: 관련 기능을 그룹화하여 일관된 네이밍 적용
- `isBlank`/`isEmpty`: 검사 메서드
- `nonNullOf`/`nonBlankOf`/`nonEmptyOf`: 기본값 제공 메서드 (값/Supplier)
- `firstNonBlankOrLast`/`OrEmpty`/`OrNull`: 우선순위 기반 선택 (가변인자/Supplier 2~5개)
- `slice`/`head`/`tail`: 부분 문자열 추출
- `lpad`/`rpad`/`pad` (String pad, 길이 1): 패딩
- `join`/`split`: 리스트-문자열 변환
**근거**: 
- 일관된 API 설계로 학습 비용 감소
- 기능별 명확한 분리
- 확장 시 규칙 따름

## 12. 튜플 클래스 설계 원칙
**결정**: 
- 데이터 타입이므로 인스턴스 메서드 사용 (유틸리티와 구분)
- `@EqualsAndHashCode`로 값 기반 동등성 보장 (Lombok)
- private 생성자 + static factory method `of()` 패턴
- 불변 객체: 모든 필드 `final`, setter 없음
- JSON 직렬화용 getter에 `@JsonProperty("1")`~`("4")` 적용
- getter 네이밍: `ord1()`~`ord4()` (순서 기반, JSON 필드명과 일치)
- `toString()`: `(t, u, v, w)` 튜플 표기법
**근거**: 
- 데이터 클래스로서의 명확한 역할 분리
- 불변성으로 스레드 안전성 보장
- Jackson 직렬화 시 필드 순서 보장 (숫자 키)
- 튜플 표기법으로 가독성 확보

## 13. 튜플 타입 계층: Pair → Triplet → Quartet
**결정**: 2/3/4개 요소별 별도 클래스 제공, 공통 인터페이스 미추출
**근거**: 
- 제네릭 타입 파라미터 수가 다르므로 공통 인터페이스 불가
- 각 크기별 최적화된 구현 가능
- 불필요한 추상화 회피 (YAGNI)
- 필요시 Quintet 등 추가 용이

## 14. CollectionUtil 메서드 설계 원칙
**결정**: 
- StringUtil과 동일한 guard clause 패턴 적용
- null 안전성: null 입력 시 빈 컬렉션/맵 또는 null 반환
- 불변성 보장: 원본 컬렉션 변경하지 않고 새 컬렉션 반환
- 제네릭 타입 안전성: `@SuppressWarnings("unchecked")` 최소화, toArray 오버로드로 타입 안전성 확보
- 스트림 API 활용: findAll, grouping에서 Stream API 사용
**근거**: 
- StringUtil과 일관된 설계로 학습 비용 감소
- 부작용 없는 순수 함수형 스타일
- Java 타입 시스템 제약 내 타입 안전성 최대화

## 15. CollectionUtil 주요 메서드 그룹화
**결정**: 관련 기능을 그룹화하여 일관된 네이밍 적용
- `isEmpty`/`emptyIfNull` (Collection/Map 오버로드): 빈 체크/기본값
- `zip` (Pair/BiFunction): 리스트 결합
- `toArray` (타입 안전한 배열 변환): 제네릭 배열 생성
- `findOne`/`findAll` (Predicate): 필터링/검색
- `unionOf`/`intersectionOf`/`differenceOf`/`symmetricDifferenceOf`: 집합 연산
- `slice`/`head`/`tail` (begin/end/size): 부분 리스트 추출
- `indexing`/`grouping` (Function→Map/Map<List>): 색인화/분류
- `asMap` (가변인자/클래스/Entry리스트)/`castKeyValue`/`copyOf`: 맵 생성/변환
**근거**: 
- 기능별 명확한 분리 및 네이밍 일관성
- Java Collections Framework 관례 준수
- Map.Entry, BiFunction 등 표준 함수형 인터페이스 활용

## 16. toArray 메서드 타입 안전성 처리
**결정**: `list.toArray(new T[0])` 패턴 대신 `list.toArray(new String[0])` 형태의 오버로드 제공
**근거**: 
- Java 제네릭 타입 소거로 인해 런타임에 타입 정보 손실
- `toArray(new Object[0])` 후 캐스팅 시 ClassCastException 발생 가능
- 호출자가 타입을 명시하는 오버로드(`toArray(list, array)`)로 타입 안전성 확보
- 기존 `toArray()`는 Object[] 반환하며 필요 시 캐스팅

## 17. StringUtil 리팩토링 (23~24단계)
**결정**: 
- 스트림/루프 구현 병행, `useStream()`으로 런타임 랜덤 분기
- `stringify`: switch expression 적용
- `head`/`tail`: 음수 size 지원 (head: 역방향 인덱스, tail: 앞에서 제외)
- `trimLeadingZero`: 정규식/루프 구현 병행, `useRegex()`로 랜덤 분기
- `repeat(char)` 제거, `repeat(String)`만 유지, 스트림/루프 랜덤 분기
- `lpad`/`rpad`/`pad`: pad 파라미터 `String` 변경, 길이 1 검증(`validatePad`), 스트림/루프 랜덤 분기
- `lpad2`/`rpad2`/`pad2` 추가 (멀티바이트 pad 지원)
- `split`: 불변 리스트(`List.of`) 반환
- `isBlank`, `repeat`, `join`, `lpad`/`rpad`/`pad`: 스트림/루프 랜덤 분기
**근거**: 
- 함수형/명령형 스타일 비교 가능, 테스트 커버리지 향상
- switch expression으로 가독성 향상
- 음수 인덱스 지원으로 Pythonic 슬라이싱 제공
- 정규식으로 성능/가독성 선택 가능
- char repeat는 String repeat로 대체 가능 (중복 제거)
- pad 타입 통일로 API 일관성 확보
- lpad2/rpad2/pad2로 멀티바이트 문자 패딩 지원
- 불변 리스트 반환으로 방어적 프로그래밍

## 18. CollectionUtil 리팩토링 (25~26단계)
**결정**: 
- 모든 반환 리스트/맵을 불변으로 변경 (`Collections.unmodifiableList/Map`)
- 스트림/루프 구현 병행, `useStream()`으로 런타임 랜덤 분기
- `head`/`tail`: 음수 size 지원 (StringUtil과 동일: head 역방향, tail 앞에서 제외)
- 스트림 구현에서 `HashMap::new` 등 팩토리 사용 후 `Collections.unmodifiableMap` 래핑
- `grouping`: 내부 리스트도 `Collections.unmodifiableList`로 불변화
- `indexing` 스트림 버전: `HashMap::new` 후 `unmodifiableMap` 래핑
- `asMap` 스트림 버전: `HashMap::new` 후 `unmodifiableMap` 래핑
- 모든 반환 컬렉션에 `UnsupportedOperationException` 검증 테스트 추가
**근거**: 
- 방어적 프로그래밍, 부작용 방지
- 함수형/명령형 스타일 비교 가능
- StringUtil과 일관된 음수 인덱스 시맨틱
- 스트림 컬렉터의 기본 맵이 불변일 수 있어 팩토리 명시 필요
- 불변성 테스트로 런타임 보장

## 19. split 메서드 불변 리스트 반환
**결정**: `split` 메서드가 `ArrayList` 대신 `List.of(parts)` 반환
**근거**: 
- 방어적 프로그래밍, 호출자가 결과 수정 불가
- Java 9+ `List.of`로 간결하게 불변 리스트 생성
- StringUtil/CollectionUtil 일관성

## 20. StringUtil 추가 리팩토링 (29단계)
**결정**: 
- private 메서드 `_` 접두사 완전 통일 (`useStream`→`_useStream`, `useRegex`→`_useRegex`, `trimLeadingZeroLoop`→`_trimLeadingZeroLoop` 등)
- `isBlank`: `s == null ? true : s.isBlank()` (3항 연산자, `isEmpty`와 일관성)
- `nonNullOf`/`nonBlankOf`/`nonEmptyOf` (Supplier 버전): supplier null 시 `dfltSupplier` 호출 (기존: null 반환)
- `firstNonBlankOrLastStream`: `java.util.stream.Stream` import, stream 구현 추가
- `_firstNonBlankOrLast`/`OrEmpty`/`OrNull`: `Supplier<String>[]` → `Supplier<String>...` varargs 변경
- `_firstNonBlankOrEmpty`/`_firstNonBlankOrNull`: 직접 구현 제거, `_firstNonBlankOrLast` 호출로 위임 (빈 문자열/null 반환용 supplier 추가)
- `_firstNonBlankOrLast`: stream 구현(`_firstNonBlankOrLastStream`) 추가, 랜덤 분기 (if/else로 들여쓰기 균형)
- `stringify`: null 체크를 switch 표현식 안으로 이동
- `slice(begin)`: 3-parameter 버전(`slice(s, begin, Integer.MAX_VALUE)`) 호출
- `head`/`tail`: `s.substring` 마지막에 한 번만 호출하도록 리팩토링 (start/end 계산 후 단일 호출)
- `lpad2`/`rpad2`/`pad2`: 멀티바이트 pad 지원 메서드 추가 (join 앞 배치), `_repeatPad2`가 정확한 길이로 패딩 생성
**근거**: 
- 코드 스타일 일관성 (`_` 접두사 통일)
- Java 11+ `String.isBlank()` 활용으로 가독성/성능 향상
- Supplier null 시 기본값 제공 로직 통일 (null 반환 대신 기본값 사용)
- Stream API import로 코드 간결화
- varargs로 호출 편의성 증대
- 중복 코드 제거 (Empty/Null이 Last 호출로 위임)
- 스트림/루프 구현 병행으로 테스트 커버리지 향상
- switch expression 내 null 처리로 가독성 향상
- 메서드 위임으로 DRY 원칙 준수
- substring 단일 호출로 성능/가독성 향상
- 멀티바이트 문자(한글, 이모지 등) 패딩 지원