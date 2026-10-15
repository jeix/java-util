package s.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtil {

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter FMT_DATE2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_TIME = DateTimeFormatter.ofPattern("HHmmss");
    private static final DateTimeFormatter FMT_TIME2 = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FMT_DATETIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter FMT_DATETIME2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ------------------------------------------------------------------
    // Current date / time
    // ------------------------------------------------------------------

    public static String today() {
        return date();
    }

    public static String date() {
        return LocalDate.now().format(FMT_DATE);
    }

    public static String date2() {
        return LocalDate.now().format(FMT_DATE2);
    }

    public static String time() {
        return LocalTime.now().format(FMT_TIME);
    }

    public static String time2() {
        return LocalTime.now().format(FMT_TIME2);
    }

    public static String datetime() {
        return LocalDateTime.now().format(FMT_DATETIME);
    }

    public static String datetime2() {
        return LocalDateTime.now().format(FMT_DATETIME2);
    }

    // ------------------------------------------------------------------
    // stringify
    // ------------------------------------------------------------------

    public static String stringify(LocalDate date) {
        return date == null ? "" : date.format(FMT_DATE);
    }

    public static String stringify2(LocalDate date) {
        return date == null ? "" : date.format(FMT_DATE2);
    }

    public static String stringify(LocalTime time) {
        return time == null ? "" : time.format(FMT_TIME);
    }

    public static String stringify2(LocalTime time) {
        return time == null ? "" : time.format(FMT_TIME2);
    }

    public static String stringify(LocalDateTime datetime) {
        return datetime == null ? "" : datetime.format(FMT_DATETIME);
    }

    public static String stringify2(LocalDateTime datetime) {
        return datetime == null ? "" : datetime.format(FMT_DATETIME2);
    }

    // ------------------------------------------------------------------
    // Parse from string
    // ------------------------------------------------------------------

    public static LocalDate localDateOf(String date) {
        if (date == null) return null;
        return date.contains("-")
            ? LocalDate.parse(date, FMT_DATE2)
            : LocalDate.parse(date, FMT_DATE);
    }

    public static LocalTime localTimeOf(String time) {
        if (time == null) return null;
        return time.contains(":")
            ? LocalTime.parse(time, FMT_TIME2)
            : LocalTime.parse(time, FMT_TIME);
    }

    public static LocalDateTime localDateTimeOf(String datetime) {
        if (datetime == null) return null;
        return datetime.contains("-")
            ? LocalDateTime.parse(datetime, FMT_DATETIME2)
            : LocalDateTime.parse(datetime, FMT_DATETIME);
    }

    // ------------------------------------------------------------------
    // Timestamp conversion
    // ------------------------------------------------------------------

    public static Timestamp toTimestamp(LocalDate date) {
        if (date == null) return null;
        return Timestamp.valueOf(date.atStartOfDay());
    }

    public static Timestamp toTimestamp(LocalDateTime datetime) {
        if (datetime == null) return null;
        return Timestamp.valueOf(datetime);
    }

    // ------------------------------------------------------------------
    // Comparison
    // ------------------------------------------------------------------

    public static boolean eval(String date1, String op, String date2) {
        LocalDate d1 = localDateOf(date1);
        LocalDate d2 = localDateOf(date2);
        return switch (op) {
            case "="  -> d1.isEqual(d2);
            case "!=" -> !d1.isEqual(d2);
            case ">=" -> !d1.isBefore(d2);
            case ">"  -> d1.isAfter(d2);
            case "<=" -> !d1.isAfter(d2);
            case "<"  -> d1.isBefore(d2);
            default  -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    // ------------------------------------------------------------------
    // Date arithmetic
    // ------------------------------------------------------------------

    public static String eval(String date, String op, int n, String unit) {
        LocalDate d = localDateOf(date);
        long amount = switch (op) {
            case "+" -> n;
            case "-" -> -n;
            default  -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
        LocalDate result = switch (unit) {
            case "D" -> d.plusDays(amount);
            case "W" -> d.plusWeeks(amount);
            case "M" -> d.plusMonths(amount);
            case "Y" -> d.plusYears(amount);
            default  -> throw new IllegalArgumentException("Unsupported unit: " + unit);
        };
        return _formatDate(date, result);
    }

    // ------------------------------------------------------------------
    // End of month
    // ------------------------------------------------------------------

    public static String eom(String date) {
        if (date == null) return null;
        LocalDate d = localDateOf(date);
        LocalDate lastDay = d.withDayOfMonth(d.lengthOfMonth());
        return _formatDate(date, lastDay);
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private static String _formatDate(String input, LocalDate date) {
        return input != null && input.contains("-")
            ? date.format(FMT_DATE2)
            : date.format(FMT_DATE);
    }
}
