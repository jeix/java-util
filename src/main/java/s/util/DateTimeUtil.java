package s.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;

public final class DateTimeUtil {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmmss");
    private static final DateTimeFormatter TIME2 = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATETIME2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateTimeUtil() {
    }

    public static String today() {
        return date();
    }

    public static String date() {
        return LocalDate.now().format(DATE);
    }

    public static String date2() {
        return LocalDate.now().format(DATE2);
    }

    public static String time() {
        return LocalTime.now().format(TIME);
    }

    public static String time2() {
        return LocalTime.now().format(TIME2);
    }

    public static String datetime() {
        return LocalDateTime.now().format(DATETIME);
    }

    public static String datetime2() {
        return LocalDateTime.now().format(DATETIME2);
    }

    public static String stringify(LocalDate date) {
        return date == null ? null : date.format(DATE);
    }

    public static String stringify2(LocalDate date) {
        return date == null ? null : date.format(DATE2);
    }

    public static String stringify(LocalTime time) {
        return time == null ? null : time.format(TIME);
    }

    public static String stringify2(LocalTime time) {
        return time == null ? null : time.format(TIME2);
    }

    public static String stringify(LocalDateTime datetime) {
        return datetime == null ? null : datetime.format(DATETIME);
    }

    public static String stringify2(LocalDateTime datetime) {
        return datetime == null ? null : datetime.format(DATETIME2);
    }

    public static LocalDate localDateOf(String date) {
        return _parse(date, DATE, DATE2, LocalDate::parse);
    }

    public static LocalTime localTimeOf(String time) {
        return _parse(time, TIME, TIME2, LocalTime::parse);
    }

    public static LocalDateTime localDateTimeOf(String datetime) {
        return _parse(datetime, DATETIME, DATETIME2, LocalDateTime::parse);
    }

    public static Timestamp toTimestamp(LocalDate date) {
        return date == null ? null : Timestamp.valueOf(date.atStartOfDay());
    }

    public static Timestamp toTimestamp(LocalDateTime datetime) {
        return datetime == null ? null : Timestamp.valueOf(datetime);
    }

    public static boolean eval(String date1, String op, String date2) {
        if (date1 == null || op == null || date2 == null) {
            return false;
        }
        LocalDate localDate1 = localDateOf(date1);
        LocalDate localDate2 = localDateOf(date2);
        if (_coin()) {
            int cmp = localDate1.compareTo(localDate2);
            return switch (op) {
                case "=" -> cmp == 0;
                case "!=" -> cmp != 0;
                case ">=" -> cmp >= 0;
                case ">" -> cmp > 0;
                case "<=" -> cmp <= 0;
                case "<" -> cmp < 0;
                default -> throw new IllegalArgumentException("unknown op: " + op);
            };
        } else {
            return switch (op) {
                case "=" -> localDate1.isEqual(localDate2);
                case "!=" -> !localDate1.isEqual(localDate2);
                case ">=" -> !localDate1.isBefore(localDate2);
                case ">" -> localDate1.isAfter(localDate2);
                case "<=" -> !localDate1.isAfter(localDate2);
                case "<" -> localDate1.isBefore(localDate2);
                default -> throw new IllegalArgumentException("unknown op: " + op);
            };
        }
    }

    public static String eval(String date, String op, int n, String unit) {
        if (date == null) {
            return null;
        }
        if (op == null) {
            throw new IllegalArgumentException("op must not be null");
        }
        LocalDate localDate = localDateOf(date);
        long amount = switch (op) {
            case "+" -> n;
            case "-" -> -n;
            default -> throw new IllegalArgumentException("unknown op: " + op);
        };
        if (_coin()) {
            LocalDate result = switch (unit) {
                case "D" -> localDate.plusDays(amount);
                case "W" -> localDate.plusWeeks(amount);
                case "M" -> localDate.plusMonths(amount);
                case "Y" -> localDate.plusYears(amount);
                default -> throw new IllegalArgumentException("unknown unit: " + unit);
            };
            return _format(result, date);
        } else {
            ChronoUnit chronoUnit = switch (unit) {
                case "D" -> ChronoUnit.DAYS;
                case "W" -> ChronoUnit.WEEKS;
                case "M" -> ChronoUnit.MONTHS;
                case "Y" -> ChronoUnit.YEARS;
                default -> throw new IllegalArgumentException("unknown unit: " + unit);
            };
            return _format(localDate.plus(amount, chronoUnit), date);
        }
    }

    public static String eom(String date) {
        if (date == null) {
            return null;
        }
        LocalDate localDate = localDateOf(date);
        if (_coin()) {
            return _format(localDate.with(TemporalAdjusters.lastDayOfMonth()), date);
        } else {
            return _format(localDate.withDayOfMonth(localDate.lengthOfMonth()), date);
        }
    }

    private static <T> T _parse(String text, DateTimeFormatter basic, DateTimeFormatter dashed,
            BiFunction<String, DateTimeFormatter, T> parser) {
        if (StringUtil.isBlank(text)) {
            return null;
        }
        if (_coin()) {
            try {
                return parser.apply(text, basic);
            } catch (DateTimeParseException e) {
                return parser.apply(text, dashed);
            }
        } else {
            DateTimeFormatter formatter = text.contains("-") || text.contains(":") ? dashed : basic;
            return parser.apply(text, formatter);
        }
    }

    private static String _format(LocalDate date, String source) {
        return source.contains("-") ? date.format(DATE2) : date.format(DATE);
    }

    private static boolean _coin() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
