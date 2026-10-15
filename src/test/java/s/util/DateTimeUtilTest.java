package s.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/** {@link DateTimeUtil}의 메서드들을 검증하는 테스트입니다. */
@Slf4j
class DateTimeUtilTest {

    /** 테스트에서 공통으로 사용하는 날짜입니다. */
    private static final LocalDate DATE = LocalDate.of(2024, 1, 15);

    /** 테스트에서 공통으로 사용하는 시각입니다. */
    private static final LocalTime TIME = LocalTime.of(10, 30, 50);

    /** 테스트에서 공통으로 사용하는 일시입니다. */
    private static final LocalDateTime DATETIME = LocalDateTime.of(DATE, TIME);

    @BeforeAll
    static void setUp() {
        log.info("@Slf4j 로거를 사용하는 테스트 클래스입니다: {}", DateTimeUtilTest.class.getName());
    }

    @Nested
    @DisplayName("현재 날짜와 시각")
    class 현재날짜와시각 {

        @Test
        @DisplayName("date: 오늘 날짜를 yyyyMMdd 형식으로 반환한다")
        void date() {
            String value = DateTimeUtil.date();

            assertAll(
                    () -> assertTrue(value.matches("\\d{8}"), value),
                    () -> assertEquals(LocalDate.now(), DateTimeUtil.localDateOf(value)));
        }

        @Test
        @DisplayName("today: date의 별칭으로 오늘 날짜를 반환한다")
        void today() {
            String value = DateTimeUtil.today();

            assertAll(
                    () -> assertTrue(value.matches("\\d{8}"), value),
                    () -> assertEquals(DateTimeUtil.date(), value));
        }

        @Test
        @DisplayName("date2: 오늘 날짜를 yyyy-MM-dd 형식으로 반환한다")
        void date2() {
            String value = DateTimeUtil.date2();

            assertAll(
                    () -> assertTrue(value.matches("\\d{4}-\\d{2}-\\d{2}"), value),
                    () -> assertEquals(LocalDate.now(), DateTimeUtil.localDateOf(value)));
        }

        @Test
        @DisplayName("time: 현재 시각을 HHmmss 형식으로 반환한다")
        void time() {
            String value = DateTimeUtil.time();

            assertAll(
                    () -> assertTrue(value.matches("\\d{6}"), value),
                    () -> assertNotNull(DateTimeUtil.localTimeOf(value), value));
        }

        @Test
        @DisplayName("time2: 현재 시각을 HH:mm:ss 형식으로 반환한다")
        void time2() {
            String value = DateTimeUtil.time2();

            assertAll(
                    () -> assertTrue(value.matches("\\d{2}:\\d{2}:\\d{2}"), value),
                    () -> assertNotNull(DateTimeUtil.localTimeOf(value), value));
        }

        @Test
        @DisplayName("datetime: 현재 일시를 yyyyMMddHHmmss 형식으로 반환한다")
        void datetime() {
            String value = DateTimeUtil.datetime();

            assertAll(
                    () -> assertTrue(value.matches("\\d{14}"), value),
                    () -> assertEquals(LocalDate.now(), DateTimeUtil.localDateTimeOf(value).toLocalDate()));
        }

