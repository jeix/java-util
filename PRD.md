# PRD (Project Requirements Document)

## 1. 프로젝트 개요

### 1.1 목적
Java 유틸리티 클래스 라이브러리를 초기화하여, 공통 유틸리티 기능을 제공하고 향후 기능 확장을 위한 기반을 마련한다.

### 1.2 프로젝트명
java-util

### 1.3 패키지 구조
- 기본 패키지: `s` (소문자 단일 단어)
- 메인 소스: `src/main/java/s/`
- 테스트 소스: `src/test/java/s/`

## 2. 기술 스택

### 2.1 언어 및 런타임
| 항목 | 버전 | 비고 |
|------|------|------|
| Java | 21 | JDK 설치 필요 |

### 2.2 빌드 도구
| 항목 | 버전 | 비고 |
|------|------|------|
| Maven | 3.9.9 (via wrapper) | 시스템 Maven 의존성 없음 |

### 2.3 의존성
| 라이브러리 | 버전 | 스코프 | 용도 |
|-----------|------|--------|------|
| Lombok | 1.18.34 | provided | 보일러플레이트 감소 |
| Jackson Databind | 2.17.2 | compile | JSON 처리 |
| JUnit Jupiter | 5.10.3 | test | 단위 테스트 |
| Maven Surefire | 3.2.5 | - | 테스트 실행 |

## 3. 요구사항

### 3.1 기능 요구사항 (F)
| ID | 설명 | 우선순위 | 비고 |
|----|------|---------|------|
| F-001 | 유틸리티 클래스를 통해 문자열 인사 기능 제공 | High | `Hello.greet(String)` - 이름 인사, null/공백 처리 |
| F-002 | Jackson을 사용한 객체-JSON 변환 기능 제공 | High | `Hello.toJson(String key, String value)` |
| F-003 | 팰린드롬 판별 기능 제공 | Medium | `Hello.isPalindrome(String)` - null 처리 |
| F-004 | 각 유틸리티 메서드에 대한 단위 테스트 작성 | High | JUnit 5 Jupiter 기반 |

### 3.2 비기능 요구사항 (NF)
| ID | 설명 | 우선순위 | 비고 |
|----|------|---------|------|
| NF-001 | Maven Wrapper를 통해 빌드 환경 의존성 제거 | High | `.mvnw`로 실행 가능 |
| NF-002 | Git에 관련 없는 파일(다른 브랜치 산물) 제외 | High | `.gitignore`로 관리 |
| NF-003 | 모든 테스트 통과 시 CI/빌드 성공 | High | `./mvnw clean test` |
| NF-004 | 코드 스타일: Lombok `@UtilityClass` 사용 | Medium | 유틸리티 클래스 패턴 |
| NF-005 | 의존성 스코프 명시 (provided/compile/test) | High | 유지보수 용이 |

### 3.3 제약사항 (Constraint)
| ID | 설명 | 비고 |
|----|------|------|
| C-001 | 시스템에 Maven이 설치되어 있지 않음 | Maven Wrapper 사용 |
| C-002 | Git 브랜치 간 파일 격리 필요 | `.gitignore` 적용 |
| C-003 | 다른 브랜치 참조 금지 | 독자적 진행 |

## 4. 초기화 완료 기준

### 4.1 핵심 검증
- [x] `./mvnw clean test` 실행 시 **BUILD SUCCESS**
- [x] `s.HelloTest`의 모든 테스트 통과 (7개)
- [x] Lombok 어노테이션 처리 정상 동작
- [x] Jackson JSON 변환 정상 동작
- [x] `pom.xml`에 Lombok(provided), Jackson(compile), JUnit(test) 의존성 포함

### 4.2 Git 상태
- [x] `.gitignore`로 `.agents/`, `.codex/`, `.env-claude`, `target/` 제외
- [x] `opencode-kilo` 브랜치에서 초기화 커밋 완료

## 5. 향후 확장 가능 영역
- `s.util.*` 패키지: 문자열, 컬렉션 등 일반 유틸리티
- `s.type.*` 패키지: 튜플 및 커스텀 데이터 타입
- `s.io.*` 패키지: 파일/스트림 처리 유틸리티
