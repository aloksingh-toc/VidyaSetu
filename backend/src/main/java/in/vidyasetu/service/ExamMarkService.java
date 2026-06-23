package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.ExamMarkBulkRequest;
import in.vidyasetu.dto.response.ExamMarkResponse;
import in.vidyasetu.entity.*;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamMarkService {

    private final ExamMarkRepository    markRepository;
    private final ExamSubjectRepository subjectRepository;
    private final StudentRepository     studentRepository;
    private final SchoolRepository      schoolRepository;
    private final UserRepository        userRepository;

    @Transactional
    public List<ExamMarkResponse> bulkSave(UUID examSubjectId, ExamMarkBulkRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        ExamSubject subject = findSubject(examSubjectId, schoolId);

        UUID userId  = UUID.fromString(
                (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        User enteredBy = userRepository.getReferenceById(userId);
        School school  = schoolRepository.getReferenceById(schoolId);

        for (ExamMarkBulkRequest.MarkEntry entry : req.getEntries()) {
            Student student = findStudent(entry.getStudentId(), schoolId);

            ExamMark mark = markRepository
                    .findByStudent_IdAndExamSubject_Id(entry.getStudentId(), examSubjectId)
                    .orElseGet(() -> ExamMark.builder()
                            .school(school)
                            .student(student)
                            .examSubject(subject)
                            .build());

            boolean absent = Boolean.TRUE.equals(entry.getIsAbsent());
            mark.setIsAbsent(absent);
            mark.setMarksObtained(absent ? null : entry.getMarksObtained());
            mark.setRemarks(entry.getRemarks());
            mark.setEnteredBy(enteredBy);
            mark.setUpdatedAt(LocalDateTime.now());
            markRepository.save(mark);
        }

        return markRepository.findByExamSubjectId(examSubjectId)
                .stream()
                .map(m -> toResponse(m, subject.getMaxMarks()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExamMarkResponse> getBySubject(UUID examSubjectId) {
        UUID schoolId = TenantContext.getSchoolId();
        ExamSubject subject = findSubject(examSubjectId, schoolId);

        return markRepository.findByExamSubjectId(examSubjectId)
                .stream()
                .map(m -> toResponse(m, subject.getMaxMarks()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExamMarkResponse> getByStudent(UUID studentId, UUID examId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);

        return markRepository.findByStudentAndExam(studentId, examId)
                .stream()
                .map(m -> toResponse(m, m.getExamSubject().getMaxMarks()))
                .toList();
    }

    /* ── Tenant-safe lookups ─────────────────────────────────────────────── */

    private ExamSubject findSubject(UUID subjectId, UUID schoolId) {
        ExamSubject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamSubject", subjectId));
        if (!subject.getExam().getSchool().getId().equals(schoolId)) {
            throw new ResourceNotFoundException("ExamSubject", subjectId);
        }
        return subject;
    }

    private Student findStudent(UUID studentId, UUID schoolId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        if (!student.getSchool().getId().equals(schoolId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        return student;
    }

    /* ── Mapper ───────────────────────────────────────────────────────────── */

    private ExamMarkResponse toResponse(ExamMark m, BigDecimal maxMarks) {
        String studentName = m.getStudent().getFirstName() + " " + m.getStudent().getLastName();
        return ExamMarkResponse.builder()
                .id(m.getId())
                .studentId(m.getStudent().getId())
                .studentName(studentName.trim())
                .rollNumber(m.getStudent().getRollNumber())
                .examSubjectId(m.getExamSubject().getId())
                .subject(m.getExamSubject().getSubject())
                .marksObtained(m.getMarksObtained())
                .maxMarks(maxMarks)
                .isAbsent(m.getIsAbsent())
                .remarks(m.getRemarks())
                .grade(computeGrade(m.getMarksObtained(), maxMarks, m.getIsAbsent()))
                .enteredByName(m.getEnteredBy() != null ? m.getEnteredBy().getName() : null)
                .updatedAt(m.getUpdatedAt())
                .build();
    }

    private String computeGrade(BigDecimal marks, BigDecimal maxMarks, Boolean isAbsent) {
        if (Boolean.TRUE.equals(isAbsent) || marks == null || maxMarks == null
                || maxMarks.compareTo(BigDecimal.ZERO) == 0) return "AB";
        BigDecimal pct = marks.multiply(BigDecimal.valueOf(100))
                              .divide(maxMarks, 1, RoundingMode.HALF_UP);
        double p = pct.doubleValue();
        if (p >= 90) return "A+";
        if (p >= 75) return "A";
        if (p >= 60) return "B";
        if (p >= 45) return "C";
        if (p >= 33) return "D";
        return "F";
    }
}
