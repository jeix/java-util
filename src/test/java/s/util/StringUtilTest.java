package s.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

/** {@link StringUtil}의 메서드들을 검증하는 테스트입니다. */
@Slf4j
class StringUtilTest {

    /** {@code null}을 반환하는 공급자입니다. */
    private static final Supplier<String> NULL_SUPPLIER = () -> null;

    /** 공백 문자열을 반환하는 공급자입니다. */
    private static final Supplier<String> BLANK_SUPPLIER = () -> " ";

    @BeforeAll
    static void setUp() {
        log.info("@Slf4j 로거를 사용하는 테스트 클래스입니다: {}", StringUtilTest.class.getName());
    }

    @Nested
    @DisplayName("기본 검사")
    class 기본검사 {

        @Test
        @DisplayName("isBlank: null이거나 공백 문자만 있으면 true")
        void isBlank() {
            assertAll(
                    () -> assertTrue(StringUtil.isBlank(null)),
                    () -> assertTrue(StringUtil.isBlank("")),
                    () -> assertTrue(StringUtil.isBlank("   ")),
                    () -> assertTrue(StringUtil.isBlank("\t\n")),
                    () -> assertFalse(StringUtil.isBlank("a")),
                    () -> assertFalse(StringUtil.isBlank(" a ")));
        }

        @Test
        @DisplayName("isEmpty: null이거나 길이가 0이면 true")
        void isEmpty() {
            assertAll(
                    () -> assertTrue(StringUtil.isEmpty(null)),
                    () -> assertTrue(StringUtil.isEmpty("")),
                    () -> assertFalse(StringUtil.isEmpty(" ")));
        }
    }

    @Nested
    @DisplayName("대체값 선택")
    class 대체값선택 {

        @Test
        @DisplayName("nonNullOf(값): null이면 기본값, 아니면 값을 반환한다")
        void nonNullOfValue() {
            String nullValue = null;
            String nullDflt = null;

            assertAll(
                    () -> assertEquals("값", StringUtil.nonNullOf("값", "기본")),
                    () -> assertEquals("", StringUtil.nonNullOf("", "기본")),
                    () -> assertEquals("기본", StringUtil.nonNullOf(nullValue, "기본")),
                    () -> assertNull(StringUtil.nonNullOf(nullValue, nullDflt)));
        }

        @Test
        @DisplayName("nonNullOf(공급자): 값이 null이면 기본값 공급자를 호출한다")
        void nonNullOfSupplier() {
            AtomicInteger dfltCalls = new AtomicInteger();
            Supplier<String> dfltSupplier = () -> {
                dfltCalls.incrementAndGet();
                return "기본";
            };

            assertAll(
                    () -> assertEquals("값", StringUtil.nonNullOf(() -> "값", dfltSupplier)),
                    () -> assertEquals(0, dfltCalls.get(), "값이 있으면 기본값 공급자를 호출하지 않는다"),
                    () -> assertEquals("기본", StringUtil.nonNullOf(NULL_SUPPLIER, dfltSupplier)),
                    () -> assertEquals(1, dfltCalls.get()));
        }

        @Test
        @DisplayName("nonBlankOf(값): 공백이면 기본값, 아니면 값을 반환한다")
        void nonBlankOfValue() {
            String nullValue = null;

            assertAll(
                    () -> assertEquals("값", StringUtil.nonBlankOf("값", "기본")),
                    () -> assertEquals(" 값 ", StringUtil.nonBlankOf(" 값 ", "기본")),
                    () -> assertEquals("기본", StringUtil.nonBlankOf(" ", "기본")),
                    () -> assertEquals("기본", StringUtil.nonBlankOf(nullValue, "기본")));
        }

        @Test
        @DisplayName("nonBlankOf(공급자): 값이 공백이면 기본값 공급자를 호출한다")
        void nonBlankOfSupplier() {
            assertAll(
                    () -> assertEquals("값", StringUtil.nonBlankOf(() -> "값", () -> "기본")),
                    () -> assertEquals("기본", StringUtil.nonBlankOf(BLANK_SUPPLIER, () -> "기본")),
                    () -> assertEquals("기본", StringUtil.nonBlankOf(NULL_SUPPLIER, () -> "기본")));
        }

