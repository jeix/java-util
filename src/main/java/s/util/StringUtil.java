package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    private static final Pattern TRIM_LEADING_ZERO_PATTERN = Pattern.compile("^0+(?!$)");
    private static final Random RANDOM = new Random();

    public boolean isBlank(String s) {
        return s == null ? true : s.trim().isEmpty();
    }

    public boolean isEmpty(String s) {
        return s == null ? true : s.isEmpty();
    }

    public String nonNullOf(String value, String dflt) {
        return value == null ? dflt : value;
    }

    public String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier.get();
        }
        String value = supplier.get();
        return value == null ? dfltSupplier.get() : value;
    }

    public String nonBlankOf(String value, String dflt) {
        return isBlank(value) ? dflt : value;
    }

    public String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier.get();
        }
        String value = supplier.get();
        return isBlank(value) ? dfltSupplier.get() : value;
    }

    public String nonEmptyOf(String value, String dflt) {
        return isEmpty(value) ? dflt : value;
    }

    public String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        if (supplier == null) {
            return dfltSupplier.get();
        }
        String value = supplier.get();
        return isEmpty(value) ? dfltSupplier.get() : value;
    }

    public String firstNonBlankOrLast(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        if (RANDOM.nextBoolean()) {
            return Arrays.stream(values)
                    .filter(v -> !isBlank(v))
                    .findFirst()
                    .orElse(values != null && values.length > 0 ? values[values.length - 1] : null);
        } else {
            for (String value : values) {
                if (!isBlank(value)) {
                    return value;
                }
            }
            return values != null && values.length > 0 ? values[values.length - 1] : null;
        }
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
        List<Supplier<String>> all = new ArrayList<>();
        if (supplier1 != null) all.add(supplier1);
        if (supplier2 != null) all.add(supplier2);
        if (suppliers != null) {
            for (Supplier<String> s : suppliers) {
                if (s != null) all.add(s);
            }
        }
        if (RANDOM.nextBoolean()) {
            return all.stream()
                    .map(Supplier::get)
                    .filter(v -> !isBlank(v))
                    .findFirst()
                    .orElse(null);
        } else {
            for (Supplier<String> supplier : all) {
                String value = supplier.get();
                if (!isBlank(value)) {
                    return value;
                }
            }
            return null;
        }
    }

    public String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        if (RANDOM.nextBoolean()) {
            return Arrays.stream(values)
                    .filter(v -> !isBlank(v))
                    .findFirst()
                    .orElse("");
        } else {
            for (String value : values) {
                if (!isBlank(value)) {
                    return value;
                }
            }
            return "";
        }
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
        List<Supplier<String>> all = new ArrayList<>();
        if (supplier1 != null) all.add(supplier1);
        if (supplier2 != null) all.add(supplier2);
        if (suppliers != null) {
            for (Supplier<String> s : suppliers) {
                if (s != null) all.add(s);
            }
        }
        if (RANDOM.nextBoolean()) {
            return all.stream()
                    .map(Supplier::get)
                    .filter(v -> !isBlank(v))
                    .findFirst()
                    .orElse("");
        } else {
            for (Supplier<String> supplier : all) {
                String value = supplier.get();
                if (!isBlank(value)) {
                    return value;
                }
            }
            return "";
        }
    }

    public String firstNonBlankOrNull(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        if (RANDOM.nextBoolean()) {
            return Arrays.stream(values)
                    .filter(v -> !isBlank(v))
                    .findFirst()
                    .orElse(null);
        } else {
            for (String value : values) {
                if (!isBlank(value)) {
                    return value;
                }
            }
            return null;
        }
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
        List<Supplier<String>> all = new ArrayList<>();
        if (supplier1 != null) all.add(supplier1);
        if (supplier2 != null) all.add(supplier2);
        if (suppliers != null) {
            for (Supplier<String> s : suppliers) {
                if (s != null) all.add(s);
            }
        }
        if (RANDOM.nextBoolean()) {
            return all.stream()
                    .map(Supplier::get)
                    .filter(v -> !isBlank(v))
                    .findFirst()
                    .orElse(null);
        } else {
            for (Supplier<String> supplier : all) {
                String value = supplier.get();
                if (!isBlank(value)) {
                    return value;
                }
            }
            return null;
        }
    }

    public String stringify(Object obj) {
        if (obj == null) {
            return "null";
        }
        return switch (obj) {
            case Date d -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                yield sdf.format(d);
            }
            case BigDecimal bd -> bd.toPlainString();
            default -> obj.toString();
        };
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
        int len = s.length();
        if (size < 0) {
            size = len + size;
            if (size < 0) {
                size = 0;
            }
        }
        if (size > len) {
            size = len;
        }
        return s.substring(0, size);
    }

    public String tail(String s, int size) {
        if (s == null) {
            return null;
        }
        int len = s.length();
        if (size < 0) {
            size = -size;
            int beginIndex = len - size;
            if (beginIndex < 0) {
                beginIndex = 0;
            }
            return s.substring(beginIndex);
        }
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

    public String repeat(String c, int size) {
        if (size <= 0 || c == null || c.isEmpty()) {
            return "";
        }
        if (RANDOM.nextBoolean()) {
            return Stream.generate(() -> c)
                    .limit(size)
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString();
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                sb.append(c);
            }
            return sb.toString();
        }
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
        if (RANDOM.nextBoolean()) {
            return list.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(delimiter));
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append(delimiter);
                }
                sb.append(list.get(i));
            }
            return sb.toString();
        }
    }

    public List<String> split(String s, String regex) {
        if (s == null) {
            return Collections.emptyList();
        }
        if (regex == null) {
            return Collections.unmodifiableList(new ArrayList<>(List.of(s)));
        }
        String[] parts = s.split(regex, -1);
        return Collections.unmodifiableList(new ArrayList<>(Arrays.asList(parts)));
    }
}
