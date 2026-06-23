package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.response.DashboardStatsResponse;
import in.vidyasetu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository     studentRepository;
    private final FeePaymentRepository  feePaymentRepository;
    private final AttendanceRepository  attendanceRepository;
    private final ExamRepository        examRepository;
    private final SchoolClassRepository classRepository;

    @Transactional(readOnly = true)
    public DashboardStatsResponse stats(UUID academicYearId) {
        UUID      schoolId = TenantContext.getSchoolId();
        LocalDate today    = LocalDate.now();

        long totalStudents = studentRepository.countBySchool_IdAndIsActiveTrue(schoolId);
        long totalClasses  = classRepository.countBySchool_Id(schoolId);

        BigDecimal feesThisMonth = feePaymentRepository.sumThisMonth(schoolId);
        BigDecimal feesThisYear  = academicYearId != null
                ? feePaymentRepository.totalCollectedForYear(schoolId, academicYearId)
                : BigDecimal.ZERO;

        long presentToday = attendanceRepository.countBySchool_IdAndDateAndStatus(schoolId, today, "PRESENT");
        long absentToday  = attendanceRepository.countBySchool_IdAndDateAndStatus(schoolId, today, "ABSENT");
        long markedToday  = attendanceRepository.countBySchool_IdAndDate(schoolId, today);

        double attendancePct = markedToday > 0
                ? Math.round(presentToday * 1000.0 / markedToday) / 10.0
                : 0.0;

        long upcomingExams = academicYearId != null
                ? examRepository.countBySchool_IdAndAcademicYear_IdAndStartDateGreaterThanEqual(schoolId, academicYearId, today)
                : 0L;

        return DashboardStatsResponse.builder()
                .totalStudents(totalStudents)
                .feesCollectedThisMonth(feesThisMonth)
                .feesCollectedThisYear(feesThisYear)
                .presentToday(presentToday)
                .absentToday(absentToday)
                .attendancePercent(attendancePct)
                .upcomingExams(upcomingExams)
                .totalClasses(totalClasses)
                .build();
    }
}
