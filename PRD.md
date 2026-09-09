# java-util 요구사항

## 목표와 범위

JDK 21 환경에서 자바 유틸리티 클래스와 JUnit 테스트를 쉽게 추가하고 빌드·실행하는 단일 모듈 라이브러리를 만들어요.
Maven 사전 설치 없이 Wrapper로 실행하며 Lombok과 Jackson을 사용할 수 있어야 해요.
초기 구조 확인용 `s.Hello`와 `s.HelloTest`를 포함해요. 현재 범위는 라이브러리 JAR와 테스트이며 서버·UI·실행 애플리케이션은 요구사항에 없어요.

## StringUtil API

`s.util.StringUtil`의 다음 기능은 public static으로 제공하고 `s.util.StringUtilTest`에서 검증해요.

| 기능 | 요구 메서드와 인자 |
| --- | --- |
| 판별 | `isBlank(String)`, `isEmpty(String)` |
| 기본값 | `nonNullOf`, `nonBlankOf`, `nonEmptyOf`: `(String value, String dflt)`와 `(Supplier<String>, Supplier<String>)` |
| 첫 유효값 | `firstNonBlankOrLast`, `firstNonBlankOrEmpty`, `firstNonBlankOrNull`: `(String value1, String value2, String... values)`와 Supplier 2·3·4·5개 오버로드 |
| 문자열 변환 | `stringify(Object)` |
| 부분 문자열 | `slice(String, int begin)`, `slice(String, int begin, int end)`; begin 포함, end 불포함 |
| 앞·뒤 추출 | `head(String, int size)`, `tail(String, int size)`; 원문 길이 이내 추출 |
| 기타 변환 | `trimLeadingZero(String)`, `repeat(char, int size)`, `reverse(String)` |
| 단일 문자 패딩 | `lpad`, `rpad`, `pad`: `(int len, String s, char pad)` |
| 문자열 패딩 | `lpad2`, `rpad2`, `pad2`: `(int len, String s, String pad)` |
| 결합·분할 | `join(List<T>, String delimiter)`, `split(String, String regex)` → `List<String>` |

Supplier 선택 메서드는 각각 private static `_firstNonBlankOrLast`, `_firstNonBlankOrEmpty`, `_firstNonBlankOrNull`에 위임해요. 이 메서드들은 Supplier 2개와 나머지 Supplier 가변 인자를 받아요.
패딩 입력은 단일 바이트 문자열을 전제로 해요. 구현 스타일은 [AGENTS.md](AGENTS.md)를 따라요.

## 후속 확정 요구사항

- `stringify`의 `java.util.Date` 입력은 연도-월-일 형식으로 출력해요.
- `BigDecimal` 입력에는 `toPlainString()`을 호출해요.
- `StringUtilTest`에 Lombok `@Slf4j`를 적용해요.

## 검증 기준

전체 빌드와 JUnit 테스트가 통과하고 정상값·경계값, Supplier 지연 평가와 호출 순서, 날짜·숫자 변환을 검증해야 해요.
현재 세부 동작은 [README.md](README.md#문자열-유틸리티), 명세에 없는 동작의 선택 근거는 [DECISION.md](DECISION.md)에 기록해요.
