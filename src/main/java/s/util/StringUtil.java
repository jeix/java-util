package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class StringUtil {

    private static final Pattern LEADING_ZEROS_PATTERN = Pattern.compile("^0+(?=\\d)");

    private StringUtil() {
    }

    public static boolean isBlank(final String s) {
        if (s == null) {
            return true;
        }
        return useStream() ? s.chars().allMatch(Character::isWhitespace) : isBlankLoop(s);
    }

    private static boolean isBlankLoop(final String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(final String s) {
        return s == null || s.isEmpty();
    }

    public static String nonNullOf(final String value, final String dflt) {
        return value != null ? value : dflt;
    }

    public static String nonNullOf(final Supplier<String> supplier, final Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return null;
        }
        final String value = supplier.get();
        return value != null ? value : (dfltSupplier != null ? dfltSupplier.get() : null);
    }

    public static String nonBlankOf(final String value, final String dflt) {
        return isBlank(value) ? dflt : value;
    }

    public static String nonBlankOf(final Supplier<String> supplier, final Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return null;
        }
        final String value = supplier.get();
        return isBlank(value) ? (dfltSupplier != null ? dfltSupplier.get() : null) : value;
    }

    public static String nonEmptyOf(final String value, final String dflt) {
        return isEmpty(value) ? dflt : value;
    }

    public static String nonEmptyOf(final Supplier<String> supplier, final Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return null;
        }
        final String value = supplier.get();
        return isEmpty(value) ? (dfltSupplier != null ? dfltSupplier.get() : null) : value;
    }

    public static String firstNonBlankOrLast(final String value1, final String value2, final String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        if (values != null) {
            for (final String v : values) {
                if (!isBlank(v)) {
                    return v;
                }
            }
        }
        return value2;
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2, null);
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, new Supplier[]{supplier3});
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, new Supplier[]{supplier3, supplier4});
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4, final Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, new Supplier[]{supplier3, supplier4, supplier5});
    }

    private static String _firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String>[] suppliers) {
        if (supplier1 != null) {
            final String v1 = supplier1.get();
            if (!isBlank(v1)) {
                return v1;
            }
        }
        String last = null;
        if (supplier2 != null) {
            last = supplier2.get();
            if (!isBlank(last)) {
                return last;
            }
        }
        if (suppliers != null) {
            for (final Supplier<String> s : suppliers) {
                if (s != null) {
                    final String v = s.get();
                    if (!isBlank(v)) {
                        return v;
                    }
                    last = v;
                }
            }
        }
        return last;
    }

    public static String firstNonBlankOrEmpty(final String value1, final String value2, final String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        if (values != null) {
            for (final String v : values) {
                if (!isBlank(v)) {
                    return v;
                }
            }
        }
        return "";
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, null);
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, new Supplier[]{supplier3});
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, new Supplier[]{supplier3, supplier4});
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4, final Supplier<String> supplier5) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, new Supplier[]{supplier3, supplier4, supplier5});
    }

    private static String _firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String>[] suppliers) {
        if (supplier1 != null) {
            final String v1 = supplier1.get();
            if (!isBlank(v1)) {
                return v1;
            }
        }
        if (supplier2 != null) {
            final String v2 = supplier2.get();
            if (!isBlank(v2)) {
                return v2;
            }
        }
        if (suppliers != null) {
            for (final Supplier<String> s : suppliers) {
                if (s != null) {
                    final String v = s.get();
                    if (!isBlank(v)) {
                        return v;
                    }
                }
            }
        }
        return "";
    }

    public static String firstNonBlankOrNull(final String value1, final String value2, final String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        if (values != null) {
            for (final String v : values) {
                if (!isBlank(v)) {
                    return v;
                }
            }
        }
        return null;
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2, null);
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3) {
        return _firstNonBlankOrNull(supplier1, supplier2, new Supplier[]{supplier3});
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4) {
        return _firstNonBlankOrNull(supplier1, supplier2, new Supplier[]{supplier3, supplier4});
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4, final Supplier<String> supplier5) {
        return _firstNonBlankOrNull(supplier1, supplier2, new Supplier[]{supplier3, supplier4, supplier5});
    }

    private static String _firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String>[] suppliers) {
        if (supplier1 != null) {
            final String v1 = supplier1.get();
            if (!isBlank(v1)) {
                return v1;
            }
        }
        if (supplier2 != null) {
            final String v2 = supplier2.get();
            if (!isBlank(v2)) {
                return v2;
            }
        }
        if (suppliers != null) {
            for (final Supplier<String> s : suppliers) {
                if (s != null) {
                    final String v = s.get();
                    if (!isBlank(v)) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    public static String stringify(final Object obj) {
        if (obj == null) {
            return null;
        }
        return switch (obj) {
            case Date d -> new SimpleDateFormat("yyyy-MM-dd").format(d);
            case BigDecimal bd -> bd.toPlainString();
            default -> obj.toString();
        };
    }

    public static String slice(final String s, final int begin) {
        if (s == null) {
            return null;
        }
        final int len = s.length();
        int b = begin;
        if (b < 0) {
            b = len + b;
            if (b < 0) {
                b = 0;
            }
        }
        if (b >= len) {
            return "";
        }
        return s.substring(b);
    }

    public static String slice(final String s, final int begin, final int end) {
        if (s == null) {
            return null;
        }
        int b = begin;
        int e = end;
        final int len = s.length();
        if (b < 0) {
            b = len + b;
            if (b < 0) {
                b = 0;
            }
        }
        if (e < 0) {
            e = len + e;
        }
        if (b >= len) {
            return "";
        }
        if (e > len) {
            e = len;
        }
        if (b >= e) {
            return "";
        }
        return s.substring(b, e);
    }

    public static String head(final String s, final int size) {
        if (s == null) {
            return null;
        }
        final int len = s.length();
        if (size == 0) {
            return "";
        }
        if (size > 0) {
            if (size >= len) {
                return s;
            }
            return s.substring(0, size);
        }
        int start = len + size;
        if (start < 0) {
            return "";
        }
        return s.substring(start);
    }

    public static String tail(final String s, final int size) {
        if (s == null) {
            return null;
        }
        final int len = s.length();
        if (size == 0) {
            return "";
        }
        if (size > 0) {
            if (size >= len) {
                return s;
            }
            return s.substring(len - size);
        }
        int exclude = Math.abs(size);
        if (exclude >= len) {
            return "";
        }
        return s.substring(exclude);
    }

    public static String trimLeadingZero(final String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return useRegex() ? trimLeadingZeroRegex(s) : trimLeadingZeroLoop(s);
    }

    private static String trimLeadingZeroLoop(final String s) {
        int i = 0;
        while (i < s.length() && s.charAt(i) == '0') {
            i++;
        }
        if (i == s.length()) {
            return "0";
        }
        if (i > 0 && i < s.length() && s.charAt(i) == '.') {
            return "0" + s.substring(i);
        }
        return s.substring(i);
    }

    private static String trimLeadingZeroRegex(final String s) {
        final String result = LEADING_ZEROS_PATTERN.matcher(s).replaceFirst("");
        if (result.isEmpty()) {
            return "0";
        }
        if (result.startsWith(".")) {
            return "0" + result;
        }
        return result;
    }

    private static boolean useStream() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    private static boolean useRegex() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static String repeat(final String s, final int size) {
        if (s == null || size <= 0) {
            return "";
        }
        if (useStream()) {
            return IntStream.range(0, size).mapToObj(i -> s).collect(Collectors.joining());
        }
        final StringBuilder sb = new StringBuilder(s.length() * size);
        for (int i = 0; i < size; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String reverse(final String s) {
        if (s == null) {
            return null;
        }
        return new StringBuilder(s).reverse().toString();
    }

    private static String repeatPad(final String pad, final int size) {
        if (pad == null || pad.isEmpty() || size <= 0) {
            return "";
        }
        if (pad.length() != 1) {
            throw new IllegalArgumentException("pad must be a single character string");
        }
        final char c = pad.charAt(0);
        if (useStream()) {
            return IntStream.range(0, size).mapToObj(i -> pad).collect(Collectors.joining());
        }
        final char[] arr = new char[size];
        for (int i = 0; i < size; i++) {
            arr[i] = c;
        }
        return new String(arr);
    }

    public static String lpad(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        validatePad(pad);
        if (s == null) {
            return repeatPad(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return repeatPad(pad, len - s.length()) + s;
    }

    public static String rpad(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        validatePad(pad);
        if (s == null) {
            return repeatPad(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return s + repeatPad(pad, len - s.length());
    }

    public static String pad(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        validatePad(pad);
        if (s == null) {
            return repeatPad(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        final int totalPad = len - s.length();
        final int leftPad = totalPad / 2;
        final int rightPad = totalPad - leftPad;
        return repeatPad(pad, leftPad) + s + repeatPad(pad, rightPad);
    }

    private static void validatePad(final String pad) {
        if (pad == null || pad.isEmpty() || pad.length() != 1) {
            throw new IllegalArgumentException("pad must be a single character string");
        }
    }

    public static <T> String join(final List<T> list, final String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        if (delimiter == null) {
            return "";
        }
        if (useStream()) {
            return list.stream()
                    .map(item -> item != null ? item.toString() : "null")
                    .collect(Collectors.joining(delimiter));
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            final T item = list.get(i);
            sb.append(item != null ? item.toString() : "null");
        }
        return sb.toString();
    }

    public static List<String> split(final String s, final String regex) {
        if (s == null) {
            return Collections.emptyList();
        }
        if (regex == null || regex.isEmpty()) {
            return Collections.singletonList(s);
        }
        final String[] parts = s.split(regex);
        return List.of(parts);
    }
}