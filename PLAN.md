# PLAN - 실행 계획 및 마일스톤

## 전체 진행 현황
- [x] 1단계: Maven 프로젝트 초기화 (pom.xml 작성)
- [x] 2단계: 디렉토리 구조 생성
- [x] 3단계: 메인 클래스 작성 (s.Hello)
- [x] 4단계: 테스트 클래스 작성 (s.HelloTest)
- [x] 5단계: Maven Wrapper 설정
- [x] 6단계: 빌드 및 테스트 검증 (Hello)
- [x] 7단계: 문서화 (AGENT, PRD, PLAN, DECISION)
- [x] 8단계: s.util 패키지 디렉토리 생성
- [x] 9단계: StringUtil 클래스 구현 (모든 메서드)
- [x] 10단계: StringUtilTest 클래스 구현 (@Slf4j, 테스트 메서드)
- [x] 11단계: 빌드 및 테스트 검증 (StringUtil)
- [x] 12단계: 문서 갱신 (1차)
- [x] 13단계: s.type.tuple 패키지 디렉토리 생성
- [x] 14단계: 튜플 클래스 구현 (Pair, Triplet, Quartet)
- [x] 15단계: TupleTest 클래스 구현 (@Slf4j, 테스트 메서드)
- [x] 16단계: @JsonProperty 추가로 JSON 직렬화 지원
- [x] 17단계: 빌드 및 테스트 검증 (Tuple)
- [x] 18단계: 문서 갱신 (2차)
- [x] 19단계: CollectionUtil 클래스 구현 (모든 메서드)
- [x] 20단계: CollectionUtilTest 클래스 구현 (@Slf4j, 테스트 메서드)
- [x] 21단계: 빌드 및 테스트 검증 (CollectionUtil)
- [x] 22단계: 문서 갱신 (3차)
- [x] 23단계: StringUtil 리팩토링 (스트림/랜덤 분기, switch, head/tail 음수, trimLeadingZero 정규식, repeat char 제거, pad String, split 불변 리스트)
- [x] 24단계: StringUtilTest 갱신 (char repeat 제거, pad String, head/tail 음수, split 불변 리스트 검증)
- [x] 25단계: CollectionUtil 리팩토링 (불변 리스트/맵 반환, 스트림/랜덤 분기, head/tail 음수)
- [x] 26단계: CollectionUtilTest 갱신 (불변성 검증, head/tail 음수, 불변 리스트/맵 검증)
- [x] 27단계: 빌드 및 테스트 검증 (전체 리팩토링 후)
- [x] 28단계: 문서 갱신 (4차)

## 단계별 상세

### 1단계: Maven 프로젝트 초기화 ✅
- **입력**: 빈 디렉토리 + README.md
- **출력**: pom.xml (의존성, 플러그인, Java 21 설정)
- **수정 파일**: pom.xml
- **완료 기준**: pom.xml 유효성 검사 통과

### 2단계: 디렉토리 구조 생성 ✅
- **입력**: pom.xml
- **출력**: src/main/java/s, src/test/java/s 디렉토리
- **수정 파일**: 없음 (디렉토리만 생성)
- **완료 기준**: 디렉토리 존재 확인

### 3단계: 메인 클래스 작성 ✅
- **입력**: 디렉토리 구조
- **출력**: src/main/java/s/Hello.java
- **수정 파일**: Hello.java
- **완료 기준**: Lombok 어노테이션 적용, greet() 메서드 구현

### 4단계: 테스트 클래스 작성 ✅
- **입력**: Hello 클래스
- **출력**: src/test/java/s/HelloTest.java
- **수정 파일**: HelloTest.java
- **완료 기준**: 3개 테스트 메서드 작성 (정상, 경계, Lombok 검증)

