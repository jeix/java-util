package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@Slf4j
@DisplayName("StringUtil")
class StringUtilTest {

    @Nested
    @DisplayName("isEmpty / isBlank")
    class Checks {

        @Test
        @DisplayName("null 이거나 빈 문자열이면 isEmpty 가 true")
        void isEmpty() {
            assertTrue(StringUtil.isEmpty(null));
            assertTrue(StringUtil.isEmpty(""));
            assertFalse(StringUtil.isEmpty(" "));
            assertFalse(StringUtil.isEmpty("a"));
        }

        @Test
        @DisplayName("null 이거나 공백 문자만 있으면 isBlank 가 true")
        void isBlank() {
            assertTrue(StringUtil.isBlank(null));
            assertTrue(StringUtil.isBlank(""));
            assertTrue(StringUtil.isBlank("   "));
            assertTrue(StringUtil.isBlank("\t\n"));
            assertFalse(StringUtil.isBlank(" a "));
        }
    }

    @Nested
    @DisplayName("nonNullOf / nonBlankOf / nonEmptyOf")
    class Fallbacks {

        @Test
        @DisplayName("nonNullOf: null 일 때만 기본값을 쓴다")
        void nonNullOf() {
            assertEquals("a", StringUtil.nonNullOf("a", "d"));
            assertEquals("", StringUtil.nonNullOf("", "d"));
            assertEquals(" ", StringUtil.nonNullOf(" ", "d"));
            assertEquals("d", StringUtil.nonNullOf(null, "d"));
        }

        @Test
        @DisplayName("nonNullOf: 기본값 supplier 는 값이 null 일 때만 평가한다")
        void nonNullOfSupplier() {
            Supplier<String> neverCalled = () -> {
                throw new IllegalStateException("기본값 supplier 를 평가하면 안 된다");
            };

            assertEquals("a", StringUtil.nonNullOf(() -> "a", neverCalled));
            assertEquals("d", StringUtil.nonNullOf(() -> null, () -> "d"));
        }

        @Test
        @DisplayName("nonBlankOf: null 이거나 공백뿐이면 기본값을 쓴다")
        void nonBlankOf() {
            assertEquals("a", StringUtil.nonBlankOf("a", "d"));
            assertEquals("d", StringUtil.nonBlankOf("", "d"));
            assertEquals("d", StringUtil.nonBlankOf("   ", "d"));
            assertEquals("d", StringUtil.nonBlankOf(null, "d"));
        }

        @Test
        @DisplayName("nonBlankOf: 기본값 supplier 는 값이 공백일 때만 평가한다")
        void nonBlankOfSupplier() {
            Supplier<String> neverCalled = () -> {
                throw new IllegalStateException("기본값 supplier 를 평가하면 안 된다");
            };

            assertEquals("a", StringUtil.nonBlankOf(() -> "a", neverCalled));
            assertEquals("d", StringUtil.nonBlankOf(() -> "  ", () -> "d"));
        }

        @Test
        @DisplayName("nonEmptyOf: null 이거나 빈 문자열이면 기본값을 쓴다")
        void nonEmptyOf() {
            assertEquals("a", StringUtil.nonEmptyOf("a", "d"));
            assertEquals(" ", StringUtil.nonEmptyOf(" ", "d"));
            assertEquals("d", StringUtil.nonEmptyOf("", "d"));
            assertEquals("d", StringUtil.nonEmptyOf(null, "d"));
        }

        @Test
        @DisplayName("nonEmptyOf: 기본값 supplier 는 값이 빈 문자열일 때만 평가한다")
        void nonEmptyOfSupplier() {
            Supplier<String> neverCalled = () -> {
                throw new IllegalStateException("기본값 supplier 를 평가하면 안 된다");
            };

            assertEquals(" ", StringUtil.nonEmptyOf(() -> " ", neverCalled));
            assertEquals("d", StringUtil.nonEmptyOf(() -> "", () -> "d"));
        }
    }

    @Nested
    @DisplayName("firstNonBlankOrLast / OrEmpty / OrNull")
    class FirstNonBlank {

        @Test
        @DisplayName("OrLast: 처음으로 공백이 아닌 값, 모두 공백이면 마지막 값")
        void orLast() {
            assertEquals("a", StringUtil.firstNonBlankOrLast("a", "b"));
            assertEquals("b", StringUtil.firstNonBlankOrLast(null, "b"));
            assertEquals("c", StringUtil.firstNonBlankOrLast(null, "", "c"));
            assertEquals("  ", StringUtil.firstNonBlankOrLast("", "  "));
            assertEquals("  ", StringUtil.firstNonBlankOrLast(null, "", "  "));
            assertNull(StringUtil.firstNonBlankOrLast((String) null, (String) null));
        }

