package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    private static final Pattern TRIM_LEADING_ZERO_PATTERN = Pattern.compile("^0+(?!$)");

    public boolean isBlank(String s) {
        if (s == null) {
            return true;
        }
        return s.trim().isEmpty();
    }

    public boolean isEmpty(String s) {
        if (s == null) {
            return true;
        }
        return s.isEmpty();
    }

    public String nonNullOf(String value, String dflt) {
        if (value == null) {
            return dflt;
        }
        return value;
    }

    public String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier.get();
        }
        String value = supplier.get();
        if (value == null) {
            return dfltSupplier.get();
        }
        return value;
    }

    public String nonBlankOf(String value, String dflt) {
        if (isBlank(value)) {
            return dflt;
        }
        return value;
    }

    public String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier.get();
        }
        String value = supplier.get();
        if (isBlank(value)) {
            return dfltSupplier.get();
        }
        return value;
    }

    public String nonEmptyOf(String value, String dflt) {
        if (isEmpty(value)) {
            return dflt;
        }
        return value;
    }

    public String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier.get();
        }
        String value = supplier.get();
        if (isEmpty(value)) {
            return dfltSupplier.get();
        }
        return value;
    }

    public String firstNonBlankOrLast(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return values != null && values.length > 0 ? values[values.length - 1] : null;
    }

    public String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    public String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3);
    }

    public String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4);
    }

    public String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    private String _firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>... suppliers) {
        if (supplier1 != null) {
            String value = supplier1.get();
            if (!isBlank(value)) {
                return value;
            }
        }
        if (supplier2 != null) {
            String value = supplier2.get();
            if (!isBlank(value)) {
                return value;
            }
        }
        if (suppliers != null) {
            for (Supplier<String> supplier : suppliers) {
                if (supplier != null) {
                    String value = supplier.get();
                    if (!isBlank(value)) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    public String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return "";
    }

    public String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2);
    }

    public String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3);
    }

    public String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4);
    }

    public String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    private String _firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>... suppliers) {
        if (supplier1 != null) {
            String value = supplier1.get();
            if (!isBlank(value)) {
                return value;
            }
        }
        if (supplier2 != null) {
            String value = supplier2.get();
            if (!isBlank(value)) {
                return value;
            }
        }
        if (suppliers != null) {
            for (Supplier<String> supplier : suppliers) {
                if (supplier != null) {
                    String value = supplier.get();
                    if (!isBlank(value)) {
                        return value;
                    }
                }
            }
        }
        return "";
    }

    public String firstNonBlankOrNull(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
        }
        return null;
    }

    public String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2);
    }

    public String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3);
    }

    public String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4);
    }

    public String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    private String _firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2, Supplier<String>... suppliers) {
        if (supplier1 != null) {
            String value = supplier1.get();
            if (!isBlank(value)) {
                return value;
            }
        }
        if (supplier2 != null) {
            String value = supplier2.get();
            if (!isBlank(value)) {
                return value;
            }
        }
        if (suppliers != null) {
            for (Supplier<String> supplier : suppliers) {
                if (supplier != null) {
                    String value = supplier.get();
                    if (!isBlank(value)) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    public String stringify(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format((Date) obj);
        }
        if (obj instanceof BigDecimal) {
            return ((BigDecimal) obj).toPlainString();
        }
        return obj.toString();
    }

    public String slice(String s, int begin) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (begin < 0) {
            begin = len + begin;
            if (begin < 0) {
                begin = 0;
            }
        }
        if (begin > len) {
            begin = len;
        }
        return s.substring(begin);
    }

    public String slice(String s, int begin, int end) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (begin < 0) {
            begin = len + begin;
            if (begin < 0) {
                begin = 0;
            }
        }
        if (end < 0) {
            end = len + end;
            if (end < 0) {
                end = 0;
            }
        }
        if (begin > len) {
            begin = len;
        }
        if (end > len) {
            end = len;
        }
        if (begin > end) {
            begin = end;
        }
        return s.substring(begin, end);
    }

    public String head(String s, int size) {
        if (s == null) {
            return null;
        }
        if (size < 0) {
            return "";
        }
        if (size > s.length()) {
            return s;
        }
        return s.substring(0, size);
    }

    public String tail(String s, int size) {
        if (s == null) {
            return null;
        }
        if (size < 0) {
            return "";
        }
        int len = s.length();
        if (size > len) {
            return s;
        }
        return s.substring(len - size);
    }

    public String trimLeadingZero(String s) {
        if (s == null) {
            return null;
        }
        return TRIM_LEADING_ZERO_PATTERN.matcher(s).replaceFirst("");
    }

    public String repeat(char c, int size) {
        if (size <= 0) {
            return "";
        }
        char[] chars = new char[size];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    public String reverse(String s) {
        if (s == null) {
            return null;
        }
        return new StringBuilder(s).reverse().toString();
    }

    public String lpad(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        int currentLen = s.length();
        if (currentLen >= len) {
            return s;
        }
        int padCount = len - currentLen;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padCount; i++) {
            sb.append(pad.charAt(0));
        }
        sb.append(s);
        return sb.toString();
    }

    public String rpad(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        int currentLen = s.length();
        if (currentLen >= len) {
            return s;
        }
        int padCount = len - currentLen;
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < padCount; i++) {
            sb.append(pad.charAt(0));
        }
        return sb.toString();
    }

    public String pad(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        int currentLen = s.length();
        if (currentLen >= len) {
            return s;
        }
        int padCount = len - currentLen;
        int leftPad = padCount / 2;
        int rightPad = padCount - leftPad;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < leftPad; i++) {
            sb.append(pad.charAt(0));
        }
        sb.append(s);
        for (int i = 0; i < rightPad; i++) {
            sb.append(pad.charAt(0));
        }
        return sb.toString();
    }

    public String lpad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        int currentLen = s.length();
        if (currentLen >= len) {
            return s;
        }
        int padCount = len - currentLen;
        StringBuilder sb = new StringBuilder();
        while (sb.length() < padCount) {
            sb.append(pad);
        }
        sb.setLength(padCount);
        sb.append(s);
        return sb.toString();
    }

    public String rpad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        int currentLen = s.length();
        if (currentLen >= len) {
            return s;
        }
        int padCount = len - currentLen;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < s.length() + padCount) {
            sb.append(pad);
        }
        sb.setLength(s.length() + padCount);
        return sb.toString();
    }

    public String pad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (pad == null || pad.isEmpty()) {
            return s;
        }
        int currentLen = s.length();
        if (currentLen >= len) {
            return s;
        }
        int padCount = len - currentLen;
        int leftPad = padCount / 2;
        int rightPad = padCount - leftPad;
        StringBuilder left = new StringBuilder();
        while (left.length() < leftPad) {
            left.append(pad);
        }
        left.setLength(leftPad);
        StringBuilder right = new StringBuilder();
        while (right.length() < rightPad) {
            right.append(pad);
        }
        right.setLength(rightPad);
        return left.toString() + s + right.toString();
    }

    public <T> String join(List<T> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        if (delimiter == null) {
            delimiter = "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public List<String> split(String s, String regex) {
        if (s == null) {
            return new ArrayList<>();
        }
        if (regex == null) {
            return new ArrayList<>(List.of(s));
        }
        String[] parts = s.split(regex, -1);
        return new ArrayList<>(Arrays.asList(parts));
    }
}
