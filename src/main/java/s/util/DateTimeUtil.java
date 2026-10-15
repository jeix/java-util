package s.util;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DateTimeUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE2_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmmss");
    private static final DateTimeFormatter TIME2_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter DATETIME2_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // today() : alias of date()
    public String today() {
        return date();
    }

    // date() : yyyyMMdd
    public String date() {
        return LocalDate.now().format(DATE_FMT);
    }

    // date2() : yyyy-MM-dd
    public String date2() {
        return LocalDate.now().format(DATE2_FMT);
    }

    // time() : HHmmss
    public String time() {
        return LocalTime.now().format(TIME_FMT);
    }

    // time2() : HH:mm:ss
    public String time2() {
        return LocalTime.now().format(TIME2_FMT);
    }

    // datetime() : yyyyMMddHHmmss
    public String datetime() {
        return LocalDateTime.now().format(DATETIME_FMT);
    }

    // datetime2() : yyyy-MM-dd HH:mm:ss
    public String datetime2() {
        return LocalDateTime.now().format(DATETIME2_FMT);
    }

    // stringify(LocalDate) : yyyyMMdd
    public String stringify(LocalDate date) {
        return date.format(DATE_FMT);
    }

    // stringify2(LocalDate) : yyyy-MM-dd
    public String stringify2(LocalDate date) {
        return date.format(DATE2_FMT);
    }

    // stringify(LocalTime) : HHmmss
    public String stringify(LocalTime time) {
        return time.format(TIME_FMT);
    }

    // stringify2(LocalTime) : HH:mm:ss
    public String stringify2(LocalTime time) {
        return time.format(TIME2_FMT);
    }

    // stringify(LocalDateTime) : yyyyMMddHHmmss
    public String stringify(LocalDateTime datetime) {
        return datetime.format(DATETIME_FMT);
    }

    // stringify2(LocalDateTime) : yyyy-MM-dd HH:mm:ss
    public String stringify2(LocalDateTime datetime) {
        return datetime.format(DATETIME2_FMT);
    }

    // localDateOf(date) : yyyyMMdd | yyyy-MM-dd
    public LocalDate localDateOf(String date) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("date string must not be null or empty");
        }
        return date.contains("-")
                ? LocalDate.parse(date, DATE2_FMT)
                : LocalDate.parse(date, DATE_FMT);
    }

    // localTimeOf(time) : HHmmss | HH:mm:ss
    public LocalTime localTimeOf(String time) {
        if (time == null || time.isEmpty()) {
            throw new IllegalArgumentException("time string must not be null or empty");
        }
        return time.contains(":")
                ? LocalTime.parse(time, TIME2_FMT)
                : LocalTime.parse(time, TIME_FMT);
    }

    // localDateTimeOf(datetime) : yyyyMMddHHmmss | yyyy-MM-dd HH:mm:ss
    public LocalDateTime localDateTimeOf(String datetime) {
        if (datetime == null || datetime.isEmpty()) {
            throw new IllegalArgumentException("datetime string must not be null or empty");
        }
        return datetime.contains("-")
                ? LocalDateTime.parse(datetime, DATETIME2_FMT)
                : LocalDateTime.parse(datetime, DATETIME_FMT);
    }

    // toTimestamp(LocalDate)
    public Timestamp toTimestamp(LocalDate date) {
        return Timestamp.valueOf(date.atStartOfDay());
    }

    // toTimestamp(LocalDateTime)
    public Timestamp toTimestamp(LocalDateTime datetime) {
        return Timestamp.valueOf(datetime);
    }

    // eval(date1, op, date2) : boolean
    //   op : "=" | "!=" | ">=" | ">" | "<=" | "<"
    public boolean eval(String date1, String op, String date2) {
        LocalDate d1 = localDateOf(date1);
        LocalDate d2 = localDateOf(date2);
        return switch (op) {
            case "=" -> d1.isEqual(d2);
            case "!=" -> !d1.isEqual(d2);
            case ">=" -> !d1.isBefore(d2);
            case ">" -> d1.isAfter(d2);
            case "<=" -> !d1.isAfter(d2);
            case "<" -> d1.isBefore(d2);
            default -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    // eval(date, op, n, unit) : String
    //   op : "+" | "-"
    //   unit : "D" | "W" | "M" | "Y"
    public String eval(String date, String op, int n, String unit) {
        LocalDate d = localDateOf(date);
        ChronoUnit chronoUnit = switch (unit) {
            case "D" -> ChronoUnit.DAYS;
            case "W" -> ChronoUnit.WEEKS;
            case "M" -> ChronoUnit.MONTHS;
            case "Y" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Unsupported unit: " + unit);
        };
        LocalDate result;
        if ("+".equals(op)) {
            result = d.plus(n, chronoUnit);
        } else if ("-".equals(op)) {
            result = d.minus(n, chronoUnit);
        } else {
            throw new IllegalArgumentException("Unsupported operator: " + op);
        }
        return result.format(DATE_FMT);
    }

    // eom(date) : String // end of month
    public String eom(String date) {
        LocalDate d = localDateOf(date);
        return d.with(TemporalAdjusters.lastDayOfMonth()).format(DATE_FMT);
    }
}
