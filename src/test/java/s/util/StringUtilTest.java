package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@Slf4j
class StringUtilTest {

    private static final String NULL = null;
    private static final Supplier<String> NULL_SUPPLIER = null;

    @Test
    void isBlank() {
        log.info("isBlank");
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank(" \t\n"));
        assertFalse(StringUtil.isBlank(" a "));
    }

    @Test
    void isEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("a"));
    }

    @Test
    void nonNullOf() {
        assertEquals("a", StringUtil.nonNullOf("a", "d"));
        assertEquals("", StringUtil.nonNullOf("", "d"));
        assertEquals("d", StringUtil.nonNullOf(NULL, "d"));
        assertNull(StringUtil.nonNullOf(NULL, NULL));
    }

    @Test
    void nonNullOfSupplier() {
        assertEquals("a", StringUtil.nonNullOf(() -> "a", () -> "d"));
        assertEquals("d", StringUtil.nonNullOf(() -> null, () -> "d"));
        assertEquals("d", StringUtil.nonNullOf(NULL_SUPPLIER, () -> "d"));
        assertNull(StringUtil.nonNullOf(NULL_SUPPLIER, NULL_SUPPLIER));
    }

    @Test
    void nonNullOfSupplierIsLazy() {
        AtomicInteger calls = new AtomicInteger();
        assertEquals("a", StringUtil.nonNullOf(() -> "a", () -> {
            calls.incrementAndGet();
            return "d";
        }));
        assertEquals(0, calls.get());
    }

    @Test
    void nonBlankOf() {
        assertEquals("a", StringUtil.nonBlankOf("a", "d"));
        assertEquals("d", StringUtil.nonBlankOf(" ", "d"));
        assertEquals("d", StringUtil.nonBlankOf("", "d"));
        assertEquals("d", StringUtil.nonBlankOf(NULL, "d"));
        assertEquals("d", StringUtil.nonBlankOf(() -> " ", () -> "d"));
        assertEquals("a", StringUtil.nonBlankOf(() -> "a", () -> "d"));
    }

    @Test
    void nonEmptyOf() {
        assertEquals("a", StringUtil.nonEmptyOf("a", "d"));
        assertEquals(" ", StringUtil.nonEmptyOf(" ", "d"));
        assertEquals("d", StringUtil.nonEmptyOf("", "d"));
        assertEquals("d", StringUtil.nonEmptyOf(NULL, "d"));
        assertEquals("d", StringUtil.nonEmptyOf(() -> "", () -> "d"));
        assertEquals(" ", StringUtil.nonEmptyOf(() -> " ", () -> "d"));
    }

    @RepeatedTest(20)
    void firstNonBlankOrLast() {
        assertEquals("a", StringUtil.firstNonBlankOrLast("a", "b"));
        assertEquals("b", StringUtil.firstNonBlankOrLast(NULL, " ", "b", "c"));
        assertEquals("c", StringUtil.firstNonBlankOrLast(NULL, "", "c"));
        assertEquals(" ", StringUtil.firstNonBlankOrLast(NULL, "", " "));
        assertNull(StringUtil.firstNonBlankOrLast(" ", "", NULL));
    }

    @RepeatedTest(20)
    void firstNonBlankOrLastSupplier() {
        assertEquals("a", StringUtil.firstNonBlankOrLast(() -> "a", () -> "b"));
        assertEquals("b", StringUtil.firstNonBlankOrLast(() -> null, () -> "b"));
        assertEquals("", StringUtil.firstNonBlankOrLast(() -> null, () -> ""));
        assertEquals("c", StringUtil.firstNonBlankOrLast(() -> null, () -> " ", () -> "c"));
        assertEquals("d", StringUtil.firstNonBlankOrLast(() -> null, () -> "", () -> " ", () -> "d"));
        assertEquals(" ", StringUtil.firstNonBlankOrLast(() -> null, () -> "", () -> "", () -> "", () -> " "));
        assertEquals("e", StringUtil.firstNonBlankOrLast(() -> null, () -> "", () -> "", () -> "", () -> "e"));
        assertNull(StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, NULL_SUPPLIER));
    }

    @RepeatedTest(20)
    void firstNonBlankOrLastStopsAtFirstMatch() {
        AtomicInteger calls = new AtomicInteger();
        Supplier<String> counting = () -> {
            calls.incrementAndGet();
            return "x";
        };
        assertEquals("b", StringUtil.firstNonBlankOrLast(() -> "", () -> "b", counting, counting));
        assertEquals(0, calls.get());
    }

    @RepeatedTest(20)
    void firstNonBlankOrEmpty() {
        assertEquals("a", StringUtil.firstNonBlankOrEmpty("a", "b"));
        assertEquals("c", StringUtil.firstNonBlankOrEmpty(NULL, " ", "c"));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(NULL, " ", ""));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(NULL, NULL, NULL, " "));
    }

    @RepeatedTest(20)
    void firstNonBlankOrEmptySupplier() {
        assertEquals("a", StringUtil.firstNonBlankOrEmpty(() -> "a", () -> "b"));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(() -> null, () -> " "));
        assertEquals("c", StringUtil.firstNonBlankOrEmpty(() -> null, () -> " ", () -> "c"));
        assertEquals("d", StringUtil.firstNonBlankOrEmpty(() -> null, () -> "", () -> " ", () -> "d"));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(() -> null, () -> "", () -> " ", () -> null));
        assertEquals("e", StringUtil.firstNonBlankOrEmpty(() -> null, () -> "", () -> "", () -> "", () -> "e"));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, NULL_SUPPLIER));
    }

    @RepeatedTest(20)
    void firstNonBlankOrNull() {
        assertEquals("a", StringUtil.firstNonBlankOrNull("a", "b"));
        assertEquals("c", StringUtil.firstNonBlankOrNull(NULL, " ", "c"));
        assertNull(StringUtil.firstNonBlankOrNull(NULL, " ", ""));
        assertNull(StringUtil.firstNonBlankOrNull(NULL, NULL, NULL, " "));
    }

    @RepeatedTest(20)
    void firstNonBlankOrNullSupplier() {
        assertEquals("a", StringUtil.firstNonBlankOrNull(() -> "a", () -> "b"));
        assertNull(StringUtil.firstNonBlankOrNull(() -> null, () -> " "));
        assertEquals("c", StringUtil.firstNonBlankOrNull(() -> null, () -> " ", () -> "c"));
        assertEquals("d", StringUtil.firstNonBlankOrNull(() -> null, () -> "", () -> " ", () -> "d"));
        assertEquals("e", StringUtil.firstNonBlankOrNull(() -> null, () -> "", () -> "", () -> "", () -> "e"));
        assertNull(StringUtil.firstNonBlankOrNull(() -> null, () -> "", () -> "", () -> "", () -> " "));
        assertNull(StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, NULL_SUPPLIER));
    }

    @Test
    void stringify() {
        assertNull(StringUtil.stringify(null));
        assertEquals("abc", StringUtil.stringify("abc"));
        assertEquals("12", StringUtil.stringify(12));
        assertEquals("true", StringUtil.stringify(true));
        assertEquals("2024-01-05", StringUtil.stringify(new GregorianCalendar(2024, 0, 5, 13, 45).getTime()));
        assertEquals("100000000", StringUtil.stringify(new BigDecimal("1E+8")));
        assertEquals("0.00001", StringUtil.stringify(new BigDecimal("1E-5")));
    }

    @Test
    void slice() {
        assertEquals("cde", StringUtil.slice("abcde", 2));
        assertEquals("abcde", StringUtil.slice("abcde", 0));
        assertEquals("", StringUtil.slice("abcde", 5));
        assertEquals("", StringUtil.slice("abcde", 10));
        assertEquals("de", StringUtil.slice("abcde", -2));
        assertEquals("abcde", StringUtil.slice("abcde", -10));
        assertEquals("", StringUtil.slice(null, 0));
    }

    @Test
    void sliceBeginEnd() {
        assertEquals("bc", StringUtil.slice("abcde", 1, 3));
        assertEquals("abcde", StringUtil.slice("abcde", 0, 5));
        assertEquals("de", StringUtil.slice("abcde", 3, 100));
        assertEquals("", StringUtil.slice("abcde", 3, 3));
        assertEquals("", StringUtil.slice("abcde", 3, 1));
        assertEquals("cd", StringUtil.slice("abcde", -3, -1));
        assertEquals("ab", StringUtil.slice("abcde", -10, 2));
        assertEquals("bcd", StringUtil.slice("abcde", 1, -1));
        assertEquals("", StringUtil.slice("abcde", 0, -10));
        assertEquals("", StringUtil.slice(null, 0, 3));
    }

    @Test
    void head() {
        assertEquals("ab", StringUtil.head("abcde", 2));
        assertEquals("", StringUtil.head("abcde", 0));
        assertEquals("abcde", StringUtil.head("abcde", 5));
        assertEquals("abcde", StringUtil.head("abcde", 6));
        assertEquals("abcd", StringUtil.head("abcde", -1));
        assertEquals("a", StringUtil.head("abcde", -4));
        assertEquals("", StringUtil.head("abcde", -5));
        assertEquals("", StringUtil.head("abcde", -10));
        assertEquals("", StringUtil.head(null, 1));
    }

    @Test
    void tail() {
        assertEquals("de", StringUtil.tail("abcde", 2));
        assertEquals("", StringUtil.tail("abcde", 0));
        assertEquals("abcde", StringUtil.tail("abcde", 5));
        assertEquals("abcde", StringUtil.tail("abcde", 6));
        assertEquals("bcde", StringUtil.tail("abcde", -1));
        assertEquals("cde", StringUtil.tail("abcde", -2));
        assertEquals("", StringUtil.tail("abcde", -5));
        assertEquals("", StringUtil.tail("abcde", -10));
        assertEquals("", StringUtil.tail("abcde", Integer.MIN_VALUE));
        assertEquals("", StringUtil.tail(null, 1));
    }

    @RepeatedTest(20)
    void trimLeadingZero() {
        assertEquals("123", StringUtil.trimLeadingZero("000123"));
        assertEquals("123", StringUtil.trimLeadingZero("123"));
        assertEquals("1020", StringUtil.trimLeadingZero("01020"));
        assertEquals("0", StringUtil.trimLeadingZero("000"));
        assertEquals("0", StringUtil.trimLeadingZero("0"));
        assertEquals("0.5", StringUtil.trimLeadingZero("000.5"));
        assertEquals("-7", StringUtil.trimLeadingZero("-007"));
        assertEquals("", StringUtil.trimLeadingZero(""));
        assertEquals("", StringUtil.trimLeadingZero(null));
    }

    @Test
    void repeat() {
        assertEquals("aaa", StringUtil.repeat("a", 3));
        assertEquals("abab", StringUtil.repeat("ab", 2));
        assertEquals("", StringUtil.repeat("a", 0));
        assertEquals("", StringUtil.repeat("a", -1));
        assertEquals("", StringUtil.repeat("", 3));
        assertEquals("", StringUtil.repeat(null, 3));
    }

    @Test
    void reverse() {
        assertEquals("edcba", StringUtil.reverse("abcde"));
        assertEquals("a", StringUtil.reverse("a"));
        assertEquals("", StringUtil.reverse(""));
        assertEquals("", StringUtil.reverse(null));
    }

    @Test
    void lpad() {
        assertEquals("00012", StringUtil.lpad(5, "12", "0"));
        assertEquals("12345", StringUtil.lpad(5, "12345", "0"));
        assertEquals("123456", StringUtil.lpad(5, "123456", "0"));
        assertEquals("12", StringUtil.lpad(5, "12", "ab"));
        assertEquals("12", StringUtil.lpad(5, "12", "do"));
        assertEquals("12", StringUtil.lpad(5, "12", ""));
        assertEquals("12", StringUtil.lpad(5, "12", null));
        assertEquals("가나", StringUtil.lpad(5, "가나", "0"));
        assertEquals("12", StringUtil.lpad(5, "12", "가"));
        assertEquals("000", StringUtil.lpad(3, null, "0"));
    }

    @Test
    void rpad() {
        assertEquals("12000", StringUtil.rpad(5, "12", "0"));
        assertEquals("123456", StringUtil.rpad(5, "123456", "0"));
        assertEquals("12", StringUtil.rpad(5, "12", "ab"));
        assertEquals("가나", StringUtil.rpad(5, "가나", "0"));
        assertEquals("   ", StringUtil.rpad(3, null, " "));
    }

    @Test
    void pad() {
        assertEquals("*12**", StringUtil.pad(5, "12", "*"));
        assertEquals("**12**", StringUtil.pad(6, "12", "*"));
        assertEquals("12", StringUtil.pad(2, "12", "*"));
        assertEquals("12", StringUtil.pad(6, "12", "ab"));
        assertEquals("가나", StringUtil.pad(6, "가나", "*"));
    }

    @Test
    void lpad2() {
        assertEquals("ababax", StringUtil.lpad2(6, "x", "ab"));
        assertEquals("00012", StringUtil.lpad2(5, "12", "0"));
        assertEquals("abab12", StringUtil.lpad2(6, "12", "ab"));
        assertEquals("12", StringUtil.lpad2(2, "12", "ab"));
        assertEquals("12", StringUtil.lpad2(5, "12", ""));
        assertEquals("12", StringUtil.lpad2(5, "12", "가"));
        assertEquals("ababa", StringUtil.lpad2(5, null, "ab"));
    }

    @Test
    void rpad2() {
        assertEquals("xababa", StringUtil.rpad2(6, "x", "ab"));
        assertEquals("12abab", StringUtil.rpad2(6, "12", "ab"));
        assertEquals("12", StringUtil.rpad2(2, "12", "ab"));
        assertEquals("12", StringUtil.rpad2(5, "12", null));
        assertEquals("가", StringUtil.rpad2(5, "가", "ab"));
    }

    @Test
    void pad2() {
        assertEquals("ab12ab", StringUtil.pad2(6, "12", "ab"));
        assertEquals("ab12ab", StringUtil.pad2(6, "12", "aba"));
        assertEquals("a12ab", StringUtil.pad2(5, "12", "ab"));
        assertEquals("ab1ab", StringUtil.pad2(5, "1", "ab"));
        assertEquals("12", StringUtil.pad2(2, "12", "ab"));
        assertEquals("12", StringUtil.pad2(6, "12", ""));
        assertEquals("가", StringUtil.pad2(6, "가", "ab"));
    }

    @RepeatedTest(20)
    void join() {
        assertEquals("a,b,c", StringUtil.join(List.of("a", "b", "c"), ","));
        assertEquals("1, 2", StringUtil.join(List.of(1, 2), ", "));
        assertEquals("abc", StringUtil.join(List.of("a", "b", "c"), null));
        assertEquals("a,,c", StringUtil.join(Arrays.asList("a", null, "c"), ","));
        assertEquals("2024-01-05|1.5", StringUtil.join(
                List.of(new GregorianCalendar(2024, 0, 5).getTime(), new BigDecimal("1.50").stripTrailingZeros()), "|"));
        assertEquals("", StringUtil.join(List.of(), ","));
        assertEquals("", StringUtil.join(null, ","));
    }

    @Test
    void split() {
        assertEquals(List.of("a", "b", "c"), StringUtil.split("a,b,c", ","));
        assertEquals(List.of("a", "b", "", ""), StringUtil.split("a,b,,", ","));
        assertEquals(List.of("a", "b", "c"), StringUtil.split("a1b22c", "\\d+"));
        assertEquals(List.of("abc"), StringUtil.split("abc", ","));
        assertEquals(List.of("abc"), StringUtil.split("abc", ""));
        assertEquals(List.of("abc"), StringUtil.split("abc", null));
        assertEquals(List.of(""), StringUtil.split("", ","));
        assertEquals(List.of(), StringUtil.split(null, ","));
        assertThrows(UnsupportedOperationException.class, () -> StringUtil.split("a,b", ",").add("c"));
        assertThrows(UnsupportedOperationException.class, () -> StringUtil.split("abc", null).add("c"));
        assertThrows(UnsupportedOperationException.class, () -> StringUtil.split(null, ",").add("c"));
    }

    @Test
    void pipe() {
        String s = "2025-03-19 12:26:41.012345000";
        s = StringUtil.pipe(
                        (s1) -> StringUtil.slice(s1, 20),
                        StringUtil::reverse,
                        StringUtil::trimLeadingZero,
                        StringUtil::reverse)
                .apply(s);
        log.info(s);
        assertEquals("012345", s);
    }

    @Test
    void pipeOrderAndEdgeCases() {
        assertEquals("a-b", StringUtil.pipe((String x) -> x + "-", x -> x + "b").apply("a"));
        assertEquals("ba", StringUtil.pipe((String x) -> "b" + x, x -> x.substring(0, 1) + "a").apply("x"));
        assertEquals("abc", StringUtil.pipe().apply("abc"));
        assertEquals("abc", StringUtil.pipe((Function<String, String>[]) null).apply("abc"));
        assertEquals("CBA", StringUtil.pipe(null, StringUtil::reverse, null, String::toUpperCase).apply("abc"));
        assertNull(StringUtil.pipe().apply(null));
        assertEquals("", StringUtil.pipe(StringUtil::reverse).apply(null));
    }

    @Test
    void pipeline() {
        String s = "2025-03-19 12:26:41.012345000";
        s = StringUtil.pipe()
                .then((s1) -> StringUtil.slice(s1, 20))
                .then(StringUtil::reverse)
                .then(StringUtil::trimLeadingZero)
                .then(StringUtil::reverse)
                .apply(s);
        log.info(s);
        assertEquals("012345", s);
    }

    @Test
    void pipelineIsImmutable() {
        StringUtil.Pipeline base = StringUtil.pipe().then(String::trim);
        StringUtil.Pipeline upper = base.then(String::toUpperCase);
        assertEquals("ab", base.apply(" ab "));
        assertEquals("AB", upper.apply(" ab "));
        assertEquals("abc", StringUtil.pipe().apply("abc"));
        assertEquals("ab", base.then(null).apply(" ab "));
    }
}
