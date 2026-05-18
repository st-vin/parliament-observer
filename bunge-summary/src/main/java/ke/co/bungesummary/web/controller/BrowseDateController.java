package ke.co.bungesummary.web.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import ke.co.bungesummary.api.dto.ProceedingSummaryResponse;
import ke.co.bungesummary.api.dto.SittingDetailResponse;
import ke.co.bungesummary.api.dto.SittingSummaryResponse;
import ke.co.bungesummary.api.service.ProceedingService;
import ke.co.bungesummary.api.service.SittingService;
import ke.co.bungesummary.domain.repository.SittingRepository;
import ke.co.bungesummary.web.CalendarBuilder;
import ke.co.bungesummary.web.CalendarBuilder.CalendarCell;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Profile("api")
public class BrowseDateController {

    private static final DateTimeFormatter MONTH_LABEL =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    private final SittingService sittingService;
    private final ProceedingService proceedingService;
    private final SittingRepository sittingRepository;

    public BrowseDateController(
            SittingService sittingService,
            ProceedingService proceedingService,
            SittingRepository sittingRepository) {
        this.sittingService = sittingService;
        this.proceedingService = proceedingService;
        this.sittingRepository = sittingRepository;
    }

    @GetMapping("/browse/date")
    public String browseDate(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                    LocalDate date,
            Model model) {
        LocalDate selected = date;
        YearMonth current;
        if (year != null && month != null) {
            current = YearMonth.of(year, month);
        } else if (selected != null) {
            current = YearMonth.from(selected);
        } else {
            current = YearMonth.now();
        }

        List<SittingSummaryResponse> monthSittings =
                sittingService.list(current.getYear(), current.getMonthValue(), 0, 100).content();
        Set<LocalDate> sittingDates =
                monthSittings.stream().map(SittingSummaryResponse::date).collect(Collectors.toSet());

        List<CalendarCell> cells = CalendarBuilder.buildMonth(current, sittingDates, selected);

        model.addAttribute("title", "Browse by Date");
        model.addAttribute("year", current.getYear());
        model.addAttribute("month", current.getMonthValue());
        model.addAttribute("monthLabel", current.format(MONTH_LABEL));
        model.addAttribute("prevYear", current.minusMonths(1).getYear());
        model.addAttribute("prevMonth", current.minusMonths(1).getMonthValue());
        model.addAttribute("nextYear", current.plusMonths(1).getYear());
        model.addAttribute("nextMonth", current.plusMonths(1).getMonthValue());
        model.addAttribute("calendarCells", cells);
        model.addAttribute("selectedDate", selected);

        if (selected != null) {
            Optional<SittingSummaryResponse> sittingOnDate =
                    monthSittings.stream().filter(s -> s.date().equals(selected)).findFirst();
            if (sittingOnDate.isEmpty()) {
                sittingOnDate =
                        sittingRepository
                                .findByDate(selected)
                                .map(
                                        s ->
                                                new SittingSummaryResponse(
                                                        s.getId(),
                                                        s.getDate(),
                                                        s.getChamber(),
                                                        0,
                                                        s.getIngestionStatus()));
            }
            if (sittingOnDate.isPresent()) {
                SittingDetailResponse detail = sittingService.getById(sittingOnDate.get().id());
                model.addAttribute("selectedSitting", detail);
                boolean processing =
                        !"COMPLETED".equalsIgnoreCase(detail.ingestionStatus());
                model.addAttribute("processing", processing);
                List<ProceedingSummaryResponse> proceedings =
                        proceedingService
                                .list(
                                        detail.id(),
                                        null,
                                        selected,
                                        selected,
                                        null,
                                        0,
                                        50)
                                .content();
                model.addAttribute("dayProceedings", proceedings);
            } else {
                model.addAttribute("noSitting", true);
            }
        }

        return "browse-date";
    }
}
