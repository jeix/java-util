# 작업 상태

## 현재 기준

- 저장소: `https://github.com/jeix/java-util.git`, 작업 브랜치: `codex`.
- 작업 시작 기준 커밋: `c6c3e3f` — `문자열 유틸리티 동작 개선`.
- `codex`는 `origin/codex`를 추적해요.
- CollectionUtil 불변 컬렉션 변경, StringUtil pipeline, Maven 명령 안내의 구현·검증을 완료했어요.

## 완료

- JDK 21, Maven Wrapper 3.9.11, Lombok/Jackson/JUnit 기반 초기화와 Hello 예제 커밋.
- `s.util.StringUtil` 전체 요청 API와 Supplier 2~5개 오버로드, `s.util.StringUtilTest` 구현.
- `stringify`의 Date·BigDecimal 처리, StringUtilTest의 `@Slf4j`, 테스트용 SLF4J API/Simple 의존성 추가.
- 컨텍스트 정리: `AGENTS.md`·`PRD.md` 생성, `README.md`·`PLAN.md`·`DECISION.md` 현행화.
- private 메서드의 `_` 접두사 규칙과 기존 코드 소급 적용을 `eb2492c`에 커밋.
- CollectionUtil과 테스트를 `317a25c`에 커밋.
- 튜플 타입과 JSON 변환을 `abb8284`에 커밋하고 `origin/codex`로 push.
- StringUtil 인덱스·패딩·stream 후속 변경을 `c6c3e3f`에 커밋했어요.

## 검증 결과

- 2026-09-10, JDK 21.0.11에서 오프라인 `clean verify` 성공 및 JAR 생성.
- pipeline 추가 후 Tuple 테스트 7개 + CollectionUtil 테스트 22개 + StringUtil 테스트 63개 + Hello 테스트 2개 = 총 94개 통과, 실패·오류·건너뜀 0개.
- 결과 파일은 `target/surefire-reports/`에 있으며 생성물이므로 커밋하지 않아요.
- README에 compile·test·package·verify·install과 테스트 생략·오프라인 실행 명령을 구분해 기록했고, `-DskipTests package` 실행을 확인했어요.

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

- 구현 완료: `CollectionUtil`, 당시 `s.util.Pair` record, `CollectionUtilTest`와 관련 문서.
- 대칭차는 `symmetricDifferenceOf`로 구분하고 배열 타입 명시용 `toArray(list, Class<T>)`를 추가했어요. 이름·배열 경계값 제안에 별도 확정 응답은 없었어요.
- 2026-09-10 JDK 21.0.11에서 오프라인 `clean verify` 성공: CollectionUtil 20개 + 기존 63개 = 총 83개 테스트 통과, 실패·오류·건너뜀 0개.
- 당시 검증 범위: null/빈 입력, 중복·순서, 콜백 호출·예외, 타입 추론·검증 실패, subList 뷰, 맵 형변환과 얕은 복사. subList 뷰와 수정 가능한 결과는 후속 변경에서 불변 복사로 대체했어요.
- 커밋 완료: `317a25c` (`컬렉션 유틸리티와 테스트 추가`).

## 튜플 타입 추가

- 구현 완료: `s.type.tuple.Pair`, `Triplet`, `Quartet`, `TupleTest`와 관련 문서.
- 기존 `s.util.Pair` record를 제거하고 새 Pair로 `CollectionUtil.zip`과 테스트를 변경했어요.
- 세 타입에 `@EqualsAndHashCode`, `of(...)`, `ordN()`, 지정 형식의 `toString()`을 구현했어요. Jackson annotation으로 JSON 직렬화·역직렬화를 지원해요.
- 2026-09-10 JDK 21.0.11에서 오프라인 `clean verify` 성공: 신규 Tuple 테스트 7개를 포함한 전체 90개 테스트 통과. Tuple·CollectionUtil 대상 테스트 27개도 별도로 통과했어요.
- 커밋 및 push 완료: `abb8284` (`튜플 타입과 JSON 변환 추가`), `origin/codex`.

## StringUtil 후속 변경

- 구현 완료: loop/stream 무작위 분기, 삼항 연산자와 switch 적용, 음수 slice/head/tail 규칙, trimLeadingZero 정규식 분기, String 기반 repeat·단일 문자 pad, 불변 split 결과.
- 함수 배열형 `pipe`에 입력값 기반 reduce와 `Function::andThen` 합성 구현을 두고 무작위로 선택하도록 했어요. immutable fluent `Pipeline`도 추가하고 두 사용 형태가 같은 결과를 내는 테스트를 추가했어요.
- 검토 반영: Supplier stream 구현에서 실행이 보장되지 않는 `peek` 부수효과를 제거하고, 인덱스·값 후보를 stream 결과로 선택하도록 변경했어요.
- 테스트 갱신: 음수·범위 초과 인덱스, `Integer.MIN_VALUE`, 문자열 반복, 잘못된 pad 반례 `"가"`·`"do"`, split 불변성을 확인해요. trimLeadingZero는 반복 호출로 두 구현의 동일한 결과를 확인해요.
- 2026-09-10 JDK 21.0.11에서 `clean verify` 성공: pipeline 테스트 2개를 포함한 전체 테스트 94개 통과, 실패·오류·건너뜀 0개.
- push는 별도 요청이 있을 때만 해요.

## CollectionUtil 후속 변경

- 구현 완료: 모든 List/Map 결과의 불변 복사, loop/stream 무작위 분기, `emptyIfNull` 삼항 연산자, 음수 slice/head/tail 규칙.
- 테스트 갱신: 원본과 결과의 분리, 중첩 그룹을 포함한 불변성, 음수·범위 초과 인덱스, `Integer.MIN_VALUE`, loop/stream 결과의 동등성을 확인해요.
- CollectionUtil 대상 테스트 22개가 통과했고, 최신 전체 `clean verify` 결과는 위 검증 결과의 94개예요.
- 문서와 diff 검증을 완료했어요. push는 별도 요청이 있을 때만 해요.
