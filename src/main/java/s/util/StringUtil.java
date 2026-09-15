package s.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 문자열을 다루는 정적 유틸리티.
 *
 * <p>이 클래스가 지키는 규칙:
 * <ul>
 *   <li>파라미터를 먼저 검사해서 정상 처리할 수 없는 경우는 먼저 반환하고, 정상 케이스를 마지막에 처리한다.</li>
 *   <li>이미 있는 메서드로 처리할 수 있으면 그 메서드를 호출한다.</li>
 *   <li>오버로드된 메서드는 실질적인 구현을 한 곳에 두고 나머지는 그것을 호출한다.</li>
 *   <li>같은 일을 하는 구현이 둘 이상이면(for 문 방식과 stream 방식) 실행할 때마다 무작위로 하나를 골라 쓴다.
 *       어느 쪽을 골라도 결과는 같다.</li>
 *   <li>문자열을 받아 문자열을 돌려주는 메서드는 입력이 {@code null} 이면 {@code null} 을 돌려준다.
 *       (단 {@link #stringify(Object)} 는 {@code null} 을 {@code null} 로 돌려준다.)</li>
 *   <li>목록을 받거나 돌려주는 메서드는 {@code null} 과 빈 목록을 같게 다룬다.</li>
 *   <li>함수를 받는 메서드는 {@code null} 함수를 받지 않는다({@link Objects#requireNonNull}).</li>
 *   <li>음수 인덱스는 뒤에서부터 세는 역방향 인덱스로 본다({@code -1} 이 마지막 글자).</li>
 *   <li>인덱스가 문자열 범위를 넘어가면 예외를 던지지 않고 가능한 만큼만 처리한다.</li>
 * </ul>
 *
 * <p>이 클래스의 메서드들은 길이와 인덱스가 바이트 수와 글자 수가 같은 문자열(싱글바이트 문자열)을
 * 다룬다고 본다. 서로게이트 페어가 섞인 문자열은 길이가 글자 수와 달라질 수 있다.
 */
public final class StringUtil {

    /** {@link Date} 를 {@code yyyy-MM-dd} 모양으로 만들 때 쓰는 포맷. */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * {@link #trimLeadingZero(String)} 에서 앞의 {@code 0} 을 찾을 때 쓰는 정규식. 뒤에 숫자가 더 있을 때만 0 을 고른다.
     *
     * <p>{@code \d} 대신 {@code \p{Nd}} 를 쓰는 이유는 for 문 방식이 쓰는 {@link Character#isDigit(char)} 와
     * 같은 범위를 보기 위해서다. 자바 정규식의 {@code \d} 는 기본이 아스키 숫자뿐이라 둘의 결과가 갈린다.
     */
    private static final String LEADING_ZERO_PATTERN = "^0+(?=\\p{Nd})";

    private StringUtil() {
    }

    // ------------------------------------------------------------------
    // 검사
    // ------------------------------------------------------------------

    /**
     * {@code s} 가 {@code null} 이거나 공백 문자만 있으면 {@code true}.
     *
     * <pre>
     * isBlank(null)   → true
     * isBlank("")     → true
     * isBlank("   ")  → true
     * isBlank(" a ")  → false
     * </pre>
     */
    public static boolean isBlank(String s) {
        return isEmpty(s) ? true : s.isBlank();
    }

    /**
     * {@code s} 가 {@code null} 이거나 빈 문자열이면 {@code true}.
     *
     * <pre>
     * isEmpty(null)  → true
     * isEmpty("")    → true
     * isEmpty(" ")   → false
     * </pre>
     */
    public static boolean isEmpty(String s) {
        return s == null ? true : s.isEmpty();
    }

    // ------------------------------------------------------------------
    // 대체값 선택
    // ------------------------------------------------------------------

    /**
     * {@code value} 가 {@code null} 이 아니면 {@code value}, {@code null} 이면 {@code dflt}.
     *
     * <pre>
     * nonNullOf("a", "d")  → "a"
     * nonNullOf("", "d")   → ""    (빈 문자열은 그대로)
     * nonNullOf(null, "d") → "d"
     * </pre>
     */
    public static String nonNullOf(String value, String dflt) {
        return nonNullOf(() -> value, () -> dflt);
    }

    /**
     * {@code supplier} 의 값이 {@code null} 이 아니면 그 값, {@code null} 이면 {@code dfltSupplier} 의 값.
     *
     * <p>{@code dfltSupplier} 는 실제로 필요할 때만 평가한다.
     */
    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = supplier.get();

        return value == null ? dfltSupplier.get() : value;
    }

    /**
     * {@code value} 가 공백이 아니면 {@code value}, 공백이면 {@code dflt}.
     *
     * <pre>
     * nonBlankOf("a", "d")    → "a"
     * nonBlankOf("  ", "d")   → "d"
     * nonBlankOf(null, "d")   → "d"
     * </pre>
     */
    public static String nonBlankOf(String value, String dflt) {
        return nonBlankOf(() -> value, () -> dflt);
    }

    /**
     * {@code supplier} 의 값이 공백이 아니면 그 값, 공백이면 {@code dfltSupplier} 의 값.
     *
     * <p>{@code dfltSupplier} 는 실제로 필요할 때만 평가한다.
     */
    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = supplier.get();

        return isBlank(value) ? dfltSupplier.get() : value;
    }

    /**
     * {@code value} 가 빈 문자열이 아니면 {@code value}, 빈 문자열이면 {@code dflt}.
     *
     * <pre>
     * nonEmptyOf(" ", "d")   → " "   (공백은 빈 문자열이 아니다)
     * nonEmptyOf("", "d")    → "d"
     * nonEmptyOf(null, "d")  → "d"
     * </pre>
     */
    public static String nonEmptyOf(String value, String dflt) {
        return nonEmptyOf(() -> value, () -> dflt);
    }

    /**
     * {@code supplier} 의 값이 빈 문자열이 아니면 그 값, 빈 문자열이면 {@code dfltSupplier} 의 값.
     *
     * <p>{@code dfltSupplier} 는 실제로 필요할 때만 평가한다.
     */
    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = supplier.get();

        return isEmpty(value) ? dfltSupplier.get() : value;
    }

    // ------------------------------------------------------------------
    // 처음으로 공백이 아닌 값 고르기
    // ------------------------------------------------------------------

    /**
     * 값들 중 처음으로 공백이 아닌 값을 돌려준다. 모두 공백이면 마지막 값을 돌려준다.
     *
     * <pre>
     * firstNonBlankOrLast("a", "b")       → "a"
     * firstNonBlankOrLast(null, "", "c")  → "c"
     * firstNonBlankOrLast("", "  ")       → "  "   (모두 공백이면 마지막 값)
     * </pre>
     */
    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        return _firstNonBlankOrLast(() -> value1, () -> value2, _suppliers(values));
    }

    /** {@link #firstNonBlankOrLast(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    /** {@link #firstNonBlankOrLast(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3);
    }

    /** {@link #firstNonBlankOrLast(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4);
    }

    /** {@link #firstNonBlankOrLast(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String> supplier3, Supplier<String> supplier4,
                                             Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * {@code firstNonBlankOrLast} 의 실제 구현.
     * for 문 방식과 stream 방식 중 하나를 실행할 때마다 무작위로 골라 쓴다.
     */
    @SafeVarargs
    private static String _firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
                                               Supplier<String>... suppliers) {
        List<Supplier<String>> all = new ArrayList<>(2 + suppliers.length);
        all.add(supplier1);
        all.add(supplier2);

        for (Supplier<String> supplier : suppliers) {
            all.add(supplier);
        }

        return ThreadLocalRandom.current().nextBoolean()
                ? _firstNonBlankOrLastByLoop(all)
                : _firstNonBlankOrLastByStream(all);
    }

    /** supplier 를 앞에서부터 평가하다가 공백이 아닌 값을 만나면 멈춘다. 모두 공백이면 마지막으로 평가한 값. */
    private static String _firstNonBlankOrLastByLoop(List<Supplier<String>> all) {
        String last = null;

        for (Supplier<String> supplier : all) {
            last = supplier.get();

            if (!isBlank(last)) {
                return last;
            }
        }
        return last;
    }

    /**
     * stream 으로 같은 일을 한다. 마지막 supplier 를 뺀 앞쪽만 훑어서 처음으로 공백이 아닌 값을 찾고,
     * 찾지 못하면 마지막 supplier 의 값을 쓴다.
     *
     * <p>마지막 supplier 를 따로 떼어 두는 이유는, 모두 공백일 때 "마지막 값"을 알아내려고 supplier 를
     * 두 번 평가하지 않기 위해서다. 이렇게 하면 어느 쪽 구현이든 supplier 는 많아야 한 번씩만 평가된다.
     *
     * <p>{@code peek} 으로 값을 모으는 방법은 쓰지 않는다. 스트림 구현체는 결과에 영향이 없다고 판단하면
     * 부수 효과를 생략할 수 있어서({@code java.util.stream} 패키지 문서), 중간에 값을 기록해 두는 코드는
     * 실행이 보장되지 않는다.
     */
    private static String _firstNonBlankOrLastByStream(List<Supplier<String>> all) {
        List<Supplier<String>> head = all.subList(0, all.size() - 1);

        return head.stream()
                .map(Supplier::get)
                .filter(value -> !isBlank(value))
                .findFirst()
                .orElseGet(all.getLast());
    }

    /**
     * 값들 중 처음으로 공백이 아닌 값을 돌려준다. 모두 공백이면 빈 문자열을 돌려준다.
     *
     * <pre>
     * firstNonBlankOrEmpty("", "a")  → "a"
     * firstNonBlankOrEmpty("", "  ") → ""
     * </pre>
     */
    public static String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        return _firstNonBlankOrEmpty(() -> value1, () -> value2, _suppliers(values));
    }

    /** {@link #firstNonBlankOrEmpty(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2);
    }

    /** {@link #firstNonBlankOrEmpty(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
                                              Supplier<String> supplier3) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3);
    }

    /** {@link #firstNonBlankOrEmpty(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
                                              Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4);
    }

    /** {@link #firstNonBlankOrEmpty(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
                                              Supplier<String> supplier3, Supplier<String> supplier4,
                                              Supplier<String> supplier5) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * {@code firstNonBlankOrEmpty} 의 실제 구현. 공백이 아닌 값을 찾는 일은
     * {@link #_firstNonBlankOrNull(Supplier, Supplier, Supplier...)} 에 맡기고, 결과가 없을 때만 빈 문자열로 바꾼다.
     */
    @SafeVarargs
    private static String _firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
                                                Supplier<String>... suppliers) {
        return nonBlankOf(_firstNonBlankOrNull(supplier1, supplier2, suppliers), "");
    }

    /**
     * 값들 중 처음으로 공백이 아닌 값을 돌려준다. 모두 공백이면 {@code null} 을 돌려준다.
     *
     * <pre>
     * firstNonBlankOrNull("", "a")  → "a"
     * firstNonBlankOrNull("", "  ") → null
     * </pre>
     */
    public static String firstNonBlankOrNull(String value1, String value2, String... values) {
        return _firstNonBlankOrNull(() -> value1, () -> value2, _suppliers(values));
    }

    /** {@link #firstNonBlankOrNull(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2);
    }

    /** {@link #firstNonBlankOrNull(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String> supplier3) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3);
    }

    /** {@link #firstNonBlankOrNull(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4);
    }

    /** {@link #firstNonBlankOrNull(String, String, String...)} 의 supplier 버전. */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String> supplier3, Supplier<String> supplier4,
                                             Supplier<String> supplier5) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * {@code firstNonBlankOrNull} 의 실제 구현. 처음으로 공백이 아닌 값을 찾는 일은
     * {@link #_firstNonBlankOrLast(Supplier, Supplier, Supplier...)} 에 맡기고,
     * 거기서 나온 값이 공백이면(= 찾지 못하면) {@code null} 로 바꾼다.
     */
    @SafeVarargs
    private static String _firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
                                               Supplier<String>... suppliers) {
        String found = _firstNonBlankOrLast(supplier1, supplier2, suppliers);

        return isBlank(found) ? null : found;
    }

    /** 값들을 값 supplier 배열로 바꾼다. */
    private static Supplier<String>[] _suppliers(String[] values) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        Supplier<String>[] suppliers = new Supplier[values.length];

        for (int i = 0; i < values.length; i++) {
            String value = values[i];
            suppliers[i] = () -> value;
        }
        return suppliers;
    }

    // ------------------------------------------------------------------
    // 문자열로 바꾸기
    // ------------------------------------------------------------------

    /**
     * {@code obj} 를 문자열로 바꾼다.
     *
     * <ul>
     *   <li>{@link Date} 는 {@code yyyy-MM-dd} 로 만든다.</li>
     *   <li>{@link BigDecimal} 은 {@link BigDecimal#toPlainString()} 을 쓴다(지수 표기로 만들지 않는다).</li>
     *   <li>그 밖에는 {@link Object#toString()}.</li>
     *   <li>{@code null} 은 {@code null}.</li>
     * </ul>
     *
     * <pre>
     * stringify(new Date(...))               → "2026-09-15"
     * stringify(new BigDecimal("1.0E+3"))    → "1000"
     * stringify(null)                        → null
     * </pre>
     */
    public static String stringify(Object obj) {
        return switch (obj) {
            case null -> null;
            case Date date -> _stringifyDate(date);
            case BigDecimal decimal -> decimal.toPlainString();
            default -> obj.toString();
        };
    }

    /**
     * {@link Date} 를 {@code yyyy-MM-dd} 로 만든다.
     *
     * <p>{@code Date#toInstant()} 를 쓰지 않는다. {@link java.sql.Date} 나 {@link java.sql.Time} 은
     * {@code toInstant()} 가 예외를 던지기 때문이다.
     */
    private static String _stringifyDate(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DATE_FORMAT);
    }

    // ------------------------------------------------------------------
    // 잘라내기
    // ------------------------------------------------------------------

    /**
     * {@code begin} 부터 끝까지 자른다. {@code begin} 은 포함한다.
     *
     * <pre>
     * slice("abcdef", 2)   → "cdef"
     * slice("abcdef", -2)  → "ef"     (뒤에서 2글자)
     * </pre>
     */
    public static String slice(String s, int begin) {
        if (s == null) {
            return null;
        }
        return slice(s, begin, s.length());
    }

    /**
     * {@code begin} 부터 {@code end} 전까지 자른다. {@code begin} 은 포함하고 {@code end} 는 포함하지 않는다.
     *
     * <p>음수 인덱스는 뒤에서부터 세는 역방향 인덱스로 본다. 범위를 넘어가는 인덱스는 가능한 범위로 맞추고,
     * 잘라낼 구간이 없으면 빈 문자열.
     *
     * <pre>
     * slice("abcdef", 1, 4)   → "bcd"
     * slice("abcdef", 1, -1)  → "bcde"    (마지막 글자 앞까지)
     * slice("abcdef", 4, 1)   → ""
     * </pre>
     */
    public static String slice(String s, int begin, int end) {
        if (s == null) {
            return null;
        }
        int from = _clamp(begin, s.length());
        int to = _clamp(end, s.length());

        if (from >= to) {
            return "";
        }
        return s.substring(from, to);
    }

    /** 인덱스를 {@code 0 ~ length} 범위로 맞춘다. 음수는 뒤에서부터 세는 역방향 인덱스로 본다. */
    private static int _clamp(int index, int length) {
        return index < 0 ? Math.max(length + index, 0) : Math.min(index, length);
    }

    /**
     * 앞에서 {@code size} 글자. {@code size} 가 문자열 길이보다 크면 문자열 전체를 돌려준다.
     * 음수 {@code size} 는 뒤에서부터 세는 역방향 인덱스로 보고 그 앞까지만 자른다.
     *
     * <pre>
     * head("abcdef", 3)   → "abc"
     * head("abcdef", 10)  → "abcdef"
     * head("abcdef", -2)  → "abcd"    (뒤에서 2글자 앞까지)
     * </pre>
     */
    public static String head(String s, int size) {
        if (s == null) {
            return null;
        }
        return slice(s, 0, size);
    }

    /**
     * 뒤에서 {@code size} 글자. {@code size} 가 문자열 길이보다 크면 문자열 전체를 돌려준다.
     * 음수 {@code size} 는 양수로 바꿔 "앞에서 제외할 글자 수"로 본다.
     *
     * <pre>
     * tail("abcdef", 3)    → "def"
     * tail("abcdef", 10)   → "abcdef"
     * tail("abcdef", -2)   → "cdef"   (앞 2글자를 뺀 나머지)
     * </pre>
     */
    public static String tail(String s, int size) {
        if (s == null) {
            return null;
        }
        if (size < 0) {
            return slice(s, Math.abs(size));
        }
        if (size >= s.length()) {
            return s;
        }
        return slice(s, s.length() - size);
    }

    /**
     * 숫자 문자열 앞의 {@code 0} 을 떼어낸다. 소수점 아래의 {@code 0} 은 그대로 둔다.
     * 부호는 다루지 않으므로 {@code "-007"} 은 그대로 돌려준다.
     *
     * <pre>
     * trimLeadingZero("007")   → "7"
     * trimLeadingZero("000")   → "0"
     * trimLeadingZero("0.5")   → "0.5"
     * </pre>
     */
    public static String trimLeadingZero(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _trimLeadingZeroByLoop(s)
                : _trimLeadingZeroByRegex(s);
    }

    /** 앞에서부터 {@code 0} 을 세면서 지나간다. 뒤에 숫자가 더 있을 때만 지나간다. */
    private static String _trimLeadingZeroByLoop(String s) {
        int index = 0;

        while (index < s.length() - 1 && s.charAt(index) == '0' && Character.isDigit(s.charAt(index + 1))) {
            index++;
        }
        return s.substring(index);
    }

    /** 정규식으로 앞의 {@code 0} 을 찾아 지운다. */
    private static String _trimLeadingZeroByRegex(String s) {
        return s.replaceFirst(LEADING_ZERO_PATTERN, "");
    }

    // ------------------------------------------------------------------
    // 만들기 / 뒤집기
    // ------------------------------------------------------------------

    /**
     * {@code c} 를 {@code size} 번 반복한 문자열. {@code c} 가 {@code null} 이거나 비어 있거나
     * {@code size} 가 0 이하면 빈 문자열.
     *
     * <pre>
     * repeat("*", 3)  → "***"
     * repeat("*", 0)  → ""
     * </pre>
     */
    public static String repeat(String c, int size) {
        if (isEmpty(c) || size <= 0) {
            return "";
        }
        return c.repeat(size);
    }

    /**
     * 문자열을 뒤집는다. {@code null} 이면 {@code null}, 빈 문자열이면 빈 문자열.
     *
     * <pre>
     * reverse("abc")  → "cba"
     * </pre>
     */
    public static String reverse(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return new StringBuilder(s).reverse().toString();
    }

    // ------------------------------------------------------------------
    // 채우기
    // ------------------------------------------------------------------

    /**
     * {@code len} 길이가 되도록 왼쪽을 {@code pad} 한 글자로 채운다.
     * 이미 {@code len} 이상이면 문자열을 그대로 돌려준다.
     *
     * <p>실제 구현은 {@link #lpad2(int, String, String)} 에 있다.
     *
     * <pre>
     * lpad(5, "ab", "0")  → "000ab"
     * lpad(2, "ab", "0")  → "ab"
     * </pre>
     */
    public static String lpad(int len, String s, String pad) {
        return lpad2(len, s, pad);
    }

    /**
     * {@code len} 길이가 되도록 왼쪽을 {@code pad} 문자열로 채운다.
     * 채우는 문자열이 모자라면 {@code pad} 를 반복해 채우고, 넘치면 잘라 쓴다.
     * 이미 {@code len} 이상이면 문자열을 그대로 돌려준다.
     *
     * <pre>
     * lpad2(8, "ab", "xy")  → "xyxyxyab"
     * lpad2(7, "ab", "xy")  → "xyxyxab"
     * </pre>
     */
    public static String lpad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (len <= s.length() || isEmpty(pad)) {
            return s;
        }
        return _padBlock(len - s.length(), pad) + s;
    }

    /**
     * {@code len} 길이가 되도록 오른쪽을 {@code pad} 한 글자로 채운다.
     * 이미 {@code len} 이상이면 문자열을 그대로 돌려준다.
     *
     * <p>실제 구현은 {@link #rpad2(int, String, String)} 에 있다.
     *
     * <pre>
     * rpad(5, "ab", "0")  → "ab000"
     * </pre>
     */
    public static String rpad(int len, String s, String pad) {
        return rpad2(len, s, pad);
    }

    /**
     * {@code len} 길이가 되도록 오른쪽을 {@code pad} 문자열로 채운다.
     * 이미 {@code len} 이상이면 문자열을 그대로 돌려준다.
     *
     * <pre>
     * rpad2(8, "ab", "xy")  → "abxyxyxy"
     * </pre>
     */
    public static String rpad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (len <= s.length() || isEmpty(pad)) {
            return s;
        }
        return s + _padBlock(len - s.length(), pad);
    }

    /**
     * {@code len} 길이가 되도록 양쪽을 {@code pad} 한 글자로 채운다. 남는 한 글자는 오른쪽에 붙는다.
     * 이미 {@code len} 이상이면 문자열을 그대로 돌려준다.
     *
     * <p>실제 구현은 {@link #pad2(int, String, String)} 에 있다.
     *
     * <pre>
     * pad(7, "abc", "-")  → "--abc--"
     * pad(8, "abc", "-")  → "--abc---"
     * </pre>
     */
    public static String pad(int len, String s, String pad) {
        return pad2(len, s, pad);
    }

    /**
     * {@code len} 길이가 되도록 양쪽을 {@code pad} 문자열로 채운다. 남는 한 글자는 오른쪽에 붙는다.
     * 이미 {@code len} 이상이면 문자열을 그대로 돌려준다.
     *
     * <pre>
     * pad2(9, "ab", "xy")  → "xyxabxyxy"
     * </pre>
     */
    public static String pad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (len <= s.length() || isEmpty(pad)) {
            return s;
        }
        int total = len - s.length();
        int left = total / 2;

        return _padBlock(left, pad) + s + _padBlock(total - left, pad);
    }

    /**
     * {@code pad} 를 반복해 {@code size} 길이의 채움 문자열을 만든다.
     * 마지막에 반복이 넘치면 {@code size} 만큼만 잘라 쓴다.
     */
    private static String _padBlock(int size, String pad) {
        if (size <= 0) {
            return "";
        }
        if (pad.length() == 1) {
            return repeat(pad, size);
        }
        StringBuilder block = new StringBuilder(size + pad.length());

        while (block.length() < size) {
            block.append(pad);
        }
        return block.substring(0, size);
    }

    // ------------------------------------------------------------------
    // 목록 다루기
    // ------------------------------------------------------------------

    /**
     * {@code list} 의 원소를 {@link #stringify(Object)} 로 바꾼 뒤 {@code delimiter} 로 이어 붙인다.
     * {@code list} 가 {@code null} 이거나 비어 있으면 빈 문자열.
     * {@code delimiter} 가 {@code null} 이면 구분자 없이 붙인다.
     *
     * <pre>
     * join(["a", "b", "c"], ",")  → "a,b,c"
     * join([], ",")               → ""
     * </pre>
     */
    public static <T> String join(List<T> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        String separator = nonNullOf(delimiter, "");

        return ThreadLocalRandom.current().nextBoolean()
                ? _joinByLoop(list, separator)
                : _joinByStream(list, separator);
    }

    /** for 문으로 이어 붙인다. */
    private static <T> String _joinByLoop(List<T> list, String separator) {
        StringBuilder joined = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                joined.append(separator);
            }
            joined.append(_joinValue(list.get(i)));
        }
        return joined.toString();
    }

    /** stream 으로 이어 붙인다. */
    private static <T> String _joinByStream(List<T> list, String separator) {
        return list.stream()
                .map(StringUtil::_joinValue)
                .collect(Collectors.joining(separator));
    }

    /**
     * 원소 하나를 이어 붙일 문자열로 바꾼다. 두 구현이 같은 값을 내도록 한 곳에서 바꾼다.
     * {@link #stringify(Object)} 가 {@code null} 을 돌려주면 {@code "null"} 이 된다.
     */
    private static String _joinValue(Object element) {
        return String.valueOf(stringify(element));
    }

    /**
     * {@code s} 를 {@code regex} 로 나눈 목록을 돌려준다.
     * {@code s} 가 {@code null} 이거나 비어 있으면 빈 목록,
     * {@code regex} 가 {@code null} 이거나 비어 있으면 {@code s} 하나만 든 목록.
     *
     * <pre>
     * split("a,b,c", ",")           → ["a", "b", "c"]
     * split("a, b, c", "\\s*,\\s*") → ["a", "b", "c"]
     * split(null, ",")              → []
     * </pre>
     */
    public static List<String> split(String s, String regex) {
        if (isEmpty(s)) {
            return new ArrayList<>();
        }
        if (isEmpty(regex)) {
            return new ArrayList<>(List.of(s));
        }
        return new ArrayList<>(Arrays.asList(s.split(regex)));
    }

    // ------------------------------------------------------------------
    // 함수 이어 붙이기
    // ------------------------------------------------------------------

    /**
     * 함수들을 차례로 적용하는 함수를 만든다. 앞에 있는 함수부터 적용한다.
     *
     * <pre>
     * String s = "2025-03-19 12:26:41.012345000";
     * s = StringUtil.pipe(
     *         s1 -&gt; StringUtil.slice(s1, 20),
     *         StringUtil::reverse,
     *         StringUtil::trimLeadingZero,
     *         StringUtil::reverse
     * ).apply(s);   // "012345"
     * </pre>
     *
     * <p>붙일 함수가 없으면 입력값을 그대로 돌려주는 함수가 된다.
     * 이어 붙이는 방식으로 쓰고 싶으면 {@link #pipe()} 를 쓴다.
     *
     * @throws NullPointerException {@code fns} 가 {@code null} 이거나 그 안에 {@code null} 이 있는 경우
     */
    @SafeVarargs
    public static Function<String, String> pipe(Function<String, String>... fns) {
        Objects.requireNonNull(fns, "fns");

        // 두 구현이 같은 시점에 실패하도록 여기서 미리 검사한다.
        // 합성 방식은 붙이는 순간에, 적용 방식은 apply 할 때 NPE 가 나서 그냥 두면 갈린다.
        // 가변인자 배열을 그대로 다른 메서드에 넘기지 않으려고 목록에 옮겨 담는다.
        List<Function<String, String>> all = new ArrayList<>(fns.length);

        for (Function<String, String> fn : fns) {
            all.add(Objects.requireNonNull(fn, "fns 안의 함수"));
        }

        return ThreadLocalRandom.current().nextBoolean()
                ? _pipeByApply(all)
                : _pipeByCompose(all);
    }

    /** 붙여 둔 함수를 입력값에 차례로 적용하는 함수를 돌려준다. 붙일 때는 아무것도 하지 않는다. */
    private static Function<String, String> _pipeByApply(List<Function<String, String>> fns) {
        return input -> fns.stream().reduce(input, (value, fn) -> fn.apply(value), (left, right) -> right);
    }

    /** 붙여 둔 함수를 미리 합성해 둔 함수를 돌려준다. */
    private static Function<String, String> _pipeByCompose(List<Function<String, String>> fns) {
        return fns.stream().reduce(Function.identity(), Function::andThen);
    }

    /** 아무것도 붙이지 않은 {@link Pipeline} 을 만든다. {@link #pipe(Function[])} 의 이어 붙이기 버전. */
    public static Pipeline pipe() {
        return Pipeline.init();
    }

    /**
     * 함수를 차례로 이어 붙이는 파이프라인. {@link #pipe()} 로 시작한다.
     *
     * <pre>
     * String s = StringUtil.pipe()
     *         .then(s1 -&gt; StringUtil.slice(s1, 20))
     *         .then(StringUtil::reverse)
     *         .then(StringUtil::trimLeadingZero)
     *         .then(StringUtil::reverse)
     *         .apply("2025-03-19 12:26:41.012345000");   // "012345"
     * </pre>
     *
     * <p>{@code then} 은 지금까지 붙인 것을 그대로 두고 새 파이프라인을 만들어 돌려준다.
     */
    public static final class Pipeline {

        private final Function<String, String> fn;

        private Pipeline(Function<String, String> fn) {
            this.fn = fn;
        }

        private static Pipeline init() {
            return new Pipeline(Function.identity());
        }

        /**
         * 다음에 적용할 함수를 붙인 새 파이프라인을 돌려준다.
         *
         * @throws NullPointerException {@code next} 가 {@code null} 인 경우
         */
        public Pipeline then(Function<String, String> next) {
            Objects.requireNonNull(next, "next");

            return new Pipeline(fn.andThen(next));
        }

        /** 입력값에 지금까지 붙인 함수를 차례로 적용한다. */
        public String apply(String input) {
            return fn.apply(input);
        }
    }
}