        @Test
        @DisplayName("OrLast: supplier 는 앞에서부터 필요한 만큼만 평가한다")
        void orLastSupplier() {
            Supplier<String> neverCalled = () -> {
                throw new IllegalStateException("뒤 supplier 를 평가하면 안 된다");
            };

            assertEquals("a", StringUtil.firstNonBlankOrLast(() -> "a", neverCalled));
            assertEquals("c", StringUtil.firstNonBlankOrLast(() -> null, () -> "", () -> "c"));
            assertEquals(" ", StringUtil.firstNonBlankOrLast(() -> "", () -> " "));
        }

        @Test
        @DisplayName("OrLast: supplier 를 4개, 5개까지 받는다")
        void orLastSupplierOverloads() {
            assertEquals("d", StringUtil.firstNonBlankOrLast(() -> null, () -> "", () -> " ", () -> "d"));
            assertEquals("e", StringUtil.firstNonBlankOrLast(() -> null, () -> "", () -> " ", () -> null, () -> "e"));
        }

        @Test
        @DisplayName("OrEmpty: 처음으로 공백이 아닌 값, 모두 공백이면 빈 문자열")
        void orEmpty() {
            assertEquals("a", StringUtil.firstNonBlankOrEmpty("", "a"));
            assertEquals("a", StringUtil.firstNonBlankOrEmpty(null, "", "a"));
            assertEquals("", StringUtil.firstNonBlankOrEmpty("", "  "));
            assertEquals("", StringUtil.firstNonBlankOrEmpty((String) null, (String) null));
        }

        @Test
        @DisplayName("OrEmpty: supplier 버전")
        void orEmptySupplier() {
            assertEquals("a", StringUtil.firstNonBlankOrEmpty(() -> null, () -> "a"));
            assertEquals("", StringUtil.firstNonBlankOrEmpty(() -> "", () -> "  "));
            assertEquals("e", StringUtil.firstNonBlankOrEmpty(() -> null, () -> "", () -> " ", () -> null, () -> "e"));
        }

        @Test
        @DisplayName("OrNull: 처음으로 공백이 아닌 값, 모두 공백이면 null")
        void orNull() {
            assertEquals("a", StringUtil.firstNonBlankOrNull("", "a"));
            assertEquals("a", StringUtil.firstNonBlankOrNull(null, "", "a"));
            assertNull(StringUtil.firstNonBlankOrNull("", "  "));
            assertNull(StringUtil.firstNonBlankOrNull((String) null, (String) null));
        }

        @Test
        @DisplayName("OrNull: supplier 버전")
        void orNullSupplier() {
            assertEquals("a", StringUtil.firstNonBlankOrNull(() -> null, () -> "a"));
            assertNull(StringUtil.firstNonBlankOrNull(() -> "", () -> "  "));
            assertNull(StringUtil.firstNonBlankOrNull(() -> null, () -> "", () -> " "));
        }
    }

    @Nested
    @DisplayName("stringify")
    class Stringify {

        @Test
        @DisplayName("null 은 null")
        void nullValue() {
            assertNull(StringUtil.stringify(null));
        }

        @Test
        @DisplayName("문자열과 숫자는 toString")
        void plain() {
            assertEquals("text", StringUtil.stringify("text"));
            assertEquals("123", StringUtil.stringify(123));
        }

        @Test
        @DisplayName("java.util.Date 는 yyyy-MM-dd")
        void date() {
            Date date = Date.from(LocalDate.of(2026, 9, 15).atStartOfDay(ZoneId.systemDefault()).toInstant());
            String text = StringUtil.stringify(date);

            log.info("Date stringify: {}", text);

            assertEquals("2026-09-15", text);
        }

        @Test
        @DisplayName("BigDecimal 은 지수 표기 없이")
        void bigDecimal() {
            assertEquals("1000", StringUtil.stringify(new BigDecimal("1.0E+3")));
            assertEquals("0.10", StringUtil.stringify(new BigDecimal("0.10")));
        }
    }

    @Nested
    @DisplayName("slice / head / tail")
    class Substrings {

        @ParameterizedTest(name = "slice({0}, {1}) = {2}")
        @CsvSource({
                "abcdef, 2, cdef",
                "abcdef, 0, abcdef",
                "abcdef, 6, ''",
                "abcdef, 10, ''",
                "abcdef, -1, f",
                "abcdef, -2, ef",
                "abcdef, -10, abcdef",
        })
        void sliceBegin(String s, int begin, String expected) {
            assertEquals(expected, StringUtil.slice(s, begin));
        }

        @ParameterizedTest(name = "slice({0}, {1}, {2}) = {3}")
        @CsvSource({
                "abcdef, 1, 4, bcd",
                "abcdef, 0, 6, abcdef",
                "abcdef, 3, 3, ''",
                "abcdef, 4, 1, ''",
                "abcdef, 1, -1, bcde",
                "abcdef, -3, -1, de",
                "abcdef, -1, 10, f",
        })
        void sliceRange(String s, int begin, int end, String expected) {
            assertEquals(expected, StringUtil.slice(s, begin, end));
        }