        @Test
        @DisplayName("datetime2: 현재 일시를 yyyy-MM-dd HH:mm:ss 형식으로 반환한다")
        void datetime2() {
            String value = DateTimeUtil.datetime2();

            assertAll(
                    () -> assertTrue(value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), value),
                    () -> assertEquals(LocalDate.now(), DateTimeUtil.localDateTimeOf(value).toLocalDate()));
        }
    }

    @Nested
    @DisplayName("stringify")
    class Stringify {

        @Test
        @DisplayName("stringify(LocalDate): yyyyMMdd로 변환한다")
        void stringifyDate() {
            LocalDate nullDate = null;

            assertAll(
                    () -> assertEquals("20240115", DateTimeUtil.stringify(DATE)),
                    () -> assertNull(DateTimeUtil.stringify(nullDate)));
        }

        @Test
        @DisplayName("stringify2(LocalDate): yyyy-MM-dd로 변환한다")
        void stringify2Date() {
            LocalDate nullDate = null;

            assertAll(
                    () -> assertEquals("2024-01-15", DateTimeUtil.stringify2(DATE)),
                    () -> assertNull(DateTimeUtil.stringify2(nullDate)));
        }

        @Test
        @DisplayName("stringify(LocalTime): HHmmss로 변환한다")
        void stringifyTime() {
            LocalTime nullTime = null;

            assertAll(
                    () -> assertEquals("103050", DateTimeUtil.stringify(TIME)),
                    () -> assertNull(DateTimeUtil.stringify(nullTime)));
        }

        @Test
        @DisplayName("stringify2(LocalTime): HH:mm:ss로 변환한다")
        void stringify2Time() {
            LocalTime nullTime = null;

            assertAll(
                    () -> assertEquals("10:30:50", DateTimeUtil.stringify2(TIME)),
                    () -> assertNull(DateTimeUtil.stringify2(nullTime)));
        }

        @Test
        @DisplayName("stringify(LocalDateTime): yyyyMMddHHmmss로 변환한다")
        void stringifyDateTime() {
            LocalDateTime nullDateTime = null;

            assertAll(
                    () -> assertEquals("20240115103050", DateTimeUtil.stringify(DATETIME)),
                    () -> assertNull(DateTimeUtil.stringify(nullDateTime)));
        }

        @Test
        @DisplayName("stringify2(LocalDateTime): yyyy-MM-dd HH:mm:ss로 변환한다")
        void stringify2DateTime() {
            LocalDateTime nullDateTime = null;

            assertAll(
                    () -> assertEquals("2024-01-15 10:30:50", DateTimeUtil.stringify2(DATETIME)),
                    () -> assertNull(DateTimeUtil.stringify2(nullDateTime)));
        }

        @Test
        @DisplayName("stringify와 localDateOf는 서로 변환된다")
        void roundTrip() {
            assertAll(
                    () -> assertEquals(DATE, DateTimeUtil.localDateOf(DateTimeUtil.stringify(DATE))),
                    () -> assertEquals(TIME, DateTimeUtil.localTimeOf(DateTimeUtil.stringify(TIME))),
                    () -> assertEquals(DATETIME, DateTimeUtil.localDateTimeOf(DateTimeUtil.stringify(DATETIME))),
                    () -> assertEquals(DATE, DateTimeUtil.localDateOf(DateTimeUtil.stringify2(DATE))),
                    () -> assertEquals(TIME, DateTimeUtil.localTimeOf(DateTimeUtil.stringify2(TIME))),
                    () -> assertEquals(DATETIME, DateTimeUtil.localDateTimeOf(DateTimeUtil.stringify2(DATETIME))));
        }
    }

    @Nested
    @DisplayName("파싱")
    class 파싱 {

        @Test
        @DisplayName("localDateOf: yyyyMMdd와 yyyy-MM-dd를 읽는다")
        void localDateOf() {
            assertAll(
                    () -> assertEquals(DATE, DateTimeUtil.localDateOf("20240115")),
                    () -> assertEquals(DATE, DateTimeUtil.localDateOf("2024-01-15")),
                    () -> assertEquals(DATE, DateTimeUtil.localDateOf(" 20240115 "), "앞뒤 공백은 무시한다"));
        }

        @Test
        @DisplayName("localDateOf: 값이 없거나 읽을 수 없으면 null")
        void localDateOfInvalid() {
            assertAll(
                    () -> assertNull(DateTimeUtil.localDateOf(null)),
                    () -> assertNull(DateTimeUtil.localDateOf("")),
                    () -> assertNull(DateTimeUtil.localDateOf("   ")),
                    () -> assertNull(DateTimeUtil.localDateOf("2024/01/15")),
                    () -> assertNull(DateTimeUtil.localDateOf("2024023")),
                    () -> assertNull(DateTimeUtil.localDateOf("20240230"), "2월 30일은 보정하지 않는다"),
                    () -> assertNull(DateTimeUtil.localDateOf("20241301")),
                    () -> assertNull(DateTimeUtil.localDateOf("20240115 103050"), "날짜만 읽는 메서드이다"));
        }

        @Test
        @DisplayName("localTimeOf: HHmmss와 HH:mm:ss를 읽는다")
        void localTimeOf() {
            assertAll(
                    () -> assertEquals(TIME, DateTimeUtil.localTimeOf("103050")),
                    () -> assertEquals(TIME, DateTimeUtil.localTimeOf("10:30:50")),
                    () -> assertEquals(TIME, DateTimeUtil.localTimeOf(" 10:30:50 ")));
        }

        @Test
        @DisplayName("localTimeOf: 값이 없거나 읽을 수 없으면 null")
        void localTimeOfInvalid() {
            assertAll(
                    () -> assertNull(DateTimeUtil.localTimeOf(null)),
                    () -> assertNull(DateTimeUtil.localTimeOf("")),
                    () -> assertNull(DateTimeUtil.localTimeOf("1030")),
                    () -> assertNull(DateTimeUtil.localTimeOf("250001"), "24시는 없다"),
                    () -> assertNull(DateTimeUtil.localTimeOf("106005")),
                    () -> assertNull(DateTimeUtil.localTimeOf("20240115")));
        }

        @Test
        @DisplayName("localDateTimeOf: yyyyMMddHHmmss와 yyyy-MM-dd HH:mm:ss를 읽는다")
        void localDateTimeOf() {
            assertAll(
                    () -> assertEquals(DATETIME, DateTimeUtil.localDateTimeOf("20240115103050")),
                    () -> assertEquals(DATETIME, DateTimeUtil.localDateTimeOf("2024-01-15 10:30:50")),
                    () -> assertEquals(DATETIME, DateTimeUtil.localDateTimeOf(" 20240115103050 ")));
        }

        @Test
        @DisplayName("localDateTimeOf: 값이 없거나 읽을 수 없으면 null")
        void localDateTimeOfInvalid() {
            assertAll(
                    () -> assertNull(DateTimeUtil.localDateTimeOf(null)),
                    () -> assertNull(DateTimeUtil.localDateTimeOf("")),
                    () -> assertNull(DateTimeUtil.localDateTimeOf("20240115"), "날짜만으로는 읽지 않는다"),
                    () -> assertNull(DateTimeUtil.localDateTimeOf("2024-01-15T10:30:50"), "구분자는 공백만 지원한다"),
                    () -> assertNull(DateTimeUtil.localDateTimeOf("2024011510305")),
                    () -> assertNull(DateTimeUtil.localDateTimeOf("20240115240350")));
        }
    }

    @Nested
    @DisplayName("toTimestamp")
    class ToTimestamp {

        @Test
        @DisplayName("toTimestamp(LocalDate): 그 날의 00:00:00으로 변환한다")
        void toTimestampDate() {
            LocalDate nullDate = null;

            assertAll(
                    () -> assertEquals(Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 0, 0, 0)),
                            DateTimeUtil.toTimestamp(DATE)),
                    () -> assertNull(DateTimeUtil.toTimestamp(nullDate)));
        }

        @Test
        @DisplayName("toTimestamp(LocalDateTime): 일시를 그대로 변환한다")
        void toTimestampDateTime() {
            LocalDateTime nullDateTime = null;

            assertAll(
                    () -> assertEquals(Timestamp.valueOf(DATETIME), DateTimeUtil.toTimestamp(DATETIME)),
                    () -> assertNull(DateTimeUtil.toTimestamp(nullDateTime)));
        }
    }

    @Nested
    @DisplayName("eval 비교")
    class Eval비교 {

        @Test
        @DisplayName("eval: 모든 비교 연산자를 처리한다")
        void evalOperators() {
            assertAll(
                    () -> assertTrue(DateTimeUtil.eval("20240115", "=", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", "=", "20240116")),
                    () -> assertTrue(DateTimeUtil.eval("20240116", "!=", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", "!=", "20240115")),
                    () -> assertTrue(DateTimeUtil.eval("20240116", ">=", "20240115")),
                    () -> assertTrue(DateTimeUtil.eval("20240115", ">=", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240114", ">=", "20240115")),
                    () -> assertTrue(DateTimeUtil.eval("20240116", ">", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", ">", "20240115")),
                    () -> assertTrue(DateTimeUtil.eval("20240114", "<=", "20240115")),
                    () -> assertTrue(DateTimeUtil.eval("20240115", "<=", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240116", "<=", "20240115")),
                    () -> assertTrue(DateTimeUtil.eval("20240114", "<", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", "<", "20240115")));
        }

        @Test
        @DisplayName("eval: 형식이 달라도 같은 날짜면 같다고 본다")
        void evalMixedFormats() {
            assertAll(
                    () -> assertTrue(DateTimeUtil.eval("20240115", "=", "2024-01-15")),
                    () -> assertTrue(DateTimeUtil.eval("2024-01-15", "<", "20240116")));
        }

        @Test
        @DisplayName("eval: 일시는 시각까지 비교한다")
        void evalDateTime() {
            assertAll(
                    () -> assertTrue(DateTimeUtil.eval("20240115103050", "=", "2024-01-15 10:30:50")),
                    () -> assertTrue(DateTimeUtil.eval("20240115103050", ">", "20240115"), "시각이 있으면 그 날의 00:00:00보다 크다"),
                    () -> assertTrue(DateTimeUtil.eval("20240115103050", "<", "20240115103051")));
        }

        @Test
        @DisplayName("eval: 값이 없거나 연산자를 모르면 false")
        void evalInvalid() {
            assertAll(
                    () -> assertFalse(DateTimeUtil.eval(null, "=", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", "=", null)),
                    () -> assertFalse(DateTimeUtil.eval("2024/01/15", "=", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", null, "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", "  ", "20240115")),
                    () -> assertFalse(DateTimeUtil.eval("20240115", "==", "20240115")));
        }
    }

    @Nested
    @DisplayName("eval 연산")
    class Eval연산 {

        @Test
        @DisplayName("eval: 일(D)을 더하고 뺀다")
        void evalDays() {
            assertAll(
                    () -> assertEquals("20240116", DateTimeUtil.eval("20240115", "+", 1, "D")),
                    () -> assertEquals("20240114", DateTimeUtil.eval("20240115", "-", 1, "D")),
                    () -> assertEquals("20240115", DateTimeUtil.eval("20240115", "+", 0, "D")),
                    () -> assertEquals("20240129", DateTimeUtil.eval("20240115", "+", 14, "D")));
        }

        @Test
        @DisplayName("eval: 주(W)를 더하고 뺀다")
        void evalWeeks() {
            assertAll(
                    () -> assertEquals("20240122", DateTimeUtil.eval("20240115", "+", 1, "W")),
                    () -> assertEquals("20240108", DateTimeUtil.eval("20240115", "-", 1, "W")),
                    () -> assertEquals("20240129", DateTimeUtil.eval("20240115", "+", 2, "W")));
        }

        @Test
        @DisplayName("eval: 월(M)을 더하고 뺀다. 그 달에 없는 날은 마지막 날로 보정한다")
        void evalMonths() {
            assertAll(
                    () -> assertEquals("20240215", DateTimeUtil.eval("20240115", "+", 1, "M")),
                    () -> assertEquals("20231215", DateTimeUtil.eval("20240115", "-", 1, "M")),
                    () -> assertEquals("20240229", DateTimeUtil.eval("20240131", "+", 1, "M")),
                    () -> assertEquals("2024-04-30", DateTimeUtil.eval("2024-03-31", "+", 1, "M")));
        }

        @Test
        @DisplayName("eval: 년(Y)을 더하고 뺀다. 윤년 2월 29일은 2월 28일로 보정한다")
        void evalYears() {
            assertAll(
                    () -> assertEquals("20250115", DateTimeUtil.eval("20240115", "+", 1, "Y")),
                    () -> assertEquals("20230115", DateTimeUtil.eval("20240115", "-", 1, "Y")),
                    () -> assertEquals("2025-02-28", DateTimeUtil.eval("2024-02-29", "+", 1, "Y")));
        }

        @Test
        @DisplayName("eval: 입력과 같은 형식으로 반환하고 단위 대소문자는 구분하지 않는다")
        void evalFormats() {
            assertAll(
                    () -> assertEquals("20240116", DateTimeUtil.eval("20240115", "+", 1, "d")),
                    () -> assertEquals("2024-01-16", DateTimeUtil.eval("2024-01-15", "+", 1, "d")),
                    () -> assertEquals("2024-01-16", DateTimeUtil.eval("2024-01-15", "+", 1, " D ")));
        }

        @Test
        @DisplayName("eval: 일시에 연산하면 시각도 유지한다")
        void evalDateTime() {
            assertAll(
                    () -> assertEquals("20240116103050", DateTimeUtil.eval("20240115103050", "+", 1, "D")),
                    () -> assertEquals("2024-01-16 10:30:50", DateTimeUtil.eval("2024-01-15 10:30:50", "+", 1, "D")));
        }

        @Test
        @DisplayName("eval: 값이 없거나 연산자/단위를 모르면 null")
        void evalInvalid() {
            assertAll(
                    () -> assertNull(DateTimeUtil.eval(null, "+", 1, "D")),
                    () -> assertNull(DateTimeUtil.eval("", "+", 1, "D")),
                    () -> assertNull(DateTimeUtil.eval("2024/01/15", "+", 1, "D")),
                    () -> assertNull(DateTimeUtil.eval("20240115", null, 1, "D")),
                    () -> assertNull(DateTimeUtil.eval("20240115", "*", 1, "D")),
                    () -> assertNull(DateTimeUtil.eval("20240115", "+", 1, null)),
                    () -> assertNull(DateTimeUtil.eval("20240115", "+", 1, "  ")),
                    () -> assertNull(DateTimeUtil.eval("20240115", "+", 1, "H")));
        }
    }

    @Nested
    @DisplayName("eom")
    class Eom {

        @Test
        @DisplayName("eom: 그 달의 마지막 날을 반환한다")
        void eom() {
            assertAll(
                    () -> assertEquals("20240131", DateTimeUtil.eom("20240115")),
                    () -> assertEquals("20240131", DateTimeUtil.eom("20240101")),
                    () -> assertEquals("20240131", DateTimeUtil.eom("20240131")),
                    () -> assertEquals("20240430", DateTimeUtil.eom("20240401")),
                    () -> assertEquals("20241231", DateTimeUtil.eom("20241205")));
        }

        @Test
        @DisplayName("eom: 2월은 윤년을 반영한다")
        void eomFebruary() {
            assertAll(
                    () -> assertEquals("20240229", DateTimeUtil.eom("20240210")),
                    () -> assertEquals("20230228", DateTimeUtil.eom("20230210")),
                    () -> assertEquals("20000229", DateTimeUtil.eom("20000210")));
        }

        @Test
        @DisplayName("eom: 입력과 같은 형식으로 반환한다")
        void eomFormats() {
            assertAll(
                    () -> assertEquals("2024-01-31", DateTimeUtil.eom("2024-01-15")),
                    () -> assertEquals("2024-01-31", DateTimeUtil.eom("2024-01-15 10:30:50")),
                    () -> assertEquals("20240131", DateTimeUtil.eom(" 20240115 ")));
        }

        @Test
        @DisplayName("eom: 값이 없거나 읽을 수 없으면 null")
        void eomInvalid() {
            assertAll(
                    () -> assertNull(DateTimeUtil.eom(null)),
                    () -> assertNull(DateTimeUtil.eom("")),
                    () -> assertNull(DateTimeUtil.eom("2024/01/15")),
                    () -> assertNull(DateTimeUtil.eom("20240230")));
        }
    }
}
