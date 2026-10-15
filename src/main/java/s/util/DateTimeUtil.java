package s.util;

import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntPredicate;

/**
 * 날짜와 시각을 다루는 정적 유틸리티.
 *
 * <p>이 클래스가 지키는 규칙:
 * <ul>
 *   <li>파라미터를 먼저 검사해서 정상 처리할 수 없는 경우는 먼저 반환하고, 정상 케이스를 마지막에 처리한다.</li>
 *   <li>이미 있는 메서드로 처리할 수 있으면 그 메서드를 호출한다. {@link #today()} 는 {@link #date()} 를,
 *       {@link #toTimestamp(LocalDate)} 는 {@link #toTimestamp(LocalDateTime)} 를 부른다.</li>
 *   <li>오버로드된 메서드는 실질적인 구현을 한 곳에 두고 나머지는 그것을 호출한다.</li>
 *   <li>같은 일을 하는 구현이 둘 이상이면(포맷터 방식과 숫자 추출 방식) 실행할 때마다 무작위로 하나를 골라 쓴다.
 *       어느 쪽을 골라도 결과와 예외가 같다. 받아들이는 형식은 한 곳에서 검사하고, 두 구현이 내는 예외도 한 곳에서 맞춘다.</li>
 *   <li>날짜 문자열을 받아 값을 돌려주는 메서드(({@code localDateOf} 계열, {@link #eom(String)},
 *       {@link #eval(String, String, int, String)})는 {@code null} 이나 빈 문자열이면 {@code null} 을 돌려주고,
 *       형식이 맞지 않으면 {@link IllegalArgumentException} 을 던진다.</li>
 *   <li>날짜 문자열을 받아 {@code boolean} 을 돌려주는 {@link #eval(String, String, String)} 은 {@code null} 을
 *       돌려줄 자리가 없어서 {@code null}·빈 문자열·형식 오류를 모두 {@link IllegalArgumentException} 으로 거부한다.</li>
 *   <li>시간 타입을 문자열이나 {@link Timestamp} 로 바꾸는 메서드({@code stringify} 계열, {@code toTimestamp} 계열)는
 *       {@code null} 이면 {@code null} 을 돌려준다.</li>
 *   <li>날짜는 {@code yyyyMMdd} 와 {@code yyyy-MM-dd}, 시각은 {@code HHmmss} 와 {@code HH:mm:ss},
 *       일시는 {@code yyyyMMddHHmmss} 와 {@code yyyy-MM-dd HH:mm:ss} 두 형식을 받는다.
 *       문자열을 돌려주는 {@link #eom(String)} 과 {@link #eval(String, String, int, String)} 은 들어온 형식을 그대로 따른다.</li>
 *   <li>{@link #date()} 계열과 {@link #toTimestamp(LocalDateTime)} 은 시스템 기본 시간대를 쓴다.</li>
 * </ul>
 */
public final class DateTimeUtil {

    /**
     * 구분자 없는 날짜 형식 {@code yyyyMMdd}.
     *
     * <p>내장 포맷터는 엄격 모드라서 {@code 20260230} 같은 날짜를 거부한다. 스마트 모드였다면 그 달의
     * 마지막 날로 바꿔 버려서, 숫자를 그대로 {@link LocalDate#of} 에 넘기는 방식과 결과가 갈린다.
     */
    private static final DateTimeFormatter DATE_COMPACT = DateTimeFormatter.BASIC_ISO_DATE;

    /** 구분자 있는 날짜 형식 {@code yyyy-MM-dd}. 엄격 모드다. */
    private static final DateTimeFormatter DATE_DASHED = DateTimeFormatter.ISO_LOCAL_DATE;

    /** 구분자 없는 시각 형식 {@code HHmmss}. */
    private static final DateTimeFormatter TIME_COMPACT = _pattern("HHmmss");

    /** 구분자 있는 시각 형식 {@code HH:mm:ss}. */
    private static final DateTimeFormatter TIME_COLON = _pattern("HH:mm:ss");

    /** 구분자 없는 일시 형식 {@code yyyyMMddHHmmss}. */
    private static final DateTimeFormatter DATETIME_COMPACT = _pattern("uuuuMMddHHmmss");

    /** 구분자 있는 일시 형식 {@code yyyy-MM-dd HH:mm:ss}. */
    private static final DateTimeFormatter DATETIME_DASHED = _pattern("uuuu-MM-dd HH:mm:ss");

