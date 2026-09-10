# PLAN - 실행 계획 및 마일스톤

## 전체 진행 현황
- [x] 1단계: Maven 프로젝트 초기화 (pom.xml 작성)
- [x] 2단계: 디렉토리 구조 생성
- [x] 3단계: 메인 클래스 작성 (s.Hello)
- [x] 4단계: 테스트 클래스 작성 (s.HelloTest)
- [x] 5단계: Maven Wrapper 설정
- [x] 6단계: 빌드 및 테스트 검증
- [x] 7단계: 문서화 (AGENT, PRD, PLAN, DECISION)

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

### 6단계: 빌드 및 테스트 검증 ✅
- **입력**: 전체 소스 코드
- **출력**: target/classes, target/test-classes, 테스트 리포트
- **실행 명령**: ./mvnw compile test-compile test
- **완료 기준**: BUILD SUCCESS, Tests run: 3, Failures: 0

### 7단계: 문서화 ✅
- **입력**: 완료된 프로젝트
- **출력**: AGENT.md, PRD.md, PLAN.md, DECISION.md
- **수정 파일**: 4개 마크다운 파일
- **완료 기준**: 모든 문서 파일 생성됨

## 다음 단계 (향후 확장 시)
- [ ] CI/CD 파이프라인 구성 (GitHub Actions 등)
- [ ] 코드 품질 도구 추가 (SpotBugs, Checkstyle, PMD)
- [ ] 추가 유틸리티 클래스 구현
- [ ] 통합 테스트 모듈 분리