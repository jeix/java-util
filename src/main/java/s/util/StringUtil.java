package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.function.Supplier;
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
        if (value == null) {
            return dflt;
        }
        return value;
    }

    /** 필요한 Supplier만 한 번 호출해요. null Supplier는 null 값으로 취급해요. */
    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _supply(supplier);
        if (value == null) {
            return _supply(dfltSupplier);
        }
        return value;
    }

    public static String nonBlankOf(String value, String dflt) {
        if (isBlank(value)) {
            return dflt;
        }
        return value;
    }

    /** 필요한 Supplier만 한 번 호출해요. null Supplier는 null 값으로 취급해요. */
    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _supply(supplier);
        if (isBlank(value)) {
            return _supply(dfltSupplier);
        }
        return value;
    }

    public static String nonEmptyOf(String value, String dflt) {
        if (isEmpty(value)) {
            return dflt;
        }
        return value;
    }

    /** 필요한 Supplier만 한 번 호출해요. null Supplier는 null 값으로 취급해요. */
    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _supply(supplier);
        if (isEmpty(value)) {
            return _supply(dfltSupplier);
        }
        return value;
    }

    /** 첫 non-blank 값을 반환하며, 없으면 마지막 값을 그대로 반환해요. */
    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2) || values == null || values.length == 0) {
            return value2;
        }
        String last = value2;
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
            last = value;
        }
        return last;
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
        for (Supplier<String> supplier : suppliers) {
            value = _supply(supplier);
            if (!isBlank(value)) {
                return value;
            }
        }
        return value;
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
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    /** null은 빈 문자열, Date는 시스템 기본 시간대의 yyyy-MM-dd, BigDecimal은 일반 표기로 변환해요. */
    public static String stringify(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof Date date) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(date);
        }
        if (obj instanceof BigDecimal decimal) {
            return decimal.toPlainString();
        }
        return obj.toString();
    }

    public static String slice(String s, int begin) {
        return slice(s, begin, s == null ? 0 : s.length());
    }

    /** null은 null로, 잘못된 범위는 빈 문자열로 반환해요. */
    public static String slice(String s, int begin, int end) {
        if (s == null) {
            return null;
        }
        if (begin < 0 || end < begin || end > s.length()) {
            return "";
        }
        return s.substring(begin, end);
    }

    /** size가 0 이하이면 빈 문자열, 원문 길이 이상이면 원문을 반환해요. */
    public static String head(String s, int size) {
        if (s == null) {
            return null;
        }
        return slice(s, 0, Math.min(Math.max(size, 0), s.length()));
    }

    /** size가 0 이하이면 빈 문자열, 원문 길이 이상이면 원문을 반환해요. */
    public static String tail(String s, int size) {
        if (s == null) {
            return null;
        }
        return slice(s, s.length() - Math.min(Math.max(size, 0), s.length()));
    }

    /** 부호를 포함한 정수·소수의 선행 0을 제거해요. 숫자가 아니면 원문을 반환해요. */
    public static String trimLeadingZero(String s) {
        if (isEmpty(s) || !s.matches("[+-]?[0-9]+(?:\\.[0-9]+)?")) {
            return s;
        }
        int start = s.charAt(0) == '+' || s.charAt(0) == '-' ? 1 : 0;
        int first = start;
        while (first + 1 < s.length() && s.charAt(first) == '0'
                && s.charAt(first + 1) != '.') {
            first++;
        }
        return s.substring(0, start) + s.substring(first);
    }

    public static String repeat(char c, int size) {
        if (size <= 0) {
            return "";
        }
        return String.valueOf(c).repeat(size);
    }

    public static String reverse(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return new StringBuilder(s).reverse().toString();
    }

    public static String lpad(int len, String s, char pad) {
        return lpad2(len, s, String.valueOf(pad));
    }

    public static String rpad(int len, String s, char pad) {
        return rpad2(len, s, String.valueOf(pad));
    }

    /** 가운데 정렬하며 남는 한 칸은 오른쪽에 채워요. */
    public static String pad(int len, String s, char pad) {
        return pad2(len, s, String.valueOf(pad));
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

    /** null 입력은 빈 리스트, null 정규식은 원문 하나를 반환해요. 끝의 빈 항목은 유지해요. */
    public static List<String> split(String s, String regex) {
        if (s == null) {
            return new ArrayList<>();
        }
        if (regex == null) {
            return new ArrayList<>(List.of(s));
        }
        return new ArrayList<>(List.of(s.split(regex, -1)));
    }
}