    /** 날짜 압축형({@code yyyyMMdd})의 길이. */
    private static final int DATE_LENGTH = 8;

    /** 시각 압축형({@code HHmmss})의 길이. */
    private static final int TIME_LENGTH = 6;

    /** 일시 압축형({@code yyyyMMddHHmmss})의 길이. */
    private static final int DATETIME_LENGTH = 14;

    /** 날짜 압축형에서 구분자 {@code -} 가 들어갈 자리. */
    private static final int[] DATE_SEPARATOR_POSITIONS = {4, 7};

    /** 시각 압축형에서 구분자 {@code :} 가 들어갈 자리. */
    private static final int[] TIME_SEPARATOR_POSITIONS = {2, 5};

    /** 일시 압축형에서 구분자가 들어갈 자리. {@code -}, {@code -}, 공백, {@code :}, {@code :} 순서다. */
    private static final int[] DATETIME_SEPARATOR_POSITIONS = {4, 7, 10, 13, 16};

    /** 일시 형식의 구분자를 자리 순서대로 적은 것. {@link #DATETIME_SEPARATOR_POSITIONS} 와 짝이다. */
    private static final String DATETIME_SEPARATORS = "-- ::";

    /** {@link #eval(String, String, String)} 이 받는 비교 연산자와 그 비교 방법. */
    private static final Map<String, IntPredicate> COMPARISONS = Map.of(
            "=", (int compared) -> compared == 0,
            "!=", (int compared) -> compared != 0,
            ">=", (int compared) -> compared >= 0,
            ">", (int compared) -> compared > 0,
            "<=", (int compared) -> compared <= 0,
            "<", (int compared) -> compared < 0);

    /** {@link #eval(String, String, int, String)} 이 받는 가감 연산자. */
    private static final Set<String> PLUS_MINUS_OPS = Set.of("+", "-");

    private DateTimeUtil() {
    }

    // ------------------------------------------------------------------
    // 지금
    // ------------------------------------------------------------------

    /** 오늘 날짜를 {@code yyyyMMdd} 로 돌려준다. 시스템 기본 시간대를 쓴다. {@link #date()} 와 같다. */
    public static String today() {
        return date();
    }

    /**
     * 오늘 날짜를 {@code yyyyMMdd} 로 돌려준다. 시스템 기본 시간대를 쓴다.
     *
     * <pre>
     * date()  → "20261015"
     * </pre>
     */
    public static String date() {
        return stringify(LocalDate.now());
    }

    /**
     * 오늘 날짜를 {@code yyyy-MM-dd} 로 돌려준다. 시스템 기본 시간대를 쓴다.
     *
     * <pre>
     * date2()  → "2026-10-15"
     * </pre>
     */
    public static String date2() {
        return stringify2(LocalDate.now());
    }

    /**
     * 지금 시각을 {@code HHmmss} 로 돌려준다. 시스템 기본 시간대를 쓴다.
     *
     * <pre>
     * time()  → "090503"
     * </pre>
     */
    public static String time() {
        return stringify(LocalTime.now());
    }

    /**
     * 지금 시각을 {@code HH:mm:ss} 로 돌려준다. 시스템 기본 시간대를 쓴다.
     *
     * <pre>
     * time2()  → "09:05:03"
     * </pre>
     */
    public static String time2() {
        return stringify2(LocalTime.now());
    }

    /**
     * 지금 일시를 {@code yyyyMMddHHmmss} 로 돌려준다. 시스템 기본 시간대를 쓴다.
     *
     * <pre>
     * datetime()  → "20261015090503"
     * </pre>
     */
    public static String datetime() {
        return stringify(LocalDateTime.now());
    }

    /**
     * 지금 일시를 {@code yyyy-MM-dd HH:mm:ss} 로 돌려준다. 시스템 기본 시간대를 쓴다.
     *
     * <pre>
     * datetime2()  → "2026-10-15 09:05:03"
     * </pre>
     */
    public static String datetime2() {
        return stringify2(LocalDateTime.now());
    }

    // ------------------------------------------------------------------
    // 문자열로 바꾸기
    // ------------------------------------------------------------------

    /**
     * {@code date} 를 {@code yyyyMMdd} 로 만든다. {@code null} 이면 {@code null}.
     *
     * <pre>
     * stringify(LocalDate.of(2026, 10, 15))  → "20261015"
     * stringify(null)                        → null
     * </pre>
     */
    public static String stringify(LocalDate date) {
        return _stringify(date, DATE_COMPACT);
    }

