package s.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * 날짜와 시각을 다루는 유틸리티 메서드를 모아 둔 클래스입니다.
 *
 * <p>구현 규칙은 {@link StringUtil}과 같습니다.
 *
 * <ul>
 *   <li>파라미터를 먼저 검사하고, 정상적으로 처리할 수 없는 경우는 그대로 리턴합니다.
 *       문자열을 받는 메서드는 {@code null}/공백이거나 지원하는 포맷으로 읽을 수 없으면 {@code null}을,
 *       {@link #eval(String, String, String)}은 {@code false}를 반환합니다.</li>
 *   <li>같은 기능이 이미 다른 메서드에 있으면 그 메서드를 호출합니다.</li>
 *   <li>오버로드된 메서드는 실질적인 구현 메서드 하나를 호출하고, 구현 메서드가 private이면 이름 앞에 {@code _}를 붙입니다.</li>
 *   <li>단순 분기는 3항 연산자를 사용합니다.</li>
 * </ul>
 *
 * <p>포맷은 압축형({@code yyyyMMdd}, {@code HHmmss}, {@code yyyyMMddHHmmss})과 구분자형
 * ({@code yyyy-MM-dd}, {@code HH:mm:ss}, {@code yyyy-MM-dd HH:mm:ss})을 지원합니다. 이름에 {@code 2}가 붙은
 * 메서드는 구분자형을 사용하고, 문자열을 받아 문자열을 돌려주는
 * {@link #eval(String, String, int, String)}과 {@link #eom(String)}은 입력이 구분자형이면 구분자형으로,
 * 압축형이면 압축형으로 반환합니다.
 */
public final class DateTimeUtil {

    /** {@code yyyyMMdd} 포맷입니다. */
    private static final DateTimeFormatter DATE = _formatter("uuuuMMdd");

    /** {@code yyyy-MM-dd} 포맷입니다. */
    private static final DateTimeFormatter DATE2 = _formatter("uuuu-MM-dd");

    /** {@code HHmmss} 포맷입니다. */
    private static final DateTimeFormatter TIME = _formatter("HHmmss");

    /** {@code HH:mm:ss} 포맷입니다. */
    private static final DateTimeFormatter TIME2 = _formatter("HH:mm:ss");

    /** {@code yyyyMMddHHmmss} 포맷입니다. */
    private static final DateTimeFormatter DATETIME = _formatter("uuuuMMddHHmmss");

    /** {@code yyyy-MM-dd HH:mm:ss} 포맷입니다. */
    private static final DateTimeFormatter DATETIME2 = _formatter("uuuu-MM-dd HH:mm:ss");

    /** 날짜 문자열을 파싱할 때 순서대로 시도하는 포맷들입니다. */
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(DATE, DATE2);

    /** 시각 문자열을 파싱할 때 순서대로 시도하는 포맷들입니다. */
    private static final List<DateTimeFormatter> TIME_FORMATS = List.of(TIME, TIME2);

    /** 일시 문자열을 파싱할 때 순서대로 시도하는 포맷들입니다. */
    private static final List<DateTimeFormatter> DATETIME_FORMATS = List.of(DATETIME, DATETIME2);

    private DateTimeUtil() {
    }

    /**
     * 오늘 날짜를 {@code yyyyMMdd} 형식으로 반환합니다. {@link #date()}의 별칭입니다.
     *
     * @return 오늘 날짜
     */
    public static String today() {
        return date();
    }

    /**
     * 오늘 날짜를 {@code yyyyMMdd} 형식으로 반환합니다.
     *
     * @return 오늘 날짜
     */
    public static String date() {
        return stringify(LocalDate.now());
    }

    /**
     * 오늘 날짜를 {@code yyyy-MM-dd} 형식으로 반환합니다.
     *
     * @return 오늘 날짜
     */
    public static String date2() {
        return stringify2(LocalDate.now());
    }

    /**
     * 현재 시각을 {@code HHmmss} 형식으로 반환합니다.
     *
     * @return 현재 시각
     */
    public static String time() {
        return stringify(LocalTime.now());
    }

    /**
     * 현재 시각을 {@code HH:mm:ss} 형식으로 반환합니다.
     *
     * @return 현재 시각
     */
    public static String time2() {
        return stringify2(LocalTime.now());
    }

    /**
     * 현재 일시를 {@code yyyyMMddHHmmss} 형식으로 반환합니다.
     *
     * @return 현재 일시
     */
    public static String datetime() {
        return stringify(LocalDateTime.now());
    }

    /**
     * 현재 일시를 {@code yyyy-MM-dd HH:mm:ss} 형식으로 반환합니다.
     *
     * @return 현재 일시
     */
    public static String datetime2() {
        return stringify2(LocalDateTime.now());
    }

    /**
     * 날짜를 {@code yyyyMMdd} 형식의 문자열로 변환합니다.
     *
     * @param date 날짜, {@code null}이면 {@code null}
     * @return {@code yyyyMMdd} 형식의 날짜 문자열
     */
    public static String stringify(LocalDate date) {
        return _stringifyDate(date, false);
    }

    /**
     * 날짜를 {@code yyyy-MM-dd} 형식의 문자열로 변환합니다.
     *
     * @param date 날짜, {@code null}이면 {@code null}
     * @return {@code yyyy-MM-dd} 형식의 날짜 문자열
     */
    public static String stringify2(LocalDate date) {
        return _stringifyDate(date, true);
    }

    /**
     * 시각을 {@code HHmmss} 형식의 문자열로 변환합니다.
     *
     * @param time 시각, {@code null}이면 {@code null}
     * @return {@code HHmmss} 형식의 시각 문자열
     */
    public static String stringify(LocalTime time) {
        return _stringifyTime(time, false);
    }

    /**
     * 시각을 {@code HH:mm:ss} 형식의 문자열로 변환합니다.
     *
     * @param time 시각, {@code null}이면 {@code null}
     * @return {@code HH:mm:ss} 형식의 시각 문자열
     */
    public static String stringify2(LocalTime time) {
        return _stringifyTime(time, true);
    }

    /**
     * 일시를 {@code yyyyMMddHHmmss} 형식의 문자열로 변환합니다.
     *
     * @param datetime 일시, {@code null}이면 {@code null}
     * @return {@code yyyyMMddHHmmss} 형식의 일시 문자열
     */
    public static String stringify(LocalDateTime datetime) {
        return _stringifyDateTime(datetime, false);
    }

    /**
     * 일시를 {@code yyyy-MM-dd HH:mm:ss} 형식의 문자열로 변환합니다.
     *
     * @param datetime 일시, {@code null}이면 {@code null}
     * @return {@code yyyy-MM-dd HH:mm:ss} 형식의 일시 문자열
     */
    public static String stringify2(LocalDateTime datetime) {
        return _stringifyDateTime(datetime, true);
    }

    /**
     * {@code yyyyMMdd} 또는 {@code yyyy-MM-dd} 형식의 문자열을 {@link LocalDate}로 읽습니다.
     *
     * @param date 날짜 문자열
     * @return 읽은 날짜, {@code null}/공백이거나 지원하는 포맷이 아니면 {@code null}
     */
    public static LocalDate localDateOf(String date) {
        return _parseFormats(_trimToNull(date), DATE_FORMATS, LocalDate::from);
    }

    /**
     * {@code HHmmss} 또는 {@code HH:mm:ss} 형식의 문자열을 {@link LocalTime}으로 읽습니다.
     *
     * @param time 시각 문자열
     * @return 읽은 시각, {@code null}/공백이거나 지원하는 포맷이 아니면 {@code null}
     */
    public static LocalTime localTimeOf(String time) {
        return _parseFormats(_trimToNull(time), TIME_FORMATS, LocalTime::from);
    }

    /**
     * {@code yyyyMMddHHmmss} 또는 {@code yyyy-MM-dd HH:mm:ss} 형식의 문자열을 {@link LocalDateTime}으로 읽습니다.
     *
     * @param datetime 일시 문자열
     * @return 읽은 일시, {@code null}/공백이거나 지원하는 포맷이 아니면 {@code null}
     */
    public static LocalDateTime localDateTimeOf(String datetime) {
        return _parseFormats(_trimToNull(datetime), DATETIME_FORMATS, LocalDateTime::from);
    }

    /**
     * 날짜를 {@link Timestamp}로 변환합니다. 날짜만 있으므로 그 날의 00:00:00으로 해석합니다.
     *
     * @param date 날짜, {@code null}이면 {@code null}
     * @return {@link Timestamp}
     */
    public static Timestamp toTimestamp(LocalDate date) {
        return date == null ? null : toTimestamp(date.atStartOfDay());
    }

    /**
     * 일시를 {@link Timestamp}로 변환합니다.
     *
     * @param datetime 일시, {@code null}이면 {@code null}
     * @return {@link Timestamp}
     */
    public static Timestamp toTimestamp(LocalDateTime datetime) {
        return datetime == null ? null : Timestamp.valueOf(datetime);
    }

    /**
     * 두 날짜(또는 일시)를 비교합니다. 날짜만 있으면 그 날의 00:00:00으로 해석하므로
     * {@code eval("20240115", "=", "2024-01-15")}는 {@code true}입니다.
     *
     * @param date1 첫 번째 날짜 문자열
     * @param op    비교 연산자({@code "="}, {@code "!="}, {@code ">="}, {@code ">"}, {@code "<="}, {@code "<"})
     * @param date2 두 번째 날짜 문자열
     * @return 비교 결과, 값이 없거나 지원하지 않는 연산자이면 {@code false}
     */
    public static boolean eval(String date1, String op, String date2) {
        ParsedDateTime parsed1 = _parseDateTime(date1);
        ParsedDateTime parsed2 = _parseDateTime(date2);
        if (parsed1 == null || parsed2 == null || StringUtil.isBlank(op)) {
            return false;
        }
        int compared = parsed1.value().compareTo(parsed2.value());
        return switch (op.trim()) {
            case "=" -> compared == 0;
            case "!=" -> compared != 0;
            case ">=" -> compared >= 0;
            case ">" -> compared > 0;
            case "<=" -> compared <= 0;
            case "<" -> compared < 0;
            default -> false;
        };
    }

    /**
     * 날짜(또는 일시)에 일/주/월/년을 더하거나 뺍니다. 대소문자는 구분하지 않습니다.
     * 결과는 입력과 같은 형식(압축형/구분자형)으로 반환하고, 입력에 시각이 있으면 시각도 유지합니다.
     *
     * @param date 기준 날짜 문자열
     * @param op   연산자({@code "+"} 또는 {@code "-"})
     * @param n    더하거나 뺄 값
     * @param unit 단위({@code "D"}(일), {@code "W"}(주), {@code "M"}(월), {@code "Y"}(년)})
     * @return 연산 결과 문자열, 값이 없거나 지원하지 않는 연산자/단위이면 {@code null}
     */
    public static String eval(String date, String op, int n, String unit) {
        ParsedDateTime parsed = _parseDateTime(date);
        if (parsed == null || StringUtil.isBlank(op) || StringUtil.isBlank(unit)) {
            return null;
        }
        String action = op.trim();
        if (!"+".equals(action) && !"-".equals(action)) {
            return null;
        }
        LocalDateTime shifted = _shift(parsed.value(), "-".equals(action) ? -n : n, unit.trim());
        if (shifted == null) {
            return null;
        }
        return parsed.hasTime()
                ? _stringifyDateTime(shifted, parsed.dashed())
                : _stringifyDate(shifted.toLocalDate(), parsed.dashed());
    }

    /**
     * 해당 날짜가 속한 달의 마지막 날을 반환합니다. 결과는 입력과 같은 형식(압축형/구분자형)의 날짜입니다.
     *
     * @param date 기준 날짜 문자열
     * @return 그 달의 마지막 날, 값이 없으면 {@code null}
     */
    public static String eom(String date) {
        ParsedDateTime parsed = _parseDateTime(date);
        return parsed == null
                ? null
                : _stringifyDate(parsed.value().with(TemporalAdjusters.lastDayOfMonth()).toLocalDate(),
                        parsed.dashed());
    }

    /**
     * 파싱한 날짜/일시와 입력 형식 정보를 함께 담습니다.
     *
     * @param value   파싱한 일시
     * @param hasTime 입력에 시각이 있었는지 여부
     * @param dashed  입력이 구분자형이었는지 여부
     */
    private record ParsedDateTime(LocalDateTime value, boolean hasTime, boolean dashed) {
    }

    /**
     * 압축형/구분자형을 함께 읽을 수 있도록 {@code yyyy} 대신 {@code uuuu}를 사용하고, 잘못된 날짜(예: 2월 30일)를
     * 보정하지 않도록 {@link ResolverStyle#STRICT}를 적용한 포맷을 만듭니다.
     *
     * @param pattern 포맷 문자열
     * @return 포맷터
     */
    private static DateTimeFormatter _formatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * 공백을 제거한 문자열을 반환합니다.
     *
     * @param text 대상 문자열
     * @return 공백을 제거한 문자열, {@code null}/공백이면 {@code null}
     */
    private static String _trimToNull(String text) {
        return StringUtil.isBlank(text) ? null : text.trim();
    }

    /**
     * 포맷들을 순서대로 시도해 문자열을 읽습니다.
     *
     * @param text    대상 문자열
     * @param formats 순서대로 시도할 포맷들
     * @param parser  포맷 결과를 값으로 바꾸는 함수
     * @param <T>     값 타입
     * @return 읽은 값, 어떤 포맷으로도 읽을 수 없으면 {@code null}
     */
    private static <T> T _parseFormats(String text, List<DateTimeFormatter> formats,
            Function<TemporalAccessor, T> parser) {
        if (text == null) {
            return null;
        }
        for (DateTimeFormatter format : formats) {
            try {
                return parser.apply(format.parse(text));
            } catch (DateTimeParseException ignored) {
                // 다음 포맷으로 넘어갑니다.
            }
        }
        return null;
    }

    /**
     * 날짜 또는 일시 문자열을 읽습니다. 날짜만 있으면 그 날의 00:00:00으로 해석합니다.
     *
     * @param text 대상 문자열
     * @return 읽은 결과, 읽을 수 없으면 {@code null}
     */
    private static ParsedDateTime _parseDateTime(String text) {
        String source = _trimToNull(text);
        if (source == null) {
            return null;
        }
        LocalDateTime datetime = _parseFormats(source, DATETIME_FORMATS, LocalDateTime::from);
        if (datetime != null) {
            return new ParsedDateTime(datetime, true, source.contains("-"));
        }
        LocalDate date = _parseFormats(source, DATE_FORMATS, LocalDate::from);
        return date == null ? null : new ParsedDateTime(date.atStartOfDay(), false, source.contains("-"));
    }

    /**
     * 날짜를 문자열로 변환합니다.
     *
     * @param date   날짜
     * @param dashed {@code true}이면 구분자형({@code yyyy-MM-dd})
     * @return 날짜 문자열
     */
    private static String _stringifyDate(LocalDate date, boolean dashed) {
        return date == null ? null : (dashed ? DATE2 : DATE).format(date);
    }

    /**
     * 시각을 문자열로 변환합니다.
     *
     * @param time   시각
     * @param dashed {@code true}이면 구분자형({@code HH:mm:ss})
     * @return 시각 문자열
     */
    private static String _stringifyTime(LocalTime time, boolean dashed) {
        return time == null ? null : (dashed ? TIME2 : TIME).format(time);
    }

    /**
     * 일시를 문자열로 변환합니다.
     *
     * @param datetime 일시
     * @param dashed   {@code true}이면 구분자형({@code yyyy-MM-dd HH:mm:ss})
     * @return 일시 문자열
     */
    private static String _stringifyDateTime(LocalDateTime datetime, boolean dashed) {
        return datetime == null ? null : (dashed ? DATETIME2 : DATETIME).format(datetime);
    }

    /**
     * 일시를 단위만큼 이동합니다.
     *
     * @param datetime 일시
     * @param amount   이동할 값
     * @param unit     단위({@code "D"}, {@code "W"}, {@code "M"}, {@code "Y"}, 대소문자 구분 없음)
     * @return 이동한 일시, 지원하지 않는 단위이면 {@code null}
     */
    private static LocalDateTime _shift(LocalDateTime datetime, int amount, String unit) {
        return switch (unit.toUpperCase(Locale.ROOT)) {
            case "D" -> datetime.plusDays(amount);
            case "W" -> datetime.plusWeeks(amount);
            case "M" -> datetime.plusMonths(amount);
            case "Y" -> datetime.plusYears(amount);
            default -> null;
        };
    }
}
