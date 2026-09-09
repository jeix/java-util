# 작업 상태

## 현재 기준

- 저장소: `https://github.com/jeix/java-util.git`, 작업 브랜치: `codex`.
- 초기화 기준 커밋: `294d430` — `자바 유틸리티 프로젝트 초기화`.
- StringUtil 기능과 컨텍스트 문서는 `55044cd` (`문자열 유틸리티와 테스트 추가`)로 커밋했어요. push 요청은 없어요.

## 완료

- JDK 21, Maven Wrapper 3.9.11, Lombok/Jackson/JUnit 기반 초기화와 Hello 예제 커밋.
- `s.util.StringUtil` 전체 요청 API와 Supplier 2~5개 오버로드, `s.util.StringUtilTest` 구현.
- `stringify`의 Date·BigDecimal 처리, StringUtilTest의 `@Slf4j`, 테스트용 SLF4J API/Simple 의존성 추가.
- 컨텍스트 정리: `AGENTS.md`·`PRD.md` 생성, `README.md`·`PLAN.md`·`DECISION.md` 현행화.

## 검증 결과

- 2026-09-09, JDK 21.0.11에서 `clean verify` 성공 및 JAR 생성.
- StringUtil 테스트 61개 + Hello 테스트 2개 = 총 63개 통과, 실패·오류·건너뜀 0개.
- 연말·연초·윤일, 지수 표기·소수점 뒤 0, join 연동, Supplier 평가 순서·횟수·예외, 패딩·범위 경계값을 검증했고 실제 로그 출력도 확인했어요.
- 결과 파일: `target/surefire-reports/s.util.StringUtilTest.txt`, `target/surefire-reports/s.HelloTest.txt` (생성물이므로 커밋 제외).
- 커밋 전 오프라인 `clean verify`를 다시 실행해 위 결과를 확인했어요.

## 검증 환경 재사용

기본 실행법은 [README.md](README.md)에 있어요. 이 세션은 캐시 쓰기 경로를 `/tmp`로 지정했어요.

```bash
MAVEN_USER_HOME=/tmp/java-util-maven-home ./mvnw -B -ntp -Dmaven.repo.local=/tmp/java-util-m2 clean verify
```

캐시가 남아 있으면 `-o`로 오프라인 실행할 수 있어요. `/tmp`는 영구 보존되지 않으며 캐시가 없으면 다운로드가 필요해요.
이 작업 환경에서는 네트워크 다운로드와 `.git` 쓰기에 sandbox 권한 확장이 필요했어요.

## 대기와 다음 단계

- 사용자가 후속 요구사항을 주면 현재 브랜치에서 이어서 작업해요.
- 명세에 없던 경계값 규칙은 구현 가정으로 유지 중이에요. 별도의 사용자 확정 응답은 없었으며 상세 내용은 [DECISION.md](DECISION.md)에 있어요.

## private 메서드 명명 규칙 소급 적용

- `AGENTS.md`에 private 메서드 이름의 `_` 접두사 규칙을 추가했어요.
- StringUtil의 `supply`/`padded`/`padding`과 테스트 보조 메서드 `assertSupplierSelection`에 접두사를 붙이고 호출부를 수정했어요.
- 검증: `clean verify`로 전체 테스트 63개가 통과했고 `git diff --check`도 통과했어요. 소스의 모든 private 메서드가 `_` 접두사를 사용하는지 확인했어요.
- 사용자 요청에 따라 명명 규칙과 소급 적용 변경을 하나의 작업 단위로 커밋해요.
