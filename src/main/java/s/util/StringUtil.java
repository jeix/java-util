package s.util;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 문자열을 다루는 유틸리티 메서드를 모아 둔 클래스입니다.
 *
 * <p>구현 규칙은 다음과 같습니다.
 *
 * <ul>
 *   <li>파라미터를 먼저 검사하고, 정상적으로 처리할 수 없는 경우는 그대로 리턴합니다.</li>
 *   <li>같은 기능이 이미 다른 메서드에 있으면 그 메서드를 호출합니다.</li>
 *   <li>오버로드된 메서드는 실질적인 구현 메서드 하나를 호출하고, 구현 메서드가 private이면 이름 앞에 {@code _}를 붙입니다.</li>
 *   <li>반복문 구현과 스트림 구현을 함께 두는 메서드는 실행 시점에 랜덤하게 분기합니다.</li>
 * </ul>
 */
public final class StringUtil {

    /** {@link #stringify(Object)}에서 {@link Date}를 변환할 때 사용하는 포맷입니다. */
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    private StringUtil() {
    }

    /**
     * 문자열이 {@code null}이거나 공백 문자만으로 이루어져 있으면 {@code true}를 반환합니다.
     *
     * @param s 검사할 문자열
     * @return {@code null} 또는 공백 문자열 여부
     */
    public static boolean isBlank(String s) {
        return s == null ? true : s.isBlank();
    }

    /**
     * 문자열이 {@code null}이거나 길이가 0이면 {@code true}를 반환합니다.
     *
     * @param s 검사할 문자열
     * @return {@code null} 또는 빈 문자열 여부
     */
    public static boolean isEmpty(String s) {
        return s == null ? true : s.isEmpty();
    }

    /**
     * 값이 {@code null}이면 기본값을, 아니면 값을 반환합니다.
     *
     * @param value 값
     * @param dflt  기본값
     * @return {@code null}이 아닌 값 또는 기본값
     */
    public static String nonNullOf(String value, String dflt) {
        return value != null ? value : dflt;
    }