        @Test
        @DisplayName("nonEmptyOf(값): 비어 있으면 기본값, 아니면 값을 반환한다")
        void nonEmptyOfValue() {
            String nullValue = null;

            assertAll(
                    () -> assertEquals("값", StringUtil.nonEmptyOf("값", "기본")),
                    () -> assertEquals(" ", StringUtil.nonEmptyOf(" ", "기본")),
                    () -> assertEquals("기본", StringUtil.nonEmptyOf("", "기본")),
                    () -> assertEquals("기본", StringUtil.nonEmptyOf(nullValue, "기본")));
        }

        @Test
        @DisplayName("nonEmptyOf(공급자): 값이 비어 있으면 기본값 공급자를 호출한다")
        void nonEmptyOfSupplier() {
            assertAll(
                    () -> assertEquals("값", StringUtil.nonEmptyOf(() -> "값", () -> "기본")),
                    () -> assertEquals(" ", StringUtil.nonEmptyOf(BLANK_SUPPLIER, () -> "기본")),
                    () -> assertEquals("기본", StringUtil.nonEmptyOf(() -> "", () -> "기본")),
                    () -> assertEquals("기본", StringUtil.nonEmptyOf(NULL_SUPPLIER, () -> "기본")));
        }
    }

    @Nested
    @DisplayName("공백이 아닌 첫 번째 값")
    class 첫번째비공백값 {

        @Test
        @DisplayName("firstNonBlankOrLast(값): 공백이 아닌 첫 값, 모두 공백이면 마지막 값")
        void firstNonBlankOrLastValue() {
            assertAll(
                    () -> assertEquals("a", StringUtil.firstNonBlankOrLast("a", "b", "c")),
                    () -> assertEquals("b", StringUtil.firstNonBlankOrLast("", "b", "c")),
                    () -> assertEquals("c", StringUtil.firstNonBlankOrLast(null, " ", "c")),
                    () -> assertEquals(" ", StringUtil.firstNonBlankOrLast("", "", " ")),
                    () -> assertEquals("", StringUtil.firstNonBlankOrLast("", "")),
                    () -> assertEquals("", StringUtil.firstNonBlankOrLast("", "", (String[]) null)),
                    () -> assertNull(StringUtil.firstNonBlankOrLast(null, null)));
        }