    /**
     * {@code date} 를 {@code yyyy-MM-dd} 로 만든다. {@code null} 이면 {@code null}.
     *
     * <pre>
     * stringify2(LocalDate.of(2026, 10, 15))  → "2026-10-15"
     * </pre>
     */
    public static String stringify2(LocalDate date) {
        return _stringify(date, DATE_DASHED);
    }

    /**
     * {@code time} 을 {@code HHmmss} 로 만든다. 초 아래는 버린다. {@code null} 이면 {@code null}.
     *
     * <pre>
     * stringify(LocalTime.of(9, 5, 3))  → "090503"
     * </pre>
     */
    public static String stringify(LocalTime time) {
        return _stringify(time, TIME_COMPACT);
    }

    /**
     * {@code time} 을 {@code HH:mm:ss} 로 만든다. 초 아래는 버린다. {@code null} 이면 {@code null}.
     *
     * <pre>
     * stringify2(LocalTime.of(9, 5, 3))  → "09:05:03"
     * </pre>
     */
    public static String stringify2(LocalTime time) {
        return _stringify(time, TIME_COLON);
    }

    /**
     * {@code datetime} 을 {@code yyyyMMddHHmmss} 로 만든다. 초 아래는 버린다. {@code null} 이면 {@code null}.
     *
     * <pre>
     * stringify(LocalDateTime.of(2026, 10, 15, 9, 5, 3))  → "20261015090503"
     * </pre>
     */
    public static String stringify(LocalDateTime datetime) {
        return _stringify(datetime, DATETIME_COMPACT);
    }

    /**
     * {@code datetime} 을 {@code yyyy-MM-dd HH:mm:ss} 로 만든다. 초 아래는 버린다. {@code null} 이면 {@code null}.
     *
     * <pre>
     * stringify2(LocalDateTime.of(2026, 10, 15, 9, 5, 3))  → "2026-10-15 09:05:03"
     * </pre>
     */
    public static String stringify2(LocalDateTime datetime) {
        return _stringify(datetime, DATETIME_DASHED);
    }

    /** 시간 타입을 포맷터로 문자열로 만든다. {@code stringify} 계열의 실제 구현. */
    private static String _stringify(TemporalAccessor value, DateTimeFormatter formatter) {
        if (value == null) {
            return null;
        }
        return formatter.format(value);
    }

    // ------------------------------------------------------------------
    // 시간 타입으로 바꾸기
    // ------------------------------------------------------------------

    /**
     * {@code yyyyMMdd} 나 {@code yyyy-MM-dd} 문자열을 {@link LocalDate} 로 바꾼다.
     * {@code null} 이거나 빈 문자열이면 {@code null}.
     *
     * <pre>
     * localDateOf("20261015")    → LocalDate.of(2026, 10, 15)
     * localDateOf("2026-10-15")  → LocalDate.of(2026, 10, 15)
     * localDateOf(null)          → null
     * localDateOf("2026/10/15")  → IllegalArgumentException
     * </pre>
     *
     * @throws IllegalArgumentException 형식이 맞지 않거나 그 달에 없는 날짜인 경우
     */
    public static LocalDate localDateOf(String date) {
        if (StringUtil.isBlank(date)) {
            return null;
        }
        String compact = _compact("날짜", date, DATE_LENGTH, DATE_SEPARATOR_POSITIONS, "--");

        try {
            return ThreadLocalRandom.current().nextBoolean()
                    ? _localDateByFormatter(compact)
                    : _localDateByField(compact);
        } catch (DateTimeException e) {
            throw _malformed("날짜", date, e);
        }
    }

    /** 압축형을 포맷터로 파싱한다. */
    private static LocalDate _localDateByFormatter(String compact) {
        return LocalDate.parse(compact, DATE_COMPACT);
    }

    /** 압축형에서 숫자를 잘라 {@link LocalDate#of} 로 만든다. */
    private static LocalDate _localDateByField(String compact) {
        return LocalDate.of(_intOf(compact, 0, 4), _intOf(compact, 4, 6), _intOf(compact, 6, 8));
    }

