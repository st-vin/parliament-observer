package ke.co.bungesummary.web;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class CalendarBuilder {

    private CalendarBuilder() {}

    public static List<CalendarCell> buildMonth(
            YearMonth month, Set<LocalDate> sittingDates, LocalDate selected) {
        LocalDate today = LocalDate.now();
        LocalDate first = month.atDay(1);
        int offset = (first.getDayOfWeek().getValue() + 6) % 7;

        List<CalendarCell> cells = new ArrayList<>();
        YearMonth prev = month.minusMonths(1);
        int prevDays = prev.lengthOfMonth();
        for (int i = 0; i < offset; i++) {
            int day = prevDays - offset + i + 1;
            LocalDate date = prev.atDay(day);
            cells.add(cell(day, date, false, sittingDates, selected, today));
        }
        for (int d = 1; d <= month.lengthOfMonth(); d++) {
            LocalDate date = month.atDay(d);
            cells.add(cell(d, date, true, sittingDates, selected, today));
        }
        int trailing = (7 - (cells.size() % 7)) % 7;
        YearMonth next = month.plusMonths(1);
        for (int d = 1; d <= trailing; d++) {
            LocalDate date = next.atDay(d);
            cells.add(cell(d, date, false, sittingDates, selected, today));
        }
        return cells;
    }

    private static CalendarCell cell(
            int dayOfMonth,
            LocalDate date,
            boolean inCurrentMonth,
            Set<LocalDate> sittingDates,
            LocalDate selected,
            LocalDate today) {
        return new CalendarCell(
                dayOfMonth,
                date,
                inCurrentMonth,
                sittingDates.contains(date),
                selected != null && date.equals(selected),
                date.equals(today));
    }

    public record CalendarCell(
            int dayOfMonth,
            LocalDate date,
            boolean inCurrentMonth,
            boolean hasSitting,
            boolean selected,
            boolean today) {}
}
