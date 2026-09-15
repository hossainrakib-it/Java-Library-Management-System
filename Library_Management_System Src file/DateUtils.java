import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Central date parsing and booking-overlap utility class.
 * Booking periods use an exclusive end date:
 * [startDate, dueDate). A copy due on 2026-07-20 may be borrowed again
 * starting on 2026-07-20.
 */
public final class DateUtils {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtils() {
    }

    public static LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim(), FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid date. Please use yyyy-MM-dd format.");
        }
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(FORMATTER);
    }

    public static boolean isValidBookingPeriod(LocalDate startDate, LocalDate dueDate) {
        return startDate != null && dueDate != null && dueDate.isAfter(startDate);
    }

    public static boolean rangesOverlap(
            LocalDate firstStart,
            LocalDate firstEnd,
            LocalDate secondStart,
            LocalDate secondEnd) {

        // End dates are exclusive.
        return firstStart.isBefore(secondEnd) && secondStart.isBefore(firstEnd);
    }

    public static long durationDays(LocalDate startDate, LocalDate dueDate) {
        return ChronoUnit.DAYS.between(startDate, dueDate);
    }
}
