package s.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
@DisplayName("DateTimeUtil")
class DateTimeUtilTest {

    @Nested
    @DisplayName("지금: today / date / date2 / time / time2 / datetime / datetime2")
    class Now {

        @Test
        @DisplayName("date() 는 오늘을 yyyyMMdd 로 돌려준다")
        void date() {
            LocalDate before = LocalDate.now();
            String actual = DateTimeUtil.date();
            LocalDate after = LocalDate.now();

            _assertBetween(before, after, LocalDate.parse(actual, DateTimeFormatter.BASIC_ISO_DATE));
        }

        @Test
        @DisplayName("today() 는 date() 와 같다")
        void today() {
            LocalDate before = LocalDate.now();
            String actual = DateTimeUtil.today();
            LocalDate after = LocalDate.now();

            _assertBetween(before, after, LocalDate.parse(actual, DateTimeFormatter.BASIC_ISO_DATE));
        }

        @Test
        @DisplayName("date2() 는 오늘을 yyyy-MM-dd 로 돌려준다")
        void date2() {
            LocalDate before = LocalDate.now();
            String actual = DateTimeUtil.date2();
            LocalDate after = LocalDate.now();

            _assertBetween(before, after, LocalDate.parse(actual, DateTimeFormatter.ISO_LOCAL_DATE));
        }

        @Test
        @DisplayName("time() 는 지금 시각을 HHmmss 로 돌려준다")
        void time() {
            String actual = DateTimeUtil.time();

            // 자정을 넘어가는 순간을 견디려고 지금 시각과 견주는 대신 모양만 본다.
            assertTrue(actual.matches("[0-9]{6}"), actual);
            assertDoesNotThrow(() -> LocalTime.parse(actual, DateTimeFormatter.ofPattern("HHmmss")), actual);
        }

        @Test
        @DisplayName("time2() 는 지금 시각을 HH:mm:ss 로 돌려준다")
        void time2() {
            String actual = DateTimeUtil.time2();

            assertTrue(actual.matches("[0-9]{2}:[0-9]{2}:[0-9]{2}"), actual);
            assertDoesNotThrow(() -> LocalTime.parse(actual, DateTimeFormatter.ofPattern("HH:mm:ss")), actual);
        }

        @Test
        @DisplayName("datetime() 는 지금 일시를 yyyyMMddHHmmss 로 돌려준다")
        void datetime() {
            LocalDate before = LocalDate.now();
            String actual = DateTimeUtil.datetime();
            LocalDate after = LocalDate.now();

            assertTrue(actual.matches("[0-9]{14}"), actual);
            LocalDateTime parsed = LocalDateTime.parse(actual, DateTimeFormatter.ofPattern("uuuuMMddHHmmss"));
            _assertBetween(before, after, parsed.toLocalDate());
        }

        @Test
        @DisplayName("datetime2() 는 지금 일시를 yyyy-MM-dd HH:mm:ss 로 돌려준다")
        void datetime2() {
            LocalDate before = LocalDate.now();
            String actual = DateTimeUtil.datetime2();
            LocalDate after = LocalDate.now();

            assertTrue(actual.matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}"), actual);
            LocalDateTime parsed = LocalDateTime.parse(actual, DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
            _assertBetween(before, after, parsed.toLocalDate());
            log.info("지금: {} {} {} {} {} {}", DateTimeUtil.date(), DateTimeUtil.date2(), DateTimeUtil.time(),
                    DateTimeUtil.time2(), DateTimeUtil.datetime(), DateTimeUtil.datetime2());
        }

        /** 날짜가 {@code before} 와 {@code after} 사이인지 본다. 자정을 넘어가는 순간에도 깨지지 않게 이렇게 본다. */
        private static void _assertBetween(LocalDate before, LocalDate after, LocalDate actual) {
            assertFalse(actual.isBefore(before), actual.toString());
            assertFalse(actual.isAfter(after), actual.toString());
        }
    }

    @Nested
    @DisplayName("stringify / stringify2")
    class Stringify {

        @ParameterizedTest
        @CsvSource({
                "2026-10-15, 20261015",
                "2026-01-01, 20260101",
                "2024-02-29, 20240229",
                "0999-12-31, 09991231",
        })
        @DisplayName("stringify(LocalDate) 는 yyyyMMdd")
        void stringifyDate(String source, String expected) {
            assertEquals(expected, DateTimeUtil.stringify(LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE)));
        }

