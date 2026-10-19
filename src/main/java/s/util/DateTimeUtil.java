package s.util;

import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 날짜·시간 유틸리티.
 *
 * <p>문자열 날짜는 {@code yyyyMMdd} 또는 {@code yyyy-MM-dd}, 시간은 {@code HHmmss} 또는 {@code HH:mm:ss},
 * 일시는 {@code yyyyMMddHHmmss} 또는 {@code yyyy-MM-dd HH:mm:ss}예요. 파싱은 엄격해서
 * 없는 날짜("20240230")는 받아들이지 않아요. null이거나 처리할 수 없는 입력은 예외 없이 null(boolean은 false)을 돌려줘요.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateTimeUtil {

    private static final DateTimeFormatter DATE = _formatter("uuuuMMdd");
    private static final DateTimeFormatter DATE2 = _formatter("uuuu-MM-dd");
    private static final DateTimeFormatter TIME = _formatter("HHmmss");
    private static final DateTimeFormatter TIME2 = _formatter("HH:mm:ss");
    private static final DateTimeFormatter DATETIME = _formatter("uuuuMMddHHmmss");
    private static final DateTimeFormatter DATETIME2 = _formatter("uuuu-MM-dd HH:mm:ss");

    private static DateTimeFormatter _formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    // ---- 현재 시각 ----

    /** {@link #date()}와 같아요. */
    public static String today() {
        return date();
    }

    public static String date() {
        return stringify(LocalDate.now());
    }

    public static String date2() {
        return stringify2(LocalDate.now());
    }

    public static String time() {
        return stringify(LocalTime.now());
    }

    public static String time2() {
        return stringify2(LocalTime.now());
    }

    public static String datetime() {
        return stringify(LocalDateTime.now());
    }

    public static String datetime2() {
        return stringify2(LocalDateTime.now());
    }

    // ---- 문자열로 ----

    public static String stringify(LocalDate date) {
        return _format(date, DATE);
    }

    public static String stringify2(LocalDate date) {
        return _format(date, DATE2);
    }

    public static String stringify(LocalTime time) {
        return _format(time, TIME);
    }

    public static String stringify2(LocalTime time) {
        return _format(time, TIME2);
    }

    public static String stringify(LocalDateTime datetime) {
        return _format(datetime, DATETIME);
    }

    public static String stringify2(LocalDateTime datetime) {
        return _format(datetime, DATETIME2);
    }

    private static String _format(TemporalAccessor temporal, DateTimeFormatter formatter) {
        return temporal == null ? null : formatter.format(temporal);
    }

    // ---- 문자열에서 ----

    public static LocalDate localDateOf(String date) {
        return _parse(date, 8, DATE, DATE2, LocalDate::parse);
    }

    public static LocalTime localTimeOf(String time) {
        return _parse(time, 6, TIME, TIME2, LocalTime::parse);
    }

    public static LocalDateTime localDateTimeOf(String datetime) {
        return _parse(datetime, 14, DATETIME, DATETIME2, LocalDateTime::parse);
    }

    /** 길이로 구분자 없는 형식과 있는 형식을 가려 파싱해요. */
    private static <T> T _parse(
            String s,
            int compactLength,
            DateTimeFormatter compact,
            DateTimeFormatter delimited,
            BiFunction<CharSequence, DateTimeFormatter, T> parser) {
        if (s == null) {
            return null;
        }
        try {
            return parser.apply(s, s.length() == compactLength ? compact : delimited);
        } catch (DateTimeException e) {
            return null;
        }
    }

    // ---- Timestamp ----

    /** 그날 0시 0분 0초. */
    public static Timestamp toTimestamp(LocalDate date) {
        return date == null ? null : toTimestamp(date.atStartOfDay());
    }

    public static Timestamp toTimestamp(LocalDateTime datetime) {
        return datetime == null ? null : Timestamp.valueOf(datetime);
    }

    // ---- 비교·계산 ----

    /** op: "=", "!=", ">=", ">", "<=", "<". 날짜가 올바르지 않거나 op를 모르면 false. */
    public static boolean eval(String date1, String op, String date2) {
        LocalDate d1 = localDateOf(date1);
        LocalDate d2 = localDateOf(date2);
        if (d1 == null || d2 == null || op == null) {
            return false;
        }
        int cmp = d1.compareTo(d2);
        return switch (op) {
            case "=" -> cmp == 0;
            case "!=" -> cmp != 0;
            case ">=" -> cmp >= 0;
            case ">" -> cmp > 0;
            case "<=" -> cmp <= 0;
            case "<" -> cmp < 0;
            default -> false;
        };
    }

    /**
     * date에 n단위를 더하거나(op "+") 빼요(op "-"). unit: "D"(일), "W"(주), "M"(월), "Y"(년).
     * 결과는 date와 같은 형식이에요. 달·년 계산에서 해당 날짜가 없으면 그 달의 마지막 날로 맞춰요
     * ("20240131" + 1 M → "20240229"). 입력이 올바르지 않거나 범위를 넘으면 null.
     */
    public static String eval(String date, String op, int n, String unit) {
        LocalDate d = localDateOf(date);
        if (d == null || unit == null || !("+".equals(op) || "-".equals(op))) {
            return null;
        }
        long amount = "+".equals(op) ? n : -(long) n;
        try {
            LocalDate result = switch (unit) {
                case "D" -> d.plusDays(amount);
                case "W" -> d.plusWeeks(amount);
                case "M" -> d.plusMonths(amount);
                case "Y" -> d.plusYears(amount);
                default -> null;
            };
            return _stringifyLike(result, date);
        } catch (DateTimeException e) {
            return null;
        }
    }

    /** 그 달의 마지막 날. date와 같은 형식이에요. */
    public static String eom(String date) {
        LocalDate d = localDateOf(date);
        if (d == null) {
            return null;
        }
        return _stringifyLike(d.with(TemporalAdjusters.lastDayOfMonth()), date);
    }

    /** original과 같은 형식(구분자 유무)으로 문자열화해요. */
    private static String _stringifyLike(LocalDate date, String original) {
        if (date == null) {
            return null;
        }
        return original.length() == 8 ? stringify(date) : stringify2(date);
    }
}
