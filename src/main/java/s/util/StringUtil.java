package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    // ------------------------------------------------------------------
    // Boolean checks
    // ------------------------------------------------------------------

    public static boolean isBlank(String s) {
        if (s == null) return true;
        return s.isEmpty() || s.trim().isEmpty();
    }

    public static boolean isEmpty(String s) {
        if (s == null) return true;
        return s.isEmpty();
    }

    // ------------------------------------------------------------------
    // Default-value helpers (value-based delegates to supplier-based)
    // ------------------------------------------------------------------

    public static String nonNullOf(String value, String dflt) {
        return nonNullOf(() -> value, () -> dflt);
    }

    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) return _dflt(dfltSupplier);
        String val = supplier.get();
        if (val != null) return val;
        return _dflt(dfltSupplier);
    }

    public static String nonBlankOf(String value, String dflt) {
        return nonBlankOf(() -> value, () -> dflt);
    }

    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) return _dflt(dfltSupplier);
        String val = supplier.get();
        if (!isBlank(val)) return val;
        return _dflt(dfltSupplier);
    }

    public static String nonEmptyOf(String value, String dflt) {
        return nonEmptyOf(() -> value, () -> dflt);
    }

    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) return _dflt(dfltSupplier);
        String val = supplier.get();
        if (!isEmpty(val)) return val;
        return _dflt(dfltSupplier);
    }

    // ------------------------------------------------------------------
    // firstNonBlankOrLast — returns first non-blank, or the last value
    // ------------------------------------------------------------------

    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        @SuppressWarnings("unchecked")
        Supplier<String>[] rest = new Supplier[values != null ? values.length : 0];
        for (int i = 0; i < rest.length; i++) {
            final int idx = i;
            rest[idx] = () -> values[idx];
        }
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

    // ------------------------------------------------------------------
    // firstNonBlankOrEmpty — returns first non-blank, or ""
    // ------------------------------------------------------------------

    public static String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        @SuppressWarnings("unchecked")
        Supplier<String>[] rest = new Supplier[values != null ? values.length : 0];
        for (int i = 0; i < rest.length; i++) {
            final int idx = i;
            rest[idx] = () -> values[idx];
        }
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

    // ------------------------------------------------------------------
    // firstNonBlankOrNull — returns first non-blank, or null
    // ------------------------------------------------------------------

    public static String firstNonBlankOrNull(String value1, String value2, String... values) {
        @SuppressWarnings("unchecked")
        Supplier<String>[] rest = new Supplier[values != null ? values.length : 0];
        for (int i = 0; i < rest.length; i++) {
            final int idx = i;
            rest[idx] = () -> values[idx];
        }
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

    // ------------------------------------------------------------------
    // stringify
    // ------------------------------------------------------------------

    public static String stringify(Object obj) {
        if (obj == null) return "";
        if (obj instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd").format((Date) obj);
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toPlainString();
        }
        return obj.toString();
    }

    // ------------------------------------------------------------------
    // slice
    // ------------------------------------------------------------------

    public static String slice(String s, int begin) {
        if (s == null) return null;
        if (begin < 0) begin = 0;
        if (begin >= s.length()) return "";
        return s.substring(begin);
    }

    public static String slice(String s, int begin, int end) {
        if (s == null) return null;
        if (begin < 0) begin = 0;
        if (end > s.length()) end = s.length();
        if (begin > end) return "";
        return s.substring(begin, end);
    }

    // ------------------------------------------------------------------
    // head / tail
    // ------------------------------------------------------------------

    public static String head(String s, int size) {
        if (s == null) return null;
        if (size <= 0) return "";
        if (size >= s.length()) return s;
        return s.substring(0, size);
    }

    public static String tail(String s, int size) {
        if (s == null) return null;
        if (size <= 0) return "";
        if (size >= s.length()) return s;
        return s.substring(s.length() - size);
    }

    // ------------------------------------------------------------------
    // numeric
    // ------------------------------------------------------------------

    public static String trimLeadingZero(String s) {
        if (s == null) return null;
        if (s.isEmpty()) return "";
        int idx = 0;
        while (idx < s.length() - 1 && s.charAt(idx) == '0') {
            idx++;
        }
        return s.substring(idx);
    }

    // ------------------------------------------------------------------
    // repeat
    // ------------------------------------------------------------------

    public static String repeat(char c, int size) {
        if (size <= 0) return "";
        return String.valueOf(c).repeat(size);
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
        StringBuilder sb = new StringBuilder();
        while (sb.length() < targetLen) {
            sb.append(pad);
        }
        sb.setLength(targetLen);
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // join / split
    // ------------------------------------------------------------------

    public static <T> String join(List<T> list, String delimiter) {
        if (list == null || list.isEmpty()) return "";
        if (delimiter == null) delimiter = "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(delimiter);
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public static List<String> split(String s, String regex) {
        if (s == null) return Collections.emptyList();
        if (regex == null) return Collections.singletonList(s);
        String[] parts = s.split(regex);
        return new ArrayList<>(Arrays.asList(parts));
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
}
