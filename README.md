# java-util

Java 언리틸리티 프로젝트입니다. JDK 21 + Maven을 기반으로 하며, Lombok과 Jackson을 사용합니다.

## 시작하기

### 빌드 및 테스트

```bash
# Maven wrapper로 테스트 실행 (Maven 설치 불필요)
./mvnw clean test

# JAR 빌드
./mvnw clean package
```

### 프로젝트 구조

```
java-util/
├── .gitignore
├── .mvn/wrapper/
│   ├── maven-wrapper.jar
│   └── maven-wrapper.properties
├── mvnw                    # Unix용 Maven wrapper
├── mvnw.cmd                # Windows용 Maven wrapper
├── pom.xml                 # Maven 빌드 설정
├── AGENTS.md               # 작업 가이드라인
├── PRD.md                  # 프로젝트 요구사항 정의
├── PLAN.md                 # 실행 계획
├── DECISION.md             # 설계 결정 기록
└── src/
    ├── main/java/
    │   ├── s/Hello.java         # 유틸리티 클래스
    │   ├── s/util/
    │   │   ├── StringUtil.java   # 문자열 유틸리티 (25개 메서드)
    │   │   └── CollectionUtil.java # 컬렉션 유틸리티 (List/Map)
    │   └── s/type/tuple/
    │       ├── Pair.java         # 2-튜플
    │       ├── Triplet.java      # 3-튜플
    │       └── Quartet.java      # 4-튜플
    └── test/java/
        ├── s/HelloTest.java
        ├── s/util/StringUtilTest.java
        ├── s/util/CollectionUtilTest.java
        └── s/type/tuple/TupleTest.java
```

## 모듈

| 패키지 | 설명 |
|--------|------|
| `s.Hello` | 기본 유틸리티 (greet, toJson, isPalindrome) |
| `s.util.StringUtil` | 문자열 처리 (isBlank, isEmpty, slice, trimLeadingZero, repeat, reverse, lpad/rpad, join, split, pipe, Pipeline) |
| `s.util.CollectionUtil` | 컬렉션 처리 (zip, set operations, slice, asMap, castKeyValue) |
| `s.type.tuple` | 튜플 타입 (Pair, Triplet, Quartet) - Jackson 직렬화 지원 |

## 테스트

```bash
# 전체 테스트 (163개)
./mvnw clean test
```

## 의존성

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Lombok | 1.18.34 | 보일러플레이트 코드 제거 |
| Jackson Databind | 2.17.2 | JSON 처리 |
| JUnit Jupiter | 5.10.3 | 테스트 프레임워크 |

## 요구사항

- JDK 21
- 인터넷 연결 (의존성 다운로드용)