        @Test
        @DisplayName("firstNonBlankOrLast(공급자 2개)")
        void firstNonBlankOrLastSupplier2() {
            assertAll(
                    () -> assertEquals("첫째", StringUtil.firstNonBlankOrLast(() -> "첫째", () -> "둘째")),
                    () -> assertEquals("둘째", StringUtil.firstNonBlankOrLast(BLANK_SUPPLIER, () -> "둘째")),
                    () -> assertNull(StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, NULL_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrLast(공급자 3개)")
        void firstNonBlankOrLastSupplier3() {
            assertAll(
                    () -> assertEquals("첫째",
                            StringUtil.firstNonBlankOrLast(() -> "첫째", NULL_SUPPLIER, NULL_SUPPLIER)),
                    () -> assertEquals("셋째",
                            StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, BLANK_SUPPLIER, () -> "셋째")),
                    () -> assertNull(StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrLast(공급자 4개)")
        void firstNonBlankOrLastSupplier4() {
            assertAll(
                    () -> assertEquals("셋째",
                            StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, NULL_SUPPLIER, () -> "셋째", () -> "넷째")),
                    () -> assertEquals("넷째",
                            StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER, () -> "넷째")),
                    () -> assertEquals(" ",
                            StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, NULL_SUPPLIER, NULL_SUPPLIER,
                                    BLANK_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrLast(공급자 5개)")
        void firstNonBlankOrLastSupplier5() {
            assertAll(
                    () -> assertEquals("넷째",
                            StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, NULL_SUPPLIER, NULL_SUPPLIER,
                                    () -> "넷째", () -> "다섯째")),
                    () -> assertEquals("다섯째",
                            StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER,
                                    BLANK_SUPPLIER, () -> "다섯째")),
                    () -> assertEquals(" ",
                            StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, NULL_SUPPLIER, NULL_SUPPLIER,
                                    NULL_SUPPLIER, BLANK_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrLast(공급자): 유효한 값을 찾으면 뒤의 공급자는 호출하지 않는다")
        void firstNonBlankOrLastSupplierLazy() {
            AtomicInteger calls = new AtomicInteger();
            Supplier<String> counting = () -> {
                calls.incrementAndGet();
                return "값";
            };
            AtomicInteger callsInValues = new AtomicInteger();
            Supplier<String> countingInValues = () -> {
                callsInValues.incrementAndGet();
                return "값";
            };

            String result = StringUtil.firstNonBlankOrLast(() -> "첫째", counting, counting, counting);
            String resultInValues = StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, NULL_SUPPLIER, countingInValues,
                    countingInValues);

            assertAll(
                    () -> assertEquals("첫째", result),
                    () -> assertEquals(0, calls.get(), "앞에서 값을 찾으면 뒤의 공급자를 호출하지 않는다"),
                    () -> assertEquals("값", resultInValues),
                    () -> assertEquals(1, callsInValues.get(), "가변 인자에서도 첫 번째 값만 호출한다"));
        }

        @Test
        @DisplayName("firstNonBlankOrLast: 반복문 구현과 스트림 구현이 같은 결과를 반환한다")
        void firstNonBlankOrLastRandomBranch() {
            List<String> found = new ArrayList<>();
            List<String> foundInSuppliers = new ArrayList<>();
            List<String> allBlank = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                found.add(StringUtil.firstNonBlankOrLast("", " ", "값", null));
                foundInSuppliers.add(StringUtil.firstNonBlankOrLast(NULL_SUPPLIER, BLANK_SUPPLIER, () -> "값"));
                allBlank.add(StringUtil.firstNonBlankOrLast("", " ", (String) null));
            }

            assertAll(
                    () -> assertTrue(found.stream().allMatch("값"::equals), "값이 있으면 항상 같은 값을 반환한다"),
                    () -> assertTrue(foundInSuppliers.stream().allMatch("값"::equals)),
                    () -> assertTrue(allBlank.stream().allMatch(value -> value == null), "모두 공백이면 마지막 값을 반환한다"));
        }

        @Test
        @DisplayName("firstNonBlankOrEmpty(값): 모두 공백이면 빈 문자열")
        void firstNonBlankOrEmptyValue() {
            assertAll(
                    () -> assertEquals("a", StringUtil.firstNonBlankOrEmpty("a", "b")),
                    () -> assertEquals("b", StringUtil.firstNonBlankOrEmpty(" ", "b", (String[]) null)),
                    () -> assertEquals("", StringUtil.firstNonBlankOrEmpty("", " ", (String[]) null)),
                    () -> assertEquals("", StringUtil.firstNonBlankOrEmpty(null, null)));
        }

        @Test
        @DisplayName("firstNonBlankOrEmpty(공급자 2개)")
        void firstNonBlankOrEmptySupplier2() {
            assertAll(
                    () -> assertEquals("둘째", StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, () -> "둘째")),
                    () -> assertEquals("", StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, BLANK_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrEmpty(공급자 3개)")
        void firstNonBlankOrEmptySupplier3() {
            assertAll(
                    () -> assertEquals("둘째", StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, () -> "둘째", NULL_SUPPLIER)),
                    () -> assertEquals("셋째",
                            StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, BLANK_SUPPLIER, () -> "셋째")),
                    () -> assertEquals("", StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrEmpty(공급자 4개)")
        void firstNonBlankOrEmptySupplier4() {
            assertAll(
                    () -> assertEquals("넷째",
                            StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER, () -> "넷째")),
                    () -> assertEquals("",
                            StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER,
                                    BLANK_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrEmpty(공급자 5개)")
        void firstNonBlankOrEmptySupplier5() {
            assertAll(
                    () -> assertEquals("다섯째",
                            StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER,
                                    BLANK_SUPPLIER, () -> "다섯째")),
                    () -> assertEquals("",
                            StringUtil.firstNonBlankOrEmpty(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER,
                                    BLANK_SUPPLIER, NULL_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrNull(값): 모두 공백이면 null")
        void firstNonBlankOrNullValue() {
            assertAll(
                    () -> assertEquals("a", StringUtil.firstNonBlankOrNull(null, "a")),
                    () -> assertEquals("b", StringUtil.firstNonBlankOrNull("", " ", "b")),
                    () -> assertNull(StringUtil.firstNonBlankOrNull("", " ", (String[]) null)),
                    () -> assertNull(StringUtil.firstNonBlankOrNull(null, null)));
        }

        @Test
        @DisplayName("firstNonBlankOrNull(공급자 2개)")
        void firstNonBlankOrNullSupplier2() {
            assertAll(
                    () -> assertEquals("둘째", StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, () -> "둘째")),
                    () -> assertNull(StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, BLANK_SUPPLIER)),
                    () -> assertNull(StringUtil.firstNonBlankOrNull(BLANK_SUPPLIER, NULL_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrNull(공급자 3개)")
        void firstNonBlankOrNullSupplier3() {
            assertAll(
                    () -> assertEquals("셋째",
                            StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, BLANK_SUPPLIER, () -> "셋째")),
                    () -> assertNull(StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrNull(공급자 4개)")
        void firstNonBlankOrNullSupplier4() {
            assertAll(
                    () -> assertEquals("넷째",
                            StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER, () -> "넷째")),
                    () -> assertNull(
                            StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER, BLANK_SUPPLIER)));
        }

        @Test
        @DisplayName("firstNonBlankOrNull(공급자 5개)")
        void firstNonBlankOrNullSupplier5() {
            assertAll(
                    () -> assertEquals("다섯째",
                            StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER,
                                    BLANK_SUPPLIER, () -> "다섯째")),
                    () -> assertNull(
                            StringUtil.firstNonBlankOrNull(NULL_SUPPLIER, BLANK_SUPPLIER, NULL_SUPPLIER,
                                    BLANK_SUPPLIER, NULL_SUPPLIER)));
        }
    }

    @Nested
    @DisplayName("문자열 변환")
    class 문자열변환 {

        @Test
        @DisplayName("stringify: null, Date, BigDecimal, 그 외 객체를 문자열로 변환한다")
        void stringify() {
            Date date = Date.from(LocalDate.of(2024, 1, 2).atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Object> values = Arrays.asList(new BigDecimal("1.50"), 2);

            assertAll(
                    () -> assertNull(StringUtil.stringify(null)),
                    () -> assertEquals("text", StringUtil.stringify("text")),
                    () -> assertEquals("42", StringUtil.stringify(42)),
                    () -> assertEquals("1.50", StringUtil.stringify(new BigDecimal("1.50"))),
                    () -> assertEquals("1000", StringUtil.stringify(new BigDecimal("1E+3"))),
                    () -> assertEquals("2024-01-02", StringUtil.stringify(date)),
                    () -> assertEquals("1.50,2", StringUtil.join(values, ",")));
        }
    }

    @Nested
    @DisplayName("부분 문자열")
    class 부분문자열 {

        @Test
        @DisplayName("slice(s, begin): 시작 인덱스부터 끝까지 자른다")
        void sliceWithBegin() {
            assertAll(
                    () -> assertEquals("cdef", StringUtil.slice("abcdef", 2)),
                    () -> assertEquals("abcdef", StringUtil.slice("abcdef", 0)),
                    () -> assertEquals("", StringUtil.slice("abcdef", 6)),
                    () -> assertEquals("", StringUtil.slice("abcdef", 100)),
                    () -> assertEquals("f", StringUtil.slice("abcdef", -1), "음수는 역방향 인덱스다"),
                    () -> assertEquals("def", StringUtil.slice("abcdef", -3)),
                    () -> assertEquals("abcdef", StringUtil.slice("abcdef", -6)),
                    () -> assertEquals("abcdef", StringUtil.slice("abcdef", -100), "앞을 벗어나면 0으로 보정한다"),
                    () -> assertEquals("", StringUtil.slice("", 2)),
                    () -> assertNull(StringUtil.slice(null, 2)));
        }

        @Test
        @DisplayName("slice(s, begin, end): 시작 인덱스(포함)부터 끝 인덱스(불포함)까지 자른다")
        void sliceWithBeginAndEnd() {
            assertAll(
                    () -> assertEquals("cd", StringUtil.slice("abcdef", 2, 4)),
                    () -> assertEquals("", StringUtil.slice("abcdef", 4, 2)),
                    () -> assertEquals("cde", StringUtil.slice("abcdef", 2, -1), "음수 end는 역방향 인덱스다"),
                    () -> assertEquals("cde", StringUtil.slice("abcdef", -4, -1)),
                    () -> assertEquals("f", StringUtil.slice("abcdef", -1, 6)),
                    () -> assertEquals("f", StringUtil.slice("abcdef", -1, 99)),
                    () -> assertEquals("", StringUtil.slice("abcdef", -1, -3)),
                    () -> assertEquals("abcdef", StringUtil.slice("abcdef", 0, 6)),
                    () -> assertNull(StringUtil.slice(null, 2, 4)));
        }

        @Test
        @DisplayName("head(s, size): 앞에서부터 size만큼 자른다")
        void head() {
            assertAll(
                    () -> assertEquals("abc", StringUtil.head("abcdef", 3)),
                    () -> assertEquals("abcdef", StringUtil.head("abcdef", 6)),
                    () -> assertEquals("abcdef", StringUtil.head("abcdef", 100)),
                    () -> assertEquals("", StringUtil.head("abcdef", 0)),
                    () -> assertEquals("abcde", StringUtil.head("abcdef", -1), "음수 size는 역방향 인덱스다"),
                    () -> assertEquals("abc", StringUtil.head("abcdef", -3)),
                    () -> assertEquals("", StringUtil.head("abcdef", -6)),
                    () -> assertEquals("", StringUtil.head("abcdef", -100)),
                    () -> assertNull(StringUtil.head(null, 3)));
        }

        @Test
        @DisplayName("tail(s, size): 뒤에서부터 size만큼 자른다")
        void tail() {
            assertAll(
                    () -> assertEquals("def", StringUtil.tail("abcdef", 3)),
                    () -> assertEquals("abcdef", StringUtil.tail("abcdef", 6)),
                    () -> assertEquals("abcdef", StringUtil.tail("abcdef", 100)),
                    () -> assertEquals("", StringUtil.tail("abcdef", 0)),
                    () -> assertEquals("bcdef", StringUtil.tail("abcdef", -1), "음수 size는 앞에서 제외할 인덱스다"),
                    () -> assertEquals("cdef", StringUtil.tail("abcdef", -2)),
                    () -> assertEquals("f", StringUtil.tail("abcdef", -5)),
                    () -> assertEquals("", StringUtil.tail("abcdef", -6)),
                    () -> assertEquals("", StringUtil.tail("abcdef", -100)),
                    () -> assertNull(StringUtil.tail(null, 3)));
        }
    }

    @Nested
    @DisplayName("문자열 가공")
    class 문자열가공 {

        @Test
        @DisplayName("trimLeadingZero: 앞쪽 0을 제거하고 부호와 소수점은 유지한다")
        void trimLeadingZero() {
            assertAll(
                    () -> assertEquals("123", StringUtil.trimLeadingZero("000123")),
                    () -> assertEquals("0", StringUtil.trimLeadingZero("0")),
                    () -> assertEquals("0", StringUtil.trimLeadingZero("000")),
                    () -> assertEquals("-12", StringUtil.trimLeadingZero("-0012")),
                    () -> assertEquals("+12", StringUtil.trimLeadingZero("+0012")),
                    () -> assertEquals("0.5", StringUtil.trimLeadingZero("00.5")),
                    () -> assertEquals("100", StringUtil.trimLeadingZero("100")),
                    () -> assertEquals("abc", StringUtil.trimLeadingZero("abc")),
                    () -> assertEquals("", StringUtil.trimLeadingZero("")),
                    () -> assertNull(StringUtil.trimLeadingZero(null)));
        }

        @Test
        @DisplayName("repeat: 문자열을 지정한 길이만큼 반복한다")
        void repeat() {
            assertAll(
                    () -> assertEquals("***", StringUtil.repeat("*", 3)),
                    () -> assertEquals("a", StringUtil.repeat("a", 1)),
                    () -> assertEquals("ababa", StringUtil.repeat("ab", 5), "길이를 넘으면 잘라낸다"),
                    () -> assertEquals("", StringUtil.repeat("*", 0)),
                    () -> assertEquals("", StringUtil.repeat("*", -5)),
                    () -> assertEquals("", StringUtil.repeat("", 3)));
        }

        @Test
        @DisplayName("reverse: 문자열 순서를 뒤집는다")
        void reverse() {
            assertAll(
                    () -> assertEquals("cba", StringUtil.reverse("abc")),
                    () -> assertEquals("a", StringUtil.reverse("a")),
                    () -> assertEquals("", StringUtil.reverse("")),
                    () -> assertNull(StringUtil.reverse(null)));
        }
    }

    @Nested
    @DisplayName("패딩")
    class 패딩 {

        @Test
        @DisplayName("lpad: 단일 문자를 왼쪽에 채운다")
        void lpad() {
            assertAll(
                    () -> assertEquals("00012", StringUtil.lpad(5, "12", "0")),
                    () -> assertEquals("x12", StringUtil.lpad(3, "12", "x")),
                    () -> assertEquals("xxx12", StringUtil.lpad(5, "12", "xy"), "패딩은 첫 번째 문자만 사용한다"),
                    () -> assertEquals("123456", StringUtil.lpad(5, "123456", "0")),
                    () -> assertEquals("12", StringUtil.lpad(1, "12", "0")),
                    () -> assertEquals("12", StringUtil.lpad(5, "12", "")),
                    () -> assertNull(StringUtil.lpad(5, null, "0")));
        }

        @Test
        @DisplayName("rpad: 단일 문자를 오른쪽에 채운다")
        void rpad() {
            assertAll(
                    () -> assertEquals("12000", StringUtil.rpad(5, "12", "0")),
                    () -> assertEquals("12x", StringUtil.rpad(3, "12", "x")),
                    () -> assertEquals("12xxx", StringUtil.rpad(5, "12", "xy"), "패딩은 첫 번째 문자만 사용한다"),
                    () -> assertEquals("123456", StringUtil.rpad(5, "123456", "0")),
                    () -> assertEquals("12", StringUtil.rpad(1, "12", "0")),
                    () -> assertEquals("12", StringUtil.rpad(5, "12", "")),
                    () -> assertNull(StringUtil.rpad(5, null, "0")));
        }

        @Test
        @DisplayName("pad: 단일 문자를 양쪽에 채운다(남는 길이가 홀수면 오른쪽이 1글자 길다)")
        void pad() {
            assertAll(
                    () -> assertEquals("00ab000", StringUtil.pad(7, "ab", "0")),
                    () -> assertEquals("0ab0", StringUtil.pad(4, "ab", "0")),
                    () -> assertEquals("ab", StringUtil.pad(1, "ab", "0")),
                    () -> assertEquals("ab", StringUtil.pad(2, "ab", "0")),
                    () -> assertEquals("ab", StringUtil.pad(7, "ab", "")),
                    () -> assertNull(StringUtil.pad(7, null, "0")));
        }

        @Test
        @DisplayName("lpad2: 여러 문자 패딩을 왼쪽에 채운다")
        void lpad2() {
            assertAll(
                    () -> assertEquals("xyxyxabc", StringUtil.lpad2(8, "abc", "xy")),
                    () -> assertEquals("xabc", StringUtil.lpad2(4, "abc", "xy")),
                    () -> assertEquals("abc", StringUtil.lpad2(3, "abc", "xy")),
                    () -> assertEquals("abc", StringUtil.lpad2(10, "abc", ""),
                            "패딩 문자열이 없으면 원본을 반환한다"),
                    () -> assertNull(StringUtil.lpad2(10, null, "xy")));
        }

        @Test
        @DisplayName("rpad2: 여러 문자 패딩을 오른쪽에 채운다")
        void rpad2() {
            assertAll(
                    () -> assertEquals("abcxyxyx", StringUtil.rpad2(8, "abc", "xy")),
                    () -> assertEquals("abcx", StringUtil.rpad2(4, "abc", "xy")),
                    () -> assertEquals("abc", StringUtil.rpad2(3, "abc", "xy")),
                    () -> assertEquals("abc", StringUtil.rpad2(10, "abc", "")),
                    () -> assertNull(StringUtil.rpad2(10, null, "xy")));
        }

        @Test
        @DisplayName("pad2: 여러 문자 패딩을 양쪽에 채운다")
        void pad2() {
            assertAll(
                    () -> assertEquals("xyabcxyx", StringUtil.pad2(8, "abc", "xy")),
                    () -> assertEquals("xabcx", StringUtil.pad2(5, "abc", "xy")),
                    () -> assertEquals("abcx", StringUtil.pad2(4, "abc", "xy"),
                            "남는 길이가 홀수이면 오른쪽에 채운다"),
                    () -> assertEquals("abc", StringUtil.pad2(3, "abc", "xy")),
                    () -> assertEquals("abc", StringUtil.pad2(10, "abc", "")),
                    () -> assertNull(StringUtil.pad2(10, null, "xy")));
        }
    }

    @Nested
    @DisplayName("목록 변환")
    class 목록변환 {

        @Test
        @DisplayName("join: 목록 요소를 구분자로 연결한다")
        void join() {
            List<Object> values = Arrays.asList("a", null, new BigDecimal("1.50"));

            assertAll(
                    () -> assertEquals("a,b,c", StringUtil.join(List.of("a", "b", "c"), ",")),
                    () -> assertEquals("abc", StringUtil.join(List.of("a", "b", "c"), null)),
                    () -> assertEquals("a-null-1.50", StringUtil.join(values, "-"), "stringify를 재사용한다"),
                    () -> assertEquals("", StringUtil.join(List.of(), ",")),
                    () -> assertEquals("", StringUtil.join(null, ",")));
        }

        @Test
        @DisplayName("split: 정규식으로 나눈 결과를 불변 목록으로 반환한다")
        void split() {
            assertAll(
                    () -> assertEquals(List.of("a", "b", "c"), StringUtil.split("a,b,c", ",")),
                    () -> assertEquals(List.of("a", "b", "c"), StringUtil.split("a  b c", "\\s+")),
                    () -> assertEquals(List.of("a", "b", "c"), StringUtil.split("a1b2c", "[0-9]")),
                    () -> assertEquals(List.of(), StringUtil.split(",", ",")),
                    () -> assertEquals(List.of(), StringUtil.split("", ",")),
                    () -> assertEquals(List.of(), StringUtil.split(null, ",")),
                    () -> assertEquals(List.of(), StringUtil.split("a,b", null)));
        }
    }

    @Nested
    @DisplayName("파이프")
    class 파이프 {

        @Test
        @DisplayName("pipe(fns): 함수들을 순서대로 적용한다")
        void pipeWithFunctions() {
            String s = "2025-03-19 12:26:41.012345000";

            String result = StringUtil.pipe(
                    (input) -> StringUtil.slice(input, 20),
                    StringUtil::reverse,
                    StringUtil::trimLeadingZero,
                    StringUtil::reverse).apply(s);

            log.info("pipe 결과: {}", result);

            assertEquals("012345", result);
        }

        @Test
        @DisplayName("pipe(fns): 함수가 없거나 null이면 입력을 그대로 반환한다")
        void pipeWithoutFunctions() {
            Function<String, String>[] nullFns = null;

            assertAll(
                    () -> assertEquals("abc", StringUtil.pipe().apply("abc"), "빈 파이프라인은 항등이다"),
                    () -> assertEquals("abc", StringUtil.pipe(nullFns).apply("abc")),
                    () -> assertEquals("abc", StringUtil.pipe((Function<String, String>) null).apply("abc"),
                            "null 함수는 건너뛴다"),
                    () -> assertEquals("cba", StringUtil.pipe(null, StringUtil::reverse).apply("abc")));
        }

        @Test
        @DisplayName("pipe(fns): 결과를 다른 함수와 합성할 수 있다")
        void pipeIsComposable() {
            Function<String, String> twice = StringUtil.pipe(StringUtil::reverse).andThen(StringUtil::reverse);

            assertEquals("abc", twice.apply("abc"));
        }

        @Test
        @DisplayName("pipe(fns): 함수 합성 구현과 입력값 축소 구현이 같은 결과를 반환한다")
        void pipeRandomBranch() {
            List<String> results = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                results.add(StringUtil.pipe(
                        input -> StringUtil.slice(input, 20),
                        StringUtil::reverse,
                        StringUtil::trimLeadingZero,
                        StringUtil::reverse).apply("2025-03-19 12:26:41.012345000"));
                results.add(StringUtil.pipe(null, StringUtil::reverse).apply("abc"));
                results.add(StringUtil.pipe((Function<String, String>) null).apply("abc"));
            }

            assertAll(
                    () -> assertEquals(3, results.stream().distinct().count(), "012345, cba, abc만 나온다"),
                    () -> assertTrue(results.contains("012345")),
                    () -> assertTrue(results.contains("cba")),
                    () -> assertTrue(results.contains("abc")));
        }

        @Test
        @DisplayName("Pipeline: then으로 함수를 이어 붙여 적용한다")
        void pipeline() {
            String result = StringUtil.pipe()
                    .then(StringUtil::reverse)
                    .then(input -> input + "!")
                    .apply("abc");

            assertAll(
                    () -> assertEquals("cba!", result),
                    () -> assertEquals("abc", StringUtil.pipe().apply("abc"), "빈 파이프라인은 항등이다"),
                    () -> assertEquals("cba", StringUtil.pipe().then(StringUtil::reverse).then(null).apply("abc"),
                            "null 함수는 건너뛴다"));
        }
    }
}
