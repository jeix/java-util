package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.regex.PatternSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Slf4j
class StringUtilTest {

    @ParameterizedTest
    @NullAndEmptySource
    void nullAndEmptyAreBlankAndEmpty(String value) {
        assertTrue(StringUtil.isBlank(value));
        assertTrue(StringUtil.isEmpty(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\t\n", "\u2003"})
    void whitespaceIsBlankButNotEmpty(String value) {
        assertTrue(StringUtil.isBlank(value));
        assertFalse(StringUtil.isEmpty(value));
    }

    @Test
    void textIsNeitherBlankNorEmpty() {
        assertFalse(StringUtil.isBlank(" a "));
        assertFalse(StringUtil.isEmpty(" a "));
    }

    @Test
    void nonNullOfUsesFallbackOnlyWhenNecessary() {
        assertEquals("fallback", StringUtil.nonNullOf((String) null, "fallback"));
        assertEquals("fallback", StringUtil.nonNullOf(null, "fallback"));
        assertEquals("", StringUtil.nonNullOf("", "fallback"));
        assertNull(StringUtil.nonNullOf((String) null, (String) null));

        AtomicInteger calls = new AtomicInteger();
        Supplier<String> fallback = () -> {
            calls.incrementAndGet();
            return "fallback";
        };
        assertEquals("", StringUtil.nonNullOf(() -> "", fallback));
        assertEquals(0, calls.get());
        assertEquals("fallback", StringUtil.nonNullOf(() -> null, fallback));
        assertEquals(1, calls.get());
        assertEquals("fallback", StringUtil.nonNullOf((Supplier<String>) null, fallback));
        assertEquals(2, calls.get());
        assertNull(StringUtil.nonNullOf(() -> null, null));
        assertNull(StringUtil.nonNullOf(() -> null, () -> null));
    }

    @Test
    void nonBlankOfUsesFallbackOnlyWhenNecessary() {
        assertEquals("fallback", StringUtil.nonBlankOf((String) null, "fallback"));
        assertEquals("fallback", StringUtil.nonBlankOf(" \t", "fallback"));
        assertEquals(" a ", StringUtil.nonBlankOf(" a ", "fallback"));
        assertNull(StringUtil.nonBlankOf((String) null, (String) null));

        AtomicInteger calls = new AtomicInteger();
        Supplier<String> fallback = () -> {
            calls.incrementAndGet();
            return "fallback";
        };
        assertEquals(" a ", StringUtil.nonBlankOf(() -> " a ", fallback));
        assertEquals(0, calls.get());
        assertEquals("fallback", StringUtil.nonBlankOf(() -> " \t", fallback));
        assertEquals(1, calls.get());
        assertEquals("fallback", StringUtil.nonBlankOf((Supplier<String>) null, fallback));
        assertEquals(2, calls.get());
        assertNull(StringUtil.nonBlankOf(() -> null, null));
        assertNull(StringUtil.nonBlankOf(() -> null, () -> null));
    }

    @Test
    void nonEmptyOfUsesFallbackOnlyWhenNecessary() {
        assertEquals("fallback", StringUtil.nonEmptyOf((String) null, "fallback"));
        assertEquals("fallback", StringUtil.nonEmptyOf("", "fallback"));
        assertEquals(" ", StringUtil.nonEmptyOf(" ", "fallback"));
        assertNull(StringUtil.nonEmptyOf((String) null, (String) null));

        AtomicInteger calls = new AtomicInteger();
        Supplier<String> fallback = () -> {
            calls.incrementAndGet();
            return "fallback";
        };
        assertEquals(" ", StringUtil.nonEmptyOf(() -> " ", fallback));
        assertEquals(0, calls.get());
        assertEquals("fallback", StringUtil.nonEmptyOf(() -> "", fallback));
        assertEquals(1, calls.get());
        assertEquals("fallback", StringUtil.nonEmptyOf((Supplier<String>) null, fallback));
        assertEquals(2, calls.get());
        assertNull(StringUtil.nonEmptyOf(() -> null, null));
        assertNull(StringUtil.nonEmptyOf(() -> null, () -> null));
    }

    @Test
    void firstNonBlankOrLastWithValues() {
        assertEquals("first", StringUtil.firstNonBlankOrLast("first", "second", "third"));
        assertEquals("second", StringUtil.firstNonBlankOrLast(null, "second", "third"));
        assertEquals("third", StringUtil.firstNonBlankOrLast("", " ", "third", "fourth"));
        assertEquals(" ", StringUtil.firstNonBlankOrLast(null, "", " "));
        assertEquals(" ", StringUtil.firstNonBlankOrLast("", " "));
        assertEquals(" ", StringUtil.firstNonBlankOrLast("", " ", (String[]) null));
        assertEquals(null, StringUtil.firstNonBlankOrLast("", " ", (String) null));
    }

    @Test
    void firstNonBlankOrLastWith2Suppliers() {
        _assertSupplierSelection(2, " ", values -> StringUtil.firstNonBlankOrLast(
                values.get(0), values.get(1)));
    }

    @Test
    void firstNonBlankOrLastWith3Suppliers() {
        _assertSupplierSelection(3, " ", values -> StringUtil.firstNonBlankOrLast(
                values.get(0), values.get(1), values.get(2)));
    }

    @Test
    void firstNonBlankOrLastWith4Suppliers() {
        _assertSupplierSelection(4, " ", values -> StringUtil.firstNonBlankOrLast(
                values.get(0), values.get(1), values.get(2), values.get(3)));
    }

    @Test
    void firstNonBlankOrLastWith5Suppliers() {
        _assertSupplierSelection(5, " ", values -> StringUtil.firstNonBlankOrLast(
                values.get(0), values.get(1), values.get(2), values.get(3), values.get(4)));
    }

    @Test
    void firstNonBlankOrEmptyWithValues() {
        assertEquals("first", StringUtil.firstNonBlankOrEmpty("first", "second", "third"));
        assertEquals("second", StringUtil.firstNonBlankOrEmpty(null, "second", "third"));
        assertEquals("third", StringUtil.firstNonBlankOrEmpty("", " ", "third", "fourth"));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(null, "", " "));
        assertEquals("", StringUtil.firstNonBlankOrEmpty("", " "));
        assertEquals("", StringUtil.firstNonBlankOrEmpty("", " ", (String[]) null));
        assertEquals("", StringUtil.firstNonBlankOrEmpty("", " ", (String) null));
    }

    @Test
    void firstNonBlankOrEmptyWith2Suppliers() {
        _assertSupplierSelection(2, "", values -> StringUtil.firstNonBlankOrEmpty(
                values.get(0), values.get(1)));
    }

    @Test
    void firstNonBlankOrEmptyWith3Suppliers() {
        _assertSupplierSelection(3, "", values -> StringUtil.firstNonBlankOrEmpty(
                values.get(0), values.get(1), values.get(2)));
    }

    @Test
    void firstNonBlankOrEmptyWith4Suppliers() {
        _assertSupplierSelection(4, "", values -> StringUtil.firstNonBlankOrEmpty(
                values.get(0), values.get(1), values.get(2), values.get(3)));
    }

    @Test
    void firstNonBlankOrEmptyWith5Suppliers() {
        _assertSupplierSelection(5, "", values -> StringUtil.firstNonBlankOrEmpty(
                values.get(0), values.get(1), values.get(2), values.get(3), values.get(4)));
    }

    @Test
    void firstNonBlankOrNullWithValues() {
        assertEquals("first", StringUtil.firstNonBlankOrNull("first", "second", "third"));
        assertEquals("second", StringUtil.firstNonBlankOrNull(null, "second", "third"));
        assertEquals("third", StringUtil.firstNonBlankOrNull("", " ", "third", "fourth"));
        assertEquals(null, StringUtil.firstNonBlankOrNull(null, "", " "));
        assertEquals(null, StringUtil.firstNonBlankOrNull("", " "));
        assertEquals(null, StringUtil.firstNonBlankOrNull("", " ", (String[]) null));
        assertEquals(null, StringUtil.firstNonBlankOrNull("", " ", (String) null));
    }

    @Test
    void firstNonBlankOrNullWith2Suppliers() {
        _assertSupplierSelection(2, null, values -> StringUtil.firstNonBlankOrNull(
                values.get(0), values.get(1)));
    }

    @Test
    void firstNonBlankOrNullWith3Suppliers() {
        _assertSupplierSelection(3, null, values -> StringUtil.firstNonBlankOrNull(
                values.get(0), values.get(1), values.get(2)));
    }

    @Test
    void firstNonBlankOrNullWith4Suppliers() {
        _assertSupplierSelection(4, null, values -> StringUtil.firstNonBlankOrNull(
                values.get(0), values.get(1), values.get(2), values.get(3)));
    }

    @Test
    void firstNonBlankOrNullWith5Suppliers() {
        _assertSupplierSelection(5, null, values -> StringUtil.firstNonBlankOrNull(
                values.get(0), values.get(1), values.get(2), values.get(3), values.get(4)));
    }

    private static void _assertSupplierSelection(
            int count, String fallback, Function<List<Supplier<String>>, String> select) {
        List<Supplier<String>> blanks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            blanks.add(() -> " ");
        }
        assertEquals(fallback, select.apply(blanks));

        for (int winner = 0; winner < count; winner++) {
            int selected = winner;
            List<Integer> visited = new ArrayList<>();
            List<Supplier<String>> suppliers = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                int index = i;
                suppliers.add(() -> {
                    visited.add(index);
                    if (index > selected) {
                        throw new AssertionError("must not evaluate after a non-blank value");
                    }
                    return index == selected ? "found" : " ";
                });
            }
            assertEquals("found", select.apply(suppliers));
            assertEquals(IntStream.rangeClosed(0, winner).boxed().toList(), visited);
        }
    }

