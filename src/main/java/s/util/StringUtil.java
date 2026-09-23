package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    // ------------------------------------------------------------------
    // Boolean checks (ternary operator)
    // ------------------------------------------------------------------

    public static boolean isBlank(String s) {
        return s == null ? true : s.isEmpty() || s.trim().isEmpty();
    }

    public static boolean isEmpty(String s) {
        return s == null ? true : s.isEmpty();
    }

    // ------------------------------------------------------------------
    // Default-value helpers (value-based delegates to supplier-based)
    // ------------------------------------------------------------------

    public static String nonNullOf(String value, String dflt) {
        return nonNullOf(() -> value, () -> dflt);
    }

    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String val = supplier != null ? supplier.get() : null;
        return val != null ? val : _dflt(dfltSupplier);
    }

    public static String nonBlankOf(String value, String dflt) {
        return nonBlankOf(() -> value, () -> dflt);
    }

    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String val = supplier != null ? supplier.get() : null;
        return (val != null && !val.isBlank()) ? val : _dflt(dfltSupplier);
    }

    public static String nonEmptyOf(String value, String dflt) {
        return nonEmptyOf(() -> value, () -> dflt);
    }

    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String val = supplier != null ? supplier.get() : null;
        return (val != null && !val.isEmpty()) ? val : _dflt(dfltSupplier);
    }

    // ------------------------------------------------------------------
    // firstNonBlankOrLast — returns first non-blank, or the last value
    // ------------------------------------------------------------------

    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        Supplier<String>[] rest = Math.random() < 0.5 ? _toSuppliersStream(values) : _toSuppliersLoop(values);
        return _firstNonBlankOrLast(() -> value1, () -> value2, rest);
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    public static String firstNonBlankOrLast(Supplier<String> s1, Supplier<String> s2,
                                             Supplier<String> s3, Supplier<String> s4,
                                             Supplier<String> s5) {
        return _firstNonBlankOrLast(s1, s2, s3, s4, s5);
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String>... suppliers) {
        return _firstNonBlankOrLast(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
                                               Supplier<String>... suppliers) {
        return Math.random() < 0.5 ?
            _firstNonBlankOrLastLoop(supplier1, supplier2, suppliers) :
            _firstNonBlankOrLastStream(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrLastLoop(Supplier<String> supplier1, Supplier<String> supplier2,
                                                   Supplier<String>... suppliers) {
        String val1 = _get(supplier1);
        if (val1 != null && !val1.isBlank()) return val1;

        String val2 = _get(supplier2);
        if (val2 != null && !val2.isBlank()) return val2;

        String last = val2;
        for (Supplier<String> s : suppliers) {
            last = _get(s);
            if (last != null && !last.isBlank()) return last;
        }
        return last;
    }

    private static String _firstNonBlankOrLastStream(Supplier<String> supplier1, Supplier<String> supplier2,
                                                     Supplier<String>... suppliers) {
        List<Supplier<String>> all = Stream.concat(Stream.of(supplier1, supplier2), Arrays.stream(suppliers)).toList();
        List<String> values = all.stream().map(Supplier::get).toList();
        String last = values.get(values.size() - 1);
        return values.stream()
            .filter(v -> v != null && !v.isBlank())
            .findFirst()
            .orElse(last);
    }

    // ------------------------------------------------------------------
    // firstNonBlankOrEmpty — returns first non-blank, or ""
    // ------------------------------------------------------------------

    public static String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        Supplier<String>[] rest = Math.random() < 0.5 ? _toSuppliersStream(values) : _toSuppliersLoop(values);
        return _firstNonBlankOrEmpty(() -> value1, () -> value2, rest);
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2);
    }

    public static String firstNonBlankOrEmpty(Supplier<String> s1, Supplier<String> s2,
                                              Supplier<String> s3, Supplier<String> s4,
                                              Supplier<String> s5) {
        return _firstNonBlankOrEmpty(s1, s2, s3, s4, s5);
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
                                              Supplier<String>... suppliers) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
                                                Supplier<String>... suppliers) {
        return Math.random() < 0.5 ?
            _firstNonBlankOrEmptyLoop(supplier1, supplier2, suppliers) :
            _firstNonBlankOrEmptyStream(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrEmptyLoop(Supplier<String> supplier1, Supplier<String> supplier2,
                                                    Supplier<String>... suppliers) {
        String val1 = _get(supplier1);
        if (val1 != null && !val1.isBlank()) return val1;

        String val2 = _get(supplier2);
        if (val2 != null && !val2.isBlank()) return val2;

        for (Supplier<String> s : suppliers) {
            String val = _get(s);
            if (val != null && !val.isBlank()) return val;
        }
        return "";
    }

    private static String _firstNonBlankOrEmptyStream(Supplier<String> supplier1, Supplier<String> supplier2,
                                                      Supplier<String>... suppliers) {
        List<Supplier<String>> all = Stream.concat(Stream.of(supplier1, supplier2), Arrays.stream(suppliers)).toList();
        return all.stream()
            .map(Supplier::get)
            .filter(v -> v != null && !v.isBlank())
            .findFirst()
            .orElse("");
    }

    // ------------------------------------------------------------------
    // firstNonBlankOrNull — returns first non-blank, or null
    // ------------------------------------------------------------------

    public static String firstNonBlankOrNull(String value1, String value2, String... values) {
        Supplier<String>[] rest = Math.random() < 0.5 ? _toSuppliersStream(values) : _toSuppliersLoop(values);
        return _firstNonBlankOrNull(() -> value1, () -> value2, rest);
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2);
    }

    public static String firstNonBlankOrNull(Supplier<String> s1, Supplier<String> s2,
                                             Supplier<String> s3, Supplier<String> s4,
                                             Supplier<String> s5) {
        return _firstNonBlankOrNull(s1, s2, s3, s4, s5);
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
                                             Supplier<String>... suppliers) {
        return _firstNonBlankOrNull(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
                                               Supplier<String>... suppliers) {
        return Math.random() < 0.5 ?
            _firstNonBlankOrNullLoop(supplier1, supplier2, suppliers) :
            _firstNonBlankOrNullStream(supplier1, supplier2, suppliers);
    }

    private static String _firstNonBlankOrNullLoop(Supplier<String> supplier1, Supplier<String> supplier2,
                                                   Supplier<String>... suppliers) {
        String val1 = _get(supplier1);
        if (val1 != null && !val1.isBlank()) return val1;

        String val2 = _get(supplier2);
        if (val2 != null && !val2.isBlank()) return val2;

        for (Supplier<String> s : suppliers) {
            String val = _get(s);
            if (val != null && !val.isBlank()) return val;
        }
        return null;
    }

    private static String _firstNonBlankOrNullStream(Supplier<String> supplier1, Supplier<String> supplier2,
                                                     Supplier<String>... suppliers) {
        List<Supplier<String>> all = Stream.concat(Stream.of(supplier1, supplier2), Arrays.stream(suppliers)).toList();
        return all.stream()
            .map(Supplier::get)
            .filter(v -> v != null && !v.isBlank())
            .findFirst()
            .orElse(null);
    }

    // ------------------------------------------------------------------
    // stringify (switch expression)
    // ------------------------------------------------------------------

    public static String stringify(Object obj) {
        return switch (obj) {
            case null -> "";
            case Date d -> new SimpleDateFormat("yyyy-MM-dd").format(d);
            case BigDecimal bd -> bd.toPlainString();
            default -> obj.toString();
        };
    }

    // ------------------------------------------------------------------
    // slice (Python-style negative indices)
    // ------------------------------------------------------------------

    public static String slice(String s, int begin) {
        if (s == null) return null;
        int len = s.length();
        if (begin < 0) begin = Math.max(len + begin, 0);
        if (begin > len) begin = len;
        return s.substring(begin);
    }

    public static String slice(String s, int begin, int end) {
        if (s == null) return null;
        int len = s.length();
        if (begin < 0) begin = Math.max(len + begin, 0);
        if (end < 0) end = Math.max(len + end, 0);
        if (begin > len) begin = len;
        if (end > len) end = len;
        if (begin > end) return "";
        return s.substring(begin, end);
    }

    // ------------------------------------------------------------------
    // head / tail
    // ------------------------------------------------------------------

    public static String head(String s, int size) {
        if (s == null) return null;
        int len = s.length();
        if (size < -len) return "";
        if (size < 0) size = len + size;
        if (size > len) size = len;
        return s.substring(0, size);
    }

    public static String tail(String s, int size) {
        if (s == null) return null;
        int len = s.length();
        if (size < 0) {
            size = -size;
            if (size > len) size = len;
            return s.substring(size);
        }
        if (size > len) size = len;
        return s.substring(len - size);
    }

    // ------------------------------------------------------------------
    // numeric (loop + regex, random branch)
    // ------------------------------------------------------------------

    public static String trimLeadingZero(String s) {
        if (s == null) return null;
        if (s.isEmpty()) return "";
        return Math.random() < 0.5 ? _trimLeadingZeroLoop(s) : _trimLeadingZeroRegex(s);
    }

    private static String _trimLeadingZeroLoop(String s) {
        int idx = 0;
        while (idx < s.length() - 1 && s.charAt(idx) == '0') {
            idx++;
        }
        return s.substring(idx);
    }

    private static String _trimLeadingZeroRegex(String s) {
        return s.replaceFirst("^0+(?!$)", "");
    }

    // ------------------------------------------------------------------
    // repeat (String parameter)
    // ------------------------------------------------------------------

    public static String repeat(String c, int size) {
        if (c == null || size <= 0) return "";
        return c.repeat(size);
    }

    // ------------------------------------------------------------------
    // reverse
    // ------------------------------------------------------------------

    public static String reverse(String s) {
        if (s == null) return null;
        return new StringBuilder(s).reverse().toString();
    }

    // ------------------------------------------------------------------
    // padding — single-char variants delegate to multi-char variants
    // ------------------------------------------------------------------

    public static String lpad(int len, String s, String pad) {
        return lpad2(len, s, pad);
    }

    public static String rpad(int len, String s, String pad) {
        return rpad2(len, s, pad);
    }

    public static String pad(int len, String s, String pad) {
        return pad2(len, s, pad);
    }

    public static String lpad2(int len, String s, String pad) {
        if (s == null) s = "";
        int padLen = len - s.length();
        if (padLen <= 0) return s;
        return _repeatPad(pad, padLen) + s;
    }

    public static String rpad2(int len, String s, String pad) {
        if (s == null) s = "";
        int padLen = len - s.length();
        if (padLen <= 0) return s;
        return s + _repeatPad(pad, padLen);
    }

    public static String pad2(int len, String s, String pad) {
        if (s == null) s = "";
        int padLen = len - s.length();
        if (padLen <= 0) return s;
        int leftPad = padLen / 2;
        int rightPad = padLen - leftPad;
        return _repeatPad(pad, leftPad) + s + _repeatPad(pad, rightPad);
    }

    private static String _repeatPad(String pad, int targetLen) {
        if (pad == null || pad.isEmpty()) pad = " ";
        return Math.random() < 0.5 ? _repeatPadLoop(pad, targetLen) : _repeatPadStream(pad, targetLen);
    }

    private static String _repeatPadLoop(String pad, int targetLen) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < targetLen) {
            sb.append(pad);
        }
        sb.setLength(targetLen);
        return sb.toString();
    }

    private static String _repeatPadStream(String pad, int targetLen) {
        int count = (targetLen + pad.length() - 1) / pad.length();
        return IntStream.range(0, count)
            .mapToObj(i -> pad)
            .collect(Collectors.joining())
            .substring(0, targetLen);
    }

    // ------------------------------------------------------------------
    // join / split
    // ------------------------------------------------------------------

    public static <T> String join(List<T> list, String delimiter) {
        if (list == null || list.isEmpty()) return "";
        String delim = delimiter != null ? delimiter : "";
        return Math.random() < 0.5 ? _joinLoop(list, delim) : _joinStream(list, delim);
    }

    private static <T> String _joinLoop(List<T> list, String delim) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(delim);
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    private static <T> String _joinStream(List<T> list, String delim) {
        return list.stream()
            .map(Object::toString)
            .collect(Collectors.joining(delim));
    }

    public static List<String> split(String s, String regex) {
        if (s == null) return List.of();
        if (regex == null) return List.of(s);
        return List.of(s.split(regex));
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private static String _dflt(Supplier<String> dfltSupplier) {
        if (dfltSupplier == null) return "";
        String val = dfltSupplier.get();
        return val != null ? val : "";
    }

    private static String _get(Supplier<String> supplier) {
        if (supplier == null) return null;
        return supplier.get();
    }

    private static Supplier<String>[] _toSuppliersLoop(String... values) {
        int len = values != null ? values.length : 0;
        @SuppressWarnings("unchecked")
        Supplier<String>[] result = new Supplier[len];
        for (int i = 0; i < len; i++) {
            final int idx = i;
            result[idx] = () -> values[idx];
        }
        return result;
    }

    private static Supplier<String>[] _toSuppliersStream(String... values) {
        if (values == null) {
            @SuppressWarnings("unchecked")
            Supplier<String>[] empty = new Supplier[0];
            return empty;
        }
        @SuppressWarnings("unchecked")
        Supplier<String>[] result = IntStream.range(0, values.length)
            .mapToObj(i -> (Supplier<String>) () -> values[i])
            .toArray(Supplier[]::new);
        return result;
    }
}