### 5단계: Maven Wrapper 설정 ✅
- **입력**: 프로젝트 루트
- **출력**: mvnw, mvnw.cmd, .mvn/wrapper/*
- **수정 파일**: 4개 파일
- **완료 기준**: ./mvnw --version 실행 가능

### 6단계: 빌드 및 테스트 검증 (Hello) ✅
- **입력**: 전체 소스 코드
- **출력**: target/classes, target/test-classes, 테스트 리포트
- **실행 명령**: ./mvnw compile test-compile test
- **완료 기준**: BUILD SUCCESS, Tests run: 3, Failures: 0

### 7단계: 문서화 ✅
- **입력**: 완료된 프로젝트
- **출력**: AGENT.md, PRD.md, PLAN.md, DECISION.md
- **수정 파일**: 4개 마크다운 파일
- **완료 기준**: 모든 문서 파일 생성됨

### 8단계: s.util 패키지 디렉토리 생성 ✅
- **입력**: 프로젝트 루트
- **출력**: src/main/java/s/util, src/test/java/s/util
- **수정 파일**: 없음 (디렉토리만 생성)
- **완료 기준**: 디렉토리 존재 확인

### 9단계: StringUtil 클래스 구현 ✅
- **입력**: 디렉토리 구조, PRD 기능 요구사항
- **출력**: src/main/java/s/util/StringUtil.java
- **수정 파일**: StringUtil.java
- **완료 기준**: 40여 개 메서드 구현 완료, 컴파일 성공

### 10단계: StringUtilTest 클래스 구현 ✅
- **입력**: StringUtil 클래스
- **출력**: src/test/java/s/util/StringUtilTest.java
- **수정 파일**: StringUtilTest.java
- **완료 기준**: @Slf4j 적용, 23개 테스트 메서드 작성

### 11단계: 빌드 및 테스트 검증 (StringUtil) ✅
- **입력**: 전체 소스 코드 (Hello + StringUtil)
- **출력**: target/classes, target/test-classes, 테스트 리포트
- **실행 명령**: ./mvnw clean test
- **완료 기준**: BUILD SUCCESS, Tests run: 26, Failures: 0

### 12단계: 문서 갱신 (1차) ✅
- **입력**: 완료된 프로젝트
- **출력**: AGENT.md, PRD.md, PLAN.md, DECISION.md 갱신
- **수정 파일**: 4개 마크다운 파일
- **완료 기준**: 모든 문서가 현재 상태 반영

### 13단계: s.type.tuple 패키지 디렉토리 생성 ✅
- **입력**: 프로젝트 루트
- **출력**: src/main/java/s/type/tuple, src/test/java/s/type/tuple
- **수정 파일**: 없음 (디렉토리만 생성)
- **완료 기준**: 디렉토리 존재 확인

### 14단계: 튜플 클래스 구현 ✅
- **입력**: 디렉토리 구조, PRD 기능 요구사항
- **출력**: Pair.java, Triplet.java, Quartet.java
- **수정 파일**: 3개 파일
- **완료 기준**: @EqualsAndHashCode, private 생성자, of(), ord1~4(), toString() 구현

### 15단계: TupleTest 클래스 구현 ✅
- **입력**: 튜플 클래스 3종
- **출력**: src/test/java/s/type/tuple/TupleTest.java
- **수정 파일**: TupleTest.java
- **완료 기준**: @Slf4j 적용, 8개 테스트 메서드 (생성/조회, equals/hashCode, null, 불변성)

### 16단계: @JsonProperty 추가로 JSON 직렬화 지원 ✅
- **입력**: 튜플 클래스 3종
- **출력**: getter에 @JsonProperty("1")~("4") 추가
- **수정 파일**: Pair.java, Triplet.java, Quartet.java
- **완료 기준**: Jackson 직렬화 시 {"1":...,"2":...} 형식 출력

### 17단계: 빌드 및 테스트 검증 (Tuple) ✅
- **입력**: 전체 소스 코드 (Hello + StringUtil + Tuple)
- **출력**: target/classes, target/test-classes, 테스트 리포트
- **실행 명령**: ./mvnw test
- **완료 기준**: BUILD SUCCESS, Tests run: 35, Failures: 0

### 18단계: 문서 갱신 (2차) ✅
- **입력**: 완료된 프로젝트
- **출력**: AGENT.md, PRD.md, PLAN.md, DECISION.md 갱신
- **수정 파일**: 4개 마크다운 파일
- **완료 기준**: 모든 문서가 현재 상태 반영

### 19단계: CollectionUtil 클래스 구현 ✅
- **입력**: 디렉토리 구조, PRD 기능 요구사항
- **출력**: src/main/java/s/util/CollectionUtil.java
- **수정 파일**: CollectionUtil.java
- **완료 기준**: 25개 메서드 구현 완료 (빈 체크, 결합, 필터링, 집합 연산, 슬라이싱, 색인화, 맵 변환), 컴파일 성공

### 20단계: CollectionUtilTest 클래스 구현 ✅
- **입력**: CollectionUtil 클래스
- **출력**: src/test/java/s/util/CollectionUtilTest.java
- **수정 파일**: CollectionUtilTest.java
- **완료 기준**: @Slf4j 적용, 21개 테스트 메서드 작성 (빈 체크, 결합, 필터링, 집합 연산, 슬라이싱, 색인화, 맵 변환)

### 21단계: 빌드 및 테스트 검증 (CollectionUtil) ✅
- **입력**: 전체 소스 코드 (Hello + StringUtil + Tuple + CollectionUtil)
- **출력**: target/classes, target/test-classes, 테스트 리포트
- **실행 명령**: ./mvnw test
- **완료 기준**: BUILD SUCCESS, Tests run: 56, Failures: 0

### 22단계: 문서 갱신 (3차) ✅
- **입력**: 완료된 프로젝트
- **출력**: AGENT.md, PRD.md, PLAN.md, DECISION.md 갱신
- **수정 파일**: 4개 마크다운 파일
- **완료 기준**: 모든 문서가 현재 상태 반영

### 23단계: StringUtil 리팩토링 ✅
- **입력**: 기존 StringUtil 클래스
- **출력**: 리팩토링된 StringUtil.java
- **수정 파일**: StringUtil.java
- **완료 기준**: 
  - 스트림/루프 구현 병행 및 랜덤 분기 (isBlank, repeat, join, lpad/rpad/pad)
  - stringify switch expression 적용
  - head/tail 음수 size 지원 (역방향/앞에서 제외)
  - trimLeadingZero 정규식/루프 랜덤 분기
  - repeat(char) 제거, repeat(String)만 유지
  - lpad/rpad/pad pad 파라미터 String 변경, 길이 1 검증
  - lpad2/rpad2/pad2 추가 (멀티바이트 pad 지원)
  - split 불변 리스트 반환
- **완료 기준**: 컴파일 성공, 기존 테스트 통과

### 24단계: StringUtilTest 갱신 ✅
- **입력**: 리팩토링된 StringUtil
- **출력**: 갱신된 StringUtilTest.java
- **수정 파일**: StringUtilTest.java
- **완료 기준**: 
  - char repeat 테스트 제거
  - pad String 타입 테스트 (길이 1 검증 예외 확인)
  - head/tail 음수 size 테스트 추가
  - split 불변 리스트 검증 추가
  - 20개 테스트 통과

### 25단계: CollectionUtil 리팩토링 ✅
- **입력**: 기존 CollectionUtil 클래스
- **출력**: 리팩토링된 CollectionUtil.java
- **수정 파일**: CollectionUtil.java
- **완료 기준**:
  - 모든 반환 리스트/맵을 불변으로 변경 (Collections.unmodifiableList/Map)
  - 스트림/루프 구현 병행 및 랜덤 분기 (useStream())
  - head/tail 음수 size 지원 (StringUtil과 동일)
  - 스트림 구현에서 HashMap::new 사용 후 unmodifiableMap 래핑
  - grouping 내부 리스트도 불변화
  - indexing 스트림 버전 HashMap::new 후 unmodifiableMap
- **완료 기준**: 컴파일 성공, 기존 테스트 통과

### 26단계: CollectionUtilTest 갱신 ✅
- **입력**: 리팩토링된 CollectionUtil
- **출력**: 갱신된 CollectionUtilTest.java
- **수정 파일**: CollectionUtilTest.java
- **완료 기준**:
  - 모든 반환 리스트/맵에 대해 UnsupportedOperationException 검증 추가
  - head/tail 음수 size 테스트 추가
  - grouping 내부 리스트 불변성 검증 추가
  - copyOf 테스트 수정 (불변 맵 검증)
  - 21개 테스트 통과

### 27단계: 빌드 및 테스트 검증 (전체 리팩토링 후) ✅
- **입력**: 전체 소스 코드
- **출력**: target/classes, target/test-classes, 테스트 리포트
- **실행 명령**: ./mvnw test (3회 연속 실행)
- **완료 기준**: BUILD SUCCESS, Tests run: 53, Failures: 0, 3회 연속 성공

### 28단계: 문서 갱신 (4차) ✅
- **입력**: 완료된 프로젝트
- **출력**: AGENT.md, PRD.md, PLAN.md, DECISION.md 갱신
- **수정 파일**: 4개 마크다운 파일
- **완료 기준**: 모든 문서가 현재 상태 반영

### 29단계: StringUtil 추가 리팩토링 ✅
- **입력**: 기존 StringUtil 클래스
- **출력**: 리팩토링된 StringUtil.java
- **수정 파일**: StringUtil.java
- **완료 기준**:
  - private 메서드 `_` 접두사 통일
  - isBlank: `s.isBlank()` + 3항 연산자 사용
  - nonNullOf/nonBlankOf/nonEmptyOf (Supplier): supplier null 시 dfltSupplier 사용
  - firstNonBlankOrLastStream: Stream import, stream 구현 추가
  - _firstNonBlankOrLast/Empty/Null: varargs 변경, Empty/Null은 Last 호출로 위임
  - _firstNonBlankOrLast: stream 구현 추가 및 랜덤 분기 (if/else 들여쓰기 균형)
  - stringify: null 체크를 switch 안으로
  - slice(2 params): 3-params 버전 호출
  - head/tail: substring 마지막에 한 번만 호출
  - lpad2/rpad2/pad2: 멀티바이트 pad 지원 메서드 추가 (join 앞)
  - 컴파일 성공, 테스트 통과

### 30단계: CollectionUtil 추가 리팩토링 ✅
- **입력**: 기존 CollectionUtil 클래스
- **출력**: 리팩토링된 CollectionUtil.java
- **수정 파일**: CollectionUtil.java, StringUtilTest.java
- **완료 기준**:
  - private 메서드 `_` 접두사 통일 (useStream → _useStream)
  - zip 메서드: if/else로 랜덤 분기 (들여쓰기 균형)
  - slice(2 params): 3-params 버전 호출 (list.size() 사용)
  - StringUtilTest: nonNullOf/nonBlankOf/nonEmptyOf supplier null 시 dfltSupplier 반환 검증
  - 컴파일 성공, 테스트 통과 (3회 연속)

## 다음 단계 (향후 확장 시)
- [ ] CI/CD 파이프라인 구성 (GitHub Actions 등)
- [ ] 코드 품질 도구 추가 (SpotBugs, Checkstyle, PMD)
- [ ] 추가 유틸리티 클래스 구현 (DateUtil 등)
- [ ] 통합 테스트 모듈 분리