package s.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateTimeUtilTest {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE2_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // today()

    @Test
    void today_returnsDateInYyyyMMddFormat() {
        String result = DateTimeUtil.today();
        assertEquals(8, result.length());
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.now().format(DATE_FMT), result);
    }

    @Test
    void today_returnsSameValueAsDate() {
        assertEquals(DateTimeUtil.date(), DateTimeUtil.today());
    }

    // date()

    @Test
    void date_returnsTodayInYyyyMMddFormat() {
        String result = DateTimeUtil.date();
        assertEquals(8, result.length());
        assertTrue(result.matches("\\d{8}"));
        assertEquals(LocalDate.now().format(DATE_FMT), result);
    }

    // date2()

    @Test
    void date2_returnsTodayInYyyy_MM_ddFormat() {
        String result = DateTimeUtil.date2();
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
        assertEquals(LocalDate.now().format(DATE2_FMT), result);
    }

    // time()

    @Test
    void time_returnsCurrentTimeInHHmmssFormat() {
        String result = DateTimeUtil.time();
        assertEquals(6, result.length());
        assertTrue(result.matches("\\d{6}"));
    }

    // time2()

    @Test
    void time2_returnsCurrentTimeInHH_mm_ssFormat() {
        String result = DateTimeUtil.time2();
        assertTrue(result.matches("\\d{2}:\\d{2}:\\d{2}"));
    }

    // datetime()

    @Test
    void datetime_returnsCurrentDatetimeInYyyyMMddHHmmssFormat() {
        String result = DateTimeUtil.datetime();
        assertEquals(14, result.length());
        assertTrue(result.matches("\\d{14}"));
    }

    // datetime2()

    @Test
    void datetime2_returnsCurrentDatetimeInYyyy_MM_dd_HH_mm_ssFormat() {
        String result = DateTimeUtil.datetime2();
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    // stringify(LocalDate)

    @Test
    void stringify_localDate_returnsYyyyMMdd() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        assertEquals("20240115", DateTimeUtil.stringify(date));
    }

    @Test
    void stringify_localDate_singleDigitMonthAndDay_paddedWithZero() {
        LocalDate date = LocalDate.of(2024, 3, 5);
        assertEquals("20240305", DateTimeUtil.stringify(date));
    }

    // stringify2(LocalDate)

    @Test
    void stringify2_localDate_returnsYyyy_MM_dd() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        assertEquals("2024-01-15", DateTimeUtil.stringify2(date));
    }

    @Test
    void stringify2_localDate_singleDigitMonthAndDay_paddedWithZero() {
        LocalDate date = LocalDate.of(2024, 3, 5);
        assertEquals("2024-03-05", DateTimeUtil.stringify2(date));
    }

    // stringify(LocalTime)

    @Test
    void stringify_localTime_returnsHHmmss() {
        LocalTime time = LocalTime.of(12, 30, 45);
        assertEquals("123045", DateTimeUtil.stringify(time));
    }

    @Test
    void stringify_localTime_singleDigitFields_paddedWithZero() {
        LocalTime time = LocalTime.of(1, 2, 3);
        assertEquals("010203", DateTimeUtil.stringify(time));
    }

    // stringify2(LocalTime)

    @Test
    void stringify2_localTime_returnsHH_mm_ss() {
        LocalTime time = LocalTime.of(12, 30, 45);
        assertEquals("12:30:45", DateTimeUtil.stringify2(time));
    }

    @Test
    void stringify2_localTime_singleDigitFields_paddedWithZero() {
        LocalTime time = LocalTime.of(1, 2, 3);
        assertEquals("01:02:03", DateTimeUtil.stringify2(time));
    }

    // stringify(LocalDateTime)

    @Test
    void stringify_localDateTime_returnsYyyyMMddHHmmss() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 12, 30, 45);
        assertEquals("20240115123045", DateTimeUtil.stringify(dt));
    }

    // stringify2(LocalDateTime)

    @Test
    void stringify2_localDateTime_returnsYyyy_MM_dd_HH_mm_ss() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 12, 30, 45);
        assertEquals("2024-01-15 12:30:45", DateTimeUtil.stringify2(dt));
    }

    // localDateOf()

    @Test
    void localDateOf_yyyyMMdd_returnsLocalDate() {
        LocalDate result = DateTimeUtil.localDateOf("20240115");
        assertEquals(LocalDate.of(2024, 1, 15), result);
    }

    @Test
    void localDateOf_yyyy_MM_dd_returnsLocalDate() {
        LocalDate result = DateTimeUtil.localDateOf("2024-01-15");
        assertEquals(LocalDate.of(2024, 1, 15), result);
    }

    @Test
    void localDateOf_yyyyMMddAnd_yyyy_MM_dd_returnSameLocalDate() {
        assertEquals(
                DateTimeUtil.localDateOf("20240115"),
                DateTimeUtil.localDateOf("2024-01-15")
        );
    }

    @Test
    void localDateOf_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateOf(null));
    }

    @Test
    void localDateOf_empty_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateOf(""));
    }

    @Test
    void localDateOf_invalidFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeUtil.localDateOf("invalid"));
    }

    // localTimeOf()

    @Test
    void localTimeOf_HHmmss_returnsLocalTime() {
        LocalTime result = DateTimeUtil.localTimeOf("123045");
        assertEquals(LocalTime.of(12, 30, 45), result);
    }

    @Test
    void localTimeOf_HH_mm_ss_returnsLocalTime() {
        LocalTime result = DateTimeUtil.localTimeOf("12:30:45");
        assertEquals(LocalTime.of(12, 30, 45), result);
    }

    @Test
    void localTimeOf_HHmmssAnd_HH_mm_ss_returnSameLocalTime() {
        assertEquals(
                DateTimeUtil.localTimeOf("123045"),
                DateTimeUtil.localTimeOf("12:30:45")
        );
    }

    @Test
    void localTimeOf_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localTimeOf(null));
    }

    @Test
    void localTimeOf_empty_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localTimeOf(""));
    }

    // localDateTimeOf()

    @Test
    void localDateTimeOf_yyyyMMddHHmmss_returnsLocalDateTime() {
        LocalDateTime result = DateTimeUtil.localDateTimeOf("20240115123045");
        assertEquals(LocalDateTime.of(2024, 1, 15, 12, 30, 45), result);
    }

    @Test
    void localDateTimeOf_yyyy_MM_dd_HH_mm_ss_returnsLocalDateTime() {
        LocalDateTime result = DateTimeUtil.localDateTimeOf("2024-01-15 12:30:45");
        assertEquals(LocalDateTime.of(2024, 1, 15, 12, 30, 45), result);
    }

    @Test
    void localDateTimeOf_compactAndFormatted_returnSameLocalDateTime() {
        assertEquals(
                DateTimeUtil.localDateTimeOf("20240115123045"),
                DateTimeUtil.localDateTimeOf("2024-01-15 12:30:45")
        );
    }

    @Test
    void localDateTimeOf_null_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateTimeOf(null));
    }

    @Test
    void localDateTimeOf_empty_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.localDateTimeOf(""));
    }

    // toTimestamp(LocalDate)

    @Test
    void toTimestamp_localDate_returnsTimestampAtStartOfDay() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        Timestamp ts = DateTimeUtil.toTimestamp(date);
        assertEquals(LocalDateTime.of(2024, 1, 15, 0, 0, 0), ts.toLocalDateTime());
    }

    @Test
    void toTimestamp_localDate_midnight_returnsZeroNanos() {
        LocalDate date = LocalDate.of(2024, 1, 15);
        Timestamp ts = DateTimeUtil.toTimestamp(date);
        assertEquals(0, ts.getNanos());
    }

    // toTimestamp(LocalDateTime)

    @Test
    void toTimestamp_localDateTime_returnsMatchingTimestamp() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 12, 30, 45);
        Timestamp ts = DateTimeUtil.toTimestamp(dt);
        assertEquals(dt, ts.toLocalDateTime());
    }

    @Test
    void toTimestamp_localDateTime_withNanos_returnsMatchingNanos() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 12, 30, 45).withNano(123456789);
        Timestamp ts = DateTimeUtil.toTimestamp(dt);
        assertEquals(dt, ts.toLocalDateTime());
        assertEquals(123456789, ts.getNanos());
    }

    // eval(date1, op, date2) : boolean

    @Test
    void eval_equal_sameDates_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240115", "=", "20240115"));
    }

    @Test
    void eval_equal_differentDates_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240115", "=", "20240116"));
    }

    @Test
    void eval_notEqual_differentDates_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240115", "!=", "20240116"));
    }

    @Test
    void eval_notEqual_sameDates_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240115", "!=", "20240115"));
    }

    @Test
    void eval_greaterThan_laterDate_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240116", ">", "20240115"));
    }

    @Test
    void eval_greaterThan_earlierDate_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240114", ">", "20240115"));
    }

    @Test
    void eval_greaterThan_equalDates_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240115", ">", "20240115"));
    }

    @Test
    void eval_greaterThanOrEqual_sameDates_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240115", ">=", "20240115"));
    }

    @Test
    void eval_greaterThanOrEqual_laterDate_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240116", ">=", "20240115"));
    }

    @Test
    void eval_greaterThanOrEqual_earlierDate_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240114", ">=", "20240115"));
    }

    @Test
    void eval_lessThan_earlierDate_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240114", "<", "20240115"));
    }

    @Test
    void eval_lessThan_laterDate_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240116", "<", "20240115"));
    }

    @Test
    void eval_lessThan_equalDates_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240115", "<", "20240115"));
    }

    @Test
    void eval_lessThanOrEqual_sameDates_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240115", "<=", "20240115"));
    }

    @Test
    void eval_lessThanOrEqual_earlierDate_returnsTrue() {
        assertTrue(DateTimeUtil.eval("20240114", "<=", "20240115"));
    }

    @Test
    void eval_lessThanOrEqual_laterDate_returnsFalse() {
        assertFalse(DateTimeUtil.eval("20240116", "<=", "20240115"));
    }

    @Test
    void eval_mixedFormats_comparesCorrectly() {
        assertTrue(DateTimeUtil.eval("20240115", "=", "2024-01-15"));
        assertTrue(DateTimeUtil.eval("2024-01-15", "=", "20240115"));
        assertTrue(DateTimeUtil.eval("20240116", ">", "2024-01-15"));
    }

    @Test
    void eval_unsupportedOperator_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "<>", "20240115"));
    }

    // eval(date, op, n, unit) : String

    @Test
    void evalAdd_days_returnsYyyyMMdd() {
        assertEquals("20240116", DateTimeUtil.eval("20240115", "+", 1, "D"));
        assertEquals("20240117", DateTimeUtil.eval("20240115", "+", 2, "D"));
        assertEquals("20240114", DateTimeUtil.eval("20240115", "+", -1, "D"));
    }

    @Test
    void evalAdd_week_returnsSevenDaysLater() {
        assertEquals("20240122", DateTimeUtil.eval("20240115", "+", 1, "W"));
    }

    @Test
    void evalAdd_month_returnsOneMonthLater() {
        assertEquals("20240215", DateTimeUtil.eval("20240115", "+", 1, "M"));
        assertEquals("20240315", DateTimeUtil.eval("20240115", "+", 2, "M"));
    }

    @Test
    void evalAdd_month_fromJan31_returnsFeb29() {
        assertEquals("20240229", DateTimeUtil.eval("20240131", "+", 1, "M"));
    }

    @Test
    void evalAdd_year_returnsOneYearLater() {
        assertEquals("20250115", DateTimeUtil.eval("20240115", "+", 1, "Y"));
        assertEquals("20260115", DateTimeUtil.eval("20240115", "+", 2, "Y"));
    }

    @Test
    void evalSub_days_returnsYyyyMMdd() {
        assertEquals("20240114", DateTimeUtil.eval("20240115", "-", 1, "D"));
        assertEquals("20240113", DateTimeUtil.eval("20240115", "-", 2, "D"));
    }

    @Test
    void evalSub_week_returnsSevenDaysEarlier() {
        assertEquals("20240108", DateTimeUtil.eval("20240115", "-", 1, "W"));
    }

    @Test
    void evalSub_month_returnsOneMonthEarlier() {
        assertEquals("20231215", DateTimeUtil.eval("20240115", "-", 1, "M"));
    }

    @Test
    void evalSub_year_returnsOneYearEarlier() {
        assertEquals("20230115", DateTimeUtil.eval("20240115", "-", 1, "Y"));
    }

    @Test
    void evalAdd_formattedDateInput_returnsYyyyMMdd() {
        assertEquals("20240116", DateTimeUtil.eval("2024-01-15", "+", 1, "D"));
    }

    @Test
    void evalAdd_unsupportedOperator_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "*", 1, "D"));
    }

    @Test
    void evalAdd_unsupportedUnit_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DateTimeUtil.eval("20240115", "+", 1, "H"));
    }

    // eom()

    @Test
    void eom_midJanuary_returnsLastDayOfJanuary() {
        assertEquals("20240131", DateTimeUtil.eom("20240115"));
    }

    @Test
    void eom_midFebruary_leapYear_returns29th() {
        assertEquals("20240229", DateTimeUtil.eom("2024-02-15"));
    }

    @Test
    void eom_midFebruary_nonLeapYear_returns28th() {
        assertEquals("20230228", DateTimeUtil.eom("20230215"));
    }

    @Test
    void eom_midApril_returns30th() {
        assertEquals("20240430", DateTimeUtil.eom("2024-04-15"));
    }

    @Test
    void eom_midDecember_returns31st() {
        assertEquals("20241231", DateTimeUtil.eom("20241215"));
    }

    @Test
    void eom_firstDayOfMonth_returnsLastDayOfMonth() {
        assertEquals("20240131", DateTimeUtil.eom("20240101"));
    }

    @Test
    void eom_lastDayOfMonth_returnsSameDay() {
        assertEquals("20240131", DateTimeUtil.eom("20240131"));
    }
}