    /**
     * 공급자가 제공한 값이 {@code null}이면 기본값 공급자의 값을 반환합니다.
     * 기본값은 앞의 값이 유효할 때는 사용하지 않습니다.
     *
     * @param supplier     값 공급자
     * @param dfltSupplier 기본값 공급자
     * @return {@code null}이 아닌 값 또는 기본값
     */
    public static String nonNullOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _get(supplier);
        return value != null ? value : _get(dfltSupplier);
    }

    /**
     * 값이 비어 있거나 공백 문자만으로 이루어져 있으면 기본값을, 아니면 값을 반환합니다.
     *
     * @param value 값
     * @param dflt  기본값
     * @return 공백이 아닌 값 또는 기본값
     */
    public static String nonBlankOf(String value, String dflt) {
        return !isBlank(value) ? value : dflt;
    }

    /**
     * 공급자가 제공한 값이 비어 있거나 공백 문자만으로 이루어져 있으면 기본값 공급자의 값을 반환합니다.
     * 기본값은 앞의 값이 유효할 때는 사용하지 않습니다.
     *
     * @param supplier     값 공급자
     * @param dfltSupplier 기본값 공급자
     * @return 공백이 아닌 값 또는 기본값
     */
    public static String nonBlankOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _get(supplier);
        return !isBlank(value) ? value : _get(dfltSupplier);
    }

    /**
     * 값이 {@code null}이거나 길이가 0이면 기본값을, 아니면 값을 반환합니다.
     *
     * @param value 값
     * @param dflt  기본값
     * @return 비어 있지 않은 값 또는 기본값
     */
    public static String nonEmptyOf(String value, String dflt) {
        return !isEmpty(value) ? value : dflt;
    }

    /**
     * 공급자가 제공한 값이 {@code null}이거나 길이가 0이면 기본값 공급자의 값을 반환합니다.
     * 기본값은 앞의 값이 유효할 때는 사용하지 않습니다.
     *
     * @param supplier     값 공급자
     * @param dfltSupplier 기본값 공급자
     * @return 비어 있지 않은 값 또는 기본값
     */
    public static String nonEmptyOf(Supplier<String> supplier, Supplier<String> dfltSupplier) {
        String value = _get(supplier);
        return !isEmpty(value) ? value : _get(dfltSupplier);
    }

    /**
     * 앞에서부터 공백이 아닌 첫 번째 값을 반환하고, 모두 공백이면 마지막 값을 반환합니다.
     *
     * @param value1 첫 번째 값
     * @param value2 두 번째 값
     * @param values 나머지 값
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    public static String firstNonBlankOrLast(String value1, String value2, String... values) {
        if (!isBlank(value1)) {
            return value1;
        }
        if (!isBlank(value2)) {
            return value2;
        }
        if (values == null) {
            return value2;
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _firstNonBlankOrLastByLoop(value2, values)
                : _firstNonBlankOrLastByStream(value2, values);
    }

    /**
     * 값 기반 {@code firstNonBlankOrLast}의 반복문 구현입니다.
     *
     * @param last   모두 공백일 때 반환할 마지막 값
     * @param values 나머지 값
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    private static String _firstNonBlankOrLastByLoop(String last, String... values) {
        for (String value : values) {
            if (!isBlank(value)) {
                return value;
            }
            last = value;
        }
        return last;
    }

    /**
     * 값 기반 {@code firstNonBlankOrLast}의 스트림 구현입니다.
     * 마지막 값은 스트림의 마지막 요소 위치에서 바로 꺼내므로 요소가 {@code null}이어도 안전합니다.
     *
     * @param last   모두 공백일 때 반환할 마지막 값
     * @param values 나머지 값
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    private static String _firstNonBlankOrLastByStream(String last, String... values) {
        return Stream.of(values)
                .filter(value -> !isBlank(value))
                .findFirst()
                .orElseGet(() -> values.length == 0 ? last : values[values.length - 1]);
    }

    /**
     * 앞에서부터 공백이 아닌 첫 번째 공급자의 값을 반환하고, 모두 공백이면 마지막 공급자의 값을 반환합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrLast(supplier1, supplier2);
    }

    /**
     * {@link #firstNonBlankOrLast(Supplier, Supplier)}와 같고 세 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3);
    }

    /**
     * {@link #firstNonBlankOrLast(Supplier, Supplier)}와 같고 네 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @param supplier4 네 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * {@link #firstNonBlankOrLast(Supplier, Supplier)}와 같고 다섯 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @param supplier4 네 번째 값 공급자
     * @param supplier5 다섯 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    public static String firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrLast(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * 공급자 기반 {@code firstNonBlankOrLast} 오버로드들의 실질적인 구현입니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param suppliers 나머지 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    private static String _firstNonBlankOrLast(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String>... suppliers) {
        String value1 = _get(supplier1);
        if (!isBlank(value1)) {
            return value1;
        }
        String value2 = _get(supplier2);
        if (!isBlank(value2)) {
            return value2;
        }
        if (suppliers == null) {
            return value2;
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _firstNonBlankOrLastSupplierByLoop(value2, suppliers)
                : _firstNonBlankOrLastSupplierByStream(value2, suppliers);
    }

    /**
     * 공급자 기반 {@code firstNonBlankOrLast}의 반복문 구현입니다.
     *
     * @param last      모두 공백일 때 반환할 마지막 값
     * @param suppliers 나머지 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    private static String _firstNonBlankOrLastSupplierByLoop(String last, Supplier<String>... suppliers) {
        for (Supplier<String> supplier : suppliers) {
            String value = _get(supplier);
            if (!isBlank(value)) {
                return value;
            }
            last = value;
        }
        return last;
    }

    /**
     * 공급자 기반 {@code firstNonBlankOrLast}의 스트림 구현입니다.
     * {@code findFirst()}가 단락 평가되므로 공백이 아닌 값을 찾으면 뒤의 공급자는 호출하지 않습니다.
     *
     * @param last      모두 공백일 때 반환할 마지막 값
     * @param suppliers 나머지 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 마지막 값
     */
    private static String _firstNonBlankOrLastSupplierByStream(String last, Supplier<String>... suppliers) {
        String[] lastValue = {last};
        return Stream.of(suppliers)
                .map(StringUtil::_get)
                .peek(value -> lastValue[0] = value)
                .filter(value -> !isBlank(value))
                .findFirst()
                .orElse(lastValue[0]);
    }

    /**
     * 앞에서부터 공백이 아닌 첫 번째 값을 반환하고, 모두 공백이면 빈 문자열을 반환합니다.
     *
     * @param value1 첫 번째 값
     * @param value2 두 번째 값
     * @param values 나머지 값
     * @return 공백이 아닌 첫 번째 값 또는 빈 문자열
     */
    public static String firstNonBlankOrEmpty(String value1, String value2, String... values) {
        String result = firstNonBlankOrLast(value1, value2, values);
        return !isBlank(result) ? result : "";
    }

    /**
     * 앞에서부터 공백이 아닌 첫 번째 공급자의 값을 반환하고, 모두 공백이면 빈 문자열을 반환합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 빈 문자열
     */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrEmpty(supplier1, supplier2);
    }

    /**
     * {@link #firstNonBlankOrEmpty(Supplier, Supplier)}와 같고 세 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 빈 문자열
     */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3);
    }

    /**
     * {@link #firstNonBlankOrEmpty(Supplier, Supplier)}와 같고 네 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @param supplier4 네 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 빈 문자열
     */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * {@link #firstNonBlankOrEmpty(Supplier, Supplier)}와 같고 다섯 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @param supplier4 네 번째 값 공급자
     * @param supplier5 다섯 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 빈 문자열
     */
    public static String firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrEmpty(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * 공급자 기반 {@code firstNonBlankOrEmpty} 오버로드들의 실질적인 구현입니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param suppliers 나머지 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 빈 문자열
     */
    private static String _firstNonBlankOrEmpty(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String>... suppliers) {
        String result = _firstNonBlankOrLast(supplier1, supplier2, suppliers);
        return !isBlank(result) ? result : "";
    }

    /**
     * 앞에서부터 공백이 아닌 첫 번째 값을 반환하고, 모두 공백이면 {@code null}을 반환합니다.
     *
     * @param value1 첫 번째 값
     * @param value2 두 번째 값
     * @param values 나머지 값
     * @return 공백이 아닌 첫 번째 값 또는 {@code null}
     */
    public static String firstNonBlankOrNull(String value1, String value2, String... values) {
        String result = firstNonBlankOrLast(value1, value2, values);
        return !isBlank(result) ? result : null;
    }

    /**
     * 앞에서부터 공백이 아닌 첫 번째 공급자의 값을 반환하고, 모두 공백이면 {@code null}을 반환합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 {@code null}
     */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2) {
        return _firstNonBlankOrNull(supplier1, supplier2);
    }

    /**
     * {@link #firstNonBlankOrNull(Supplier, Supplier)}와 같고 세 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 {@code null}
     */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3);
    }

    /**
     * {@link #firstNonBlankOrNull(Supplier, Supplier)}와 같고 네 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @param supplier4 네 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 {@code null}
     */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * {@link #firstNonBlankOrNull(Supplier, Supplier)}와 같고 다섯 번째 값 공급자까지 검사합니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param supplier3 세 번째 값 공급자
     * @param supplier4 네 번째 값 공급자
     * @param supplier5 다섯 번째 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 {@code null}
     */
    public static String firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String> supplier3, Supplier<String> supplier4, Supplier<String> supplier5) {
        return _firstNonBlankOrNull(supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * 공급자 기반 {@code firstNonBlankOrNull} 오버로드들의 실질적인 구현입니다.
     *
     * @param supplier1 첫 번째 값 공급자
     * @param supplier2 두 번째 값 공급자
     * @param suppliers 나머지 값 공급자
     * @return 공백이 아닌 첫 번째 값 또는 {@code null}
     */
    private static String _firstNonBlankOrNull(Supplier<String> supplier1, Supplier<String> supplier2,
            Supplier<String>... suppliers) {
        String result = _firstNonBlankOrLast(supplier1, supplier2, suppliers);
        return !isBlank(result) ? result : null;
    }

    /**
     * 객체를 문자열로 변환합니다.
     *
     * <ul>
     *   <li>{@code null}은 {@code null}로 반환합니다.</li>
     *   <li>{@link Date}는 {@code yyyy-MM-dd} 포맷으로 변환합니다.</li>
     *   <li>{@link BigDecimal}은 {@link BigDecimal#toPlainString()}의 결과로 변환합니다.</li>
     *   <li>그 외에는 {@link String#valueOf(Object)}의 결과로 변환합니다.</li>
     * </ul>
     *
     * @param obj 변환할 객체
     * @return 변환된 문자열
     */
    public static String stringify(Object obj) {
        return switch (obj) {
            case null -> null;
            case BigDecimal decimal -> decimal.toPlainString();
            case Date date -> DATE_FORMATTER.format(date.toInstant());
            default -> String.valueOf(obj);
        };
    }

    /**
     * 시작 인덱스(포함)부터 문자열 끝까지 잘라냅니다.
     * 시작 인덱스가 음수이면 뒤에서부터 세는 역방향 인덱스로 해석합니다(예: {@code -1}은 마지막 문자).
     * 역방향 인덱스가 문자열 앞을 벗어나면 0으로, 문자열 길이 이상이면 빈 문자열로 처리합니다.
     *
     * @param s     대상 문자열
     * @param begin 시작 인덱스(포함, 음수이면 역방향 인덱스)
     * @return 잘라낸 문자열
     */
    public static String slice(String s, int begin) {
        if (isEmpty(s)) {
            return s;
        }
        return slice(s, begin, s.length());
    }

    /**
     * 시작 인덱스(포함)부터 끝 인덱스(불포함)까지 잘라냅니다.
     * 인덱스가 음수이면 뒤에서부터 세는 역방향 인덱스로 해석합니다(예: {@code -1}은 마지막 문자 다음 위치).
     * 시작 인덱스는 0 이상으로, 끝 인덱스는 시작 인덱스 이상으로 보정합니다.
     *
     * @param s     대상 문자열
     * @param begin 시작 인덱스(포함, 음수이면 역방향 인덱스)
     * @param end   끝 인덱스(불포함, 음수이면 역방향 인덱스)
     * @return 잘라낸 문자열
     */
    public static String slice(String s, int begin, int end) {
        if (isEmpty(s)) {
            return s;
        }
        int from = _clamp(_toAbsoluteIndex(begin, s.length()), 0, s.length());
        int to = _clamp(_toAbsoluteIndex(end, s.length()), from, s.length());
        return s.substring(from, to);
    }

    /**
     * 문자열 앞에서부터 지정한 길이만큼 잘라냅니다.
     * 길이가 문자열 길이보다 크면 문자열 전체를 반환합니다.
     * 길이가 음수이면 뒤에서부터 세는 역방향 인덱스로 해석합니다(예: {@code -1}은 마지막 문자를 제외).
     *
     * @param s    대상 문자열
     * @param size 길이(음수이면 역방향 인덱스)
     * @return 잘라낸 문자열
     */
    public static String head(String s, int size) {
        return slice(s, 0, size);
    }

    /**
     * 문자열 뒤에서부터 지정한 길이만큼 잘라냅니다.
     * 길이가 문자열 길이보다 크면 문자열 전체를 반환합니다.
     * 길이가 음수이면 부호를 바꾼 값을 앞에서 제외할 인덱스로 해석합니다(예: {@code -2}는 앞의 두 문자를 제외).
     *
     * @param s    대상 문자열
     * @param size 길이(음수이면 앞에서 제외할 문자 수)
     * @return 잘라낸 문자열
     */
    public static String tail(String s, int size) {
        if (isEmpty(s)) {
            return s;
        }
        if (size < 0) {
            return slice(s, _clamp(-size, 0, s.length()));
        }
        return slice(s, s.length() - _clamp(size, 0, s.length()));
    }

    /**
     * 숫자 문자열 앞쪽의 0을 제거합니다. 부호는 유지하고, 0만 있는 경우에는 0 하나를 남깁니다.
     *
     * <p>{@code "000123"}은 {@code "123"}, {@code "-0012"}는 {@code "-12"}, {@code "00.5"}는 {@code "0.5"}가 됩니다.
     *
     * @param s 대상 문자열
     * @return 앞쪽 0을 제거한 문자열
     */
    public static String trimLeadingZero(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return s.replaceFirst("^([+-]?)0+(?=\\d)", "$1");
    }

    /**
     * 문자열을 지정한 길이만큼 반복한 문자열을 반환합니다.
     * 반복 결과가 길이를 넘으면 잘라서 반환합니다(예: {@code repeat("ab", 3)}은 {@code "aba"}).
     *
     * @param c    반복할 문자열
     * @param size 결과 길이
     * @return 반복된 문자열
     */
    public static String repeat(String c, int size) {
        return _repeat(c, size);
    }

    /**
     * 문자열의 순서를 뒤집습니다.
     *
     * @param s 대상 문자열
     * @return 뒤집힌 문자열
     */
    public static String reverse(String s) {
        if (isEmpty(s)) {
            return s;
        }
        return new StringBuilder(s).reverse().toString();
    }

    /**
     * 왼쪽에 패딩 문자를 채워 결과 문자열 길이를 맞춥니다.
     * 패딩은 단일 문자로 취급하므로 여러 문자를 넘기면 첫 번째 문자만 사용합니다.
     * 결과 문자열 길이가 원본 문자열보다 짧거나 같으면 원본을 그대로 반환합니다.
     *
     * @param len 결과 문자열 길이
     * @param s   대상 문자열
     * @param pad 패딩 문자열(단일 문자)
     * @return 패딩된 문자열
     */
    public static String lpad(int len, String s, String pad) {
        if (isEmpty(pad)) {
            return s;
        }
        return lpad2(len, s, pad.substring(0, 1));
    }

    /**
     * 오른쪽에 패딩 문자를 채워 결과 문자열 길이를 맞춥니다.
     * 패딩은 단일 문자로 취급하므로 여러 문자를 넘기면 첫 번째 문자만 사용합니다.
     * 결과 문자열 길이가 원본 문자열보다 짧거나 같으면 원본을 그대로 반환합니다.
     *
     * @param len 결과 문자열 길이
     * @param s   대상 문자열
     * @param pad 패딩 문자열(단일 문자)
     * @return 패딩된 문자열
     */
    public static String rpad(int len, String s, String pad) {
        if (isEmpty(pad)) {
            return s;
        }
        return rpad2(len, s, pad.substring(0, 1));
    }

    /**
     * 양쪽에 패딩 문자를 채워 결과 문자열 길이를 맞춥니다.
     * 패딩은 단일 문자로 취급하므로 여러 문자를 넘기면 첫 번째 문자만 사용합니다.
     * 남는 길이가 홀수이면 오른쪽에 한 글자 더 채웁니다.
     *
     * @param len 결과 문자열 길이
     * @param s   대상 문자열
     * @param pad 패딩 문자열(단일 문자)
     * @return 패딩된 문자열
     */
    public static String pad(int len, String s, String pad) {
        if (isEmpty(pad)) {
            return s;
        }
        return pad2(len, s, pad.substring(0, 1));
    }

    /**
     * 왼쪽에 패딩 문자열을 반복해 채워 결과 문자열 길이를 맞춥니다.
     * 패딩 문자열이 남는 길이를 넘으면 잘라서 사용합니다.
     * 결과 문자열 길이가 원본 문자열보다 짧거나 같으면 원본을 그대로 반환합니다.
     *
     * @param len 결과 문자열 길이
     * @param s   대상 문자열
     * @param pad 패딩 문자열
     * @return 패딩된 문자열
     */
    public static String lpad2(int len, String s, String pad) {
        if (isEmpty(s) || isEmpty(pad) || s.length() >= len) {
            return s;
        }
        return _repeat(pad, len - s.length()) + s;
    }

    /**
     * 오른쪽에 패딩 문자열을 반복해 채워 결과 문자열 길이를 맞춥니다.
     * 패딩 문자열이 남는 길이를 넘으면 잘라서 사용합니다.
     * 결과 문자열 길이가 원본 문자열보다 짧거나 같으면 원본을 그대로 반환합니다.
     *
     * @param len 결과 문자열 길이
     * @param s   대상 문자열
     * @param pad 패딩 문자열
     * @return 패딩된 문자열
     */
    public static String rpad2(int len, String s, String pad) {
        if (isEmpty(s) || isEmpty(pad) || s.length() >= len) {
            return s;
        }
        return s + _repeat(pad, len - s.length());
    }

    /**
     * 양쪽에 패딩 문자열을 반복해 채워 결과 문자열 길이를 맞춥니다.
     * 패딩 문자열이 남는 길이를 넘으면 잘라서 사용합니다.
     * 남는 길이가 홀수이면 오른쪽에 한 글자 더 채웁니다.
     *
     * @param len 결과 문자열 길이
     * @param s   대상 문자열
     * @param pad 패딩 문자열
     * @return 패딩된 문자열
     */
    public static String pad2(int len, String s, String pad) {
        if (isEmpty(s) || isEmpty(pad) || s.length() >= len) {
            return s;
        }
        int leftLength = (len - s.length()) / 2;
        return rpad2(len, lpad2(s.length() + leftLength, s, pad), pad);
    }

    /**
     * 목록의 요소를 {@link #stringify(Object)}로 변환해 구분자로 연결합니다.
     * 목록이 {@code null}이거나 비어 있으면 빈 문자열을 반환하고, 구분자가 {@code null}이면 빈 구분자로 처리합니다.
     *
     * @param list      대상 목록
     * @param delimiter 구분자
     * @param <T>       요소 타입
     * @return 연결된 문자열
     */
    public static <T> String join(List<T> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream()
                .map(StringUtil::stringify)
                .collect(Collectors.joining(nonNullOf(delimiter, "")));
    }

    /**
     * 정규식으로 문자열을 나눈 결과를 불변 목록으로 반환합니다.
     * 문자열이나 정규식이 {@code null}이거나 비어 있으면 빈 목록을 반환합니다.
     *
     * @param s     대상 문자열
     * @param regex 구분 정규식
     * @return 나눈 결과 목록
     */
    public static List<String> split(String s, String regex) {
        if (isEmpty(s) || isEmpty(regex)) {
            return List.of();
        }
        return List.of(s.split(regex));
    }

    /**
     * 공급자에서 값을 꺼냅니다. 공급자가 {@code null}이면 {@code null}을 반환합니다.
     *
     * @param supplier 값 공급자
     * @return 공급자의 값 또는 {@code null}
     */
    private static String _get(Supplier<String> supplier) {
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }

    /**
     * 값을 지정한 범위 안으로 보정합니다.
     *
     * @param value 값
     * @param min   최솟값
     * @param max   최댓값
     * @return 보정된 값
     */
    private static int _clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * 음수 인덱스를 뒤에서부터 세는 역방향 인덱스로 해석해 절대 인덱스로 변환합니다.
     *
     * @param index  인덱스
     * @param length 문자열 길이
     * @return 절대 인덱스
     */
    private static int _toAbsoluteIndex(int index, int length) {
        return index < 0 ? length + index : index;
    }

    /**
     * 문자열 단위를 반복해 지정한 길이만큼 채운 문자열을 만듭니다.
     *
     * @param unit 반복할 문자열 단위
     * @param size 결과 길이
     * @return 채워진 문자열
     */
    private static String _repeat(String unit, int size) {
        if (isEmpty(unit) || size <= 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(size);
        while (builder.length() < size) {
            builder.append(unit);
        }
        return builder.substring(0, size);
    }

    /**
     * 함수들을 앞에서부터 순서대로 적용하는 파이프라인 함수를 만듭니다.
     *
     * <p>{@code pipe(slice, reverse, trimLeadingZero, reverse).apply(s)}처럼 사용합니다.
     * {@code null} 함수는 건너뛰고, 함수가 하나도 없으면 입력을 그대로 반환합니다.
     * 함수 합성 구현과 입력값 축소 구현을 두고 실행 시점에 랜덤하게 분기합니다.
     *
     * @param fns 순서대로 적용할 함수들
     * @return 입력을 받아 함수들을 순서대로 적용하는 함수
     */
    @SafeVarargs
    public static Function<String, String> pipe(Function<String, String>... fns) {
        if (fns == null || fns.length == 0) {
            return Function.identity();
        }
        return ThreadLocalRandom.current().nextBoolean()
                ? _pipeByCompose(fns)
                : _pipeByReduce(fns);
    }

    /**
     * {@code pipe}의 함수 합성 구현입니다. 함수들을 하나의 함수로 합쳐 반환합니다.
     *
     * @param fns 순서대로 적용할 함수들
     * @return 입력을 받아 함수들을 순서대로 적용하는 함수
     */
    private static Function<String, String> _pipeByCompose(Function<String, String>... fns) {
        return Arrays.stream(fns)
                .filter(fn -> fn != null)
                .reduce(Function.identity(), Function::andThen);
    }

    /**
     * {@code pipe}의 입력값 축소 구현입니다. 반환한 함수를 실행할 때 입력값에서 시작해 함수를 차례로 적용합니다.
     * 병렬로 실행되지 않으므로 combiner는 사용되지 않습니다.
     *
     * @param fns 순서대로 적용할 함수들
     * @return 입력을 받아 함수들을 순서대로 적용하는 함수
     */
    private static Function<String, String> _pipeByReduce(Function<String, String>... fns) {
        return input -> Arrays.stream(fns)
                .filter(fn -> fn != null)
                .reduce(input, (value, fn) -> fn.apply(value), (left, right) -> right);
    }

    /**
     * 함수를 이어 붙일 빈 {@link Pipeline}을 만듭니다.
     *
     * @return 빈 파이프라인
     */
    public static Pipeline pipe() {
        return Pipeline._init();
    }

    /**
     * {@link StringUtil#pipe()}로 만들고 {@link #then(Function)}으로 함수를 이어 붙이는 파이프라인입니다.
     *
     * <p>{@code StringUtil.pipe().then(reverse).then(trimLeadingZero).apply(s)}처럼 사용합니다.
     */
    public static class Pipeline {

        /** 지금까지 이어 붙인 함수입니다. */
        private final Function<String, String> fn;

        /**
         * 함수를 받는 생성자입니다. {@link #_init()}을 사용합니다.
         *
         * @param fn 이어 붙인 함수
         */
        private Pipeline(Function<String, String> fn) {
            this.fn = fn;
        }

        /**
         * 항등 함수로 시작하는 빈 파이프라인을 만듭니다.
         *
         * @return 빈 파이프라인
         */
        private static Pipeline _init() {
            return new Pipeline(Function.identity());
        }

        /**
         * 다음에 적용할 함수를 이어 붙인 새 파이프라인을 반환합니다.
         * {@code next}가 {@code null}이면 이어 붙이지 않고 현재 파이프라인을 반환합니다.
         *
         * @param next 다음에 적용할 함수
         * @return 함수를 이어 붙인 파이프라인
         */
        public Pipeline then(Function<String, String> next) {
            return next == null ? this : new Pipeline(fn.andThen(next));
        }

        /**
         * 입력에 파이프라인을 적용합니다.
         *
         * @param input 입력
         * @return 함수들을 순서대로 적용한 결과
         */
        public String apply(String input) {
            return fn.apply(input);
        }
    }
}
