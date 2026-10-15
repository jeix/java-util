package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class DateTimeUtilTest {

    @BeforeAll
    static void beforeAll() {
        log.info("DateTimeUtilTest start");
    }

    @Test
    void todayIsAliasOfDate() {
        assertTrue(DateTimeUtil.today().matches("\\d{8}"));
        assertTrue(DateTimeUtil.date().matches("\\d{8}"));
    }

    @Test
    void nowFormatsReturnExpectedPatterns() {
        assertTrue(DateTimeUtil.date().matches("\\d{8}"));
        assertTrue(DateTimeUtil.date2().matches("\\d{4}-\\d{2}-\\d{2}"));
        assertTrue(DateTimeUtil.time().matches("\\d{6}"));
        assertTrue(DateTimeUtil.time2().matches("\\d{2}:\\d{2}:\\d{2}"));
        assertTrue(DateTimeUtil.datetime().matches("\\d{14}"));
        assertTrue(DateTimeUtil.datetime2().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void stringifyFormatsLocalTypes() {
        assertEquals("20240229", DateTimeUtil.stringify(LocalDate.of(2024, 2, 29)));
        assertEquals("2024-02-29", DateTimeUtil.stringify2(LocalDate.of(2024, 2, 29)));
        assertEquals("090507", DateTimeUtil.stringify(LocalTime.of(9, 5, 7)));
        assertEquals("09:05:07", DateTimeUtil.stringify2(LocalTime.of(9, 5, 7)));
        assertEquals("20240229090507", DateTimeUtil.stringify(LocalDateTime.of(2024, 2, 29, 9, 5, 7)));
        assertEquals("2024-02-29 09:05:07", DateTimeUtil.stringify2(LocalDateTime.of(2024, 2, 29, 9, 5, 7)));
    }

    @Test
    void stringifyReturnsNullForNull() {
        assertNull(DateTimeUtil.stringify((LocalDate) null));
        assertNull(DateTimeUtil.stringify2((LocalDate) null));
        assertNull(DateTimeUtil.stringify((LocalTime) null));
        assertNull(DateTimeUtil.stringify2((LocalTime) null));
        assertNull(DateTimeUtil.stringify((LocalDateTime) null));
        assertNull(DateTimeUtil.stringify2((LocalDateTime) null));
    }

    @Test
    void localDateOfParsesBothFormats() {
        assertEquals(LocalDate.of(2024, 2, 29), DateTimeUtil.localDateOf("20240229"));
        assertEquals(LocalDate.of(2024, 2, 29), DateTimeUtil.localDateOf("2024-02-29"));
    }

    @Test
    void localTimeOfParsesBothFormats() {
        assertEquals(LocalTime.of(9, 5, 7), DateTimeUtil.localTimeOf("090507"));
        assertEquals(LocalTime.of(9, 5, 7), DateTimeUtil.localTimeOf("09:05:07"));
    }

    @Test
    void localDateTimeOfParsesBothFormats() {
        LocalDateTime expected = LocalDateTime.of(2024, 2, 29, 9, 5, 7);
        assertEquals(expected, DateTimeUtil.localDateTimeOf("20240229090507"));
        assertEquals(expected, DateTimeUtil.localDateTimeOf("2024-02-29 09:05:07"));
    }

    @Test
    void localOfReturnsNullForNullOrBlank() {
        assertNull(DateTimeUtil.localDateOf(null));
        assertNull(DateTimeUtil.localDateOf(""));
        assertNull(DateTimeUtil.localDateOf("   "));
        assertNull(DateTimeUtil.localTimeOf(null));
        assertNull(DateTimeUtil.localTimeOf(""));
        assertNull(DateTimeUtil.localDateTimeOf(null));
        assertNull(DateTimeUtil.localDateTimeOf(" "));
    }

    @Test
    void localOfThrowsForInvalidFormat() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.localDateOf("2024/02/29"));
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.localTimeOf("09-05-07"));
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.localDateTimeOf("2024-02-29"));
    }

    @Test
    void toTimestampConvertsLocalTypes() {
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2024, 2, 29, 0, 0, 0)),
                DateTimeUtil.toTimestamp(LocalDate.of(2024, 2, 29)));
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2024, 2, 29, 9, 5, 7)),
                DateTimeUtil.toTimestamp(LocalDateTime.of(2024, 2, 29, 9, 5, 7)));
    }

    @Test
    void toTimestampReturnsNullForNull() {
        assertNull(DateTimeUtil.toTimestamp((LocalDate) null));
        assertNull(DateTimeUtil.toTimestamp((LocalDateTime) null));
    }

    @Test
    void evalComparesDates() {
        assertTrue(DateTimeUtil.eval("20240101", "=", "2024-01-01"));
        assertFalse(DateTimeUtil.eval("20240101", "!=", "2024-01-01"));
        assertTrue(DateTimeUtil.eval("20240101", "!=", "20240102"));
        assertTrue(DateTimeUtil.eval("20240102", ">", "20240101"));
        assertTrue(DateTimeUtil.eval("20240102", ">=", "20240102"));
        assertTrue(DateTimeUtil.eval("20240101", "<", "2024-01-02"));
        assertTrue(DateTimeUtil.eval("20240101", "<=", "20240101"));
        assertFalse(DateTimeUtil.eval("20240101", ">", "20240102"));
    }

    @Test
    void evalCompareReturnsFalseForNull() {
        assertFalse(DateTimeUtil.eval(null, "=", "20240101"));
        assertFalse(DateTimeUtil.eval("20240101", null, "20240101"));
        assertFalse(DateTimeUtil.eval("20240101", "=", null));
    }

    @Test
    void evalComparesThrowsForUnknownOp() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240101", "==", "20240101"));
    }

    @Test
    void evalAddsAndSubtractsUnits() {
        assertEquals("20240102", DateTimeUtil.eval("20240101", "+", 1, "D"));
        assertEquals("2023-12-31", DateTimeUtil.eval("2024-01-01", "-", 1, "D"));
        assertEquals("20240108", DateTimeUtil.eval("20240101", "+", 1, "W"));
        assertEquals("20231225", DateTimeUtil.eval("20240101", "-", 1, "W"));
        assertEquals("20240201", DateTimeUtil.eval("20240101", "+", 1, "M"));
        assertEquals("20231201", DateTimeUtil.eval("20240101", "-", 1, "M"));
        assertEquals("20250101", DateTimeUtil.eval("20240101", "+", 1, "Y"));
        assertEquals("20230101", DateTimeUtil.eval("20240101", "-", 1, "Y"));
    }

    @Test
    void evalClampsDayOfMonth() {
        assertEquals("20240229", DateTimeUtil.eval("20240131", "+", 1, "M"));
        assertEquals("2024-02-29", DateTimeUtil.eval("2024-03-31", "-", 1, "M"));
    }

    @Test
    void evalAddReturnsNullForNullAndThrowsForUnknownArgs() {
        assertNull(DateTimeUtil.eval(null, "+", 1, "D"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240101", "+", 1, "H"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240101", "*", 1, "D"));
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240101", null, 1, "D"));
    }

    @Test
    void eomReturnsEndOfMonth() {
        assertEquals("20240131", DateTimeUtil.eom("20240101"));
        assertEquals("2024-02-29", DateTimeUtil.eom("2024-02-01"));
        assertEquals("20230228", DateTimeUtil.eom("20230201"));
        assertEquals("20240430", DateTimeUtil.eom("20240415"));
        assertEquals("2024-07-31", DateTimeUtil.eom("2024-07-31"));
    }

    @Test
    void eomReturnsNullForNull() {
        assertNull(DateTimeUtil.eom(null));
    }

    @Test
    void randomBranchesProduceConsistentResults() {
        IntStream.range(0, 100).forEach(i -> {
            assertEquals(LocalDate.of(2024, 2, 29), DateTimeUtil.localDateOf("20240229"));
            assertEquals(LocalDate.of(2024, 2, 29), DateTimeUtil.localDateOf("2024-02-29"));
            assertEquals(LocalTime.of(9, 5, 7), DateTimeUtil.localTimeOf("090507"));
            assertEquals(LocalTime.of(9, 5, 7), DateTimeUtil.localTimeOf("09:05:07"));
            assertEquals(LocalDateTime.of(2024, 2, 29, 9, 5, 7),
                    DateTimeUtil.localDateTimeOf("20240229090507"));
            assertEquals(LocalDateTime.of(2024, 2, 29, 9, 5, 7),
                    DateTimeUtil.localDateTimeOf("2024-02-29 09:05:07"));
            assertTrue(DateTimeUtil.eval("20240102", ">", "20240101"));
            assertTrue(DateTimeUtil.eval("20240101", "<=", "2024-01-01"));
            assertEquals("20240102", DateTimeUtil.eval("20240101", "+", 1, "D"));
            assertEquals("2024-02-29", DateTimeUtil.eval("2024-01-31", "+", 1, "M"));
            assertEquals("20240229", DateTimeUtil.eom("20240201"));
            assertEquals("2024-04-30", DateTimeUtil.eom("2024-04-01"));
        });
    }
}
