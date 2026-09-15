# java-util

간단한 자바 유틸리티 클래스를 개발하고 테스트하기 위한 프로젝트입니다.
Maven Wrapper를 포함하고 있어 별도의 빌드 도구 설치 없이 빌드·테스트할 수 있습니다.

## 요구 사항

- JDK 21
- 빌드 도구: 별도 설치 불필요 (Maven Wrapper `mvnw` 포함)

## 빌드 및 테스트

```bash
./mvnw test        # 컴파일 + 테스트 실행
./mvnw package     # 테스트 통과 후 jar 패키징
./mvnw -q test     # 조용히 실행
```

Windows에서는 `mvnw.cmd test`를 사용합니다.

## 디렉터리 구조

```text
.
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .mvn/wrapper/maven-wrapper.properties
├── docs/
│   ├── AGENT.md
│   ├── DECISION.md
│   ├── PLAN.md
│   └── PRD.md
└── src/
    ├── main/java/
    │   ├── s/Hello.java
    │   ├── s/type/tuple/Pair.java
    │   ├── s/type/tuple/Triplet.java
    │   ├── s/type/tuple/Quartet.java
    │   ├── s/util/StringUtil.java
    │   └── s/util/CollectionUtil.java
    └── test/java/
        ├── s/HelloTest.java
        ├── s/type/tuple/TupleTest.java
        ├── s/util/StringUtilTest.java
        └── s/util/CollectionUtilTest.java
```

## 의존성

| 용도 | 좌표 | 버전 | 스코프 |
| --- | --- | --- | --- |
| 코드 생성 | `org.projectlombok:lombok` | 1.18.48 | provided |
| JSON 직렬화 | `com.fasterxml.jackson.core:jackson-databind` | 2.17.1 | compile |
| 로깅 API | `org.slf4j:slf4j-api` | 2.0.19 | provided |
| 테스트 | `org.junit.jupiter:junit-jupiter` | 5.14.1 | test |
| 테스트 로깅 | `org.slf4j:slf4j-simple` | 2.0.19 | test |

## 주요 클래스

| 클래스 | 설명 |
| --- | --- |
| `s.util.StringUtil` | 문자열 판별/기본값/선택/변환/패딩/조인·분리 |
| `s.util.CollectionUtil` | 리스트·맵 판별, 집합 연산, 슬라이스, 색인·분류, 변환 (결과 컬렉션은 불변) |
| `s.type.tuple.Pair` / `Triplet` / `Quartet` | 순서 있는 값 묶음, `@EqualsAndHashCode`, JSON 키 `ord1`~`ordN` |

## 사용 예

```java
// 문자열
StringUtil.nonBlankOf("  ", "fallback"); // "fallback"
StringUtil.lpad(5, "ab", "0");           // "000ab"

// 컬렉션 (반환값은 불변)
CollectionUtil.unionOf(List.of(1, 2), List.of(2, 3)); // [1, 2, 3]
CollectionUtil.asMap("foo", 42);                      // {foo=42}

// 튜플
Quartet<Integer, String, Long, Boolean> quartet = Quartet.of(1, "a", 2L, true);
quartet.ord1();   // 1
quartet.toString(); // "(1, a, 2, true)"
```

## 문서

- [docs/AGENT.md](docs/AGENT.md): 에이전트 역할 정의 및 시작 지시사항
- [docs/PRD.md](docs/PRD.md): 프로젝트 요구사항 정의서
- [docs/PLAN.md](docs/PLAN.md): 실행 계획 및 마일스톤
- [docs/DECISION.md](docs/DECISION.md): 주요 결정 사항 및 근거
