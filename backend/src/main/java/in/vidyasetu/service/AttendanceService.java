package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.AttendanceBulkRequest;
import in.vidyasetu.dto.response.AttendanceResponse;
import in.vidyasetu.dto.response.AttendanceSummaryResponse;
import in.vidyasetu.entity.*;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private final AttendanceRepository   attendanceRepository;
    private final StudentRepository      studentRepository;
    private final SchoolClassRepository  classRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository         userRepository;
    private final ParentRepository       parentRepository;
    private final NotificationService    notificationService;

    // ── Bulk mark attendance for a class ─────────────────────────────────────

    @Transactional
    public List<AttendanceResponse> bulkMark(AttendanceBulkRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        SchoolClass  schoolClass  = resolveClass(req.getClassId(), schoolId);
        AcademicYear academicYear = resolveAcademicYear(req.getAcademicYearId(), schoolId);
        User         markedBy     = currentUser();

        List<Attendance> saved = new ArrayList<>();

        for (AttendanceBulkRequest.AttendanceEntry entry : req.getEntries()) {
            Student student = findStudent(entry.getStudentId(), schoolId);

            // Upsert: update if already marked for this date, insert otherwise
            Attendance att = attendanceRepository
                    .findByStudent_IdAndDate(entry.getStudentId(), req.getDate())
                    .orElse(Attendance.builder()
                            .school(student.getSchool())
                            .student(student)
                            .schoolClass(schoolClass)
                            .academicYear(academicYear)
                            .date(req.getDate())
                            .build());

            att.setStatus(entry.getStatus());
            att.setMarkedBy(markedBy);
            saved.add(attendanceRepository.save(att));

            if ("ABSENT".equals(entry.getStatus())) {
                String studentName = student.getFirstName() +
                        (StringUtils.hasText(student.getLastName()) ? " " + student.getLastName() : "");
                parentRepository.findByStudent_IdAndIsPrimaryTrue(student.getId()).ifPresent(parent ->
                        notificationService.sendAbsentNotification(
                                schoolId, student.getId(), parent.getPhone(), studentName));
            }
        }

        log.info("Attendance marked: {} entries for class {} on {}",
                saved.size(), schoolClass.getName(), req.getDate());
        return saved.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get attendance for a class on a date ─────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByClassAndDate(UUID classId, LocalDate date) {
        UUID schoolId = TenantContext.getSchoolId();
        return attendanceRepository.findByClassAndDate(schoolId, classId, date)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get attendance records for a student ─────────────────────────────────

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getByStudent(UUID studentId, UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);

        return attendanceRepository.findByStudentAndYear(schoolId, studentId, academicYearId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Attendance summary (counts) for a student ─────────────────────────────

    @Transactional(readOnly = true)
    public AttendanceSummaryResponse summary(UUID studentId, UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        Student      student = findStudent(studentId, schoolId);
        AcademicYear ay      = resolveAcademicYear(academicYearId, schoolId);

        long present = attendanceRepository.countBySchool_IdAndStudent_IdAndAcademicYear_IdAndStatus(
                schoolId, studentId, academicYearId, "PRESENT");
        long absent  = attendanceRepository.countBySchool_IdAndStudent_IdAndAcademicYear_IdAndStatus(
                schoolId, studentId, academicYearId, "ABSENT");
        long late    = attendanceRepository.countBySchool_IdAndStudent_IdAndAcademicYear_IdAndStatus(
                schoolId, studentId, academicYearId, "LATE");
        long leave   = attendanceRepository.countBySchool_IdAndStudent_IdAndAcademicYear_IdAndStatus(
                schoolId, studentId, academicYearId, "LEAVE");

        long totalMarked = present + absent + late + leave;
        double percent   = totalMarked > 0
                ? Math.round((present * 1000.0 / totalMarked)) / 10.0
                : 0.0;

        String name = student.getFirstName() +
                (StringUtils.hasText(student.getLastName()) ? " " + student.getLastName() : "");

        return AttendanceSummaryResponse.builder()
                .studentId(student.getId())
                .studentName(name)
                .academicYearId(ay.getId())
                .academicYearName(ay.getName())
                .totalPresent(present)
                .totalAbsent(absent)
                .totalLate(late)
                .totalLeave(leave)
                .totalMarked(totalMarked)
                .attendancePercent(percent)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Student findStudent(UUID studentId, UUID schoolId) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        if (!s.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("Student", studentId);
        return s;
    }

    private SchoolClass resolveClass(UUID classId, UUID schoolId) {
        SchoolClass sc = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", classId));
        if (!sc.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("Class", classId);
        return sc;
    }

    private AcademicYear resolveAcademicYear(UUID id, UUID schoolId) {
        AcademicYear ay = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));
        if (!ay.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("AcademicYear", id);
        return ay;
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String s && StringUtils.hasText(s)) {
            return userRepository.findById(UUID.fromString(s)).orElse(null);
        }
        return null;
    }

    AttendanceResponse toResponse(Attendance a) {
        String studentName = a.getStudent().getFirstName() +
                (StringUtils.hasText(a.getStudent().getLastName())
                        ? " " + a.getStudent().getLastName() : "");

        return AttendanceResponse.builder()
                .id(a.getId())
                .studentId(a.getStudent().getId())
                .studentName(studentName)
                .classId(a.getSchoolClass() != null ? a.getSchoolClass().getId()   : null)
                .className(a.getSchoolClass() != null ? a.getSchoolClass().getName() : null)
                .academicYearId(a.getAcademicYear() != null ? a.getAcademicYear().getId() : null)
                .date(a.getDate())
                .status(a.getStatus())
                .markedByName(a.getMarkedBy() != null ? a.getMarkedBy().getName() : null)
                .createdAt(a.getCreatedAt())
                .build();
    }
}
