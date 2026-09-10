package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

public final class StringUtil {

    private StringUtil() {
    }

    public static boolean isBlank(final String s) {
        if (s == null) {
            return true;
        }
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
        if (obj instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd").format((Date) obj);
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toPlainString();
        }
        return obj.toString();
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
        if (size <= 0) {
            return "";
        }
        if (size >= s.length()) {
            return s;
        }
        return s.substring(0, size);
    }

    public static String tail(final String s, final int size) {
        if (s == null) {
            return null;
        }
        if (size <= 0) {
            return "";
        }
        if (size >= s.length()) {
            return s;
        }
        return s.substring(s.length() - size);
    }

    public static String trimLeadingZero(final String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        int i = 0;
        while (i < s.length() && s.charAt(i) == '0') {
            i++;
        }
        if (i == s.length()) {
            return "0";
        }
        // 소수점인 경우 앞에 0 하나 남기기
        if (i > 0 && i < s.length() && s.charAt(i) == '.') {
            return "0" + s.substring(i);
        }
        return s.substring(i);
    }

    public static String repeat(final char c, final int size) {
        if (size <= 0) {
            return "";
        }
        final char[] arr = new char[size];
        for (int i = 0; i < size; i++) {
            arr[i] = c;
        }
        return new String(arr);
    }

    public static String repeat(final String s, final int size) {
        if (s == null || size <= 0) {
            return "";
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

    public static String lpad(final int len, final String s, final char pad) {
        if (len <= 0) {
            return "";
        }
        if (s == null) {
            return repeat(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return repeat(pad, len - s.length()) + s;
    }

    public static String rpad(final int len, final String s, final char pad) {
        if (len <= 0) {
            return "";
        }
        if (s == null) {
            return repeat(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        return s + repeat(pad, len - s.length());
    }

    public static String pad(final int len, final String s, final char pad) {
        if (len <= 0) {
            return "";
        }
        if (s == null) {
            return repeat(pad, len);
        }
        if (s.length() >= len) {
            return s;
        }
        final int totalPad = len - s.length();
        final int leftPad = totalPad / 2;
        final int rightPad = totalPad - leftPad;
        return repeat(pad, leftPad) + s + repeat(pad, rightPad);
    }

    public static String lpad2(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        if (s == null) {
            if (pad == null || pad.isEmpty()) {
                return "";
            }
            final StringBuilder sb = new StringBuilder(len);
            int remaining = len;
            while (remaining > 0) {
                if (remaining >= pad.length()) {
                    sb.append(pad);
                    remaining -= pad.length();
                } else {
                    sb.append(pad, 0, remaining);
                    remaining = 0;
                }
            }
            return sb.toString();
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        if (s.length() >= len) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(len);
        int remaining = len - s.length();
        while (remaining > 0) {
            if (remaining >= pad.length()) {
                sb.append(pad);
                remaining -= pad.length();
            } else {
                sb.append(pad, 0, remaining);
                remaining = 0;
            }
        }
        sb.append(s);
        return sb.toString();
    }

    public static String rpad2(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        if (s == null) {
            if (pad == null || pad.isEmpty()) {
                return "";
            }
            final StringBuilder sb = new StringBuilder(len);
            int remaining = len;
            while (remaining > 0) {
                if (remaining >= pad.length()) {
                    sb.append(pad);
                    remaining -= pad.length();
                } else {
                    sb.append(pad, 0, remaining);
                    remaining = 0;
                }
            }
            return sb.toString();
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        if (s.length() >= len) {
            return s;
        }
        final StringBuilder sb = new StringBuilder(len);
        sb.append(s);
        int remaining = len - s.length();
        while (remaining > 0) {
            if (remaining >= pad.length()) {
                sb.append(pad);
                remaining -= pad.length();
            } else {
                sb.append(pad, 0, remaining);
                remaining = 0;
            }
        }
        return sb.toString();
    }

    public static String pad2(final int len, final String s, final String pad) {
        if (len <= 0) {
            return "";
        }
        if (s == null) {
            if (pad == null || pad.isEmpty()) {
                return "";
            }
            final StringBuilder sb = new StringBuilder(len);
            int remaining = len;
            while (remaining > 0) {
                if (remaining >= pad.length()) {
                    sb.append(pad);
                    remaining -= pad.length();
                } else {
                    sb.append(pad, 0, remaining);
                    remaining = 0;
                }
            }
            return sb.toString();
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        if (s.length() >= len) {
            return s;
        }
        final int totalPad = len - s.length();
        final int leftPad = totalPad / 2;
        final int rightPad = totalPad - leftPad;
        final StringBuilder sb = new StringBuilder(len);
        int remaining = leftPad;
        while (remaining > 0) {
            if (remaining >= pad.length()) {
                sb.append(pad);
                remaining -= pad.length();
            } else {
                sb.append(pad, 0, remaining);
                remaining = 0;
            }
        }
        sb.append(s);
        remaining = rightPad;
        while (remaining > 0) {
            if (remaining >= pad.length()) {
                sb.append(pad);
                remaining -= pad.length();
            } else {
                sb.append(pad, 0, remaining);
                remaining = 0;
            }
        }
        return sb.toString();
    }

    public static <T> String join(final List<T> list, final String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        if (delimiter == null) {
            return "";
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
            return new ArrayList<>();
        }
        if (regex == null || regex.isEmpty()) {
            final List<String> result = new ArrayList<>();
            result.add(s);
            return result;
        }
        final String[] parts = s.split(regex);
        final List<String> result = new ArrayList<>(parts.length);
        for (final String part : parts) {
            result.add(part);
        }
        return result;
    }
}