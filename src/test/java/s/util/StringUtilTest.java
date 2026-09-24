package s.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class StringUtilTest {

    @Test
    void isBlank_null_returnsTrue() {
        assertTrue(StringUtil.isBlank(null));
    }

    @Test
    void isBlank_empty_returnsTrue() {
        assertTrue(StringUtil.isBlank(""));
    }

    @Test
    void isBlank_whitespace_returnsTrue() {
        assertTrue(StringUtil.isBlank("   "));
    }

    @Test
    void isBlank_nonBlank_returnsFalse() {
        assertFalse(StringUtil.isBlank("hello"));
    }

    @Test
    void isEmpty_null_returnsTrue() {
        assertTrue(StringUtil.isEmpty(null));
    }

    @Test
    void isEmpty_empty_returnsTrue() {
        assertTrue(StringUtil.isEmpty(""));
    }

    @Test
    void isEmpty_whitespace_returnsFalse() {
        assertFalse(StringUtil.isEmpty("   "));
    }

    @Test
    void isEmpty_nonEmpty_returnsFalse() {
        assertFalse(StringUtil.isEmpty("hello"));
    }

    @Test
    void nonNullOf_nullValue_returnsDefault() {
        assertEquals("default", StringUtil.nonNullOf(null, "default"));
    }

    @Test
    void nonNullOf_nonNullValue_returnsValue() {
        assertEquals("value", StringUtil.nonNullOf("value", "default"));
    }

    @Test
    void nonNullOf_supplierNull_returnsDefault() {
        assertEquals("default", StringUtil.nonNullOf(() -> null, () -> "default"));
    }

    @Test
    void nonNullOf_supplierNonNull_returnsValue() {
        assertEquals("value", StringUtil.nonNullOf(() -> "value", () -> "default"));
    }

    @Test
    void nonBlankOf_nullValue_returnsDefault() {
        assertEquals("default", StringUtil.nonBlankOf(null, "default"));
    }

    @Test
    void nonBlankOf_blankValue_returnsDefault() {
        assertEquals("default", StringUtil.nonBlankOf("   ", "default"));
    }

    @Test
    void nonBlankOf_nonBlankValue_returnsValue() {
        assertEquals("value", StringUtil.nonBlankOf("value", "default"));
    }

    @Test
    void nonBlankOf_supplierNull_returnsDefault() {
        assertEquals("default", StringUtil.nonBlankOf(() -> null, () -> "default"));
    }

    @Test
    void nonBlankOf_supplierBlank_returnsDefault() {
        assertEquals("default", StringUtil.nonBlankOf(() -> "  ", () -> "default"));
    }

    @Test
    void nonBlankOf_supplierNonBlank_returnsValue() {
        assertEquals("value", StringUtil.nonBlankOf(() -> "value", () -> "default"));
    }

    @Test
    void nonEmptyOf_nullValue_returnsDefault() {
        assertEquals("default", StringUtil.nonEmptyOf(null, "default"));
    }

    @Test
    void nonEmptyOf_emptyValue_returnsDefault() {
        assertEquals("default", StringUtil.nonEmptyOf("", "default"));
    }

    @Test
    void nonEmptyOf_nonEmptyValue_returnsValue() {
        assertEquals("value", StringUtil.nonEmptyOf("value", "default"));
    }

    @Test
    void nonEmptyOf_supplierNull_returnsDefault() {
        assertEquals("default", StringUtil.nonEmptyOf(() -> null, () -> "default"));
    }

    @Test
    void nonEmptyOf_supplierEmpty_returnsDefault() {
        assertEquals("default", StringUtil.nonEmptyOf(() -> "", () -> "default"));
    }

    @Test
    void nonEmptyOf_supplierNonEmpty_returnsValue() {
        assertEquals("value", StringUtil.nonEmptyOf(() -> "value", () -> "default"));
    }

    @Test
    void firstNonBlankOrLast_allBlank_returnsLast() {
        assertEquals("last", StringUtil.firstNonBlankOrLast("  ", "  ", "last"));
    }

    @Test
    void firstNonBlankOrLast_firstNonBlank_returnsFirst() {
        assertEquals("first", StringUtil.firstNonBlankOrLast("first", "second", "third"));
    }

    @Test
    void firstNonBlankOrLast_secondNonBlank_returnsSecond() {
        assertEquals("second", StringUtil.firstNonBlankOrLast("  ", "second", "third"));
    }

    @Test
    void firstNonBlankOrLast_supplierFirstNonBlank_returnsFirst() {
        assertEquals("first", StringUtil.firstNonBlankOrLast(() -> "first", () -> "second"));
    }

    @Test
    void firstNonBlankOrLast_supplierSecondNonBlank_returnsSecond() {
        assertEquals("second", StringUtil.firstNonBlankOrLast(() -> "  ", () -> "second"));
    }

    @Test
    void firstNonBlankOrLast_supplierAllBlank_returnsNull() {
        assertNull(StringUtil.firstNonBlankOrLast(() -> "  ", () -> "  "));
    }

    @Test
    void firstNonBlankOrLast_supplierThree_returnsFirstNonBlank() {
        assertEquals("third", StringUtil.firstNonBlankOrLast(() -> "  ", () -> "  ", () -> "third"));
    }

    @Test
    void firstNonBlankOrLast_supplierFour_returnsFirstNonBlank() {
        assertEquals("fourth", StringUtil.firstNonBlankOrLast(() -> "  ", () -> "  ", () -> "  ", () -> "fourth"));
    }

    @Test
    void firstNonBlankOrLast_supplierFive_returnsFirstNonBlank() {
        assertEquals("fifth", StringUtil.firstNonBlankOrLast(() -> "  ", () -> "  ", () -> "  ", () -> "  ", () -> "fifth"));
    }

    @Test
    void firstNonBlankOrEmpty_allBlank_returnsEmpty() {
        assertEquals("", StringUtil.firstNonBlankOrEmpty("  ", "  ", "  "));
    }

    @Test
    void firstNonBlankOrEmpty_firstNonBlank_returnsFirst() {
        assertEquals("first", StringUtil.firstNonBlankOrEmpty("first", "second", "third"));
    }

    @Test
    void firstNonBlankOrEmpty_supplierFirstNonBlank_returnsFirst() {
        assertEquals("first", StringUtil.firstNonBlankOrEmpty(() -> "first", () -> "second"));
    }

    @Test
    void firstNonBlankOrEmpty_supplierAllBlank_returnsEmpty() {
        assertEquals("", StringUtil.firstNonBlankOrEmpty(() -> "  ", () -> "  "));
    }

    @Test
    void firstNonBlankOrEmpty_supplierThree_returnsFirstNonBlank() {
        assertEquals("third", StringUtil.firstNonBlankOrEmpty(() -> "  ", () -> "  ", () -> "third"));
    }

    @Test
    void firstNonBlankOrEmpty_supplierFour_returnsFirstNonBlank() {
        assertEquals("fourth", StringUtil.firstNonBlankOrEmpty(() -> "  ", () -> "  ", () -> "  ", () -> "fourth"));
    }

    @Test
    void firstNonBlankOrEmpty_supplierFive_returnsFirstNonBlank() {
        assertEquals("fifth", StringUtil.firstNonBlankOrEmpty(() -> "  ", () -> "  ", () -> "  ", () -> "  ", () -> "fifth"));
    }

    @Test
    void firstNonBlankOrNull_allBlank_returnsNull() {
        assertNull(StringUtil.firstNonBlankOrNull("  ", "  ", "  "));
    }

    @Test
    void firstNonBlankOrNull_firstNonBlank_returnsFirst() {
        assertEquals("first", StringUtil.firstNonBlankOrNull("first", "second", "third"));
    }

    @Test
    void firstNonBlankOrNull_supplierFirstNonBlank_returnsFirst() {
        assertEquals("first", StringUtil.firstNonBlankOrNull(() -> "first", () -> "second"));
    }

    @Test
    void firstNonBlankOrNull_supplierAllBlank_returnsNull() {
        assertNull(StringUtil.firstNonBlankOrNull(() -> "  ", () -> "  "));
    }

    @Test
    void firstNonBlankOrNull_supplierThree_returnsFirstNonBlank() {
        assertEquals("third", StringUtil.firstNonBlankOrNull(() -> "  ", () -> "  ", () -> "third"));
    }

    @Test
    void firstNonBlankOrNull_supplierFour_returnsFirstNonBlank() {
        assertEquals("fourth", StringUtil.firstNonBlankOrNull(() -> "  ", () -> "  ", () -> "  ", () -> "fourth"));
    }

    @Test
    void firstNonBlankOrNull_supplierFive_returnsFirstNonBlank() {
        assertEquals("fifth", StringUtil.firstNonBlankOrNull(() -> "  ", () -> "  ", () -> "  ", () -> "  ", () -> "fifth"));
    }

    @Test
    void stringify_null_returnsNullString() {
        assertEquals("null", StringUtil.stringify(null));
    }

    @Test
    void stringify_date_returnsFormattedDate() {
        Date date = new Date(125, 0, 15); // 2025-01-15
        assertEquals("2025-01-15", StringUtil.stringify(date));
    }

    @Test
    void stringify_bigDecimal_returnsPlainString() {
        BigDecimal bd = new BigDecimal("123.450000");
        assertEquals("123.450000", StringUtil.stringify(bd));
    }

    @Test
    void stringify_object_returnsToString() {
        assertEquals("hello", StringUtil.stringify("hello"));
    }

    @Test
    void slice_begin_returnsSubstring() {
        assertEquals("llo", StringUtil.slice("hello", 2));
    }

    @Test
    void slice_beginNegative_returnsFromOffset() {
        assertEquals("lo", StringUtil.slice("hello", -2));
    }

    @Test
    void slice_beginEnd_returnsSubstring() {
        assertEquals("ell", StringUtil.slice("hello", 1, 4));
    }

    @Test
    void slice_beginEndNegative_returnsSubstring() {
        assertEquals("ll", StringUtil.slice("hello", -3, -1));
    }

    @Test
    void slice_null_returnsNull() {
        assertNull(StringUtil.slice(null, 0));
    }

    @Test
    void head_size_returnsFirstNChars() {
        assertEquals("hel", StringUtil.head("hello", 3));
    }

    @Test
    void head_sizeLargerThanLength_returnsFullString() {
        assertEquals("hello", StringUtil.head("hello", 10));
    }

    @Test
    void head_negativeSize_returnsFromOffset() {
        assertEquals("hel", StringUtil.head("hello", -2));
    }

    @Test
    void head_negativeSizeLargerThanLength_returnsEmpty() {
        assertEquals("", StringUtil.head("hello", -10));
    }

    @Test
    void tail_size_returnsLastNChars() {
        assertEquals("llo", StringUtil.tail("hello", 3));
    }

    @Test
    void tail_sizeLargerThanLength_returnsFullString() {
        assertEquals("hello", StringUtil.tail("hello", 10));
    }

    @Test
    void tail_negativeSize_returnsFromBeginIndex() {
        assertEquals("o", StringUtil.tail("hello", -1));
    }

    @Test
    void tail_negativeSizeLargerThanLength_returnsFullString() {
        assertEquals("hello", StringUtil.tail("hello", -10));
    }

    @Test
    void trimLeadingZero_removesLeadingZeros() {
        assertEquals("123", StringUtil.trimLeadingZero("000123"));
    }

    @Test
    void trimLeadingZero_allZeros_returnsSingleZero() {
        assertEquals("0", StringUtil.trimLeadingZero("0000"));
    }

    @Test
    void trimLeadingZero_noLeadingZeros_returnsOriginal() {
        assertEquals("123", StringUtil.trimLeadingZero("123"));
    }

    @Test
    void trimLeadingZero_null_returnsNull() {
        assertNull(StringUtil.trimLeadingZero(null));
    }

    @Test
    void repeat_createsRepeatedString() {
        assertEquals("aaa", StringUtil.repeat("a", 3));
    }

    @Test
    void repeat_zero_returnsEmpty() {
        assertEquals("", StringUtil.repeat("a", 0));
    }

    @Test
    void repeat_negative_returnsEmpty() {
        assertEquals("", StringUtil.repeat("a", -1));
    }

    @Test
    void repeat_multiCharString() {
        assertEquals("ababab", StringUtil.repeat("ab", 3));
    }

    @Test
    void reverse_reversesString() {
        assertEquals("olleh", StringUtil.reverse("hello"));
    }

    @Test
    void reverse_null_returnsNull() {
        assertNull(StringUtil.reverse(null));
    }

    @Test
    void lpad_padsLeft() {
        assertEquals("00123", StringUtil.lpad(5, "123", "0"));
    }

    @Test
    void lpad_alreadyLonger_returnsOriginal() {
        assertEquals("123456", StringUtil.lpad(3, "123456", "0"));
    }

    @Test
    void rpad_padsRight() {
        assertEquals("12300", StringUtil.rpad(5, "123", "0"));
    }

    @Test
    void rpad_alreadyLonger_returnsOriginal() {
        assertEquals("123456", StringUtil.rpad(3, "123456", "0"));
    }

    @Test
    void pad_padsBothSides() {
        assertEquals("01230", StringUtil.pad(5, "123", "0"));
    }

    @Test
    void pad_alreadyLonger_returnsOriginal() {
        assertEquals("123456", StringUtil.pad(3, "123456", "0"));
    }

    @Test
    void lpad2_padsLeftWithMultiChar() {
        assertEquals("ab123", StringUtil.lpad2(5, "123", "ab"));
    }

    @Test
    void rpad2_padsRightWithMultiChar() {
        assertEquals("123ab", StringUtil.rpad2(5, "123", "ab"));
    }

    @Test
    void pad2_padsBothSidesWithMultiChar() {
        assertEquals("a123a", StringUtil.pad2(5, "123", "ab"));
    }

    @Test
    void join_joinsListWithDelimiter() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("a,b,c", StringUtil.join(list, ","));
    }

    @Test
    void join_emptyList_returnsEmpty() {
        assertEquals("", StringUtil.join(Arrays.asList(), ","));
    }

    @Test
    void join_nullList_returnsEmpty() {
        assertEquals("", StringUtil.join(null, ","));
    }

    @Test
    void join_nullDelimiter_returnsConcatenated() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals("abc", StringUtil.join(list, null));
    }

    @Test
    void split_splitsString() {
        List<String> result = StringUtil.split("a,b,c", ",");
        assertEquals(Arrays.asList("a", "b", "c"), result);
    }

    @Test
    void split_nullString_returnsEmptyList() {
        assertEquals(0, StringUtil.split(null, ",").size());
    }

    @Test
    void split_nullRegex_returnsSingleElement() {
        List<String> result = StringUtil.split("hello", null);
        assertEquals(1, result.size());
        assertEquals("hello", result.get(0));
    }

    @Test
    void split_returnsImmutableList() {
        List<String> result = StringUtil.split("a,b,c", ",");
        assertThrows(UnsupportedOperationException.class, () -> result.add("d"));
    }
}
