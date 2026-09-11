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
import java.util.stream.Stream;

public final class StringUtil {

    private static final Pattern LEADING_ZEROS_PATTERN = Pattern.compile("^0+(?=\\d)");

    private StringUtil() {
    }

    public static boolean isBlank(final String s) {
        return s == null ? true : s.isBlank();
    }

    public static boolean isEmpty(final String s) {
        return s == null || s.isEmpty();
    }

    public static String nonNullOf(final String value, final String dflt) {
        return value != null ? value : dflt;
    }

    public static String nonNullOf(final Supplier<String> supplier, final Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier != null ? dfltSupplier.get() : null;
        }
        final String value = supplier.get();
        return value != null ? value : (dfltSupplier != null ? dfltSupplier.get() : null);
    }

    public static String nonBlankOf(final String value, final String dflt) {
        return isBlank(value) ? dflt : value;
    }

    public static String nonBlankOf(final Supplier<String> supplier, final Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier != null ? dfltSupplier.get() : null;
        }
        final String value = supplier.get();
        return isBlank(value) ? (dfltSupplier != null ? dfltSupplier.get() : null) : value;
    }

    public static String nonEmptyOf(final String value, final String dflt) {
        return isEmpty(value) ? dflt : value;
    }

    public static String nonEmptyOf(final Supplier<String> supplier, final Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier != null ? dfltSupplier.get() : null;
        }
        final String value = supplier.get();
        return isEmpty(value) ? (dfltSupplier != null ? dfltSupplier.get() : null) : value;
    }

    public static String firstNonBlankOrLast(final String value1, final String value2, final String... values) {
        if (_useStream()) {
            return _firstNonBlankOrLastStream(value1, value2, values);
        } else {
            return _firstNonBlankOrLastLoop(value1, value2, values);
        }
    }

    private static String _firstNonBlankOrLastLoop(final String value1, final String value2, final String... values) {
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

    private static String _firstNonBlankOrLastStream(final String value1, final String value2, final String... values) {
        return Stream.of(value1, value2)
                .filter(v -> v != null && !isBlank(v))
                .findFirst()
                .orElseGet(() -> {
                    if (values != null) {
                        return Stream.of(values)
                                .filter(v -> v != null && !isBlank(v))
                                .findFirst()
                                .orElse(value2);
                    }
                    return value2;
                });
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3);
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4);
    }

    public static String firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4, final Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    private static String _firstNonBlankOrLast(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String>... suppliers) {
        if (_useStream()) {
            return _firstNonBlankOrLastStream(supplier1, supplier2, suppliers);
        } else {
            return _firstNonBlankOrLastLoop(supplier1, supplier2, suppliers);
        }
    }

    private static String _firstNonBlankOrLastLoop(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String>... suppliers) {
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

    private static String _firstNonBlankOrLastStream(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String>... suppliers) {
        return Stream.of(supplier1, supplier2)
                .filter(s -> s != null)
                .map(Supplier::get)
                .filter(v -> v != null && !isBlank(v))
                .findFirst()
                .orElseGet(() -> {
                    if (suppliers != null) {
                        return Stream.of(suppliers)
                                .filter(s -> s != null)
                                .map(Supplier::get)
                                .filter(v -> v != null && !isBlank(v))
                                .findFirst()
                                .orElseGet(() -> {
                                    String last = null;
                                    for (final Supplier<String> s : suppliers) {
                                        if (s != null) {
                                            last = s.get();
                                        }
                                    }
                                    return last;
                                });
                    }
                    String last = null;
                    if (supplier2 != null) {
                        last = supplier2.get();
                    }
                    return last;
                });
    }

    public static String firstNonBlankOrEmpty(final String value1, final String value2, final String... values) {
        final String result = firstNonBlankOrLast(value1, value2, values);
        return (result != null && !isBlank(result)) ? result : "";
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2, (Supplier<String>) () -> "");
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, (Supplier<String>) () -> "");
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, (Supplier<String>) () -> "");
    }

    public static String firstNonBlankOrEmpty(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4, final Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5, (Supplier<String>) () -> "");
    }

    public static String firstNonBlankOrNull(final String value1, final String value2, final String... values) {
        final String result = firstNonBlankOrLast(value1, value2, values);
        return (result != null && !isBlank(result)) ? result : null;
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2, (Supplier<String>) null);
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, (Supplier<String>) null);
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, (Supplier<String>) null);
    }

    public static String firstNonBlankOrNull(final Supplier<String> supplier1, final Supplier<String> supplier2, final Supplier<String> supplier3, final Supplier<String> supplier4, final Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5, (Supplier<String>) null);
    }

    public static String stringify(final Object obj) {
        return switch (obj) {
            case null -> null;
            case Date d -> new SimpleDateFormat("yyyy-MM-dd").format(d);
            case BigDecimal bd -> bd.toPlainString();
            default -> obj.toString();
        };
    }

    public static String slice(final String s, final int begin) {
        return slice(s, begin, Integer.MAX_VALUE);
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
        int start;
        int end;
        if (size > 0) {
            start = 0;
            end = Math.min(size, len);
        } else {
            start = len + size;
            if (start < 0) {
                return "";
            }
            end = len;
        }
        return s.substring(start, end);
    }

    public static String tail(final String s, final int size) {
        if (s == null) {
            return null;
        }
        final int len = s.length();
        if (size == 0) {
            return "";
        }
        int start;
        int end;
        if (size > 0) {
            start = Math.max(0, len - size);
            end = len;
        } else {
            int exclude = Math.abs(size);
            if (exclude >= len) {
                return "";
            }
            start = exclude;
            end = len;
        }
        return s.substring(start, end);
    }

    public static String trimLeadingZero(final String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if (_useRegex()) {
            return _trimLeadingZeroRegex(s);
        } else {
            return _trimLeadingZeroLoop(s);
        }
    }

    private static String _trimLeadingZeroLoop(final String s) {
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

    private static String _trimLeadingZeroRegex(final String s) {
        final String result = LEADING_ZEROS_PATTERN.matcher(s).replaceFirst("");
        if (result.isEmpty()) {
            return "0";
        }
        if (result.startsWith(".")) {
            return "0" + result;
        }
        return result;
    }

    private static boolean _useStream() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    private static boolean _useRegex() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static String repeat(final String s, final int size) {
        if (s == null || size <= 0) {
            return "";
        }
        if (_useStream()) {
            return IntStream.range(0, size).mapToObj(i -> s).collect(Collectors.joining());
        } else {
            final StringBuilder sb = new StringBuilder(s.length() * size);
            for (int i = 0; i < size; i++) {
                sb.append(s);
            }
            return sb.toString();
        }
    }

    public static String reverse(final String s) {
        if (s == null) {
            return null;
        }
        return new StringBuilder(s).reverse().toString();
    }

    private static String _repeatPad(final String pad, final int size) {
        if (pad == null || pad.isEmpty() || size <= 0) {
            return "";
        }
        if (_useStream()) {
            return IntStream.range(0, size).mapToObj(i -> pad).collect(Collectors.joining());
        } else {
            final StringBuilder sb = new StringBuilder(pad.length() * size);
            for (int i = 0; i < size; i++) {
                sb.append(pad);
            }
            return sb.toString();
        }
    }

    public static String lpad(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        _validatePad(pad);
        if (s == null) {
            return _repeatPad(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return _repeatPad(pad, len - s.length()) + s;
    }

    public static String rpad(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        _validatePad(pad);
        if (s == null) {
            return _repeatPad(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return s + _repeatPad(pad, len - s.length());
    }

    public static String pad(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        _validatePad(pad);
        if (s == null) {
            return _repeatPad(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        final int totalPad = len - s.length();
        final int leftPad = totalPad / 2;
        final int rightPad = totalPad - leftPad;
        return _repeatPad(pad, leftPad) + s + _repeatPad(pad, rightPad);
    }

    public static String lpad2(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        if (pad == null || pad.isEmpty()) {
            throw new IllegalArgumentException("pad must not be null or empty");
        }
        if (s == null) {
            return _repeatPad2(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return _repeatPad2(pad, len - s.length()) + s;
    }

    public static String rpad2(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        if (pad == null || pad.isEmpty()) {
            throw new IllegalArgumentException("pad must not be null or empty");
        }
        if (s == null) {
            return _repeatPad2(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return s + _repeatPad2(pad, len - s.length());
    }

    public static String pad2(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        if (pad == null || pad.isEmpty()) {
            throw new IllegalArgumentException("pad must not be null or empty");
        }
        if (s == null) {
            return _repeatPad2(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        final int totalPad = len - s.length();
        final int leftPad = totalPad / 2;
        final int rightPad = totalPad - leftPad;
        return _repeatPad2(pad, leftPad) + s + _repeatPad2(pad, rightPad);
    }

    private static String _repeatPad2(final String pad, final int size) {
        if (pad == null || pad.isEmpty() || size <= 0) {
            return "";
        }
        final int padLen = pad.length();
        final int fullRepeats = size / padLen;
        final int remainder = size % padLen;
        final StringBuilder sb = new StringBuilder(size);
        if (_useStream()) {
            IntStream.range(0, fullRepeats).forEach(i -> sb.append(pad));
            if (remainder > 0) {
                sb.append(pad, 0, remainder);
            }
        } else {
            for (int i = 0; i < fullRepeats; i++) {
                sb.append(pad);
            }
            if (remainder > 0) {
                sb.append(pad, 0, remainder);
            }
        }
        return sb.toString();
    }

    private static void _validatePad(final String pad) {
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
        if (_useStream()) {
            return list.stream()
                    .map(item -> item != null ? item.toString() : "null")
                    .collect(Collectors.joining(delimiter));
        } else {
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