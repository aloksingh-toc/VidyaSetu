package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.ExamRequest;
import in.vidyasetu.dto.request.ExamSubjectRequest;
import in.vidyasetu.dto.response.ExamResponse;
import in.vidyasetu.dto.response.ExamSubjectResponse;
import in.vidyasetu.entity.*;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final ExamRepository         examRepository;
    private final ExamSubjectRepository  examSubjectRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SchoolClassRepository  classRepository;
    private final SchoolRepository       schoolRepository;

    /* ── Exams ─────────────────────────────────────────────────────────── */

    @Transactional
    public ExamResponse createExam(ExamRequest req) {
        UUID schoolId    = TenantContext.getSchoolId();
        School school    = schoolRepository.getReferenceById(schoolId);
        AcademicYear ay  = resolveAcademicYear(req.getAcademicYearId(), schoolId);

        Exam exam = Exam.builder()
                .school(school)
                .academicYear(ay)
                .name(req.getName().trim())
                .examType(req.getExamType())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .build();

        return toResponse(examRepository.save(exam), List.of());
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> listExams(UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        return examRepository
                .findBySchool_IdAndAcademicYear_IdOrderByStartDateAsc(schoolId, academicYearId)
                .stream()
                .map(e -> toResponse(e, List.of()))
                .toList();
    }

    @Transactional(readOnly = true)
    public ExamResponse getExam(UUID examId) {
        UUID schoolId = TenantContext.getSchoolId();
        Exam exam = findExam(examId, schoolId);

        List<ExamSubjectResponse> subjects = examSubjectRepository
                .findByExamId(examId)
                .stream()
                .map(this::toSubjectResponse)
                .toList();

        return toResponse(exam, subjects);
    }

    @Transactional
    public ExamResponse updateExam(UUID examId, ExamRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        Exam exam = findExam(examId, schoolId);

        exam.setName(req.getName().trim());
        exam.setExamType(req.getExamType());
        exam.setStartDate(req.getStartDate());
        exam.setEndDate(req.getEndDate());

        return toResponse(examRepository.save(exam), List.of());
    }

    @Transactional
    public void publishResults(UUID examId) {
        UUID schoolId = TenantContext.getSchoolId();
        Exam exam = findExam(examId, schoolId);

        exam.setResultPublished(true);
        exam.setPublishedAt(LocalDateTime.now());
        examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(UUID examId) {
        UUID schoolId = TenantContext.getSchoolId();
        Exam exam = findExam(examId, schoolId);
        examRepository.delete(exam);
    }

    /* ── Subjects ───────────────────────────────────────────────────────── */

    @Transactional
    public ExamSubjectResponse addSubject(UUID examId, ExamSubjectRequest req) {
        UUID schoolId   = TenantContext.getSchoolId();
        Exam exam       = findExam(examId, schoolId);
        SchoolClass cls = resolveClass(req.getClassId(), schoolId);

        ExamSubject subject = ExamSubject.builder()
                .exam(exam)
                .schoolClass(cls)
                .subject(req.getSubject().trim())
                .maxMarks(req.getMaxMarks())
                .passingMarks(req.getPassingMarks())
                .examDate(req.getExamDate())
                .build();

        return toSubjectResponse(examSubjectRepository.save(subject));
    }

    @Transactional
    public ExamSubjectResponse updateSubject(UUID subjectId, ExamSubjectRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        ExamSubject subject = findSubject(subjectId, schoolId);

        // Allow moving the subject to a different class within the same school
        if (req.getClassId() != null && !req.getClassId().equals(subject.getSchoolClass().getId())) {
            subject.setSchoolClass(resolveClass(req.getClassId(), schoolId));
        }
        subject.setSubject(req.getSubject().trim());
        subject.setMaxMarks(req.getMaxMarks());
        subject.setPassingMarks(req.getPassingMarks());
        subject.setExamDate(req.getExamDate());

        return toSubjectResponse(examSubjectRepository.save(subject));
    }

    @Transactional
    public void deleteSubject(UUID subjectId) {
        UUID schoolId = TenantContext.getSchoolId();
        ExamSubject subject = findSubject(subjectId, schoolId);
        examSubjectRepository.delete(subject);
    }

    /* ── Tenant-safe lookups ─────────────────────────────────────────────── */

    private Exam findExam(UUID examId, UUID schoolId) {
        return examRepository.findById(examId)
                .filter(e -> e.getSchool().getId().equals(schoolId))
                .orElseThrow(() -> new ResourceNotFoundException("Exam", examId));
    }

    private ExamSubject findSubject(UUID subjectId, UUID schoolId) {
        ExamSubject subject = examSubjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamSubject", subjectId));
        if (!subject.getExam().getSchool().getId().equals(schoolId)) {
            throw new ResourceNotFoundException("ExamSubject", subjectId);
        }
        return subject;
    }

    private AcademicYear resolveAcademicYear(UUID id, UUID schoolId) {
        AcademicYear ay = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));
        if (!ay.getSchool().getId().equals(schoolId)) {
            throw new ResourceNotFoundException("AcademicYear", id);
        }
        return ay;
    }

    private SchoolClass resolveClass(UUID id, UUID schoolId) {
        SchoolClass cls = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", id));
        if (!cls.getSchool().getId().equals(schoolId)) {
            throw new ResourceNotFoundException("Class", id);
        }
        return cls;
    }

    /* ── Mappers ─────────────────────────────────────────────────────────── */

    private ExamResponse toResponse(Exam e, List<ExamSubjectResponse> subjects) {
        return ExamResponse.builder()
                .id(e.getId())
                .academicYearId(e.getAcademicYear() != null ? e.getAcademicYear().getId() : null)
                .academicYearName(e.getAcademicYear() != null ? e.getAcademicYear().getName() : null)
                .name(e.getName())
                .examType(e.getExamType())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .resultPublished(e.getResultPublished())
                .publishedAt(e.getPublishedAt())
                .createdAt(e.getCreatedAt())
                .subjects(subjects)
                .build();
    }

    private ExamSubjectResponse toSubjectResponse(ExamSubject es) {
        return ExamSubjectResponse.builder()
                .id(es.getId())
                .examId(es.getExam().getId())
                .classId(es.getSchoolClass().getId())
                .className(es.getSchoolClass().getName())
                .classSection(es.getSchoolClass().getSection())
                .subject(es.getSubject())
                .maxMarks(es.getMaxMarks())
                .passingMarks(es.getPassingMarks())
                .examDate(es.getExamDate())
                .createdAt(es.getCreatedAt())
                .build();
    }
}