        @Test
        @DisplayName("문자열이 null 이면 null")
        void sliceNull() {
            assertNull(StringUtil.slice(null, 1));
            assertNull(StringUtil.slice(null, 1, 3));
        }

        @Test
        @DisplayName("head: 앞에서 size 글자. 길이보다 크면 전체, 음수는 뒤에서부터 센다")
        void head() {
            assertEquals("abc", StringUtil.head("abcdef", 3));
            assertEquals("abcdef", StringUtil.head("abcdef", 6));
            assertEquals("abcdef", StringUtil.head("abcdef", 10));
            assertEquals("", StringUtil.head("abcdef", 0));
            assertEquals("abcde", StringUtil.head("abcdef", -1));
            assertEquals("abcd", StringUtil.head("abcdef", -2));
            assertEquals("", StringUtil.head("abcdef", -6));
            assertNull(StringUtil.head(null, 3));
        }

        @Test
        @DisplayName("tail: 뒤에서 size 글자. 길이보다 크면 전체, 음수는 앞에서 제외할 글자 수")
        void tail() {
            assertEquals("def", StringUtil.tail("abcdef", 3));
            assertEquals("abcdef", StringUtil.tail("abcdef", 6));
            assertEquals("abcdef", StringUtil.tail("abcdef", 10));
            assertEquals("", StringUtil.tail("abcdef", 0));
            assertEquals("bcdef", StringUtil.tail("abcdef", -1));
            assertEquals("cdef", StringUtil.tail("abcdef", -2));
            assertEquals("", StringUtil.tail("abcdef", -10));
            assertNull(StringUtil.tail(null, 3));
        }
    }

    @Nested
    @DisplayName("trimLeadingZero")
    class TrimLeadingZero {

        @ParameterizedTest(name = "trimLeadingZero({0}) = {1}")
        @CsvSource({
                "007, 7",
                "000, 0",
                "0, 0",
                "123, 123",
                "00123, 123",
                "0.5, 0.5",
                "00.5, 0.5",
        })
        void trim(String s, String expected) {
            assertEquals(expected, StringUtil.trimLeadingZero(s));
        }

        @Test
        @DisplayName("null 과 빈 문자열은 그대로")
        void blank() {
            assertNull(StringUtil.trimLeadingZero(null));
            assertEquals("", StringUtil.trimLeadingZero(""));
        }
    }

    @Nested
    @DisplayName("repeat / reverse")
    class RepeatReverse {

        @ParameterizedTest(name = "repeat(\"*\", {0}) = {1}")
        @CsvSource({"3, ***", "1, *", "0, ''", "-1, ''"})
        void repeat(int size, String expected) {
            assertEquals(expected, StringUtil.repeat("*", size));
        }

        @Test
        @DisplayName("repeat: c 가 null 이거나 비어 있으면 빈 문자열")
        void repeatBlank() {
            assertEquals("", StringUtil.repeat(null, 3));
            assertEquals("", StringUtil.repeat("", 3));
        }

        @Test
        @DisplayName("reverse: 문자열을 뒤집는다")
        void reverse() {
            assertEquals("cba", StringUtil.reverse("abc"));
            assertEquals("a", StringUtil.reverse("a"));
            assertEquals("", StringUtil.reverse(""));
            assertNull(StringUtil.reverse(null));
        }
    }

    @Nested
    @DisplayName("lpad / rpad / pad")
    class Padding {

        @ParameterizedTest(name = "lpad({0}, {1}, {2}) = {3}")
        @CsvSource({
                "5, ab, 0, 000ab",
                "3, ab, 0, 0ab",
                "4, '', 0, 0000",
        })
        void lpad(int len, String s, String pad, String expected) {
            assertEquals(expected, StringUtil.lpad(len, s, pad));
        }

        @ParameterizedTest(name = "rpad({0}, {1}, {2}) = {3}")
        @CsvSource({
                "5, ab, 0, ab000",
                "3, ab, 0, ab0",
                "4, '', 0, 0000",
        })
        void rpad(int len, String s, String pad, String expected) {
            assertEquals(expected, StringUtil.rpad(len, s, pad));
        }

        @ParameterizedTest(name = "pad({0}, {1}, {2}) = {3}")
        @CsvSource({
                "7, abc, -, --abc--",
                "8, abc, -, --abc---",
                "9, abc, -, ---abc---",
                "5, abc, -, -abc-",
        })
        void pad(int len, String s, String pad, String expected) {
            assertEquals(expected, StringUtil.pad(len, s, pad));
        }