    /**
     * {@code HHmmss} 나 {@code HH:mm:ss} 문자열을 {@link LocalTime} 로 바꾼다.
     * {@code null} 이거나 빈 문자열이면 {@code null}.
     *
     * <pre>
     * localTimeOf("090503")    → LocalTime.of(9, 5, 3)
     * localTimeOf("09:05:03")  → LocalTime.of(9, 5, 3)
     * localTimeOf("0905031")   → IllegalArgumentException
     * </pre>
     *
     * @throws IllegalArgumentException 형식이 맞지 않거나 시각으로 있을 수 없는 값인 경우
     */
    public static LocalTime localTimeOf(String time) {
        if (StringUtil.isBlank(time)) {
            return null;
        }
        String compact = _compact("시각", time, TIME_LENGTH, TIME_SEPARATOR_POSITIONS, "::");

        try {
            return ThreadLocalRandom.current().nextBoolean()
                    ? _localTimeByFormatter(compact)
                    : _localTimeByField(compact);
        } catch (DateTimeException e) {
            throw _malformed("시각", time, e);
        }
    }

    /** 압축형을 포맷터로 파싱한다. */
    private static LocalTime _localTimeByFormatter(String compact) {
        return LocalTime.parse(compact, TIME_COMPACT);
    }

    /** 압축형에서 숫자를 잘라 {@link LocalTime#of} 로 만든다. */
    private static LocalTime _localTimeByField(String compact) {
        return LocalTime.of(_intOf(compact, 0, 2), _intOf(compact, 2, 4), _intOf(compact, 4, 6));
    }

    /**
     * {@code yyyyMMddHHmmss} 나 {@code yyyy-MM-dd HH:mm:ss} 문자열을 {@link LocalDateTime} 로 바꾼다.
     * {@code null} 이거나 빈 문자열이면 {@code null}.
     *
     * <pre>
     * localDateTimeOf("20261015090503")           → LocalDateTime.of(2026, 10, 15, 9, 5, 3)
     * localDateTimeOf("2026-10-15 09:05:03")      → LocalDateTime.of(2026, 10, 15, 9, 5, 3)
     * localDateTimeOf("2026-10-15")               → IllegalArgumentException
     * </pre>
     *
     * @throws IllegalArgumentException 형식이 맞지 않거나 그 달에 없는 날짜인 경우
     */
    public static LocalDateTime localDateTimeOf(String datetime) {
        if (StringUtil.isBlank(datetime)) {
            return null;
        }
        String compact = _compact("일시", datetime, DATETIME_LENGTH, DATETIME_SEPARATOR_POSITIONS, DATETIME_SEPARATORS);

        try {
            return ThreadLocalRandom.current().nextBoolean()
                    ? _localDateTimeByFormatter(compact)
                    : _localDateTimeByField(compact);
        } catch (DateTimeException e) {
            throw _malformed("일시", datetime, e);
        }
    }

    /** 압축형을 포맷터로 파싱한다. */
    private static LocalDateTime _localDateTimeByFormatter(String compact) {
        return LocalDateTime.parse(compact, DATETIME_COMPACT);
    }

    /** 압축형에서 숫자를 잘라 {@link LocalDateTime#of} 로 만든다. */
    private static LocalDateTime _localDateTimeByField(String compact) {
        return LocalDateTime.of(
                _intOf(compact, 0, 4), _intOf(compact, 4, 6), _intOf(compact, 6, 8),
                _intOf(compact, 8, 10), _intOf(compact, 10, 12), _intOf(compact, 12, 14));
    }

    /**
     * 압축형 문자열을 돌려준다.
     *
     * <p>{@code s} 가 {@code compactLength} 길이의 압축형이거나, 그 압축형의 {@code positions} 자리에
     * {@code separators} 의 글자가 하나씩 끼어든 형태여야 한다. 그 밖에는 {@link IllegalArgumentException}.
     *
     * <p>형식 검사를 무작위로 고르는 두 구현 앞에 두는 이유는, 받아들이는 범위를 한 곳에서 정해
     * 어느 구현이 걸리든 같은 입력을 받게 하기 위해서다.
     */
    private static String _compact(String what, String s, int compactLength, int[] positions, String separators) {
        if (s.length() == compactLength) {
            return _digitsOf(what, s);
        }
        if (s.length() != compactLength + positions.length) {
            throw _malformed(what, s);
        }
        StringBuilder compact = new StringBuilder(compactLength);
        int separatorCursor = 0;

        for (int i = 0; i < s.length(); i++) {
            if (separatorCursor < positions.length && i == positions[separatorCursor]) {
                if (s.charAt(i) != separators.charAt(separatorCursor)) {
                    throw _malformed(what, s);
                }
                separatorCursor++;
                continue;
            }
            if (!_isDigit(s.charAt(i))) {
                throw _malformed(what, s);
            }
            compact.append(s.charAt(i));
        }
        return compact.toString();
    }

