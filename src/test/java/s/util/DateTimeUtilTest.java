package s.util;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class DateTimeUtilTest {

    // ---- Current date / time ----

    @Test
    void today_matchesDate() {
        assertEquals(DateTimeUtil.date(), DateTimeUtil.today());
        assertTrue(DateTimeUtil.today().matches("\\d{8}"));
        log.debug("today: {}", DateTimeUtil.today());
    }

    @Test
    void date_format_yyyyMMdd() {
        assertTrue(DateTimeUtil.date().matches("\\d{8}"));
        log.debug("date: {}", DateTimeUtil.date());
    }

    @Test
    void date2_format_yyyyMMddHyphen() {
        assertTrue(DateTimeUtil.date2().matches("\\d{4}-\\d{2}-\\d{2}"));
        log.debug("date2: {}", DateTimeUtil.date2());
    }

    @Test
    void time_format_HHmmss() {
        assertTrue(DateTimeUtil.time().matches("\\d{6}"));
        log.debug("time: {}", DateTimeUtil.time());
    }

    @Test
    void time2_format_HHmmssColon() {
        assertTrue(DateTimeUtil.time2().matches("\\d{2}:\\d{2}:\\d{2}"));
        log.debug("time2: {}", DateTimeUtil.time2());
    }

    @Test
    void datetime_format_yyyyMMddHHmmss() {
        assertTrue(DateTimeUtil.datetime().matches("\\d{14}"));
        log.debug("datetime: {}", DateTimeUtil.datetime());
    }

    @Test
    void datetime2_format_yyyyMMddHyphenHHmmssColon() {
        assertTrue(DateTimeUtil.datetime2().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
        log.debug("datetime2: {}", DateTimeUtil.datetime2());
    }

    // ---- stringify ----

    @Test
    void stringify_localDate_compact() {
        LocalDate d = LocalDate.of(2024, 1, 15);
        assertEquals("20240115", DateTimeUtil.stringify(d));
    }

    @Test
    void stringify_localDate_delimited() {
        LocalDate d = LocalDate.of(2024, 1, 15);
        assertEquals("2024-01-15", DateTimeUtil.stringify2(d));
    }

    @Test
    void stringify_localTime_compact() {
        LocalTime t = LocalTime.of(14, 30, 45);
        assertEquals("143045", DateTimeUtil.stringify(t));
    }

    @Test
    void stringify_localTime_delimited() {
        LocalTime t = LocalTime.of(14, 30, 45);
        assertEquals("14:30:45", DateTimeUtil.stringify2(t));
    }

    @Test
    void stringify_localDateTime_compact() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        assertEquals("20240115143045", DateTimeUtil.stringify(dt));
    }

    @Test
    void stringify_localDateTime_delimited() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        assertEquals("2024-01-15 14:30:45", DateTimeUtil.stringify2(dt));
    }

    @Test
    void stringify_nullReturnsEmpty() {
        assertEquals("", DateTimeUtil.stringify((LocalDate) null));
        assertEquals("", DateTimeUtil.stringify((LocalTime) null));
        assertEquals("", DateTimeUtil.stringify((LocalDateTime) null));
        assertEquals("", DateTimeUtil.stringify2((LocalDate) null));
        assertEquals("", DateTimeUtil.stringify2((LocalTime) null));
        assertEquals("", DateTimeUtil.stringify2((LocalDateTime) null));
    }

    // ---- localDateOf ----

    @Test
    void localDateOf_compactFormat() {
        LocalDate d = DateTimeUtil.localDateOf("20240115");
        assertEquals(LocalDate.of(2024, 1, 15), d);
    }

    @Test
    void localDateOf_delimitedFormat() {
        LocalDate d = DateTimeUtil.localDateOf("2024-01-15");
        assertEquals(LocalDate.of(2024, 1, 15), d);
    }

    @Test
    void localDateOf_nullReturnsNull() {
        assertNull(DateTimeUtil.localDateOf(null));
    }

    @Test
    void localDateOf_invalidFormatThrows() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.localDateOf("invalid"));
    }

    // ---- localTimeOf ----

    @Test
    void localTimeOf_compactFormat() {
        LocalTime t = DateTimeUtil.localTimeOf("143045");
        assertEquals(LocalTime.of(14, 30, 45), t);
    }

    @Test
    void localTimeOf_delimitedFormat() {
        LocalTime t = DateTimeUtil.localTimeOf("14:30:45");
        assertEquals(LocalTime.of(14, 30, 45), t);
    }

    @Test
    void localTimeOf_nullReturnsNull() {
        assertNull(DateTimeUtil.localTimeOf(null));
    }

    @Test
    void localTimeOf_invalidFormatThrows() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.localTimeOf("invalid"));
    }

    // ---- localDateTimeOf ----

    @Test
    void localDateTimeOf_compactFormat() {
        LocalDateTime dt = DateTimeUtil.localDateTimeOf("20240115143045");
        assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30, 45), dt);
    }

    @Test
    void localDateTimeOf_delimitedFormat() {
        LocalDateTime dt = DateTimeUtil.localDateTimeOf("2024-01-15 14:30:45");
        assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30, 45), dt);
    }

    @Test
    void localDateTimeOf_nullReturnsNull() {
        assertNull(DateTimeUtil.localDateTimeOf(null));
    }

    @Test
    void localDateTimeOf_invalidFormatThrows() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.localDateTimeOf("invalid"));
    }

    // ---- toTimestamp ----

    @Test
    void toTimestamp_localDate() {
        LocalDate d = LocalDate.of(2024, 1, 15);
        Timestamp ts = DateTimeUtil.toTimestamp(d);
        assertEquals(Timestamp.valueOf("2024-01-15 00:00:00"), ts);
        log.debug("toTimestamp(LocalDate): {}", ts);
    }

    @Test
    void toTimestamp_localDateTime() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        Timestamp ts = DateTimeUtil.toTimestamp(dt);
        assertEquals(Timestamp.valueOf("2024-01-15 14:30:45"), ts);
        log.debug("toTimestamp(LocalDateTime): {}", ts);
    }

    @Test
    void toTimestamp_nullReturnsNull() {
        assertNull(DateTimeUtil.toTimestamp((LocalDate) null));
        assertNull(DateTimeUtil.toTimestamp((LocalDateTime) null));
    }

    // ---- eval (comparison) ----

    @Test
    void eval_equal() {
        assertTrue(DateTimeUtil.eval("20240101", "=", "20240101"));
        assertFalse(DateTimeUtil.eval("20240101", "=", "20240102"));
    }

    @Test
    void eval_notEqual() {
        assertTrue(DateTimeUtil.eval("20240101", "!=", "20240102"));
        assertFalse(DateTimeUtil.eval("20240101", "!=", "20240101"));
    }

    @Test
    void eval_greaterThanOrEqual() {
        assertTrue(DateTimeUtil.eval("20240102", ">=", "20240101"));
        assertTrue(DateTimeUtil.eval("20240101", ">=", "20240101"));
        assertFalse(DateTimeUtil.eval("20240101", ">=", "20240102"));
    }

    @Test
    void eval_greaterThan() {
        assertTrue(DateTimeUtil.eval("20240102", ">", "20240101"));
        assertFalse(DateTimeUtil.eval("20240101", ">", "20240101"));
        assertFalse(DateTimeUtil.eval("20240101", ">", "20240102"));
    }

    @Test
    void eval_lessThanOrEqual() {
        assertTrue(DateTimeUtil.eval("20240101", "<=", "20240102"));
        assertTrue(DateTimeUtil.eval("20240101", "<=", "20240101"));
        assertFalse(DateTimeUtil.eval("20240102", "<=", "20240101"));
    }

    @Test
    void eval_lessThan() {
        assertTrue(DateTimeUtil.eval("20240101", "<", "20240102"));
        assertFalse(DateTimeUtil.eval("20240101", "<", "20240101"));
        assertFalse(DateTimeUtil.eval("20240102", "<", "20240101"));
    }

    @Test
    void eval_mixedFormats() {
        assertTrue(DateTimeUtil.eval("2024-01-01", "=", "20240101"));
        assertTrue(DateTimeUtil.eval("20240101", "!=", "2024-01-02"));
        assertTrue(DateTimeUtil.eval("20240102", ">", "2024-01-01"));
    }

    @Test
    void eval_unsupportedOperatorThrows() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240101", "%", "20240102"));
    }

    // ---- eval (arithmetic) ----

    @Test
    void eval_addDays() {
        assertEquals("20240116", DateTimeUtil.eval("20240115", "+", 1, "D"));
        assertEquals("20240120", DateTimeUtil.eval("20240115", "+", 5, "D"));
    }

    @Test
    void eval_addWeeks() {
        assertEquals("20240129", DateTimeUtil.eval("20240115", "+", 2, "W"));
    }

    @Test
    void eval_addMonths() {
        assertEquals("20240215", DateTimeUtil.eval("20240115", "+", 1, "M"));
        assertEquals("20240315", DateTimeUtil.eval("20240115", "+", 2, "M"));
    }

    @Test
    void eval_addYears() {
        assertEquals("20250115", DateTimeUtil.eval("20240115", "+", 1, "Y"));
        assertEquals("20260115", DateTimeUtil.eval("20240115", "+", 2, "Y"));
    }

    @Test
    void eval_subtractDays() {
        assertEquals("20240114", DateTimeUtil.eval("20240115", "-", 1, "D"));
        assertEquals("20240110", DateTimeUtil.eval("20240115", "-", 5, "D"));
    }

    @Test
    void eval_subtractMonths() {
        assertEquals("20231215", DateTimeUtil.eval("20240115", "-", 1, "M"));
    }

    @Test
    void eval_subtractYears() {
        assertEquals("20230115", DateTimeUtil.eval("20240115", "-", 1, "Y"));
    }

    @Test
    void eval_delimitedFormat_inputOutput() {
        assertEquals("2024-01-16", DateTimeUtil.eval("2024-01-15", "+", 1, "D"));
        assertEquals("2024-02-15", DateTimeUtil.eval("2024-01-15", "+", 1, "M"));
    }

    @Test
    void eval_monthOverflow_adjustsToLastDay() {
        assertEquals("20240229", DateTimeUtil.eval("20240131", "+", 1, "M"));
        assertEquals("20250228", DateTimeUtil.eval("20250131", "+", 1, "M"));
    }

     @Test
     void eval_arithmetic_unsupportedOperatorThrows() {
         assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "*", 1, "D"));
     }

    @Test
    void eval_unsupportedUnitThrows() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "+", 1, "X"));
    }

    // ---- eom ----

    @Test
    void eom_endOfJanuary() {
        assertEquals("20240131", DateTimeUtil.eom("20240115"));
    }

    @Test
    void eom_endOfFebruary_leapYear() {
        assertEquals("20240229", DateTimeUtil.eom("20240201"));
    }

    @Test
    void eom_endOfFebruary_nonLeapYear() {
        assertEquals("20230228", DateTimeUtil.eom("20230215"));
    }

    @Test
    void eom_endOfApril() {
        assertEquals("20240430", DateTimeUtil.eom("20240410"));
    }

    @Test
    void eom_endOfDecember() {
        assertEquals("20241231", DateTimeUtil.eom("20241225"));
    }

    @Test
    void eom_delimitedFormat() {
        assertEquals("2024-01-31", DateTimeUtil.eom("2024-01-15"));
    }

    @Test
    void eom_alreadyLastDay() {
        assertEquals("20240131", DateTimeUtil.eom("20240131"));
    }

    @Test
    void eom_nullReturnsNull() {
        assertNull(DateTimeUtil.eom(null));
    }
}