        @ParameterizedTest(name = "lpad2({0}, {1}, {2}) = {3}")
        @CsvSource({
                "8, ab, xy, xyxyxyab",
                "7, ab, xy, xyxyxab",
                "9, ab, xy, xyxyxyxab",
        })
        void lpad2(int len, String s, String pad, String expected) {
            assertEquals(expected, StringUtil.lpad2(len, s, pad));
        }

        @ParameterizedTest(name = "rpad2({0}, {1}, {2}) = {3}")
        @CsvSource({
                "8, ab, xy, abxyxyxy",
                "7, ab, xy, abxyxyx",
                "9, ab, xy, abxyxyxyx",
        })
        void rpad2(int len, String s, String pad, String expected) {
            assertEquals(expected, StringUtil.rpad2(len, s, pad));
        }

        @ParameterizedTest(name = "pad2({0}, {1}, {2}) = {3}")
        @CsvSource({
                "9, ab, xy, xyxabxyxy",
                "10, ab, xy, xyxyabxyxy",
        })
        void pad2(int len, String s, String pad, String expected) {
            assertEquals(expected, StringUtil.pad2(len, s, pad));
        }

        @Test
        @DisplayName("이미 len 이상이면 문자열을 그대로 돌려준다")
        void alreadyLongEnough() {
            assertEquals("hello", StringUtil.lpad(3, "hello", "x"));
            assertEquals("hello", StringUtil.rpad(3, "hello", "x"));
            assertEquals("hello", StringUtil.pad(3, "hello", "x"));
            assertEquals("hello", StringUtil.lpad2(3, "hello", "xy"));
            assertEquals("hello", StringUtil.rpad2(3, "hello", "xy"));
            assertEquals("hello", StringUtil.pad2(3, "hello", "xy"));
        }

        @Test
        @DisplayName("채울 문자열이 없으면 문자열을 그대로 돌려준다")
        void blankPad() {
            assertEquals("ab", StringUtil.lpad(5, "ab", ""));
            assertEquals("ab", StringUtil.rpad(5, "ab", ""));
            assertEquals("ab", StringUtil.pad(5, "ab", ""));
            assertEquals("ab", StringUtil.lpad2(5, "ab", null));
        }

        @Test
        @DisplayName("문자열이 null 이면 null")
        void nullSource() {
            assertNull(StringUtil.lpad(5, null, "0"));
            assertNull(StringUtil.rpad(5, null, "0"));
            assertNull(StringUtil.pad(5, null, "0"));
            assertNull(StringUtil.lpad2(5, null, "xy"));
        }
    }

    @Nested
    @DisplayName("join / split")
    class JoinSplit {

        @Test
        @DisplayName("delimiter 로 이어 붙인다")
        void join() {
            List<String> list = List.of("a", "b", "c");

            assertEquals("a,b,c", StringUtil.join(list, ","));
            assertEquals("a, b, c", StringUtil.join(list, ", "));
            assertEquals("abc", StringUtil.join(list, ""));
        }

        @Test
        @DisplayName("원소는 stringify 를 거쳐 붙는다")
        void joinStringify() {
            List<Object> list = new ArrayList<>();
            list.add("a");
            list.add(123);
            list.add(new BigDecimal("1.0E+3"));
            list.add(null);

            String joined = StringUtil.join(list, "|");
            log.info("join([\"a\", 123, 1.0E+3, null], \"|\") = {}", joined);

            assertEquals("a|123|1000|null", joined);
        }

        @Test
        @DisplayName("목록이 null 이거나 비어 있으면 빈 문자열")
        void joinEmpty() {
            assertEquals("", StringUtil.join(null, ","));
            assertEquals("", StringUtil.join(List.of(), ","));
        }

        @Test
        @DisplayName("delimiter 가 null 이면 구분자 없이 붙인다")
        void joinNullDelimiter() {
            assertEquals("abc", StringUtil.join(List.of("a", "b", "c"), null));
        }

        @Test
        @DisplayName("정규식으로 나눈다")
        void split() {
            assertEquals(List.of("a", "b", "c"), StringUtil.split("a,b,c", ","));
            assertEquals(List.of("a", "b", "c"), StringUtil.split("a, b, c", "\\s*,\\s*"));
            assertEquals(List.of("a", "b"), StringUtil.split("a1b", "\\d"));
        }

        @Test
        @DisplayName("문자열이 null 이거나 비어 있으면 빈 목록")
        void splitEmpty() {
            assertEquals(List.of(), StringUtil.split(null, ","));
            assertEquals(List.of(), StringUtil.split("", ","));
        }

        @Test
        @DisplayName("정규식이 null 이거나 비어 있으면 문자열 하나짜리 목록")
        void splitBlankRegex() {
            assertEquals(List.of("a,b"), StringUtil.split("a,b", null));
            assertEquals(List.of("a,b"), StringUtil.split("a,b", ""));
        }
    }
}
