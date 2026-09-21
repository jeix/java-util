package s.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class StringUtilTest {

    // ---- isBlank / isEmpty ----

    @Test
    void isBlank_nullReturnsTrue() {
        assertTrue(StringUtil.isBlank(null));
    }

    @Test
    void isBlank_emptyReturnsTrue() {
        assertTrue(StringUtil.isBlank(""));
    }

    @Test
    void isBlank_whitespaceReturnsTrue() {
        assertTrue(StringUtil.isBlank("   "));
        assertTrue(StringUtil.isBlank("\t\n"));
    }

    @Test
    void isBlank_nonBlankReturnsFalse() {
        assertFalse(StringUtil.isBlank("hello"));
    }

    @Test
    void isEmpty_nullReturnsTrue() {
        assertTrue(StringUtil.isEmpty(null));
    }

    @Test
    void isEmpty_emptyReturnsTrue() {
        assertTrue(StringUtil.isEmpty(""));
    }

    @Test
    void isEmpty_nonEmptyReturnsFalse() {
        assertFalse(StringUtil.isEmpty("hello"));
    }

    @Test
    void isEmpty_whitespaceIsNotEmpty() {
        assertFalse(StringUtil.isEmpty("   "));
    }

    // ---- nonNullOf ----

    @Test
    void nonNullOf_valueNotNull() {
        assertEquals("hello", StringUtil.nonNullOf("hello", "default"));
    }

    @Test
    void nonNullOf_valueNullReturnsDefault() {
        assertEquals("default", StringUtil.nonNullOf(null, "default"));
    }

    @Test
    void nonNullOf_supplierValueNotNull() {
        assertEquals("hello", StringUtil.nonNullOf(() -> "hello", () -> "default"));
    }

    @Test
    void nonNullOf_supplierValueNullReturnsDefault() {
        assertEquals("default", StringUtil.nonNullOf(() -> null, () -> "default"));
    }

    @Test
    void nonNullOf_bothNullReturnsEmptyString() {
        assertEquals("", StringUtil.nonNullOf((String) null, (String) null));
        assertEquals("", StringUtil.nonNullOf(() -> null, () -> null));
    }

    // ---- nonBlankOf ----

    @Test
    void nonBlankOf_valueNotBlank() {
        assertEquals("hello", StringUtil.nonBlankOf("hello", "default"));
    }

    @Test
    void nonBlankOf_valueBlankReturnsDefault() {
        assertEquals("default", StringUtil.nonBlankOf("   ", "default"));
        assertEquals("default", StringUtil.nonBlankOf(null, "default"));
        assertEquals("default", StringUtil.nonBlankOf("", "default"));
    }

    @Test
    void nonBlankOf_supplierValueNotBlank() {
        assertEquals("hello", StringUtil.nonBlankOf(() -> "hello", () -> "default"));
    }

    @Test
    void nonBlankOf_supplierValueBlankReturnsDefault() {
        assertEquals("default", StringUtil.nonBlankOf(() -> "  ", () -> "default"));
    }

    @Test
    void nonBlankOf_bothNullReturnsEmptyString() {
        assertEquals("", StringUtil.nonBlankOf((String) null, (String) null));
    }

    // ---- nonEmptyOf ----

    @Test
    void nonEmptyOf_valueNotEmpty() {
        assertEquals("hello", StringUtil.nonEmptyOf("hello", "default"));
    }

    @Test
    void nonEmptyOf_valueEmptyReturnsDefault() {
        assertEquals("default", StringUtil.nonEmptyOf("", "default"));
        assertEquals("default", StringUtil.nonEmptyOf(null, "default"));
    }

    @Test
    void nonEmptyOf_whitespaceIsNotEmpty() {
        assertEquals("  ", StringUtil.nonEmptyOf("  ", "default"));
    }

    @Test
    void nonEmptyOf_supplierValueNotEmpty() {
        assertEquals("hello", StringUtil.nonEmptyOf(() -> "hello", () -> "default"));
    }

    // ---- firstNonBlankOrLast ----

    @Test
    void firstNonBlankOrLast_values_firstNonBlank() {
        assertEquals("second", StringUtil.firstNonBlankOrLast("  ", "second", "third"));
    }

    @Test
    void firstNonBlankOrLast_values_allBlankReturnsLast() {
        assertEquals("third", StringUtil.firstNonBlankOrLast("  ", "", "third"));
        assertEquals("", StringUtil.firstNonBlankOrLast("  ", "", ""));
    }

    @Test
    void firstNonBlankOrLast_values_twoArgs() {
        assertEquals("hello", StringUtil.firstNonBlankOrLast(null, "hello"));
        assertEquals("hello", StringUtil.firstNonBlankOrLast("hello", "world"));
        assertEquals("world", StringUtil.firstNonBlankOrLast("  ", "world"));
    }

    @Test
    void firstNonBlankOrLast_suppliers_twoArgs() {
        assertEquals("first", StringUtil.firstNonBlankOrLast(() -> "first", () -> "second"));
        assertEquals("second", StringUtil.firstNonBlankOrLast(() -> "  ", () -> "second"));
        assertEquals("  ", StringUtil.firstNonBlankOrLast(() -> null, () -> "  ")); // all blank → last value
    }

    @Test
    void firstNonBlankOrLast_suppliers_fiveArgs() {
        assertEquals("third",
            StringUtil.firstNonBlankOrLast(() -> "", () -> "  ", () -> "third", () -> "  ", () -> ""));
        assertEquals("",
            StringUtil.firstNonBlankOrLast(() -> "", () -> "", () -> "", () -> "", () -> ""));
    }

    @Test
    void firstNonBlankOrLast_suppliers_varargs() {
        assertEquals("third",
            StringUtil.firstNonBlankOrLast(() -> "  ", () -> "", () -> "third"));
        assertEquals("last",
            StringUtil.firstNonBlankOrLast(() -> "", () -> "", () -> "last"));
    }

    // ---- firstNonBlankOrEmpty ----

    @Test
    void firstNonBlankOrEmpty_values_firstNonBlank() {
        assertEquals("second", StringUtil.firstNonBlankOrEmpty("  ", "second", "third"));
    }

    @Test
    void firstNonBlankOrEmpty_values_allBlankReturnsEmptyString() {
        assertEquals("", StringUtil.firstNonBlankOrEmpty("  ", "", ""));
    }

    @Test
    void firstNonBlankOrEmpty_suppliers_twoArgs() {
        assertEquals("first", StringUtil.firstNonBlankOrEmpty(() -> "first", () -> "second"));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(() -> "  ", () -> ""));
    }

    @Test
    void firstNonBlankOrEmpty_suppliers_fiveArgs() {
        assertEquals("third",
            StringUtil.firstNonBlankOrEmpty(() -> "", () -> "  ", () -> "third", () -> "  ", () -> ""));
        assertEquals("",
            StringUtil.firstNonBlankOrEmpty(() -> "", () -> "", () -> "", () -> "", () -> ""));
    }

    // ---- firstNonBlankOrNull ----

    @Test
    void firstNonBlankOrNull_values_firstNonBlank() {
        assertEquals("second", StringUtil.firstNonBlankOrNull("  ", "second", "third"));
    }

    @Test
    void firstNonBlankOrNull_values_allBlankReturnsNull() {
        assertNull(StringUtil.firstNonBlankOrNull("  ", "", ""));
    }

    @Test
    void firstNonBlankOrNull_suppliers_twoArgs() {
        assertEquals("first", StringUtil.firstNonBlankOrNull(() -> "first", () -> "second"));
        assertNull(StringUtil.firstNonBlankOrNull(() -> "  ", () -> ""));
    }

    @Test
    void firstNonBlankOrNull_suppliers_fiveArgs() {
        assertEquals("third",
            StringUtil.firstNonBlankOrNull(() -> "", () -> "  ", () -> "third", () -> "  ", () -> ""));
        assertNull(
            StringUtil.firstNonBlankOrNull(() -> "", () -> "", () -> "", () -> "", () -> ""));
    }

    // ---- stringify ----

    @Test
    void stringify_nullReturnsEmptyString() {
        assertEquals("", StringUtil.stringify(null));
    }

    @Test
    void stringify_regularObject() {
        assertEquals("123", StringUtil.stringify(Integer.valueOf(123)));
        assertEquals("hello", StringUtil.stringify("hello"));
    }

    @Test
    void stringify_bigDecimal() {
        BigDecimal bd = new BigDecimal("123.45000");
        assertEquals("123.45000", StringUtil.stringify(bd));
    }

    @Test
    void stringify_date() {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2026, Calendar.SEPTEMBER, 21);
        Date date = cal.getTime();
        String result = StringUtil.stringify(date);
        log.debug("date result: {}", result);
        assertTrue(result.matches("\\d{4}-\\d{2}-\\d{2}"));
    }

    // ---- slice ----

    @Test
    void slice_beginOnly() {
        assertEquals("llo", StringUtil.slice("hello", 2));
        assertEquals("hello", StringUtil.slice("hello", 0));
        assertEquals("", StringUtil.slice("hello", 5));
        assertEquals("hello", StringUtil.slice("hello", -1));
        assertNull(StringUtil.slice(null, 0));
    }

    @Test
    void slice_beginEnd() {
        assertEquals("ell", StringUtil.slice("hello", 1, 4));
        assertEquals("hello", StringUtil.slice("hello", 0, 5));
        assertEquals("", StringUtil.slice("hello", 2, 2));
        assertEquals("", StringUtil.slice("hello", 4, 2));
        assertEquals("hello", StringUtil.slice("hello", 0, 100));
        assertNull(StringUtil.slice(null, 0, 5));
    }

    // ---- head / tail ----

    @Test
    void head_normalCase() {
        assertEquals("hel", StringUtil.head("hello", 3));
        assertEquals("hello", StringUtil.head("hello", 10));
        assertEquals("", StringUtil.head("hello", 0));
        assertEquals("", StringUtil.head("hello", -1));
        assertNull(StringUtil.head(null, 3));
    }

    @Test
    void tail_normalCase() {
        assertEquals("llo", StringUtil.tail("hello", 3));
        assertEquals("hello", StringUtil.tail("hello", 10));
        assertEquals("", StringUtil.tail("hello", 0));
        assertEquals("", StringUtil.tail("hello", -1));
        assertNull(StringUtil.tail(null, 3));
    }

    // ---- trimLeadingZero ----

    @Test
    void trimLeadingZero_normalCase() {
        assertEquals("123", StringUtil.trimLeadingZero("000123"));
        assertEquals("12.34", StringUtil.trimLeadingZero("0012.34"));
    }

    @Test
    void trimLeadingZero_allZeros() {
        assertEquals("0", StringUtil.trimLeadingZero("0000"));
        assertEquals("0", StringUtil.trimLeadingZero("0"));
    }

    @Test
    void trimLeadingZero_noLeadingZero() {
        assertEquals("123", StringUtil.trimLeadingZero("123"));
    }

    @Test
    void trimLeadingZero_edgeCases() {
        assertEquals("", StringUtil.trimLeadingZero(""));
        assertNull(StringUtil.trimLeadingZero(null));
    }

    // ---- repeat ----

    @Test
    void repeat_normalCase() {
        assertEquals("aaa", StringUtil.repeat('a', 3));
        assertEquals("", StringUtil.repeat('a', 0));
        assertEquals("", StringUtil.repeat('a', -1));
    }

    // ---- reverse ----

    @Test
    void reverse_normalCase() {
        assertEquals("cba", StringUtil.reverse("abc"));
        assertEquals("hello", StringUtil.reverse("olleh"));
        assertEquals("", StringUtil.reverse(""));
        assertNull(StringUtil.reverse(null));
    }

    // ---- padding ----

    @Test
    void lpad_singleChar() {
        assertEquals("00123", StringUtil.lpad(5, "123", "0"));
        assertEquals("hello", StringUtil.lpad(5, "hello", "0"));
        assertEquals("hello", StringUtil.lpad(3, "hello", "0"));
        assertEquals("  hi", StringUtil.lpad(4, "hi", " "));
        assertEquals("00000", StringUtil.lpad(5, null, "0"));
    }

    @Test
    void rpad_singleChar() {
        assertEquals("12300", StringUtil.rpad(5, "123", "0"));
        assertEquals("hello", StringUtil.rpad(5, "hello", "0"));
        assertEquals("hello", StringUtil.rpad(3, "hello", "0"));
        assertEquals("hi  ", StringUtil.rpad(4, "hi", " "));
    }

    @Test
    void pad_singleChar() {
        assertEquals("01230", StringUtil.pad(5, "123", "0"));
        assertEquals("hello", StringUtil.pad(5, "hello", "0"));
        assertEquals("hello", StringUtil.pad(3, "hello", "0"));
    }

    @Test
    void lpad2_multiChar() {
        assertEquals("abab1", StringUtil.lpad2(5, "1", "ab"));
        assertEquals("hello", StringUtil.lpad2(5, "hello", "ab"));
    }

    @Test
    void rpad2_multiChar() {
        assertEquals("1abab", StringUtil.rpad2(5, "1", "ab"));
        assertEquals("hello", StringUtil.rpad2(5, "hello", "ab"));
    }

    @Test
    void pad2_multiChar() {
        assertEquals("aba1aba", StringUtil.pad2(7, "1", "ab"));
        assertEquals("hello", StringUtil.pad2(5, "hello", "ab"));
    }

    // ---- join / split ----

    @Test
    void join_normalCase() {
        assertEquals("a,b,c", StringUtil.join(Arrays.asList("a", "b", "c"), ","));
        assertEquals("abc", StringUtil.join(Arrays.asList("a", "b", "c"), ""));
    }

    @Test
    void join_emptyOrNull() {
        assertEquals("", StringUtil.join(Collections.emptyList(), ","));
        assertEquals("", StringUtil.join(null, ","));
        assertEquals("abc", StringUtil.join(Arrays.asList("a", "b", "c"), null)); // null delimiter → ""
    }

    @Test
    void join_withIntegerList() {
        assertEquals("1,2,3", StringUtil.join(Arrays.asList(1, 2, 3), ","));
    }

    @Test
    void split_normalCase() {
        assertEquals(Arrays.asList("a", "b", "c"), StringUtil.split("a,b,c", ","));
        assertEquals(Collections.singletonList("abc"), StringUtil.split("abc", ","));
    }

    @Test
    void split_nullAndEmpty() {
        assertEquals(Collections.emptyList(), StringUtil.split(null, ","));
        assertEquals(Collections.singletonList("abc"), StringUtil.split("abc", null));
    }

    @Test
    void split_withRegex() {
        List<String> result = StringUtil.split("a1b2c", "\\d");
        assertEquals(Arrays.asList("a", "b", "c"), result);
    }
}