        @ParameterizedTest
        @CsvSource({
                "2026-10-15, 2026-10-15",
                "2024-02-29, 2024-02-29",
                "0999-12-31, 0999-12-31",
        })
        @DisplayName("stringify2(LocalDate) 는 yyyy-MM-dd")
        void stringify2Date(String source, String expected) {
            assertEquals(expected, DateTimeUtil.stringify2(LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE)));
        }

        @Test
        @DisplayName("stringify(LocalTime) / stringify2(LocalTime) 는 초 아래를 버린다")
        void stringifyTime() {
            assertEquals("090503", DateTimeUtil.stringify(LocalTime.of(9, 5, 3, 123_456_789)));
            assertEquals("09:05:03", DateTimeUtil.stringify2(LocalTime.of(9, 5, 3, 123_456_789)));
            assertEquals("000000", DateTimeUtil.stringify(LocalTime.of(0, 0, 0)));
            assertEquals("23:59:59", DateTimeUtil.stringify2(LocalTime.of(23, 59, 59)));
        }

        @Test
        @DisplayName("stringify(LocalDateTime) / stringify2(LocalDateTime) 는 초 아래를 버린다")
        void stringifyDateTime() {
            LocalDateTime datetime = LocalDateTime.of(2026, 10, 15, 9, 5, 3, 123_456_789);

            assertEquals("20261015090503", DateTimeUtil.stringify(datetime));
            assertEquals("2026-10-15 09:05:03", DateTimeUtil.stringify2(datetime));
        }

