package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 문자열 유틸리티.
 *
 * <p>null 문자열을 받는 메서드는 별도 언급이 없으면 빈 문자열("")로 취급해요.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    private static final Pattern LEADING_ZERO = Pattern.compile("^([+-]?)0+(?=[^.])");

    public static boolean isBlank(String s) {
        return s == null ? true : s.isBlank();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    // ---- nonXxxOf ----

    public static String nonNullOf(String value, String dflt) {
        return nonNullOf(() -> value, () -> dflt);
    }

    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        return _nonOf(supplier, dfltSupplier, v -> v != null);
    }

    public static String nonBlankOf(String value, String dflt) {
        return nonBlankOf(() -> value, () -> dflt);
    }

    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        return _nonOf(supplier, dfltSupplier, v -> !isBlank(v));
    }

    public static String nonEmptyOf(String value, String dflt) {
        return nonEmptyOf(() -> value, () -> dflt);
    }

    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        return _nonOf(supplier, dfltSupplier, v -> !isEmpty(v));
    }

    private static String _nonOf(Supplier<String> supplier, Supplier<String> dfltSupplier, Predicate<String> ok) {
        String v = _get(supplier);
        return ok.test(v) ? v : _get(dfltSupplier);
    }

    private static String _get(Supplier<String> supplier) {
        return supplier == null ? null : supplier.get();
    }

    /** 반복문 구현과 stream 구현 중 하나를 무작위로 골라요. */
    private static boolean _useStream() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    @SuppressWarnings("unchecked")
    private static Supplier<String>[] _suppliers(String[] values) {
        if (values == null) {
            return new Supplier[0];
        }
        return _useStream() ? _suppliersStream(values) : _suppliersLoop(values);
    }

    @SuppressWarnings("unchecked")
    private static Supplier<String>[] _suppliersLoop(String[] values) {
        Supplier<String>[] suppliers = new Supplier[values.length];
        for (int i = 0; i < values.length; i++) {
            String v = values[i];
            suppliers[i] = () -> v;
        }
        return suppliers;
    }

    @SuppressWarnings("unchecked")
    private static Supplier<String>[] _suppliersStream(String[] values) {
        return Arrays.stream(values).<Supplier<String>>map(v -> () -> v).toArray(Supplier[]::new);
    }

    private static Stream<Supplier<String>> _streamOf(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>[] suppliers) {
        return Stream.concat(
                Stream.of(supplier1, supplier2),
                suppliers == null ? Stream.<Supplier<String>>empty() : Arrays.stream(suppliers));
    }

    // ---- firstNonBlankOrLast ----

    /** 비어있지 않은 첫 값을, 없으면 마지막 인자를 그대로 돌려줘요. */
    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        return _firstNonBlankOrLast(() -> value1, () -> value2, _suppliers(values));
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    public static String firstNonBlankOrLast(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3);
    }

    public static String firstNonBlankOrLast(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3,
            Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4);
    }

    public static String firstNonBlankOrLast(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3,
            Supplier<String> supplier4,
            Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    @SafeVarargs
    private static String _firstNonBlankOrLast(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>... suppliers) {
        return _useStream()
                ? _firstNonBlankOrLastStream(supplier1, supplier2, suppliers)
                : _firstNonBlankOrLastLoop(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrLastStream(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>[] suppliers) {
        AtomicReference<String> last = new AtomicReference<>();
        return _streamOf(supplier1, supplier2, suppliers)
                .map(supplier -> {
                    String v = _get(supplier);
                    last.set(v);
                    return v;
                })
                .filter(v -> !isBlank(v))
                .findFirst()
                .orElseGet(last::get);
    }

    private static String _firstNonBlankOrLastLoop(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>[] suppliers) {
        String v = _get(supplier1);
        if (!isBlank(v)) {
            return v;
        }
        v = _get(supplier2);
        if (!isBlank(v)) {
            return v;
        }
        if (suppliers == null) {
            return v;
        }
        for (Supplier<String> supplier : suppliers) {
            v = _get(supplier);
            if (!isBlank(v)) {
                return v;
            }
        }
        return v;
    }

    // ---- firstNonBlankOrEmpty ----

    public static String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        return _firstNonBlankOrEmpty(() -> value1, () -> value2, _suppliers(values));
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2);
    }

    public static String firstNonBlankOrEmpty(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3);
    }

    public static String firstNonBlankOrEmpty(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3,
            Supplier<String> supplier4) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4);
    }

    public static String firstNonBlankOrEmpty(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3,
            Supplier<String> supplier4,
            Supplier<String> supplier5) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    @SafeVarargs
    private static String _firstNonBlankOrEmpty(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>... suppliers) {
        return nonNullOf(_firstNonBlankOrNull(supplier1, supplier2, suppliers), "");
    }

    // ---- firstNonBlankOrNull ----

    public static String firstNonBlankOrNull(String value1, String value2, String... values) {
        return _firstNonBlankOrNull(() -> value1, () -> value2, _suppliers(values));
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2);
    }

    public static String firstNonBlankOrNull(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3);
    }

    public static String firstNonBlankOrNull(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3,
            Supplier<String> supplier4) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4);
    }

    public static String firstNonBlankOrNull(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3,
            Supplier<String> supplier4,
            Supplier<String> supplier5) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    @SafeVarargs
    private static String _firstNonBlankOrNull(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>... suppliers) {
        return _useStream()
                ? _firstNonBlankOrNullStream(supplier1, supplier2, suppliers)
                : _firstNonBlankOrNullLoop(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrNullStream(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>[] suppliers) {
        return _streamOf(supplier1, supplier2, suppliers)
                .map(StringUtil::_get)
                .filter(v -> !isBlank(v))
                .findFirst()
                .orElse(null);
    }

    private static String _firstNonBlankOrNullLoop(
            Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>[] suppliers) {
        String v = _get(supplier1);
        if (!isBlank(v)) {
            return v;
        }
        v = _get(supplier2);
        if (!isBlank(v)) {
            return v;
        }
        if (suppliers == null) {
            return null;
        }
        for (Supplier<String> supplier : suppliers) {
            v = _get(supplier);
            if (!isBlank(v)) {
                return v;
            }
        }
        return null;
    }

    // ---- 변환 ----

    /** null은 null, {@link Date}는 "yyyy-MM-dd", {@link BigDecimal}은 {@code toPlainString()}, 그 밖은 {@code toString()}. */
    public static String stringify(Object obj) {
        return switch (obj) {
            case null -> null;
            case Date date -> new SimpleDateFormat("yyyy-MM-dd").format(date);
            case BigDecimal decimal -> decimal.toPlainString();
            default -> obj.toString();
        };
    }

    // ---- 부분 문자열 ----

    /** begin부터 끝까지. */
    public static String slice(String s, int begin) {
        if (s == null) {
            return "";
        }
        return slice(s, begin, s.length());
    }

    /**
     * begin(포함)부터 end(불포함)까지. 음수 인덱스는 뒤에서부터 센 위치로 해석하고(-1은 마지막 문자),
     * 길이의 음수값보다 작으면 0, 길이보다 크면 길이로 보정해요. 보정 후 begin이 end 이상이면 "".
     */
    public static String slice(String s, int begin, int end) {
        if (s == null) {
            return "";
        }
        int from = _index(begin, s.length());
        int to = _index(end, s.length());
        if (from >= to) {
            return "";
        }
        return s.substring(from, to);
    }

    private static int _index(int i, int len) {
        return i < 0 ? Math.max(0, len + i) : Math.min(i, len);
    }

    /** 앞에서 size개 문자. 음수 size는 뒤에서 -size개를 뺀 앞부분이고({@code head("abcde", -1)} → "abcd"), 길이보다 크면 전체. */
    public static String head(String s, int size) {
        if (s == null) {
            return "";
        }
        return slice(s, 0, size);
    }

    /** 뒤에서 size개 문자. 음수 size는 앞에서 -size개를 뺀 나머지({@code tail("abcde", -1)} → "bcde"). 길이보다 크면 전체. */
    public static String tail(String s, int size) {
        if (s == null) {
            return "";
        }
        if (size >= 0) {
            return slice(s, Math.max(0, s.length() - size));
        }
        return slice(s, (int) Math.min(-(long) size, s.length()));
    }

    /** 숫자 문자열의 앞쪽 0 제거. 부호는 유지하고, 정수부가 전부 0이면 "0" 하나는 남겨요("000" → "0", "00.5" → "0.5"). */
    public static String trimLeadingZero(String s) {
        if (isEmpty(s)) {
            return "";
        }
        return _useStream() ? _trimLeadingZeroRegex(s) : _trimLeadingZeroLoop(s);
    }

    private static String _trimLeadingZeroRegex(String s) {
        return LEADING_ZERO.matcher(s).replaceFirst("$1");
    }

    private static String _trimLeadingZeroLoop(String s) {
        int start = (s.charAt(0) == '-' || s.charAt(0) == '+') ? 1 : 0;
        int i = start;
        while (i < s.length() - 1 && s.charAt(i) == '0' && s.charAt(i + 1) != '.') {
            i++;
        }
        return s.substring(0, start) + s.substring(i);
    }

    /** c를 size번 반복. c가 null이거나 비었거나 size가 0 이하면 "". */
    public static String repeat(String c, int size) {
        if (isEmpty(c) || size <= 0) {
            return "";
        }
        return c.repeat(size);
    }

    public static String reverse(String s) {
        if (isEmpty(s)) {
            return "";
        }
        return new StringBuilder(s).reverse().toString();
    }

    // ---- 패딩 ----
    // lpad/rpad/pad의 pad는 싱글바이트 문자 하나로 된 길이 1 문자열이어야 해요("가", "do"는 안 돼요).
    // 패딩할 수 없는 경우(len이 s 길이 이하, pad가 조건에 안 맞음, s가 싱글바이트가 아님)는 s를 그대로 돌려줘요.
    // len보다 긴 s를 잘라내지는 않아요.

    public static String lpad(int len, String s, String pad) {
        if (!_isSinglePad(pad)) {
            return nonNullOf(s, "");
        }
        return lpad2(len, s, pad);
    }

    public static String rpad(int len, String s, String pad) {
        if (!_isSinglePad(pad)) {
            return nonNullOf(s, "");
        }
        return rpad2(len, s, pad);
    }

    public static String pad(int len, String s, String pad) {
        if (!_isSinglePad(pad)) {
            return nonNullOf(s, "");
        }
        return pad2(len, s, pad);
    }

    public static String lpad2(int len, String s, String pad) {
        String str = nonNullOf(s, "");
        if (!_canPad(len, str, pad)) {
            return str;
        }
        return _filler(len - str.length(), pad) + str;
    }

    public static String rpad2(int len, String s, String pad) {
        String str = nonNullOf(s, "");
        if (!_canPad(len, str, pad)) {
            return str;
        }
        return str + _filler(len - str.length(), pad);
    }

    /** 가운데 정렬. 남는 칸이 홀수면 오른쪽이 하나 더 채워져요. */
    public static String pad2(int len, String s, String pad) {
        String str = nonNullOf(s, "");
        if (!_canPad(len, str, pad)) {
            return str;
        }
        int total = len - str.length();
        int left = total / 2;
        return _filler(left, pad) + str + _filler(total - left, pad);
    }

    private static boolean _canPad(int len, String s, String pad) {
        return !isEmpty(pad) && len > s.length() && _isSingleByte(s) && _isSingleByte(pad);
    }

    private static boolean _isSinglePad(String pad) {
        return pad != null && pad.length() == 1 && _isSingleByte(pad);
    }

    private static boolean _isSingleByte(String s) {
        return s.chars().allMatch(c -> c < 0x80);
    }

    /** pad를 앞에서부터 반복해 길이 n으로 만든 문자열. */
    private static String _filler(int n, String pad) {
        if (n <= 0) {
            return "";
        }
        return pad.repeat(n / pad.length() + 1).substring(0, n);
    }

    // ---- 컬렉션 ----

    /** 각 원소를 {@link #stringify(Object)}로 바꿔(null이면 "") delimiter로 이어요. list가 null이면 "". */
    public static <T> String join(List<T> list, String delimiter) {
        if (list == null) {
            return "";
        }
        String delim = nonNullOf(delimiter, "");
        return _useStream() ? _joinStream(list, delim) : _joinLoop(list, delim);
    }

    private static <T> String _joinStream(List<T> list, String delim) {
        return list.stream().map(e -> nonNullOf(stringify(e), "")).collect(Collectors.joining(delim));
    }

    private static <T> String _joinLoop(List<T> list, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(delim);
            }
            sb.append(nonNullOf(stringify(list.get(i)), ""));
        }
        return sb.toString();
    }

    /** 정규식으로 나눠요. 뒤쪽 빈 문자열도 남겨요. s가 null이면 빈 리스트, regex가 비었으면 s 하나짜리 리스트. 불변 리스트를 돌려줘요. */
    public static List<String> split(String s, String regex) {
        if (s == null) {
            return List.of();
        }
        if (isEmpty(regex)) {
            return List.of(s);
        }
        return List.of(s.split(regex, -1));
    }

    // ---- 파이프라인 ----

    /**
     * 함수들을 차례로 이어 하나의 함수로 만들어요. 앞 함수의 결과가 다음 함수의 입력이에요.
     * fns가 null이거나 비었으면 입력을 그대로 돌려주고, null 함수는 건너뛰어요.
     */
    @SafeVarargs
    public static Function<String, String> pipe(Function<String, String>... fns) {
        if (fns == null) {
            return Function.identity();
        }
        return Arrays.stream(fns).filter(Objects::nonNull).reduce(Function.identity(), Function::andThen);
    }

    /** {@code pipe().then(f1).then(f2).apply(s)} 형태로 함수를 하나씩 이어요. */
    public static Pipeline pipe() {
        return Pipeline.init();
    }

    /** 불변 파이프라인. {@link #then(Function)}은 새 파이프라인을 돌려줘요. */
    public static final class Pipeline {

        private final Function<String, String> fn;

        private Pipeline(Function<String, String> fn) {
            this.fn = fn;
        }

        private static Pipeline init() {
            return new Pipeline(Function.identity());
        }

        /** next가 null이면 이 파이프라인 그대로. */
        public Pipeline then(Function<String, String> next) {
            return next == null ? this : new Pipeline(fn.andThen(next));
        }

        public String apply(String input) {
            return fn.apply(input);
        }
    }
}
