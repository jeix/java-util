package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class StringUtilTest {

    @BeforeAll
    static void beforeAll() {
        log.info("StringUtilTest start");
    }

    @Test
    void isBlankReturnsTrueForNullAndBlank() {
        assertTrue(StringUtil.isBlank(null));
        assertTrue(StringUtil.isBlank(""));
        assertTrue(StringUtil.isBlank("   "));
        assertTrue(StringUtil.isBlank("\t\n"));
    }

    @Test
    void isBlankReturnsFalseForText() {
        assertFalse(StringUtil.isBlank(" x "));
    }

    @Test
    void isEmptyReturnsTrueForNullAndEmpty() {
        assertTrue(StringUtil.isEmpty(null));
        assertTrue(StringUtil.isEmpty(""));
    }

    @Test
    void isEmptyReturnsFalseForWhitespace() {
        assertFalse(StringUtil.isEmpty(" "));
        assertFalse(StringUtil.isEmpty("a"));
    }

    @Test
    void nonNullOfReturnsValueWhenPresent() {
        assertEquals("a", StringUtil.nonNullOf("a", "d"));
    }

    @Test
    void nonNullOfReturnsDefaultWhenNull() {
        assertEquals("d", StringUtil.nonNullOf(null, "d"));
        assertNull(StringUtil.nonNullOf((String) null, null));
    }

    @Test
    void nonNullOfSupplierReturnsValueWhenPresent() {
        assertEquals("a", StringUtil.nonNullOf(() -> "a", () -> "d"));
    }

    @Test
    void nonNullOfSupplierReturnsDefaultWhenNull() {
        assertEquals("d", StringUtil.nonNullOf(() -> null, () -> "d"));
        assertNull(StringUtil.nonNullOf(null, (Supplier<String>) null));
    }

    @Test
    void nonNullOfSupplierDoesNotEvaluateDefaultWhenValuePresent() {
        AtomicInteger calls = new AtomicInteger();
        String result = StringUtil.nonNullOf(() -> "value", () -> {
            calls.incrementAndGet();
            return "default";
        });

        assertEquals("value", result);
        assertEquals(0, calls.get());
    }

    @Test
    void nonEmptyOfHandlesValue() {
        assertEquals("a", StringUtil.nonEmptyOf("a", "d"));
        assertEquals(" ", StringUtil.nonEmptyOf(" ", "d"));
        assertEquals("d", StringUtil.nonEmptyOf("", "d"));
        assertEquals("d", StringUtil.nonEmptyOf(null, "d"));
    }

    @Test
    void nonEmptyOfHandlesSupplier() {
        assertEquals("a", StringUtil.nonEmptyOf(() -> "a", () -> "d"));
        assertEquals("d", StringUtil.nonEmptyOf(() -> "", () -> "d"));
    }

    @Test
    void nonBlankOfHandlesValue() {
        assertEquals("a", StringUtil.nonBlankOf("a", "d"));
        assertEquals("d", StringUtil.nonBlankOf("  ", "d"));
        assertEquals("d", StringUtil.nonBlankOf(null, "d"));
    }

    @Test
    void nonBlankOfHandlesSupplier() {
        assertEquals("a", StringUtil.nonBlankOf(() -> "a", () -> "d"));
        assertEquals("d", StringUtil.nonBlankOf(() -> " ", () -> "d"));
    }

    @Test
    void firstNonBlankOrLastReturnsFirstNonBlankValue() {
        assertEquals("b", StringUtil.firstNonBlankOrLast(null, "b", "c"));
        assertEquals("c", StringUtil.firstNonBlankOrLast("", "  ", "c"));
    }

    @Test
    void firstNonBlankOrLastReturnsLastWhenAllBlank() {
        assertEquals(" ", StringUtil.firstNonBlankOrLast("", "  ", " "));
    }

    @Test
    void firstNonBlankOrLastHandlesSupplierOverloads() {
        assertEquals("b", StringUtil.firstNonBlankOrLast(() -> "", () -> "b"));
        assertEquals("c", StringUtil.firstNonBlankOrLast(() -> "", () -> "  ", () -> "c"));
        assertEquals("d", StringUtil.firstNonBlankOrLast(() -> "", () -> " ", () -> "  ", () -> "d"));
        assertEquals("e",
                StringUtil.firstNonBlankOrLast(() -> "", () -> " ", () -> "  ", () -> "   ", () -> "e"));
    }

    @Test
    void firstNonBlankOrLastReturnsLastSupplierValueWhenAllBlank() {
        assertEquals("   ", StringUtil.firstNonBlankOrLast(() -> "", () -> " ", () -> "   "));
        assertEquals(" ", StringUtil.firstNonBlankOrLast(() -> null, () -> " "));
    }

    @Test
    void firstNonBlankOrLastHandlesNullSupplier() {
        assertEquals("b", StringUtil.firstNonBlankOrLast((Supplier<String>) null, () -> "b"));
        assertNull(StringUtil.firstNonBlankOrLast((Supplier<String>) null, (Supplier<String>) null));
    }

    @Test
    void firstNonBlankOrEmptyReturnsFirstNonBlankValue() {
        assertEquals("b", StringUtil.firstNonBlankOrEmpty(null, "b", "c"));
    }

    @Test
    void firstNonBlankOrEmptyReturnsEmptyWhenAllBlank() {
        assertEquals("", StringUtil.firstNonBlankOrEmpty("", "  ", " "));
        assertEquals("", StringUtil.firstNonBlankOrEmpty(() -> "", () -> " ", () -> "   "));
    }

    @Test
    void firstNonBlankOrEmptyHandlesSupplierOverloads() {
        assertEquals("b", StringUtil.firstNonBlankOrEmpty(() -> "", () -> "b"));
        assertEquals("c", StringUtil.firstNonBlankOrEmpty(() -> "", () -> "  ", () -> "c"));
        assertEquals("d", StringUtil.firstNonBlankOrEmpty(() -> "", () -> " ", () -> "  ", () -> "d"));
        assertEquals("e",
                StringUtil.firstNonBlankOrEmpty(() -> "", () -> " ", () -> "  ", () -> "   ", () -> "e"));
    }

    @Test
    void firstNonBlankOrNullReturnsFirstNonBlankValue() {
        assertEquals("b", StringUtil.firstNonBlankOrNull(null, "b", "c"));
    }

    @Test
    void firstNonBlankOrNullReturnsNullWhenAllBlank() {
        assertNull(StringUtil.firstNonBlankOrNull("", "  ", " "));
        assertNull(StringUtil.firstNonBlankOrNull(() -> "", () -> " ", () -> "   "));
    }

    @Test
    void firstNonBlankOrNullHandlesSupplierOverloads() {
        assertEquals("b", StringUtil.firstNonBlankOrNull(() -> "", () -> "b"));
        assertEquals("c", StringUtil.firstNonBlankOrNull(() -> "", () -> "  ", () -> "c"));
        assertEquals("d", StringUtil.firstNonBlankOrNull(() -> "", () -> " ", () -> "  ", () -> "d"));
        assertEquals("e",
                StringUtil.firstNonBlankOrNull(() -> "", () -> " ", () -> "  ", () -> "   ", () -> "e"));
    }

    @Test
    void stringifyReturnsNullForNull() {
        assertNull(StringUtil.stringify(null));
    }

    @Test
    void stringifyFormatsDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2020, Calendar.JANUARY, 2, 0, 0, 0);

        assertEquals("2020-01-02", StringUtil.stringify(calendar.getTime()));
    }

    @Test
    void stringifyFormatsBigDecimalToPlainString() {
        assertEquals("1000", StringUtil.stringify(new BigDecimal("1E+3")));
        assertEquals("123.450", StringUtil.stringify(new BigDecimal("123.450")));
    }

    @Test
    void stringifyFallsBackToStringValueOf() {
        assertEquals("abc", StringUtil.stringify("abc"));
        assertEquals("123", StringUtil.stringify(123));
    }

    @Test
    void sliceReturnsSubstringFromBegin() {
        assertEquals("ello", StringUtil.slice("hello", 1));
    }

    @Test
    void sliceReturnsSubstringBetweenBeginAndEnd() {
        assertEquals("el", StringUtil.slice("hello", 1, 3));
    }

    @Test
    void sliceHandlesBoundaries() {
        assertEquals("hello", StringUtil.slice("hello", 0, 5));
        assertEquals("", StringUtil.slice("hello", 2, 2));
        assertNull(StringUtil.slice(null, 1));
    }

    @Test
    void sliceInterpretsNegativeIndexFromEnd() {
        assertEquals("o", StringUtil.slice("hello", -1));
        assertEquals("lo", StringUtil.slice("hello", -2));
        assertEquals("l", StringUtil.slice("hello", -2, -1));
        assertEquals("ell", StringUtil.slice("hello", 1, -1));
    }

    @Test
    void sliceClampsIndexToRange() {
        assertEquals("hello", StringUtil.slice("hello", -100));
        assertEquals("hello", StringUtil.slice("hello", 0, 100));
        assertEquals("", StringUtil.slice("hello", 100));
    }

    @Test
    void sliceReturnsNullWhenBeginAfterEnd() {
        assertNull(StringUtil.slice("hello", 3, 2));
    }

    @Test
    void headReturnsLeadingPart() {
        assertEquals("he", StringUtil.head("hello", 2));
    }

    @Test
    void headHandlesBoundaries() {
        assertEquals("", StringUtil.head("hello", 0));
        assertEquals("hello", StringUtil.head("hello", 5));
        assertEquals("hello", StringUtil.head("hello", 9));
        assertNull(StringUtil.head(null, 2));
    }

    @Test
    void headInterpretsNegativeSizeFromEnd() {
        assertEquals("hell", StringUtil.head("hello", -1));
        assertEquals("", StringUtil.head("hello", -5));
        assertEquals("", StringUtil.head("hello", -100));
    }

    @Test
    void tailReturnsTrailingPart() {
        assertEquals("lo", StringUtil.tail("hello", 2));
    }

    @Test
    void tailHandlesBoundaries() {
        assertEquals("", StringUtil.tail("hello", 0));
        assertEquals("hello", StringUtil.tail("hello", 5));
        assertEquals("hello", StringUtil.tail("hello", 9));
        assertNull(StringUtil.tail(null, 2));
    }

    @Test
    void tailTreatsNegativeSizeAsFrontExcludeIndex() {
        assertEquals("ello", StringUtil.tail("hello", -1));
        assertEquals("llo", StringUtil.tail("hello", -2));
        assertEquals("", StringUtil.tail("hello", -5));
        assertEquals("", StringUtil.tail("hello", -100));
    }

    @Test
    void trimLeadingZeroRemovesLeadingZeros() {
        assertEquals("123", StringUtil.trimLeadingZero("00123"));
        assertEquals("100", StringUtil.trimLeadingZero("100"));
    }

    @Test
    void trimLeadingZeroKeepsAtLeastOneDigit() {
        assertEquals("0", StringUtil.trimLeadingZero("0"));
        assertEquals("0", StringUtil.trimLeadingZero("000"));
        assertEquals("", StringUtil.trimLeadingZero(""));
        assertNull(StringUtil.trimLeadingZero(null));
    }

    @Test
    void repeatRepeatsCharacter() {
        assertEquals("aaa", StringUtil.repeat("a", 3));
        assertEquals("----", StringUtil.repeat("-", 4));
        assertEquals("abab", StringUtil.repeat("ab", 2));
    }

    @Test
    void repeatHandlesNonPositiveSize() {
        assertEquals("", StringUtil.repeat("a", 0));
        assertEquals("", StringUtil.repeat("a", -1));
        assertEquals("", StringUtil.repeat(null, 3));
        assertEquals("", StringUtil.repeat("", 3));
    }

    @Test
    void reverseReversesString() {
        assertEquals("cba", StringUtil.reverse("abc"));
        assertEquals("a", StringUtil.reverse("a"));
        assertEquals("", StringUtil.reverse(""));
        assertNull(StringUtil.reverse(null));
    }

    @Test
    void lpadPadsOnLeftWithSingleCharacter() {
        assertEquals("000ab", StringUtil.lpad(5, "ab", "0"));
        assertEquals("   ab", StringUtil.lpad(5, "ab", " "));
    }

    @Test
    void lpadReturnsOriginalWhenNoPaddingNeeded() {
        assertEquals("ab", StringUtil.lpad(2, "ab", "0"));
        assertEquals("ab", StringUtil.lpad(5, "ab", ""));
        assertNull(StringUtil.lpad(5, null, "0"));
    }

    @Test
    void lpad2PadsOnLeftWithMultipleCharacters() {
        assertEquals("12121ab", StringUtil.lpad2(7, "ab", "12"));
        assertEquals("121212ab", StringUtil.lpad2(8, "ab", "12"));
    }

    @Test
    void rpadPadsOnRightWithSingleCharacter() {
        assertEquals("ab000", StringUtil.rpad(5, "ab", "0"));
    }

    @Test
    void rpadReturnsOriginalWhenNoPaddingNeeded() {
        assertEquals("ab", StringUtil.rpad(2, "ab", "0"));
        assertEquals("ab", StringUtil.rpad(5, "ab", ""));
        assertNull(StringUtil.rpad(5, null, "0"));
    }

    @Test
    void rpad2PadsOnRightWithMultipleCharacters() {
        assertEquals("ab12121", StringUtil.rpad2(7, "ab", "12"));
    }

    @Test
    void padCentersWithSingleCharacter() {
        assertEquals("0ab00", StringUtil.pad(5, "ab", "0"));
        assertEquals("00ab00", StringUtil.pad(6, "ab", "0"));
    }

    @Test
    void padReturnsOriginalWhenNoPaddingNeeded() {
        assertEquals("ab", StringUtil.pad(2, "ab", "0"));
        assertNull(StringUtil.pad(5, null, "0"));
    }

    @Test
    void pad2CentersWithMultipleCharacters() {
        assertEquals("12ab121", StringUtil.pad2(7, "ab", "12"));
    }

    @Test
    void joinConcatenatesWithDelimiter() {
        assertEquals("a,b,c", StringUtil.join(List.of("a", "b", "c"), ","));
        assertEquals("a", StringUtil.join(List.of("a"), ","));
        assertEquals("1-2", StringUtil.join(List.of(1, 2), "-"));
    }

    @Test
    void joinHandlesEmptyAndNullInput() {
        assertEquals("", StringUtil.join(List.of(), ","));
        assertEquals("", StringUtil.join(null, ","));
    }

    @Test
    void joinHandlesNullElementsAndDelimiter() {
        assertEquals("a,null,c", StringUtil.join(Arrays.asList("a", null, "c"), ","));
        assertEquals("ab", StringUtil.join(List.of("a", "b"), null));
    }

    @Test
    void joinStringifiesElements() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(2020, Calendar.JANUARY, 2, 0, 0, 0);

        assertEquals("2020-01-02", StringUtil.join(List.of(calendar.getTime()), ","));
        assertEquals("1000", StringUtil.join(List.of(new BigDecimal("1E+3")), ","));
    }

    @Test
    void splitReturnsParts() {
        assertEquals(List.of("a", "b", "c"), StringUtil.split("a,b,c", ","));
        assertEquals(List.of("a", "b", "c"), StringUtil.split("a1b2c", "\\d"));
        assertEquals(List.of("a"), StringUtil.split("a", ","));
    }

    @Test
    void splitReturnsEmptyListForNull() {
        assertEquals(List.of(), StringUtil.split(null, ","));
        assertEquals(List.of(), StringUtil.split("a,b", null));
    }

    @Test
    void pipeComposesFunctions() {
        String s = "2025-03-19 12:26:41.012345000";

        String result = StringUtil.pipe(
                value -> StringUtil.slice(value, 20),
                StringUtil::reverse,
                StringUtil::trimLeadingZero,
                StringUtil::reverse)
                .apply(s);

        assertEquals("012345", result);
    }

    @Test
    void pipelineComposesFunctions() {
        String s = "2025-03-19 12:26:41.012345000";

        String result = StringUtil.pipe()
                .then(value -> StringUtil.slice(value, 20))
                .then(StringUtil::reverse)
                .then(StringUtil::trimLeadingZero)
                .then(StringUtil::reverse)
                .apply(s);

        assertEquals("012345", result);
    }

    @Test
    void pipeIdentityReturnsInput() {
        assertEquals("abc", StringUtil.pipe().apply("abc"));
        assertNull(StringUtil.pipe().apply(null));
    }

    @Test
    void randomBranchesProduceConsistentResults() {
        IntStream.range(0, 100).forEach(i -> {
            assertEquals("123", StringUtil.trimLeadingZero("00123"));
            assertEquals("0", StringUtil.trimLeadingZero("000"));
            assertEquals("100", StringUtil.trimLeadingZero("100"));
            assertEquals("b", StringUtil.firstNonBlankOrLast(null, "b", "c"));
            assertEquals(" ", StringUtil.firstNonBlankOrLast("", "  ", " "));
            assertNull(StringUtil.firstNonBlankOrNull(() -> "", () -> " "));
            assertEquals("", StringUtil.firstNonBlankOrEmpty(() -> "", () -> " "));
            assertEquals("000ab", StringUtil.lpad(5, "ab", "0"));
            assertEquals("12121ab", StringUtil.lpad2(7, "ab", "12"));
            assertEquals("a,null,b-c", StringUtil.join(Arrays.asList("a", null, "b-c"), ","));
            assertEquals("012345", StringUtil.pipe(
                    value -> StringUtil.slice(value, 20),
                    StringUtil::reverse,
                    StringUtil::trimLeadingZero,
                    StringUtil::reverse).apply("2025-03-19 12:26:41.012345000"));
        });
    }
}
