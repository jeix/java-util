# PRD - 프로젝트 요구사항 정의서

## 프로젝트 개요
- **프로젝트명**: java-util
- **목적**: Java 21 기반 유틸리티 라이브러리 기초 구조 제공
- **빌드 도구**: Maven (Wrapper 포함)
- **JDK 버전**: 21

## 필수 요구사항

### 1. 프로젝트 구조
- 표준 Maven 디렉토리 레이아웃 준수
- `src/main/java` - 메인 소스 코드
- `src/test/java` - 테스트 코드

### 2. 의존성
| 라이브러리 | 용도 | 버전 |
|-----------|------|------|
| Lombok | 보일러플레이트 코드 감소 | 1.18.34 |
| Jackson (databind, annotations) | JSON 직렬화/역직렬화 | 2.17.1 |
| JUnit Jupiter (API, Engine) | 단위 테스트 프레임워크 | 5.10.2 |

### 3. 기능 요구사항
- **Hello 클래스**: 메시지를 받아 인사말 반환 (`greet()` 메서드)
- **Lombok 어노테이션 활용**: `@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **테스트 클래스**: 정상 케이스, 경계 케이스, Lombok 생성 메서드 검증

### 4. 비기능 요구사항
- Maven Wrapper 제공으로 별도 설치 없이 빌드 가능
- Java 21 컴파일 타겟
- UTF-8 인코딩 기본 적용