    @Test
    void nullSuppliersAndExceptions() {
        Supplier<String> absent = null;
        assertNull(StringUtil.firstNonBlankOrLast(absent, absent));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(absent, absent));
        assertNull(StringUtil.firstNonBlankOrNull(absent, absent));
        assertEquals("ok", StringUtil.firstNonBlankOrLast(absent, absent, () -> "ok"));
        IllegalStateException failure = new IllegalStateException("supplier failed");
        Supplier<String> broken = () -> { throw failure; };
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> StringUtil.firstNonBlankOrLast(() -> "", broken)));
        assertSame(failure, assertThrows(IllegalStateException.class,
                () -> StringUtil.nonBlankOf(broken, () -> "fallback")));
    }

    @Test
    void stringifyUsesToString() {
        assertEquals("", StringUtil.stringify(null));
        assertEquals("42", StringUtil.stringify(42));
        assertEquals("hello", StringUtil.stringify("hello"));
        assertEquals("custom", StringUtil.stringify(new Object() {
            @Override
            public String toString() {
                return "custom";
            }
        }));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2019-12-30", "2021-01-01", "2024-02-29", "2026-09-09"})
    void stringifyFormatsDates(String expected) {
        Date date = Date.from(LocalDate.parse(expected).atTime(13, 45)
                .atZone(ZoneId.systemDefault()).toInstant());
        String actual = StringUtil.stringify(date);
        log.info("stringify(Date): {}", actual);
        assertEquals(expected, actual);
        assertEquals(expected, StringUtil.stringify(java.sql.Date.valueOf(expected)));
    }

    @ParameterizedTest
    @CsvSource({"1E+10,10000000000", "1E-10,0.0000000001", "123.4500,123.4500",
            "-1E+3,-1000", "0,0"})
    void stringifyUsesPlainDecimalNotation(String input, String expected) {
        assertEquals(expected, StringUtil.stringify(new BigDecimal(input)));
    }

    @Test
    void joinUsesDateAndDecimalFormatting() {
        assertEquals("2024-02-29|1000", StringUtil.join(
                List.of(java.sql.Date.valueOf("2024-02-29"), new BigDecimal("1E+3")), "|"));
    }

    @Test
    void sliceChecksRanges() {
        assertNull(StringUtil.slice(null, 1));
        assertNull(StringUtil.slice(null, 1, 2));
        assertEquals("bc", StringUtil.slice("abcd", 1, 3));
        assertEquals("bcd", StringUtil.slice("abcd", 1));
        assertEquals("abcd", StringUtil.slice("abcd", 0, 4));
        assertEquals("", StringUtil.slice("abcd", 4));
        assertEquals("", StringUtil.slice("abcd", 2, 2));
        assertEquals("", StringUtil.slice("", 0));
        assertEquals("", StringUtil.slice("abcd", -1, 3));
        assertEquals("", StringUtil.slice("abcd", 3, 2));
        assertEquals("", StringUtil.slice("abcd", 0, 5));
        assertEquals("", StringUtil.slice("abcd", Integer.MAX_VALUE));
    }

    @Test
    void headAndTailClampLengths() {
        assertNull(StringUtil.head(null, 1));
        assertNull(StringUtil.tail(null, 1));
        assertEquals("ab", StringUtil.head("abcd", 2));
        assertEquals("cd", StringUtil.tail("abcd", 2));
        for (int size : new int[] {0, -1, Integer.MIN_VALUE}) {
            assertEquals("", StringUtil.head("abcd", size));
            assertEquals("", StringUtil.tail("abcd", size));
        }
        for (int size : new int[] {4, 5, Integer.MAX_VALUE}) {
            assertEquals("abcd", StringUtil.head("abcd", size));
            assertEquals("abcd", StringUtil.tail("abcd", size));
        }
        assertEquals("", StringUtil.head("", 1));
        assertEquals("", StringUtil.tail("", 1));
    }

    @ParameterizedTest
    @CsvSource({"000123,123", "000,0", "0,0", "123,123", "-0012,-12", "+0012,+12",
            "000.050,0.050", "-000.00,-0.00", "001a,001a", "1e3,1e3", "00.1.2,00.1.2"})
    void trimsNumericLeadingZeros(String input, String expected) {
        assertEquals(expected, StringUtil.trimLeadingZero(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", " 001", "+", "-", ".01"})
    void preservesNonNumericValues(String value) {
        assertEquals(value, StringUtil.trimLeadingZero(value));
    }

    @Test
    void repeatAndReverse() {
        assertEquals("aaa", StringUtil.repeat('a', 3));
        assertEquals("", StringUtil.repeat('a', 0));
        assertEquals("", StringUtil.repeat('a', -1));
        assertNull(StringUtil.reverse(null));
        assertEquals("", StringUtil.reverse(""));
        assertEquals("cba", StringUtil.reverse("abc"));
        assertEquals("바😀가", StringUtil.reverse("가😀바"));
    }

    @Test
    void padsWithOneCharacter() {
        assertEquals("000ab", StringUtil.lpad(5, "ab", '0'));
        assertEquals("ab000", StringUtil.rpad(5, "ab", '0'));
        assertEquals("0ab00", StringUtil.pad(5, "ab", '0'));
        assertEquals("0ab0", StringUtil.pad(4, "ab", '0'));
        assertEquals("ab0", StringUtil.pad(3, "ab", '0'));
        assertEquals("000", StringUtil.pad(3, "", '0'));
        assertNull(StringUtil.lpad(3, null, '0'));
        assertNull(StringUtil.rpad(3, null, '0'));
        assertNull(StringUtil.pad(3, null, '0'));
        for (int size : new int[] {-1, 0, 1, 2}) {
            assertEquals("ab", StringUtil.lpad(size, "ab", '0'));
            assertEquals("ab", StringUtil.rpad(size, "ab", '0'));
            assertEquals("ab", StringUtil.pad(size, "ab", '0'));
        }
    }

    @Test
    void repeatsAndTruncatesPaddingPatterns() {
        assertEquals("xyxyxab", StringUtil.lpad2(7, "ab", "xy"));
        assertEquals("abxyxyx", StringUtil.rpad2(7, "ab", "xy"));
        assertEquals("xyabxyx", StringUtil.pad2(7, "ab", "xy"));
        assertEquals("xabx", StringUtil.pad2(4, "ab", "xyz"));
        assertEquals("abx", StringUtil.pad2(3, "ab", "xyz"));
        assertEquals("xyx", StringUtil.lpad2(3, "", "xy"));
        assertEquals("xyx", StringUtil.rpad2(3, "", "xy"));
        assertEquals("xxy", StringUtil.pad2(3, "", "xy"));
        for (String pattern : Arrays.asList(null, "", "xy")) {
            assertNull(StringUtil.lpad2(3, null, pattern));
            assertNull(StringUtil.rpad2(3, null, pattern));
            assertNull(StringUtil.pad2(3, null, pattern));
            assertEquals("abcd", StringUtil.lpad2(2, "abcd", pattern));
            assertEquals("abcd", StringUtil.rpad2(2, "abcd", pattern));
            assertEquals("abcd", StringUtil.pad2(2, "abcd", pattern));
        }
        for (String pattern : Arrays.asList(null, "")) {
            assertEquals("ab", StringUtil.lpad2(5, "ab", pattern));
            assertEquals("ab", StringUtil.rpad2(5, "ab", pattern));
            assertEquals("ab", StringUtil.pad2(5, "ab", pattern));
        }
    }

    @Test
    void joinsGenericLists() {
        assertEquals("", StringUtil.join(null, ","));
        assertEquals("", StringUtil.join(List.of(), ","));
        assertEquals("1|2|3", StringUtil.join(List.of(1, 2, 3), "|"));
        assertEquals("a,,b", StringUtil.join(Arrays.asList("a", null, "b"), ","));
        assertEquals("ab", StringUtil.join(List.of("a", "b"), null));
        assertEquals("ab", StringUtil.join(List.of("a", "b"), ""));
        assertEquals("a", StringUtil.join(List.of("a"), ","));
    }

    @Test
    void splitsByRegexAndPreservesEmptyFields() {
        assertEquals(List.of(), StringUtil.split(null, ","));
        assertEquals(List.of(""), StringUtil.split("", ","));
        assertEquals(List.of("a,b"), StringUtil.split("a,b", null));
        assertEquals(List.of("a", "b", "c"), StringUtil.split("a  b\tc", "\\s+"));
        assertEquals(List.of("", "a", "", ""), StringUtil.split(",a,,", ","));
        assertEquals(List.of("a", "b"), StringUtil.split("a.b", "\\."));
        assertEquals(List.of("a", "b", ""), StringUtil.split("ab", ""));
        assertThrows(PatternSyntaxException.class, () -> StringUtil.split("abc", "["));
    }
}
