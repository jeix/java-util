package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DateTimeUtilTest {

    private static final LocalDate D = LocalDate.of(2024, 1, 5);
    private static final LocalTime T = LocalTime.of(3, 4, 5, 678_000_000);
    private static final LocalDateTime DT = LocalDateTime.of(D, T);

    @Test
    void now() {
        LocalDateTime before = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        String today = DateTimeUtil.today();
        String date = DateTimeUtil.date();
        String date2 = DateTimeUtil.date2();
        String time = DateTimeUtil.time();
        String time2 = DateTimeUtil.time2();
        String datetime = DateTimeUtil.datetime();
        String datetime2 = DateTimeUtil.datetime2();
        LocalDateTime after = LocalDateTime.now();
        log.info("now: {} {} {} {} {} {} {}", today, date, date2, time, time2, datetime, datetime2);

        assertTrue(date.matches("\\d{8}"));
        assertTrue(date2.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertTrue(time.matches("\\d{6}"));
        assertTrue(time2.matches("\\d{2}:\\d{2}:\\d{2}"));
        assertTrue(datetime.matches("\\d{14}"));
        assertTrue(datetime2.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));

        assertEquals(date.length(), today.length());
        assertBetween(before.toLocalDate(), DateTimeUtil.localDateOf(date), after.toLocalDate());
        assertBetween(before.toLocalDate(), DateTimeUtil.localDateOf(date2), after.toLocalDate());
        assertBetween(before, DateTimeUtil.localDateTimeOf(datetime), after);
        assertBetween(before, DateTimeUtil.localDateTimeOf(datetime2), after);
        assertTrue(DateTimeUtil.localTimeOf(time) != null && DateTimeUtil.localTimeOf(time2) != null);
    }

    private static <T extends Comparable<T>> void assertBetween(T from, T actual, T to) {
        assertTrue(from.compareTo(actual) <= 0 && actual.compareTo(to) <= 0, from + " <= " + actual + " <= " + to);
    }

    @Test
    void stringifyLocalDate() {
        assertEquals("20240105", DateTimeUtil.stringify(D));
        assertEquals("2024-01-05", DateTimeUtil.stringify2(D));
        assertNull(DateTimeUtil.stringify((LocalDate) null));
        assertNull(DateTimeUtil.stringify2((LocalDate) null));
    }

    @Test
    void stringifyLocalTime() {
        assertEquals("030405", DateTimeUtil.stringify(T));
        assertEquals("03:04:05", DateTimeUtil.stringify2(T));
        assertNull(DateTimeUtil.stringify((LocalTime) null));
        assertNull(DateTimeUtil.stringify2((LocalTime) null));
    }

    @Test
    void stringifyLocalDateTime() {
        assertEquals("20240105030405", DateTimeUtil.stringify(DT));
        assertEquals("2024-01-05 03:04:05", DateTimeUtil.stringify2(DT));
        assertNull(DateTimeUtil.stringify((LocalDateTime) null));
        assertNull(DateTimeUtil.stringify2((LocalDateTime) null));
    }

    @Test
    void localDateOf() {
        assertEquals(D, DateTimeUtil.localDateOf("20240105"));
        assertEquals(D, DateTimeUtil.localDateOf("2024-01-05"));
        assertEquals(LocalDate.of(2024, 2, 29), DateTimeUtil.localDateOf("20240229"));
        assertNull(DateTimeUtil.localDateOf("20230229"));
        assertNull(DateTimeUtil.localDateOf("20240230"));
        assertNull(DateTimeUtil.localDateOf("2024-13-01"));
        assertNull(DateTimeUtil.localDateOf("2024-1-5"));
        assertNull(DateTimeUtil.localDateOf("2024/01/05"));
        assertNull(DateTimeUtil.localDateOf("2024-0105"));
        assertNull(DateTimeUtil.localDateOf("2024010"));
        assertNull(DateTimeUtil.localDateOf(" 20240105"));
        assertNull(DateTimeUtil.localDateOf("abcdefgh"));
        assertNull(DateTimeUtil.localDateOf(""));
        assertNull(DateTimeUtil.localDateOf(null));
    }

    @Test
    void localTimeOf() {
        assertEquals(LocalTime.of(3, 4, 5), DateTimeUtil.localTimeOf("030405"));
        assertEquals(LocalTime.of(3, 4, 5), DateTimeUtil.localTimeOf("03:04:05"));
        assertEquals(LocalTime.of(23, 59, 59), DateTimeUtil.localTimeOf("235959"));
        assertNull(DateTimeUtil.localTimeOf("240000"));
        assertNull(DateTimeUtil.localTimeOf("036005"));
        assertNull(DateTimeUtil.localTimeOf("3:04:05"));
        assertNull(DateTimeUtil.localTimeOf("03-04-05"));
        assertNull(DateTimeUtil.localTimeOf(""));
        assertNull(DateTimeUtil.localTimeOf(null));
    }

    @Test
    void localDateTimeOf() {
        LocalDateTime expected = LocalDateTime.of(2024, 1, 5, 3, 4, 5);
        assertEquals(expected, DateTimeUtil.localDateTimeOf("20240105030405"));
        assertEquals(expected, DateTimeUtil.localDateTimeOf("2024-01-05 03:04:05"));
        assertNull(DateTimeUtil.localDateTimeOf("20240230030405"));
        assertNull(DateTimeUtil.localDateTimeOf("20240105240000"));
        assertNull(DateTimeUtil.localDateTimeOf("2024-01-05T03:04:05"));
        assertNull(DateTimeUtil.localDateTimeOf("20240105"));
        assertNull(DateTimeUtil.localDateTimeOf(""));
        assertNull(DateTimeUtil.localDateTimeOf(null));
    }

    @Test
    void stringifyThenParse() {
        assertEquals(D, DateTimeUtil.localDateOf(DateTimeUtil.stringify(D)));
        assertEquals(D, DateTimeUtil.localDateOf(DateTimeUtil.stringify2(D)));
        assertEquals(DT.withNano(0), DateTimeUtil.localDateTimeOf(DateTimeUtil.stringify(DT)));
        assertEquals(DT.withNano(0), DateTimeUtil.localDateTimeOf(DateTimeUtil.stringify2(DT)));
    }

    @Test
    void toTimestamp() {
        assertEquals(Timestamp.valueOf("2024-01-05 00:00:00"), DateTimeUtil.toTimestamp(D));
        assertEquals(Timestamp.valueOf("2024-01-05 03:04:05.678"), DateTimeUtil.toTimestamp(DT));
        assertNull(DateTimeUtil.toTimestamp((LocalDate) null));
        assertNull(DateTimeUtil.toTimestamp((LocalDateTime) null));
    }

    @Test
    void evalCompare() {
        assertTrue(DateTimeUtil.eval("20240105", "=", "2024-01-05"));
        assertFalse(DateTimeUtil.eval("20240105", "=", "20240106"));
        assertTrue(DateTimeUtil.eval("20240105", "!=", "20240106"));
        assertFalse(DateTimeUtil.eval("20240105", "!=", "20240105"));
        assertTrue(DateTimeUtil.eval("20240106", ">", "20240105"));
        assertFalse(DateTimeUtil.eval("20240105", ">", "20240105"));
        assertTrue(DateTimeUtil.eval("20240105", ">=", "20240105"));
        assertFalse(DateTimeUtil.eval("20240104", ">=", "20240105"));
        assertTrue(DateTimeUtil.eval("2024-01-04", "<", "20240105"));
        assertFalse(DateTimeUtil.eval("20240105", "<", "20240105"));
        assertTrue(DateTimeUtil.eval("20240105", "<=", "20240105"));
        assertFalse(DateTimeUtil.eval("20240106", "<=", "20240105"));
        assertTrue(DateTimeUtil.eval("20231231", "<", "20240101"));
    }

    @Test
    void evalCompareInvalid() {
        assertFalse(DateTimeUtil.eval("20240105", "==", "20240105"));
        assertFalse(DateTimeUtil.eval("20240105", null, "20240105"));
        assertFalse(DateTimeUtil.eval("20240230", "=", "20240230"));
        assertFalse(DateTimeUtil.eval("20240230", "!=", "20240105"));
        assertFalse(DateTimeUtil.eval(null, "=", "20240105"));
        assertFalse(DateTimeUtil.eval("20240105", "=", null));
    }

    @Test
    void evalAdd() {
        assertEquals("20240110", DateTimeUtil.eval("20240105", "+", 5, "D"));
        assertEquals("2024-01-10", DateTimeUtil.eval("2024-01-05", "+", 5, "D"));
        assertEquals("20240202", DateTimeUtil.eval("20240126", "+", 1, "W"));
        assertEquals("20240305", DateTimeUtil.eval("20240105", "+", 2, "M"));
        assertEquals("20250105", DateTimeUtil.eval("20240105", "+", 1, "Y"));
        assertEquals("20240105", DateTimeUtil.eval("20240105", "+", 0, "D"));
        assertEquals("20240101", DateTimeUtil.eval("20231231", "+", 1, "D"));
    }

    @Test
    void evalSubtract() {
        assertEquals("20231231", DateTimeUtil.eval("20240101", "-", 1, "D"));
        assertEquals("2023-12-25", DateTimeUtil.eval("2024-01-01", "-", 1, "W"));
        assertEquals("20231105", DateTimeUtil.eval("20240105", "-", 2, "M"));
        assertEquals("20230105", DateTimeUtil.eval("20240105", "-", 1, "Y"));
        assertEquals("20240110", DateTimeUtil.eval("20240105", "-", -5, "D"));
        assertEquals("20240105", DateTimeUtil.eval("20240105", "-", 0, "D"));
    }

    @Test
    void evalClampsToEndOfMonth() {
        assertEquals("20240229", DateTimeUtil.eval("20240131", "+", 1, "M"));
        assertEquals("20230228", DateTimeUtil.eval("20230131", "+", 1, "M"));
        assertEquals("2024-02-29", DateTimeUtil.eval("2024-03-31", "-", 1, "M"));
        assertEquals("20250228", DateTimeUtil.eval("20240229", "+", 1, "Y"));
    }

    @Test
    void evalInvalid() {
        assertNull(DateTimeUtil.eval("20240105", "*", 1, "D"));
        assertNull(DateTimeUtil.eval("20240105", "*", 0, "D"));
        assertNull(DateTimeUtil.eval("20240105", null, 1, "D"));
        assertNull(DateTimeUtil.eval("20240105", "+", 1, "H"));
        assertNull(DateTimeUtil.eval("20240105", "+", 1, "d"));
        assertNull(DateTimeUtil.eval("20240105", "+", 1, null));
        assertNull(DateTimeUtil.eval("20240230", "+", 1, "D"));
        assertNull(DateTimeUtil.eval(null, "+", 1, "D"));
        assertNull(DateTimeUtil.eval("20240105", "+", Integer.MAX_VALUE, "Y"));
        assertNull(DateTimeUtil.eval("20240105", "-", Integer.MIN_VALUE, "Y"));
    }

    @Test
    void eom() {
        assertEquals("20240131", DateTimeUtil.eom("20240105"));
        assertEquals("2024-01-31", DateTimeUtil.eom("2024-01-05"));
        assertEquals("20240229", DateTimeUtil.eom("20240215"));
        assertEquals("20230228", DateTimeUtil.eom("20230215"));
        assertEquals("20240430", DateTimeUtil.eom("20240430"));
        assertEquals("20241231", DateTimeUtil.eom("20241201"));
        assertNull(DateTimeUtil.eom("20240230"));
        assertNull(DateTimeUtil.eom(""));
        assertNull(DateTimeUtil.eom(null));
    }
}
