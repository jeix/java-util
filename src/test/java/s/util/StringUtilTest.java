package s.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class StringUtilTest {

    @Test
    void isBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank("   "));
        assertTrue(StringUtil.isBlank("\t\n\r"));
        assertFalse(StringUtil.isBlank("a"));
        assertFalse(StringUtil.isBlank(" a "));
        log.info("isBlank tests passed");
    }

    @Test
    void isEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("a"));
        log.info("isEmpty tests passed");
    }

    @Test
    void nonNullOf() {
        assertEquals("value", StringUtil.nonNullOf("value", "default"));
        assertEquals("default", StringUtil.nonNullOf((String) null, "default"));
        assertNull(StringUtil.nonNullOf((String) null, (String) null));

        assertEquals("supplied", StringUtil.nonNullOf((Supplier<String>) () -> "supplied", () -> "default"));
        assertEquals("default", StringUtil.nonNullOf((Supplier<String>) () -> null, () -> "default"));
        assertNull(StringUtil.nonNullOf((Supplier<String>) null, () -> "default"));
        assertNull(StringUtil.nonNullOf((Supplier<String>) () -> null, (Supplier<String>) null));
        log.info("nonNullOf tests passed");
    }

    @Test
    void nonBlankOf() {
        assertEquals("value", StringUtil.nonBlankOf("value", "default"));
        assertEquals("default", StringUtil.nonBlankOf("", "default"));
        assertEquals("default", StringUtil.nonBlankOf("   ", "default"));
        assertEquals("default", StringUtil.nonBlankOf((String) null, "default"));
        assertNull(StringUtil.nonBlankOf((String) null, (String) null));

        assertEquals("supplied", StringUtil.nonBlankOf((Supplier<String>) () -> "supplied", () -> "default"));
        assertEquals("default", StringUtil.nonBlankOf((Supplier<String>) () -> "", () -> "default"));
        assertEquals("default", StringUtil.nonBlankOf((Supplier<String>) () -> null, () -> "default"));
        assertNull(StringUtil.nonBlankOf((Supplier<String>) null, () -> "default"));
        log.info("nonBlankOf tests passed");
    }

    @Test
    void nonEmptyOf() {
        assertEquals("value", StringUtil.nonEmptyOf("value", "default"));
        assertEquals("default", StringUtil.nonEmptyOf("", "default"));
        assertEquals(" ", StringUtil.nonEmptyOf(" ", "default"));
        assertEquals("default", StringUtil.nonEmptyOf((String) null, "default"));
        assertNull(StringUtil.nonEmptyOf((String) null, (String) null));

        assertEquals("supplied", StringUtil.nonEmptyOf((Supplier<String>) () -> "supplied", () -> "default"));
        assertEquals("default", StringUtil.nonEmptyOf((Supplier<String>) () -> "", () -> "default"));
        assertEquals(" ", StringUtil.nonEmptyOf((Supplier<String>) () -> " ", () -> "default"));
        assertEquals("default", StringUtil.nonEmptyOf((Supplier<String>) () -> null, () -> "default"));
        assertNull(StringUtil.nonEmptyOf((Supplier<String>) null, () -> "default"));
        log.info("nonEmptyOf tests passed");
    }

    @Test
    void firstNonBlankOrLast() {
        assertEquals("first", StringUtil.firstNonBlankOrLast("first", "second", "third"));
        assertEquals("second", StringUtil.firstNonBlankOrLast("", "second", "third"));
        assertEquals("third", StringUtil.firstNonBlankOrLast("", "  ", "third"));
        assertEquals("second", StringUtil.firstNonBlankOrLast("", "second"));
        assertEquals("second", StringUtil.firstNonBlankOrLast(null, "second"));

        assertEquals("supplied1", StringUtil.firstNonBlankOrLast(() -> "supplied1", () -> "supplied2"));
        assertEquals("supplied2", StringUtil.firstNonBlankOrLast(() -> "", () -> "supplied2"));
        assertEquals("supplied3", StringUtil.firstNonBlankOrLast(() -> "", () -> "  ", () -> "supplied3"));
        assertEquals("supplied4", StringUtil.firstNonBlankOrLast(() -> "", () -> "  ", () -> "", () -> "supplied4"));
        assertEquals("supplied5", StringUtil.firstNonBlankOrLast(() -> "", () -> "  ", () -> "", () -> "  ", () -> "supplied5"));
        log.info("firstNonBlankOrLast tests passed");
    }

    @Test
    void firstNonBlankOrEmpty() {
        assertEquals("first", StringUtil.firstNonBlankOrEmpty("first", "second", "third"));
        assertEquals("second", StringUtil.firstNonBlankOrEmpty("", "second", "third"));
        assertEquals("third", StringUtil.firstNonBlankOrEmpty("", "  ", "third"));
        assertEquals("", StringUtil.firstNonBlankOrEmpty("", "  "));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(null, null));

        assertEquals("supplied1", StringUtil.firstNonBlankOrEmpty(() -> "supplied1", () -> "supplied2"));
        assertEquals("supplied2", StringUtil.firstNonBlankOrEmpty(() -> "", () -> "supplied2"));
        assertEquals("supplied3", StringUtil.firstNonBlankOrEmpty(() -> "", () -> "  ", () -> "supplied3"));
        assertEquals("supplied4", StringUtil.firstNonBlankOrEmpty(() -> "", () -> "  ", () -> "", () -> "supplied4"));
        assertEquals("supplied5", StringUtil.firstNonBlankOrEmpty(() -> "", () -> "  ", () -> "", () -> "  ", () -> "supplied5"));
        log.info("firstNonBlankOrEmpty tests passed");
    }

    @Test
    void firstNonBlankOrNull() {
        assertEquals("first", StringUtil.firstNonBlankOrNull("first", "second", "third"));
        assertEquals("second", StringUtil.firstNonBlankOrNull("", "second", "third"));
        assertEquals("third", StringUtil.firstNonBlankOrNull("", "  ", "third"));
        assertNull(StringUtil.firstNonBlankOrNull("", "  "));
        assertNull(StringUtil.firstNonBlankOrNull(null, null));

        assertEquals("supplied1", StringUtil.firstNonBlankOrNull(() -> "supplied1", () -> "supplied2"));
        assertEquals("supplied2", StringUtil.firstNonBlankOrNull(() -> "", () -> "supplied2"));
        assertEquals("supplied3", StringUtil.firstNonBlankOrNull(() -> "", () -> "  ", () -> "supplied3"));
        assertEquals("supplied4", StringUtil.firstNonBlankOrNull(() -> "", () -> "  ", () -> "", () -> "supplied4"));
        assertEquals("supplied5", StringUtil.firstNonBlankOrNull(() -> "", () -> "  ", () -> "", () -> "  ", () -> "supplied5"));
        log.info("firstNonBlankOrNull tests passed");
    }

    @Test
    void stringify() {
        assertNull(StringUtil.stringify(null));
        assertEquals("hello", StringUtil.stringify("hello"));
        assertEquals("2023-01-15", StringUtil.stringify(new Date(123, 0, 15)));
        assertEquals("123.45", StringUtil.stringify(new BigDecimal("123.45")));
        assertEquals("100", StringUtil.stringify(new BigDecimal("100")));
        assertEquals("10000000000", StringUtil.stringify(new BigDecimal("1E+10")));
        log.info("stringify tests passed");
    }

    @Test
    void slice() {
        assertNull(StringUtil.slice(null, 0));
        assertEquals("hello", StringUtil.slice("hello", 0));
        assertEquals("ello", StringUtil.slice("hello", 1));
        assertEquals("lo", StringUtil.slice("hello", 3));
        assertEquals("", StringUtil.slice("hello", 5));
        assertEquals("", StringUtil.slice("hello", 10));
        assertEquals("llo", StringUtil.slice("hello", -3));
        assertEquals("hello", StringUtil.slice("hello", -5));
        assertEquals("hello", StringUtil.slice("hello", -10));

        assertNull(StringUtil.slice(null, 0, 5));
        assertEquals("hello", StringUtil.slice("hello", 0, 5));
        assertEquals("ell", StringUtil.slice("hello", 1, 4));
        assertEquals("", StringUtil.slice("hello", 2, 2));
        assertEquals("", StringUtil.slice("hello", 5, 5));
        assertEquals("hel", StringUtil.slice("hello", -5, -2));
        assertEquals("ell", StringUtil.slice("hello", -4, -1));
        assertEquals("hello", StringUtil.slice("hello", -10, 10));
        log.info("slice tests passed");
    }

    @Test
    void head() {
        assertNull(StringUtil.head(null, 3));
        assertEquals("", StringUtil.head("hello", 0));
        assertEquals("", StringUtil.head("hello", -1));
        assertEquals("h", StringUtil.head("hello", 1));
        assertEquals("hel", StringUtil.head("hello", 3));
        assertEquals("hello", StringUtil.head("hello", 5));
        assertEquals("hello", StringUtil.head("hello", 10));
        log.info("head tests passed");
    }

    @Test
    void tail() {
        assertNull(StringUtil.tail(null, 3));
        assertEquals("", StringUtil.tail("hello", 0));
        assertEquals("", StringUtil.tail("hello", -1));
        assertEquals("o", StringUtil.tail("hello", 1));
        assertEquals("llo", StringUtil.tail("hello", 3));
        assertEquals("hello", StringUtil.tail("hello", 5));
        assertEquals("hello", StringUtil.tail("hello", 10));
        log.info("tail tests passed");
    }

    @Test
    void trimLeadingZero() {
        assertNull(StringUtil.trimLeadingZero(null));
        assertEquals("", StringUtil.trimLeadingZero(""));
        assertEquals("0", StringUtil.trimLeadingZero("0"));
        assertEquals("0", StringUtil.trimLeadingZero("000"));
        assertEquals("123", StringUtil.trimLeadingZero("000123"));
        assertEquals("123", StringUtil.trimLeadingZero("123"));
        assertEquals("0.123", StringUtil.trimLeadingZero("00.123"));
        log.info("trimLeadingZero tests passed");
    }

    @Test
    void repeat() {
        assertEquals("", StringUtil.repeat('a', 0));
        assertEquals("", StringUtil.repeat('a', -1));
        assertEquals("a", StringUtil.repeat('a', 1));
        assertEquals("aaa", StringUtil.repeat('a', 3));

        assertEquals("", StringUtil.repeat("ab", 0));
        assertEquals("", StringUtil.repeat("ab", -1));
        assertEquals("ab", StringUtil.repeat("ab", 1));
        assertEquals("ababab", StringUtil.repeat("ab", 3));
        assertEquals("", StringUtil.repeat(null, 3));
        log.info("repeat tests passed");
    }

    @Test
    void reverse() {
        assertNull(StringUtil.reverse(null));
        assertEquals("", StringUtil.reverse(""));
        assertEquals("a", StringUtil.reverse("a"));
        assertEquals("olleh", StringUtil.reverse("hello"));
        assertEquals("12345", StringUtil.reverse("54321"));
        log.info("reverse tests passed");
    }

    @Test
    void lpad() {
        assertEquals("  hello", StringUtil.lpad(7, "hello", ' '));
        assertEquals("00hello", StringUtil.lpad(7, "hello", '0'));
        assertEquals("hello", StringUtil.lpad(3, "hello", ' '));
        assertEquals("00000", StringUtil.lpad(5, null, '0'));
        assertEquals("", StringUtil.lpad(0, "hello", ' '));
        log.info("lpad tests passed");
    }

    @Test
    void rpad() {
        assertEquals("hello  ", StringUtil.rpad(7, "hello", ' '));
        assertEquals("hello00", StringUtil.rpad(7, "hello", '0'));
        assertEquals("hello", StringUtil.rpad(3, "hello", ' '));
        assertEquals("00000", StringUtil.rpad(5, null, '0'));
        assertEquals("", StringUtil.rpad(0, "hello", ' '));
        log.info("rpad tests passed");
    }

    @Test
    void pad() {
        assertEquals(" hello ", StringUtil.pad(7, "hello", ' '));
        assertEquals("0hello0", StringUtil.pad(7, "hello", '0'));
        assertEquals("hello", StringUtil.pad(3, "hello", ' '));
        assertEquals("00000", StringUtil.pad(5, null, '0'));
        assertEquals("", StringUtil.pad(0, "hello", ' '));
        assertEquals("  hello  ", StringUtil.pad(9, "hello", ' '));
        log.info("pad tests passed");
    }

    @Test
    void lpad2() {
        assertEquals("abhello", StringUtil.lpad2(7, "hello", "ab"));
        assertEquals("ababhello", StringUtil.lpad2(9, "hello", "ab"));
        assertEquals("abhello", StringUtil.lpad2(7, "hello", "abc"));
        assertEquals("hello", StringUtil.lpad2(3, "hello", "ab"));
        assertEquals("hello", StringUtil.lpad2(7, "hello", ""));
        assertEquals("hello", StringUtil.lpad2(7, "hello", null));
        assertEquals("abab", StringUtil.lpad2(4, null, "ab"));
        assertEquals("", StringUtil.lpad2(0, "hello", "ab"));
        log.info("lpad2 tests passed");
    }

    @Test
    void rpad2() {
        assertEquals("helloab", StringUtil.rpad2(7, "hello", "ab"));
        assertEquals("helloabab", StringUtil.rpad2(9, "hello", "ab"));
        assertEquals("helloab", StringUtil.rpad2(7, "hello", "abc"));
        assertEquals("hello", StringUtil.rpad2(3, "hello", "ab"));
        assertEquals("hello", StringUtil.rpad2(7, "hello", ""));
        assertEquals("hello", StringUtil.rpad2(7, "hello", null));
        assertEquals("abab", StringUtil.rpad2(4, null, "ab"));
        assertEquals("", StringUtil.rpad2(0, "hello", "ab"));
        log.info("rpad2 tests passed");
    }

    @Test
    void pad2() {
        assertEquals("abhelloab", StringUtil.pad2(9, "hello", "ab"));
        assertEquals("abahelloaba", StringUtil.pad2(11, "hello", "ab"));
        assertEquals("hello", StringUtil.pad2(3, "hello", "ab"));
        assertEquals("hello", StringUtil.pad2(7, "hello", ""));
        assertEquals("hello", StringUtil.pad2(7, "hello", null));
        assertEquals("abab", StringUtil.pad2(4, null, "ab"));
        assertEquals("", StringUtil.pad2(0, "hello", "ab"));
        log.info("pad2 tests passed");
    }

    @Test
    void join() {
        assertEquals("", StringUtil.join((List<String>) null, ","));
        assertEquals("", StringUtil.join(List.of(), ","));
        assertEquals("a", StringUtil.join(List.of("a"), ","));
        assertEquals("a,b,c", StringUtil.join(List.of("a", "b", "c"), ","));
        assertEquals("a,,c", StringUtil.join(Arrays.asList("a", "", "c"), ","));
        assertEquals("a,null,c", StringUtil.join(Arrays.asList("a", null, "c"), ","));
        assertEquals("a b c", StringUtil.join(List.of("a", "b", "c"), " "));
        assertEquals("", StringUtil.join(List.of("a", "b"), null));
        log.info("join tests passed");
    }

    @Test
    void split() {
        assertEquals(List.of(), StringUtil.split(null, ","));
        assertEquals(List.of("abc"), StringUtil.split("abc", null));
        assertEquals(List.of("abc"), StringUtil.split("abc", ""));
        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtil.split("a,b,c", ",").toArray());
        assertArrayEquals(new String[]{"a", "", "c"}, StringUtil.split("a,,c", ",").toArray());
        assertArrayEquals(new String[]{"a b c"}, StringUtil.split("a b c", ",").toArray());
        assertArrayEquals(new String[]{"a", "b", "c"}, StringUtil.split("a, b, c", ", ").toArray());
        log.info("split tests passed");
    }
}