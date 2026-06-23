package in.vidyasetu.util;

import in.vidyasetu.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility for calculating working days for a school,
 * excluding weekends (Sat/Sun) and holidays stored in the DB.
 */
@Component
@RequiredArgsConstructor
public class WorkingDayCalculator {

    private final HolidayRepository holidayRepository;

    /**
     * Count working days in [from, to] inclusive for a school.
     */
    public int countWorkingDays(UUID schoolId, LocalDate from, LocalDate to) {
        Set<LocalDate> holidays = getHolidayDates(schoolId, from, to);
        int count = 0;
        LocalDate date = from;
        while (!date.isAfter(to)) {
            if (!isWeekend(date) && !holidays.contains(date)) {
                count++;
            }
            date = date.plusDays(1);
        }
        return count;
    }

    /**
     * Returns true if the given date is a working day for the school.
     */
    public boolean isWorkingDay(UUID schoolId, LocalDate date) {
        if (isWeekend(date)) return false;
        return !holidayRepository.existsBySchool_IdAndDate(schoolId, date);
    }

    /**
     * Returns the next working day on or after the given date.
     * Safety cap: stops after 30 days to avoid infinite loop.
     */
    public LocalDate nextWorkingDay(UUID schoolId, LocalDate from) {
        LocalDate date = from;
        int safety = 0;
        while (!isWorkingDay(schoolId, date) && safety < 30) {
            date = date.plusDays(1);
            safety++;
        }
        return date;
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    private Set<LocalDate> getHolidayDates(UUID schoolId, LocalDate from, LocalDate to) {
        return holidayRepository
                .findBySchool_IdAndDateBetweenOrderByDateAsc(schoolId, from, to)
                .stream()
                .map(h -> h.getDate())
                .collect(Collectors.toSet());
    }
}
