package s.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public final class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_FORMAT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");
    private static final DateTimeFormatter TIME_FORMAT2 = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATETIME_FORMAT2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtil() {
    }

    public static String today() {
        return date();
    }

    public static String date() {
        return LocalDate.now().format(DATE_FORMAT);
    }

    public static String date2() {
        return LocalDate.now().format(DATE_FORMAT2);
    }

    public static String time() {
        return LocalTime.now().format(TIME_FORMAT);
    }

    public static String time2() {
        return LocalTime.now().format(TIME_FORMAT2);
    }

    public static String datetime() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }

    public static String datetime2() {
        return LocalDateTime.now().format(DATETIME_FORMAT2);
    }

    public static String stringify(final LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : null;
    }

    public static String stringify2(final LocalDate date) {
        return date != null ? date.format(DATE_FORMAT2) : null;
    }

    public static String stringify(final LocalTime time) {
        return time != null ? time.format(TIME_FORMAT) : null;
    }

    public static String stringify2(final LocalTime time) {
        return time != null ? time.format(TIME_FORMAT2) : null;
    }

    public static String stringify(final LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : null;
    }

    public static String stringify2(final LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT2) : null;
    }

    public static LocalDate localDateOf(final String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        if (date.contains("-")) {
            return LocalDate.parse(date, DATE_FORMAT2);
        }
        return LocalDate.parse(date, DATE_FORMAT);
    }

    public static LocalTime localTimeOf(final String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }
        if (time.contains(":")) {
            return LocalTime.parse(time, TIME_FORMAT2);
        }
        return LocalTime.parse(time, TIME_FORMAT);
    }

    public static LocalDateTime localDateTimeOf(final String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        }
        if (dateTime.contains(" ") || dateTime.contains("-") && dateTime.contains(":")) {
            return LocalDateTime.parse(dateTime, DATETIME_FORMAT2);
        }
        return LocalDateTime.parse(dateTime, DATETIME_FORMAT);
    }

    public static Timestamp toTimestamp(final LocalDate date) {
        return date != null ? Timestamp.valueOf(date.atStartOfDay()) : null;
    }

    public static Timestamp toTimestamp(final LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }

    public static boolean eval(final String date1, final String op, final String date2) {
        final LocalDate d1 = localDateOf(date1);
        final LocalDate d2 = localDateOf(date2);
        if (d1 == null || d2 == null) {
            return false;
        }
        return switch (op) {
            case "=" -> d1.isEqual(d2);
            case "!=" -> !d1.isEqual(d2);
            case ">" -> d1.isAfter(d2);
            case ">=" -> d1.isAfter(d2) || d1.isEqual(d2);
            case "<" -> d1.isBefore(d2);
            case "<=" -> d1.isBefore(d2) || d1.isEqual(d2);
            default -> throw new IllegalArgumentException("Invalid operator: " + op);
        };
    }

    public static String eval(final String date, final String op, final int n, final String unit) {
        final LocalDate d = localDateOf(date);
        if (d == null) {
            return null;
        }
        final boolean useDashedFormat = date != null && date.contains("-");
        final ChronoUnit chronoUnit = switch (unit) {
            case "D" -> ChronoUnit.DAYS;
            case "W" -> ChronoUnit.WEEKS;
            case "M" -> ChronoUnit.MONTHS;
            case "Y" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Invalid unit: " + unit);
        };
        final LocalDate result = switch (op) {
            case "+" -> d.plus(n, chronoUnit);
            case "-" -> d.minus(n, chronoUnit);
            default -> throw new IllegalArgumentException("Invalid operator: " + op);
        };
        return useDashedFormat ? stringify2(result) : stringify(result);
    }

    public static String eom(final String date) {
        final LocalDate d = localDateOf(date);
        if (d == null) {
            return null;
        }
        return stringify(d.withDayOfMonth(d.lengthOfMonth()));
    }
}