        @Test
        @DisplayName("null 이면 null")
        void stringifyNull() {
            assertNull(DateTimeUtil.stringify((LocalDate) null));
            assertNull(DateTimeUtil.stringify2((LocalDate) null));
            assertNull(DateTimeUtil.stringify((LocalTime) null));
            assertNull(DateTimeUtil.stringify2((LocalTime) null));
            assertNull(DateTimeUtil.stringify((LocalDateTime) null));
            assertNull(DateTimeUtil.stringify2((LocalDateTime) null));
        }
    }

    @Nested
    @DisplayName("파싱: localDateOf / localTimeOf / localDateTimeOf")
    class Parsing {

        @ParameterizedTest
        @CsvSource({
                "20261015, 2026-10-15",
                "2026-10-15, 2026-10-15",
                "20240101, 2024-01-01",
                "2024-02-29, 2024-02-29",
                "09991231, 0999-12-31",
        })
        @DisplayName("localDateOf: 두 형식 모두 받는다")
        void localDateOf(String date, String expected) {
            assertEquals(LocalDate.parse(expected, DateTimeFormatter.ISO_LOCAL_DATE), DateTimeUtil.localDateOf(date));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t"})
        @DisplayName("localDateOf: null 이거나 빈 문자열이면 null")
        void localDateOfBlank(String date) {
            assertNull(DateTimeUtil.localDateOf(date));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "2026/10/15", "2026.10.15", "2026-1015", "2026-1-15", "2026101", "26-10-15",
                "abcdefgh", "2026-10-15 00:00:00", " 20261015", "20261015 ", "２０２６１０１５",
        })
        @DisplayName("localDateOf: 형식이 아니면 IllegalArgumentException")
        void localDateOfMalformed(String date) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateOf(date));
        }

        @ParameterizedTest
        @ValueSource(strings = {"20260230", "2026-02-30", "20261301", "20260000", "20260032"})
        @DisplayName("localDateOf: 달력에 없는 날짜면 IllegalArgumentException")
        void localDateOfInvalid(String date) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateOf(date));
        }

        @ParameterizedTest
        @CsvSource({
                "090503, 09:05:03",
                "09:05:03, 09:05:03",
                "000000, 00:00:00",
                "235959, 23:59:59",
        })
        @DisplayName("localTimeOf: 두 형식 모두 받는다")
        void localTimeOf(String time, String expected) {
            assertEquals(LocalTime.parse(expected, DateTimeFormatter.ofPattern("HH:mm:ss")), DateTimeUtil.localTimeOf(time));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("localTimeOf: null 이거나 빈 문자열이면 null")
        void localTimeOfBlank(String time) {
            assertNull(DateTimeUtil.localTimeOf(time));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "246000", "240000", "09:60:00", "0905031", "09:05:03.123", "9:05:03", "09-05-03", "abc",
        })
        @DisplayName("localTimeOf: 형식이 맞지 않거나 시각으로 있을 수 없으면 IllegalArgumentException")
        void localTimeOfMalformed(String time) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localTimeOf(time));
        }

        @ParameterizedTest
        @CsvSource({
                "20261015090503, 2026-10-15T09:05:03",
                "2026-10-15 09:05:03, 2026-10-15T09:05:03",
                "20240101000000, 2024-01-01T00:00:00",
                "2024-02-29 23:59:59, 2024-02-29T23:59:59",
        })
        @DisplayName("localDateTimeOf: 두 형식 모두 받는다")
        void localDateTimeOf(String datetime, String expected) {
            assertEquals(LocalDateTime.parse(expected), DateTimeUtil.localDateTimeOf(datetime));
            log.info("localDateTimeOf(\"{}\") = {}", datetime, DateTimeUtil.localDateTimeOf(datetime));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("localDateTimeOf: null 이거나 빈 문자열이면 null")
        void localDateTimeOfBlank(String datetime) {
            assertNull(DateTimeUtil.localDateTimeOf(datetime));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "2026-10-15", "2026101509050", "2026-10-15T09:05:03", "2026-10-15 09:05:03.123",
                "202610150905030", "2026-10-15 9:05:03",
        })
        @DisplayName("localDateTimeOf: 형식이 아니면 IllegalArgumentException")
        void localDateTimeOfMalformed(String datetime) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateTimeOf(datetime));
        }

        @ParameterizedTest
        @ValueSource(strings = {"20260230120000", "2026-02-30 12:00:00", "20261015240000", "2026-10-15 24:00:00"})
        @DisplayName("localDateTimeOf: 달력에 없거나 시각으로 있을 수 없으면 IllegalArgumentException")
        void localDateTimeOfInvalid(String datetime) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateTimeOf(datetime));
        }
    }

    @Nested
    @DisplayName("toTimestamp")
    class ToTimestamp {

        @Test
        @DisplayName("LocalDate 는 그 날 00:00:00 이 된다")
        void toTimestampDate() {
            assertEquals(Timestamp.valueOf(LocalDateTime.of(2026, 10, 15, 0, 0, 0)),
                    DateTimeUtil.toTimestamp(LocalDate.of(2026, 10, 15)));
        }

        @Test
        @DisplayName("LocalDateTime 은 시각까지 그대로 간다")
        void toTimestampDateTime() {
            Timestamp actual = DateTimeUtil.toTimestamp(LocalDateTime.of(2026, 10, 15, 9, 5, 3));

            assertEquals(Timestamp.valueOf(LocalDateTime.of(2026, 10, 15, 9, 5, 3)), actual);
            assertEquals(LocalDateTime.of(2026, 10, 15, 9, 5, 3), actual.toLocalDateTime());
        }

        @Test
        @DisplayName("null 이면 null")
        void toTimestampNull() {
            assertNull(DateTimeUtil.toTimestamp((LocalDate) null));
            assertNull(DateTimeUtil.toTimestamp((LocalDateTime) null));
        }
    }

    @Nested
    @DisplayName("eval(date1, op, date2)")
    class Compare {

        @ParameterizedTest
        @CsvSource({
                "2026-10-15, =,  20261015,   true",
                "2026-10-15, =,  2026-10-16, false",
                "2026-10-15, !=, 2026-10-16, true",
                "2026-10-15, !=, 20261015,   false",
                "2026-10-15, >,  2026-10-14, true",
                "2026-10-15, >,  2026-10-15, false",
                "2026-10-15, >=, 2026-10-15, true",
                "2026-10-15, >=, 2026-10-16, false",
                "2026-10-15, <,  2026-11-01, true",
                "2026-10-15, <,  2026-10-15, false",
                "2026-10-15, <=, 2026-10-15, true",
                "2026-10-15, <=, 2026-10-14, false",
        })
        @DisplayName("두 날짜를 견준다 (형식이 달라도 같은 날이면 같다)")
        void compare(String date1, String op, String date2, boolean expected) {
            assertEquals(expected, DateTimeUtil.eval(date1, op, date2));
        }

        @Test
        @DisplayName("op 가 null 이면 IllegalArgumentException")
        void compareNullOp() {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("2026-10-15", null, "2026-10-15"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"==", "===", "!", "gt", "LT", ""})
        @DisplayName("op 가 정해진 연산자가 아니면 IllegalArgumentException")
        void compareBadOp(String op) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("2026-10-15", op, "2026-10-15"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "2026/10/15", "20260230"})
        @DisplayName("date1 이 날짜가 아니면 IllegalArgumentException (null·빈 문자열 포함)")
        void compareBadDate1(String date1) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval(date1, "=", "2026-10-15"));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "2026/10/15", "20260230"})
        @DisplayName("date2 가 날짜가 아니면 IllegalArgumentException (null·빈 문자열 포함)")
        void compareBadDate2(String date2) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("2026-10-15", "=", date2));
        }
    }

    @Nested
    @DisplayName("eval(date, op, n, unit)")
    class PlusMinus {

        @ParameterizedTest
        @CsvSource({
                "2026-10-15, +, 7,  D, 2026-10-22",
                "2026-10-15, -, 7,  D, 2026-10-08",
                "20261015,   +, 1,  W, 20261022",
                "2026-10-15, +, 1,  M, 2026-11-15",
                "2026-10-15, -, 1,  M, 2026-09-15",
                "2026-10-15, +, 1,  Y, 2027-10-15",
                "2026-10-15, -, 1,  Y, 2025-10-15",
                "2026-10-15, +, 0,  D, 2026-10-15",
                "2026-10-15, -, -3, D, 2026-10-18",
                "2026-10-15, +, 5,  W, 2026-11-19",
                "2026-12-31, +, 1,  D, 2027-01-01",
        })
        @DisplayName("날짜를 더하고 뺀다 (들어온 형식을 그대로 유지한다)")
        void plusMinus(String date, String op, int n, String unit, String expected) {
            assertEquals(expected, DateTimeUtil.eval(date, op, n, unit));
        }

        @ParameterizedTest
        @CsvSource({
                "2026-01-31, +, 1, M, 2026-02-28",
                "2026-01-31, -, 1, M, 2025-12-31",
                "2024-02-29, +, 1, Y, 2025-02-28",
                "2026-03-31, +, 1, M, 2026-04-30",
        })
        @DisplayName("달·해 단위: 그 달에 없는 날짜는 그 달의 마지막 날로 맞춘다")
        void plusMinusClamp(String date, String op, int n, String unit, String expected) {
            assertEquals(expected, DateTimeUtil.eval(date, op, n, unit));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("date 가 null 이거나 빈 문자열이면 null")
        void plusMinusNullDate(String date) {
            assertNull(DateTimeUtil.eval(date, "+", 1, "D"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"*", "plus", "", "++"})
        @DisplayName("op 가 + 나 - 가 아니면 IllegalArgumentException")
        void plusMinusBadOp(String op) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("2026-10-15", op, 1, "D"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"d", "DD", "H", "S", "DAY", ""})
        @DisplayName("unit 이 D·W·M·Y 가 아니면 IllegalArgumentException")
        void plusMinusBadUnit(String unit) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("2026-10-15", "+", 1, unit));
        }

        @Test
        @DisplayName("op 나 unit 이 null 이면 IllegalArgumentException")
        void plusMinusNullOpUnit() {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("2026-10-15", null, 1, "D"));
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("2026-10-15", "+", 1, null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"2026/10/15", "20260230"})
        @DisplayName("date 가 날짜가 아니면 IllegalArgumentException")
        void plusMinusBadDate(String date) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval(date, "+", 1, "D"));
        }
    }

    @Nested
    @DisplayName("eom")
    class EndOfMonth {

        @ParameterizedTest
        @CsvSource({
                "2026-10-15, 2026-10-31",
                "20261015,   20261031",
                "2026-10-31, 2026-10-31",
                "2026-02-10, 2026-02-28",
                "2024-02-01, 2024-02-29",
                "2026-04-01, 2026-04-30",
                "2026-12-01, 2026-12-31",
                "20260101,   20260131",
        })
        @DisplayName("그 달의 마지막 날 (들어온 형식을 그대로 유지한다)")
        void eom(String date, String expected) {
            assertEquals(expected, DateTimeUtil.eom(date));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        @DisplayName("null 이거나 빈 문자열이면 null")
        void eomNull(String date) {
            assertNull(DateTimeUtil.eom(date));
        }

        @ParameterizedTest
        @ValueSource(strings = {"20260230", "2026/10/15", "2026-10-15 00:00:00"})
        @DisplayName("날짜가 아니면 IllegalArgumentException")
        void eomBadDate(String date) {
            assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eom(date));
        }
    }
}
