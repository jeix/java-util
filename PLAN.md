# PLAN - 실행 계획

## 완료된 작업
- [x] 프로젝트 구조 초기화 (Maven Wrapper, pom.xml, .gitignore)
- [x] s.Hello 유틸리티 클래스 및 s.HelloTest 생성
- [x] StringUtil 리팩토링 (stream, 3항 연산자, switch 표현식, head/tail 음수 처리)
- [x] 튜플 타입 클래스 생성 (Pair, Triplet, Quartet)
- [x] TupleTest 생성 (16개 테스트 케이스)
- [x] 컬렉션 유틸리티 (CollectionUtil) 생성 및 불변 컬렉션 반환, slice/head/tail 음수 인덱스 처리
- [x] CollectionUtil: unionOf/intersectionOf/differenceOf/symmetricDifferenceOf 에 Stream 랜덤 분기 적용
- [x] CollectionUtil: zip / asMap / castKeyValue 에 Stream 랜덤 분기 적용
- [x] 전체 빌드 및 테스트 통과 (179개 테스트, 0 실패)
- [x] AGENT.md, PRD.md 생성

## 진행 중 작업
- [x] PLAN.md 작성 중
- [x] DECISION.md 작성

## 완료된 작업 (추가)
- [x] 날짜/시간 유틸리티 (DateTimeUtil) 생성 및 75개 테스트 케이스 통과
  - today/date/date2/time/time2/datetime/datetime2: 현재 날짜/시간 조회
  - stringify/stringify2(LocalDate/LocalTime/LocalDateTime): 포맷팅
  - localDateOf/localTimeOf/localDateTimeOf: 문자열 파싱 (포맷 자동 인식)
  - toTimestamp(LocalDate/LocalDateTime): Timestamp 변환
  - eval(date1, op, date2): 날짜 비교 (6개 연산자)
  - eval(date, op, n, unit): 날짜 연산 (+/-, D/W/M/Y)
  - eom(date): 월말 계산
- [x] 전체 빌드 및 테스트 통과 (254개 테스트, 0 실패)

## 향후 계획
1. **추가 튜플 타입**
   - Quintet<T,U,V,W,X>
   - Sextet<T,U,V,W,X,Y>

2. **문서화**
   - 각 모듈별 문서화
   - 사용 예제 추가
