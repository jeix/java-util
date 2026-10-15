package s.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DateTimeUtilTest {

    @Test
    void today() {
        final String today = DateTimeUtil.today();
        assertNotNull(today);
        assertEquals(8, today.length());
        log.info("today: {}", today);
    }

    @Test
    void date() {
        final String date = DateTimeUtil.date();
        assertNotNull(date);
        assertEquals(8, date.length());
        log.info("date: {}", date);
    }

    @Test
    void date2() {
        final String date2 = DateTimeUtil.date2();
        assertNotNull(date2);
        assertEquals(10, date2.length());
        assertEquals('-', date2.charAt(4));
        assertEquals('-', date2.charAt(7));
        log.info("date2: {}", date2);
    }

    @Test
    void time() {
        final String time = DateTimeUtil.time();
        assertNotNull(time);
        assertEquals(6, time.length());
        log.info("time: {}", time);
    }

    @Test
    void time2() {
        final String time2 = DateTimeUtil.time2();
        assertNotNull(time2);
        assertEquals(8, time2.length());
        assertEquals(':', time2.charAt(2));
        assertEquals(':', time2.charAt(5));
        log.info("time2: {}", time2);
    }

    @Test
    void datetime() {
        final String datetime = DateTimeUtil.datetime();
        assertNotNull(datetime);
        assertEquals(14, datetime.length());
        log.info("datetime: {}", datetime);
    }

    @Test
    void datetime2() {
        final String datetime2 = DateTimeUtil.datetime2();
        assertNotNull(datetime2);
        assertEquals(19, datetime2.length());
        assertEquals('-', datetime2.charAt(4));
        assertEquals('-', datetime2.charAt(7));
        assertEquals(' ', datetime2.charAt(10));
        assertEquals(':', datetime2.charAt(13));
        assertEquals(':', datetime2.charAt(16));
        log.info("datetime2: {}", datetime2);
    }

    @Test
    void stringifyLocalDate() {
        final LocalDate date = LocalDate.of(2024, 1, 15);
        assertEquals("20240115", DateTimeUtil.stringify(date));
        assertEquals("2024-01-15", DateTimeUtil.stringify2(date));
        assertNull(DateTimeUtil.stringify((LocalDate) null));
        assertNull(DateTimeUtil.stringify2((LocalDate) null));
        log.info("stringify LocalDate tests passed");
    }

    @Test
    void stringifyLocalTime() {
        final LocalTime time = LocalTime.of(14, 30, 45);
        assertEquals("143045", DateTimeUtil.stringify(time));
        assertEquals("14:30:45", DateTimeUtil.stringify2(time));
        assertNull(DateTimeUtil.stringify((LocalTime) null));
        assertNull(DateTimeUtil.stringify2((LocalTime) null));
        log.info("stringify LocalTime tests passed");
    }

    @Test
    void stringifyLocalDateTime() {
        final LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        assertEquals("20240115143045", DateTimeUtil.stringify(dateTime));
        assertEquals("2024-01-15 14:30:45", DateTimeUtil.stringify2(dateTime));
        assertNull(DateTimeUtil.stringify((LocalDateTime) null));
        assertNull(DateTimeUtil.stringify2((LocalDateTime) null));
        log.info("stringify LocalDateTime tests passed");
    }

    @Test
    void localDateOf() {
        assertEquals(LocalDate.of(2024, 1, 15), DateTimeUtil.localDateOf("20240115"));
        assertEquals(LocalDate.of(2024, 1, 15), DateTimeUtil.localDateOf("2024-01-15"));
        assertNull(DateTimeUtil.localDateOf(null));
        assertNull(DateTimeUtil.localDateOf(""));
        log.info("localDateOf tests passed");
    }

    @Test
    void localTimeOf() {
        assertEquals(LocalTime.of(14, 30, 45), DateTimeUtil.localTimeOf("143045"));
        assertEquals(LocalTime.of(14, 30, 45), DateTimeUtil.localTimeOf("14:30:45"));
        assertNull(DateTimeUtil.localTimeOf(null));
        assertNull(DateTimeUtil.localTimeOf(""));
        log.info("localTimeOf tests passed");
    }

    @Test
    void localDateTimeOf() {
        assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30, 45), DateTimeUtil.localDateTimeOf("20240115143045"));
        assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30, 45), DateTimeUtil.localDateTimeOf("2024-01-15 14:30:45"));
        assertNull(DateTimeUtil.localDateTimeOf(null));
        assertNull(DateTimeUtil.localDateTimeOf(""));
        log.info("localDateTimeOf tests passed");
    }

    @Test
    void toTimestamp() {
        final LocalDate date = LocalDate.of(2024, 1, 15);
        final Timestamp ts1 = DateTimeUtil.toTimestamp(date);
        assertNotNull(ts1);
        assertEquals("2024-01-15 00:00:00.0", ts1.toString());

        final LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45);
        final Timestamp ts2 = DateTimeUtil.toTimestamp(dateTime);
        assertNotNull(ts2);
        assertEquals("2024-01-15 14:30:45.0", ts2.toString());

        assertNull(DateTimeUtil.toTimestamp((LocalDate) null));
        assertNull(DateTimeUtil.toTimestamp((LocalDateTime) null));
        log.info("toTimestamp tests passed");
    }

    @Test
    void evalTwoDates() {
        assertTrue(DateTimeUtil.eval("20240115", "=", "20240115"));
        assertTrue(DateTimeUtil.eval("2024-01-15", "=", "20240115"));
        assertFalse(DateTimeUtil.eval("20240115", "=", "20240116"));

        assertTrue(DateTimeUtil.eval("20240115", "!=", "20240116"));
        assertFalse(DateTimeUtil.eval("20240115", "!=", "20240115"));

        assertTrue(DateTimeUtil.eval("20240116", ">", "20240115"));
        assertFalse(DateTimeUtil.eval("20240115", ">", "20240115"));
        assertFalse(DateTimeUtil.eval("20240114", ">", "20240115"));

        assertTrue(DateTimeUtil.eval("20240115", ">=", "20240115"));
        assertTrue(DateTimeUtil.eval("20240116", ">=", "20240115"));
        assertFalse(DateTimeUtil.eval("20240114", ">=", "20240115"));

        assertTrue(DateTimeUtil.eval("20240114", "<", "20240115"));
        assertFalse(DateTimeUtil.eval("20240115", "<", "20240115"));
        assertFalse(DateTimeUtil.eval("20240116", "<", "20240115"));

        assertTrue(DateTimeUtil.eval("20240115", "<=", "20240115"));
        assertTrue(DateTimeUtil.eval("20240114", "<=", "20240115"));
        assertFalse(DateTimeUtil.eval("20240116", "<=", "20240115"));

        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "invalid", "20240115"));
        assertFalse(DateTimeUtil.eval(null, "=", "20240115"));
        assertFalse(DateTimeUtil.eval("20240115", "=", null));
        log.info("eval two dates tests passed");
    }

    @Test
    void evalDateWithUnit() {
        assertEquals("20240116", DateTimeUtil.eval("20240115", "+", 1, "D"));
        assertEquals("20240114", DateTimeUtil.eval("20240115", "-", 1, "D"));

        assertEquals("20240122", DateTimeUtil.eval("20240115", "+", 1, "W"));
        assertEquals("20240108", DateTimeUtil.eval("20240115", "-", 1, "W"));

        assertEquals("20240215", DateTimeUtil.eval("20240115", "+", 1, "M"));
        assertEquals("20231215", DateTimeUtil.eval("20240115", "-", 1, "M"));

        assertEquals("20250115", DateTimeUtil.eval("20240115", "+", 1, "Y"));
        assertEquals("20230115", DateTimeUtil.eval("20240115", "-", 1, "Y"));

        assertEquals("2024-01-16", DateTimeUtil.eval("2024-01-15", "+", 1, "D"));
        assertEquals("2024-01-14", DateTimeUtil.eval("2024-01-15", "-", 1, "D"));

        assertNull(DateTimeUtil.eval(null, "+", 1, "D"));
        assertNull(DateTimeUtil.eval("", "+", 1, "D"));

        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "x", 1, "D"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "+", 1, "X"));
        log.info("eval date with unit tests passed");
    }

    @Test
    void eom() {
        assertEquals("20240131", DateTimeUtil.eom("20240115"));
        assertEquals("20240229", DateTimeUtil.eom("20240201")); // leap year
        assertEquals("20230228", DateTimeUtil.eom("20230201")); // non-leap year
        assertEquals("20240430", DateTimeUtil.eom("2024-04-15"));
        assertNull(DateTimeUtil.eom(null));
        assertNull(DateTimeUtil.eom(""));
        log.info("eom tests passed");
    }
}