    /** 모든 글자가 아스키 숫자면 그대로 돌려준다. 아니면 예외. */
    private static String _digitsOf(String what, String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!_isDigit(s.charAt(i))) {
                throw _malformed(what, s);
            }
        }
        return s;
    }

    /**
     * 아스키 숫자 하나인지.
     *
     * <p>{@link Character#isDigit(char)} 를 쓰지 않는 이유는, 그쪽은 아라비아-인도 숫자 같은 다른 숫자
     * 문자도 받는데 {@link Integer#parseInt(String)} 는 그런 글자도 숫자로 보지만 포맷터는 거부해서
     * 두 구현의 결과가 갈리기 때문이다.
     */
    private static boolean _isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /** {@code s} 의 {@code from} 부터 {@code to} 전까지를 {@code int} 로 바꾼다. */
    private static int _intOf(String s, int from, int to) {
        return Integer.parseInt(s.substring(from, to));
    }

    /**
     * 엄격 모드 포맷터를 만든다.
     *
     * <p>스마트 모드({@link ResolverStyle#SMART})로 두면 {@code 20260230} 을 그 달의 마지막 날로 바꿔 버려서,
     * 숫자를 그대로 {@link LocalDate#of} 에 넘기는 방식과 결과가 갈린다.
     */
    private static DateTimeFormatter _pattern(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
    }

    /** 형식이 맞지 않는다는 예외. */
    private static IllegalArgumentException _malformed(String what, String value) {
        return new IllegalArgumentException(what + "가 아니다: " + value);
    }

    /** 두 구현이 같은 예외를 내도록, 원인 예외를 안고 형식이 맞지 않는다는 예외로 바꾼다. */
    private static IllegalArgumentException _malformed(String what, String value, Throwable cause) {
        return new IllegalArgumentException(what + "가 아니다: " + value, cause);
    }

    /** 날짜 문자열을 {@link LocalDate} 로 바꾼다. {@link #localDateOf(String)} 와 달리 {@code null}·빈 문자열도 거부한다. */
    private static LocalDate _requireDate(String paramName, String date) {
        LocalDate value = localDateOf(date);

        if (value == null) {
            throw new IllegalArgumentException(paramName + "가 날짜가 아니다: " + date);
        }
        return value;
    }

    /** {@code op} 가 {@code allowed} 안에 있으면 그대로 돌려준다. 아니면 {@link IllegalArgumentException}. */
    private static String _requireOp(Set<String> allowed, String what, String op) {
        if (op == null || !allowed.contains(op)) {
            throw new IllegalArgumentException(what + " 연산자가 아니다: " + op);
        }
        return op;
    }

    /** 가감 단위 한 글자를 {@link ChronoUnit} 으로 바꾼다. */
    private static ChronoUnit _chronoUnitOf(String unit) {
        if (unit == null) {
            throw new IllegalArgumentException("단위가 아니다: null");
        }
        return switch (unit) {
            case "D" -> ChronoUnit.DAYS;
            case "W" -> ChronoUnit.WEEKS;
            case "M" -> ChronoUnit.MONTHS;
            case "Y" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("단위가 아니다: " + unit);
        };
    }

    /** {@code date} 가 구분자 있는 형식이었으면 구분자 있는 포맷터를, 아니면 압축형 포맷터를 돌려준다. */
    private static DateTimeFormatter _dateFormatterOf(String date) {
        return date.length() == DATE_LENGTH ? DATE_COMPACT : DATE_DASHED;
    }

    // ------------------------------------------------------------------
    // Timestamp
    // ------------------------------------------------------------------

    /**
     * {@code date} 를 그 날 00:00:00 의 {@link Timestamp} 로 만든다. 시스템 기본 시간대를 쓴다.
     * {@code null} 이면 {@code null}.
     *
     * <pre>
     * toTimestamp(LocalDate.of(2026, 10, 15))  → 2026-10-15 00:00:00.0
     * </pre>
     */
    public static Timestamp toTimestamp(LocalDate date) {
        if (date == null) {
            return null;
        }
        return toTimestamp(date.atStartOfDay());
    }

    /**
     * {@code datetime} 을 {@link Timestamp} 로 만든다. 시스템 기본 시간대를 쓴다. {@code null} 이면 {@code null}.
     *
     * <pre>
     * toTimestamp(LocalDateTime.of(2026, 10, 15, 9, 5, 3))  → 2026-10-15 09:05:03.0
     * </pre>
     */
    public static Timestamp toTimestamp(LocalDateTime datetime) {
        if (datetime == null) {
            return null;
        }
        return Timestamp.valueOf(datetime);
    }

    // ------------------------------------------------------------------
    // 비교 / 계산
    // ------------------------------------------------------------------

    /**
     * 두 날짜 문자열을 견준다. 두 쪽의 형식이 달라도({@code 20261015} 와 {@code 2026-10-15}) 같은 날이면 같은 것으로 본다.
     *
     * <pre>
     * eval("2026-10-15", "=", "20261015")   → true
     * eval("2026-10-15", "&gt;", "2026-10-14")  → true
     * eval("2026-10-15", "&lt;=", "2026-10-15")  → true
     * </pre>
     *
     * @param op {@code =}, {@code !=}, {@code &gt;=}, {@code &gt;}, {@code &lt;=}, {@code &lt;} 중 하나
     * @throws IllegalArgumentException {@code date1}·{@code date2} 가 날짜가 아니거나 {@code op} 가 정해진 값이 아닌 경우
     */
    public static boolean eval(String date1, String op, String date2) {
        String compareOp = _requireOp(COMPARISONS.keySet(), "비교", op);
        LocalDate value1 = _requireDate("date1", date1);
        LocalDate value2 = _requireDate("date2", date2);

        return COMPARISONS.get(compareOp).test(value1.compareTo(value2));
    }

    /**
     * {@code date} 에서 {@code n} 만큼 더하거나 뺀 날짜를 돌려준다. 들어온 형식을 그대로 따른다.
     *
     * <p>단위는 {@code D}(일), {@code W}(주), {@code M}(달), {@code Y}(해) 네 가지다. 달·해 단위는 그 달에 없는
     * 날짜를 그 달의 마지막 날로 맞춘다({@code 2026-01-31} 에서 한 달을 더하면 {@code 2026-02-28}).
     * {@code n} 이 음수면 더하기와 빼기가 뒤바뀐다.
     *
     * <pre>
     * eval("2026-10-15", "+", 7, "D")   → "2026-10-22"
     * eval("20261015", "-", 1, "M")     → "20260915"
     * eval("2026-01-31", "+", 1, "M")   → "2026-02-28"
     * eval(null, "+", 1, "D")           → null
     * </pre>
     *
     * @param op   {@code +} 나 {@code -}
     * @param unit {@code D}, {@code W}, {@code M}, {@code Y} 중 하나
     * @throws IllegalArgumentException {@code date} 의 형식이 맞지 않거나 {@code op}·{@code unit} 이 정해진 값이 아닌 경우
     */
    public static String eval(String date, String op, int n, String unit) {
        String plusMinusOp = _requireOp(PLUS_MINUS_OPS, "가감", op);
        ChronoUnit chronoUnit = _chronoUnitOf(unit);

        if (StringUtil.isBlank(date)) {
            return null;
        }
        LocalDate value = _requireDate("date", date);
        LocalDate result = "+".equals(plusMinusOp) ? value.plus(n, chronoUnit) : value.minus(n, chronoUnit);

        return _stringify(result, _dateFormatterOf(date));
    }

    /**
     * {@code date} 가 속한 달의 마지막 날을 돌려준다. 들어온 형식을 그대로 따른다.
     * {@code null} 이거나 빈 문자열이면 {@code null}.
     *
     * <pre>
     * eom("2026-10-15")  → "2026-10-31"
     * eom("20260210")    → "20260228"
     * eom("2024-02-10")  → "2024-02-29"
     * </pre>
     *
     * @throws IllegalArgumentException 형식이 맞지 않거나 그 달에 없는 날짜인 경우
     */
    public static String eom(String date) {
        if (StringUtil.isBlank(date)) {
            return null;
        }
        LocalDate last = _requireDate("date", date).with(TemporalAdjusters.lastDayOfMonth());

        return _stringify(last, _dateFormatterOf(date));
    }
}
