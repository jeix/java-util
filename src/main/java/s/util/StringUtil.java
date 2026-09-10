package s.util;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** null을 허용하는 문자열 유틸리티예요. 인덱스와 길이는 UTF-16 단위예요. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    public static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static String nonNullOf(String value, String dflt) {
        return value == null ? dflt : value;
    }

    /** 필요한 Supplier만 한 번 호출해요. null Supplier는 null 값으로 취급해요. */
    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _supply(supplier);
        return value == null ? _supply(dfltSupplier) : value;
    }

    public static String nonBlankOf(String value, String dflt) {
        return isBlank(value) ? dflt : value;
    }

    /** 필요한 Supplier만 한 번 호출해요. null Supplier는 null 값으로 취급해요. */
    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _supply(supplier);
        return isBlank(value) ? _supply(dfltSupplier) : value;
    }

    public static String nonEmptyOf(String value, String dflt) {
        return isEmpty(value) ? dflt : value;
    }

    /** 필요한 Supplier만 한 번 호출해요. null Supplier는 null 값으로 취급해요. */
    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _supply(supplier);
        return isEmpty(value) ? _supply(dfltSupplier) : value;
    }

    /** 첫 non-blank 값을 반환하며, 없으면 마지막 값을 그대로 반환해요. */
    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2) || values == null || values.length == 0) {
            return value2;
        }
        return _chooseAlternative()
                ? _firstNonBlankOrLastWithStream(value2, values)
                : _firstNonBlankOrLastWithLoop(value2, values);
    }

    private static String _firstNonBlankOrLastWithLoop(String last, String[] values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
            last = value;
        }
        return last;
    }

    private static String _firstNonBlankOrLastWithStream(String last, String[] values) {
        return Arrays.stream(values)
                .filter(value -> !isBlank(value))
                .findFirst()
                .orElse(values.length == 0 ? last : values[values.length - 1]);
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    public static String firstNonBlankOrLast(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3) {
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
        String value = _supply(supplier1);
        if (!isBlank(value)) {
            return value;
        }
        value = _supply(supplier2);
        if (!isBlank(value) || suppliers == null) {
            return value;
        }
        return _chooseAlternative()
                ? _firstNonBlankOrLastWithStream(value, suppliers)
                : _firstNonBlankOrLastWithLoop(value, suppliers);
    }

    private static String _firstNonBlankOrLastWithLoop(
            String last, Supplier<String>[] suppliers) {
        for (Supplier<String> supplier : suppliers) {
            last = _supply(supplier);
            if (!isBlank(last)) {
                return last;
            }
        }
        return last;
    }

    private static String _firstNonBlankOrLastWithStream(
            String initial, Supplier<String>[] suppliers) {
        int lastIndex = suppliers.length - 1;
        return IntStream.range(0, suppliers.length)
                .mapToObj(index -> new _Candidate(index, _supply(suppliers[index])))
                .filter(candidate -> !isBlank(candidate.value()) || candidate.index() == lastIndex)
                .findFirst()
                .orElseGet(() -> new _Candidate(-1, initial))
                .value();
    }

    private record _Candidate(int index, String value) {
    }

    private static boolean _chooseAlternative() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /** 첫 non-blank 값을 반환하며, 없으면 빈 문자열을 반환해요. */
    public static String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        return nonBlankOf(firstNonBlankOrLast(value1, value2, values), "");
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2);
    }

    public static String firstNonBlankOrEmpty(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3) {
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
        return nonBlankOf(_firstNonBlankOrLast(supplier1, supplier2, suppliers), "");
    }

    /** 첫 non-blank 값을 반환하며, 없으면 null을 반환해요. */
    public static String firstNonBlankOrNull(String value1, String value2, String... values) {
        return nonBlankOf(firstNonBlankOrLast(value1, value2, values), (String) null);
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2);
    }

    public static String firstNonBlankOrNull(
            Supplier<String> supplier1,
            Supplier<String> supplier2,
            Supplier<String> supplier3) {
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
        return nonBlankOf(_firstNonBlankOrLast(supplier1, supplier2, suppliers), (String) null);
    }

    private static String _supply(Supplier<String> supplier) {
        return supplier == null ? null : supplier.get();
    }

    /** null은 빈 문자열, Date는 시스템 기본 시간대의 yyyy-MM-dd, BigDecimal은 일반 표기로 변환해요. */
    public static String stringify(Object obj) {
        return switch (obj) {
            case null -> "";
            case Date date -> new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(date);
            case BigDecimal decimal -> decimal.toPlainString();
            default -> obj.toString();
        };
    }

    public static String slice(String s, int begin) {
        return slice(s, begin, s == null ? 0 : s.length());
    }

    /** 음수 인덱스는 뒤에서부터 세고, 변환한 인덱스는 0부터 문자열 길이 사이로 제한해요. */
    public static String slice(String s, int begin, int end) {
        if (s == null) {
            return null;
        }
        int normalizedBegin = _normalizeIndex(s.length(), begin);
        int normalizedEnd = _normalizeIndex(s.length(), end);
        return normalizedEnd < normalizedBegin ? "" : s.substring(normalizedBegin, normalizedEnd);
    }

    /** 음수 size는 뒤에서부터 센 끝 인덱스로 해석해요. */
    public static String head(String s, int size) {
        if (s == null) {
            return null;
        }
        return slice(s, 0, size);
    }

    /** 음수 size는 절댓값만큼 앞에서 제외한 시작 인덱스로 해석해요. */
    public static String tail(String s, int size) {
        if (s == null) {
            return null;
        }
        long begin = size < 0 ? -(long) size : (long) s.length() - size;
        return slice(s, _clampIndex(s.length(), begin));
    }

    private static int _normalizeIndex(int length, int index) {
        long normalized = index < 0 ? (long) length + index : index;
        return _clampIndex(length, normalized);
    }

    private static int _clampIndex(int length, long index) {
        return (int) Math.min(Math.max(index, 0), length);
    }

    /** 부호를 포함한 정수·소수의 선행 0을 제거해요. 숫자가 아니면 원문을 반환해요. */
    public static String trimLeadingZero(String s) {
        if (isEmpty(s) || !s.matches("[+-]?[0-9]+(?:\\.[0-9]+)?")) {
            return s;
        }
        return _chooseAlternative() ? _trimLeadingZeroWithRegex(s) : _trimLeadingZeroWithLoop(s);
    }

    private static String _trimLeadingZeroWithLoop(String s) {
        int start = s.charAt(0) == '+' || s.charAt(0) == '-' ? 1 : 0;
        int first = start;
        while (first + 1 < s.length() && s.charAt(first) == '0'
                && s.charAt(first + 1) != '.') {
            first++;
        }
        return s.substring(0, start) + s.substring(first);
    }

    private static String _trimLeadingZeroWithRegex(String s) {
        return s.replaceFirst("^([+-]?)0+(?=[0-9])", "$1");
    }

    public static String repeat(String c, int size) {
        return c == null ? null : size <= 0 ? "" : c.repeat(size);
    }

    public static String reverse(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return new StringBuilder(s).reverse().toString();
    }

    public static String lpad(int len, String s, String pad) {
        return _isSingleByteCharacter(pad) ? lpad2(len, s, pad) : s;
    }

    public static String rpad(int len, String s, String pad) {
        return _isSingleByteCharacter(pad) ? rpad2(len, s, pad) : s;
    }

    /** 가운데 정렬하며 남는 한 칸은 오른쪽에 채워요. */
    public static String pad(int len, String s, String pad) {
        return _isSingleByteCharacter(pad) ? pad2(len, s, pad) : s;
    }

    private static boolean _isSingleByteCharacter(String value) {
        return value != null
                && value.length() == 1
                && value.getBytes(StandardCharsets.UTF_8).length == 1;
    }

    public static String lpad2(int len, String s, String pad) {
        return _padded(len, s, pad, -1);
    }

    public static String rpad2(int len, String s, String pad) {
        return _padded(len, s, pad, 1);
    }

    /** 양쪽에서 패턴을 처음부터 반복하며 남는 한 칸은 오른쪽에 채워요. */
    public static String pad2(int len, String s, String pad) {
        return _padded(len, s, pad, 0);
    }

    // 단일 바이트 문자열을 전제로 해요. 길이가 충분하거나 패턴이 없으면 원문을 유지해요.
    private static String _padded(int len, String s, String pad, int direction) {
        if (s == null || len <= s.length() || isEmpty(pad)) {
            return s;
        }
        int missing = len - s.length();
        int left = direction < 0 ? missing : direction > 0 ? 0 : missing / 2;
        return _padding(pad, left) + s + _padding(pad, missing - left);
    }

    private static String _padding(String pattern, int size) {
        if (size <= 0) {
            return "";
        }
        return pattern.repeat(size / pattern.length()) + head(pattern, size % pattern.length());
    }

    /** null 리스트는 빈 문자열로, null 요소와 구분자는 빈 문자열로 취급해요. */
    public static <T> String join(List<T> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        String separator = nonNullOf(delimiter, "");
        return _chooseAlternative() ? _joinWithStream(list, separator) : _joinWithLoop(list, separator);
    }

    private static <T> String _joinWithLoop(List<T> list, String separator) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (T value : list) {
            if (!first) {
                result.append(separator);
            }
            result.append(stringify(value));
            first = false;
        }
        return result.toString();
    }

    private static <T> String _joinWithStream(List<T> list, String separator) {
        return list.stream().map(StringUtil::stringify).collect(Collectors.joining(separator));
    }

    /** null 입력은 빈 리스트, null 정규식은 원문 하나를 반환해요. 끝의 빈 항목은 유지해요. */
    public static List<String> split(String s, String regex) {
        if (s == null) {
            return List.of();
        }
        if (regex == null) {
            return List.of(s);
        }
        return List.of(s.split(regex, -1));
    }
}
