# 작업 상태

## 현재 기준

- 저장소: `https://github.com/jeix/java-util.git`, 작업 브랜치: `codex`.
- 최신 커밋: `eb2492c` — `private 메서드에 밑줄 접두사 적용`.
- `CollectionUtil`, `Pair`, `CollectionUtilTest`와 관련 문서는 구현·검증을 마쳤고 사용자 요청에 따라 커밋해요. push 요청은 없어요.

## 완료

- JDK 21, Maven Wrapper 3.9.11, Lombok/Jackson/JUnit 기반 초기화와 Hello 예제 커밋.
- `s.util.StringUtil` 전체 요청 API와 Supplier 2~5개 오버로드, `s.util.StringUtilTest` 구현.
- `stringify`의 Date·BigDecimal 처리, StringUtilTest의 `@Slf4j`, 테스트용 SLF4J API/Simple 의존성 추가.
- 컨텍스트 정리: `AGENTS.md`·`PRD.md` 생성, `README.md`·`PLAN.md`·`DECISION.md` 현행화.
- private 메서드의 `_` 접두사 규칙과 기존 코드 소급 적용을 `eb2492c`에 커밋.

## 검증 결과

- 2026-09-10, JDK 21.0.11에서 오프라인 `clean verify` 성공 및 JAR 생성.
- CollectionUtil 테스트 20개 + StringUtil 테스트 61개 + Hello 테스트 2개 = 총 83개 통과, 실패·오류·건너뜀 0개.
- 결과 파일은 `target/surefire-reports/`에 있으며 생성물이므로 커밋하지 않아요.

## 검증 환경 재사용

기본 실행법은 [README.md](README.md)에 있어요. 이 세션은 캐시 쓰기 경로를 `/tmp`로 지정했어요.

```bash
MAVEN_USER_HOME=/tmp/java-util-maven-home ./mvnw -B -ntp -Dmaven.repo.local=/tmp/java-util-m2 clean verify
```

캐시가 남아 있으면 `-o`로 오프라인 실행할 수 있어요. `/tmp`는 영구 보존되지 않으며 캐시가 없으면 다운로드가 필요해요.
이 작업 환경에서는 네트워크 다운로드와 `.git` 쓰기에 sandbox 권한 확장이 필요했어요.

## 대기와 다음 단계

- 사용자가 후속 요구사항을 주면 현재 브랜치에서 이어서 작업해요.
- 명세에 없던 경계값과 CollectionUtil의 대칭차 이름·배열 타입 규칙은 구현 가정으로 유지 중이에요. 별도의 사용자 확정 응답은 없었으며 상세 내용은 [DECISION.md](DECISION.md)에 있어요.

## private 메서드 명명 규칙 소급 적용

- `AGENTS.md`에 private 메서드 이름의 `_` 접두사 규칙을 추가했어요.
- StringUtil의 `supply`/`padded`/`padding`과 테스트 보조 메서드 `assertSupplierSelection`에 접두사를 붙이고 호출부를 수정했어요.
- 당시 검증: `clean verify`로 전체 테스트 63개가 통과했고 `git diff --check`도 통과했어요. 소스의 모든 private 메서드가 `_` 접두사를 사용하는지 확인했어요.
- 명명 규칙과 소급 적용은 `eb2492c` (`private 메서드에 밑줄 접두사 적용`)로 커밋했어요.

## CollectionUtil 추가

- 구현 완료: `CollectionUtil`, `Pair` record, `CollectionUtilTest`와 관련 문서.
- 대칭차는 `symmetricDifferenceOf`로 구분하고 배열 타입 명시용 `toArray(list, Class<T>)`를 추가했어요. 이름·배열 경계값 제안에 별도 확정 응답은 없었어요.
- 2026-09-10 JDK 21.0.11에서 오프라인 `clean verify` 성공: CollectionUtil 20개 + 기존 63개 = 총 83개 테스트 통과, 실패·오류·건너뜀 0개.
- 검증 범위: null/빈 입력, 중복·순서, Supplier가 아닌 각 컬렉션 콜백의 호출·예외, 타입 추론·검증 실패, subList 뷰, 맵 형변환과 얕은 복사.
- 다음 단계: 검증 결과를 확인하고 관련 파일만 커밋해요. push 요청은 없어요.
