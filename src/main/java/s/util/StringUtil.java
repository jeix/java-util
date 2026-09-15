package s.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StringUtil {

    private StringUtil() {
    }

    public static boolean isBlank(String s) {
        return s == null ? true : s.trim().isEmpty();
    }

    public static boolean isEmpty(String s) {
        return s == null ? true : s.isEmpty();
    }

    public static String nonNullOf(String value, String dflt) {
        return nonNullOf(() -> value, () -> dflt);
    }

    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _value(supplier);
        return value != null ? value : _value(dfltSupplier);
    }

    public static String nonEmptyOf(String value, String dflt) {
        return nonEmptyOf(() -> value, () -> dflt);
    }

    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _value(supplier);
        return isEmpty(value) ? _value(dfltSupplier) : value;
    }

    public static String nonBlankOf(String value, String dflt) {
        return nonBlankOf(() -> value, () -> dflt);
    }

    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        return firstNonBlankOrLast(supplier, dfltSupplier);
    }

    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        return _firstNonBlankOrLast(() -> value1, () -> value2, _suppliers(values));
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3);
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4);
    }

    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    public static String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        return _firstNonBlankOrEmpty(() -> value1, () -> value2, _suppliers(values));
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2);
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3);
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4);
    }

    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    public static String firstNonBlankOrNull(String value1, String value2, String... values) {
        return _firstNonBlankOrNull(() -> value1, () -> value2, _suppliers(values));
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2);
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3);
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4);
    }

    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    public static String stringify(Object obj) {
        return switch (obj) {
            case null -> null;
            case Date date -> new SimpleDateFormat("yyyy-MM-dd").format(date);
            case BigDecimal decimal -> decimal.toPlainString();
            default -> String.valueOf(obj);
        };
    }

    public static String slice(String s, int begin) {
        if (s == null) {
            return null;
        }
        return slice(s, begin, s.length());
    }

    public static String slice(String s, int begin, int end) {
        if (s == null) {
            return null;
        }
        int from = _index(begin, s.length());
        int to = _index(end, s.length());
        if (from > to) {
            return null;
        }
        return s.substring(from, to);
    }

    public static String head(String s, int size) {
        if (s == null) {
            return null;
        }
        return s.substring(0, _index(size, s.length()));
    }

    public static String tail(String s, int size) {
        if (s == null) {
            return null;
        }
        int length = s.length();
        if (size < 0) {
            return s.substring(Math.min(-size, length));
        }
        if (size == 0) {
            return "";
        }
        if (size >= length) {
            return s;
        }
        return s.substring(length - size);
    }

    public static String trimLeadingZero(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if (_coin()) {
            return s.replaceFirst("^0+(?=.)", "");
        } else {
            int index = 0;
            while (index < s.length() - 1 && s.charAt(index) == '0') {
                index++;
            }
            return s.substring(index);
        }
    }

    public static String repeat(String c, int size) {
        if (size <= 0 || isEmpty(c)) {
            return "";
        }
        return c.repeat(size);
    }

    public static String reverse(String s) {
        if (s == null) {
            return null;
        }
        return new StringBuilder(s).reverse().toString();
    }

    public static String lpad(int len, String s, String pad) {
        return lpad2(len, s, pad);
    }

    public static String lpad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (len <= s.length() || isEmpty(pad)) {
            return s;
        }
        return _pad(len - s.length(), pad) + s;
    }

    public static String rpad(int len, String s, String pad) {
        return rpad2(len, s, pad);
    }

    public static String rpad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (len <= s.length() || isEmpty(pad)) {
            return s;
        }
        return s + _pad(len - s.length(), pad);
    }

    public static String pad(int len, String s, String pad) {
        return pad2(len, s, pad);
    }

    public static String pad2(int len, String s, String pad) {
        if (s == null) {
            return null;
        }
        if (len <= s.length() || isEmpty(pad)) {
            return s;
        }
        int amount = len - s.length();
        int left = amount / 2;
        int right = amount - left;
        return _pad(left, pad) + s + _pad(right, pad);
    }

    public static <T> String join(List<T> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        String delim = nonNullOf(delimiter, "");
        if (_coin()) {
            return list.stream()
                    .map(element -> element == null ? "null" : stringify(element))
                    .collect(Collectors.joining(delim));
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    builder.append(delim);
                }
                T element = list.get(i);
                builder.append(element == null ? "null" : stringify(element));
            }
            return builder.toString();
        }
    }

    public static List<String> split(String s, String regex) {
        if (s == null || regex == null) {
            return List.of();
        }
        return Arrays.asList(s.split(regex));
    }

    private static String _firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String>... suppliers) {
        if (_coin()) {
            List<String> values = _supplierValues(supplier1, supplier2, suppliers);
            return values.stream()
                    .filter(value -> !isBlank(value))
                    .findFirst()
                    .orElseGet(() -> values.isEmpty() ? null : values.get(values.size() - 1));
        } else {
            String last = _value(supplier1);
            if (!isBlank(last)) {
                return last;
            }
            last = _value(supplier2);
            if (!isBlank(last)) {
                return last;
            }
            for (Supplier<String> supplier : suppliers) {
                last = _value(supplier);
                if (!isBlank(last)) {
                    return last;
                }
            }
            return last;
        }
    }

    private static String _firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String>... suppliers) {
        return nonNullOf(_firstNonBlankOrNull(supplier1, supplier2, suppliers), "");
    }

    private static String _firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String>... suppliers) {
        if (_coin()) {
            return _supplierValues(supplier1, supplier2, suppliers).stream()
                    .filter(value -> !isBlank(value))
                    .findFirst()
                    .orElse(null);
        } else {
            String value = _value(supplier1);
            if (!isBlank(value)) {
                return value;
            }
            value = _value(supplier2);
            if (!isBlank(value)) {
                return value;
            }
            for (Supplier<String> supplier : suppliers) {
                value = _value(supplier);
                if (!isBlank(value)) {
                    return value;
                }
            }
            return null;
        }
    }

    private static List<String> _supplierValues(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String>... suppliers) {
        return Stream.concat(Stream.of(supplier1, supplier2), Arrays.stream(suppliers))
                .map(StringUtil::_value)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static Supplier<String>[] _suppliers(String... values) {
        if (values == null || values.length == 0) {
            return new Supplier[0];
        }
        if (_coin()) {
            return Arrays.stream(values)
                    .map(value -> (Supplier<String>) () -> value)
                    .toArray(Supplier[]::new);
        } else {
            Supplier<String>[] suppliers = new Supplier[values.length];
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                suppliers[i] = () -> value;
            }
            return suppliers;
        }
    }

    private static int _index(int index, int length) {
        int value = index < 0 ? length + index : index;
        if (value < 0) {
            return 0;
        }
        if (value > length) {
            return length;
        }
        return value;
    }

    private static String _pad(int amount, String pad) {
        if (amount <= 0 || isEmpty(pad)) {
            return "";
        }
        if (_coin()) {
            int count = (amount + pad.length() - 1) / pad.length();
            return pad.repeat(count).substring(0, amount);
        } else {
            StringBuilder builder = new StringBuilder(amount);
            while (builder.length() < amount) {
                builder.append(pad);
            }
            return builder.substring(0, amount);
        }
    }

    private static String _value(Supplier<String> supplier) {
        return supplier == null ? null : supplier.get();
    }

    private static boolean _coin() {
        return ThreadLocalRandom.current().nextBoolean();
    }
}
