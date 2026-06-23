package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.ParentRequest;
import in.vidyasetu.dto.request.StudentRequest;
import in.vidyasetu.dto.response.PageResponse;
import in.vidyasetu.dto.response.ParentResponse;
import in.vidyasetu.dto.response.StudentResponse;
import in.vidyasetu.entity.Parent;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.SchoolClass;
import in.vidyasetu.entity.Student;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.ParentRepository;
import in.vidyasetu.repository.SchoolClassRepository;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository    studentRepository;
    private final ParentRepository     parentRepository;
    private final SchoolRepository     schoolRepository;
    private final SchoolClassRepository classRepository;
    private final AuditLogService      auditLogService;
    private final AppNotificationService appNotificationService;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public StudentResponse create(StudentRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        SchoolClass schoolClass = resolveClass(req.getClassId(), schoolId);

        // Duplicate roll number check within same class
        if (StringUtils.hasText(req.getRollNumber()) &&
                studentRepository.existsBySchool_IdAndSchoolClass_IdAndRollNumber(
                        schoolId, req.getClassId(), req.getRollNumber())) {
            throw new BusinessRuleException("DUPLICATE_ROLL_NUMBER",
                    "Roll number '" + req.getRollNumber() + "' is already taken in this class.");
        }

        Student student = Student.builder()
                .school(school)
                .schoolClass(schoolClass)
                .rollNumber(req.getRollNumber())
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .admissionDate(req.getAdmissionDate() != null
                        ? req.getAdmissionDate() : java.time.LocalDate.now())
                .admissionNumber(req.getAdmissionNumber())
                .bloodGroup(req.getBloodGroup())
                .address(req.getAddress())
                .build();

        student = studentRepository.save(student);

        // Create parents if provided
        if (req.getParents() != null && !req.getParents().isEmpty()) {
            saveParents(req.getParents(), student, school);
        }

        log.info("Student created: {} {} (id={}) in class {} for school {}",
                student.getFirstName(), student.getLastName(),
                student.getId(), schoolClass.getName(), schoolId);

        auditLogService.log("STUDENT_CREATED", "Student", student.getId(), null,
                java.util.Map.of("firstName", student.getFirstName(), "classId", schoolClass.getId()));
        appNotificationService.notifyOwnersAndAdmins(schoolId, "STUDENT_ADDED",
                "New student admitted",
                student.getFirstName() + " " + (student.getLastName() != null ? student.getLastName() : "")
                        + " was added to " + schoolClass.getName(),
                "/students/" + student.getId());

        return toFullResponse(student);
    }

    // ── List (paginated, filterable) ──────────────────────────────────────────

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> list(int page, int size,
                                               UUID classId, String search,
                                               boolean activeOnly) {
        UUID schoolId = TenantContext.getSchoolId();

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("firstName").ascending().and(Sort.by("lastName").ascending()));

        String searchTrimmed = StringUtils.hasText(search) ? search.trim() : null;

        Page<Student> pageResult = studentRepository.findWithFilters(
                schoolId, classId, searchTrimmed, activeOnly, pageable);

        List<StudentResponse> content = pageResult.getContent()
                .stream()
                .map(this::toSummaryResponse)   // no parents in list
                .collect(Collectors.toList());

        return PageResponse.<StudentResponse>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .build();
    }

    // ── Get one (with parents) ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public StudentResponse getById(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        Student student = findStudent(id, schoolId);
        return toFullResponse(student);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public StudentResponse update(UUID id, StudentRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        Student student = findStudent(id, schoolId);

        SchoolClass schoolClass = resolveClass(req.getClassId(), schoolId);

        // Roll number duplicate check (skip if unchanged)
        boolean rollChanged  = !java.util.Objects.equals(student.getRollNumber(), req.getRollNumber());
        boolean classChanged = !java.util.Objects.equals(
                student.getSchoolClass() != null ? student.getSchoolClass().getId() : null,
                req.getClassId());

        if (StringUtils.hasText(req.getRollNumber()) && (rollChanged || classChanged) &&
                studentRepository.existsBySchool_IdAndSchoolClass_IdAndRollNumber(
                        schoolId, req.getClassId(), req.getRollNumber())) {
            throw new BusinessRuleException("DUPLICATE_ROLL_NUMBER",
                    "Roll number '" + req.getRollNumber() + "' is already taken in this class.");
        }

        student.setSchoolClass(schoolClass);
        student.setRollNumber(req.getRollNumber());
        student.setFirstName(req.getFirstName());
        student.setLastName(req.getLastName());
        student.setDateOfBirth(req.getDateOfBirth());
        student.setGender(req.getGender());
        if (req.getAdmissionDate() != null) student.setAdmissionDate(req.getAdmissionDate());
        student.setAdmissionNumber(req.getAdmissionNumber());
        student.setBloodGroup(req.getBloodGroup());
        student.setAddress(req.getAddress());

        student = studentRepository.save(student);
        log.info("Student updated: {} {}", student.getFirstName(), student.getLastName());
        auditLogService.log("STUDENT_UPDATED", "Student", student.getId(), null,
                java.util.Map.of("firstName", student.getFirstName(), "lastName",
                        student.getLastName() != null ? student.getLastName() : ""));
        return toFullResponse(student);
    }

    // ── Deactivate (soft delete) ──────────────────────────────────────────────

    @Transactional
    public void deactivate(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        Student student = findStudent(id, schoolId);

        student.setIsActive(false);
        studentRepository.save(student);
        log.info("Student deactivated: {} {} (id={})",
                student.getFirstName(), student.getLastName(), id);
        auditLogService.log("STUDENT_DEACTIVATED", "Student", student.getId(), null, null);
    }

    // ── Reactivate ────────────────────────────────────────────────────────────

    @Transactional
    public StudentResponse reactivate(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        assertBelongsToSchool(student.getSchool().getId(), schoolId);

        student.setIsActive(true);
        student = studentRepository.save(student);
        log.info("Student reactivated: {} {}", student.getFirstName(), student.getLastName());
        return toFullResponse(student);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Student findStudent(UUID id, UUID schoolId) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        assertBelongsToSchool(student.getSchool().getId(), schoolId);
        return student;
    }

    private SchoolClass resolveClass(UUID classId, UUID schoolId) {
        SchoolClass sc = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", classId));
        if (!sc.getSchool().getId().equals(schoolId)) {
            throw new ResourceNotFoundException("Class", classId);
        }
        return sc;
    }

    private void saveParents(List<ParentRequest> reqs, Student student, School school) {
        boolean hasPrimary = reqs.stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsPrimary()));

        for (int i = 0; i < reqs.size(); i++) {
            ParentRequest pr = reqs.get(i);
            boolean primary = Boolean.TRUE.equals(pr.getIsPrimary())
                    || (!hasPrimary && i == 0);   // auto-mark first if none flagged

            Parent parent = Parent.builder()
                    .school(school)
                    .student(student)
                    .name(pr.getName())
                    .relation(pr.getRelation())
                    .phone(pr.getPhone())
                    .whatsappNumber(StringUtils.hasText(pr.getWhatsappNumber())
                            ? pr.getWhatsappNumber() : pr.getPhone())
                    .isPrimary(primary)
                    .build();
            parentRepository.save(parent);
        }
    }

    private void assertBelongsToSchool(UUID ownerSchoolId, UUID requestSchoolId) {
        if (!ownerSchoolId.equals(requestSchoolId)) {
            throw new ResourceNotFoundException("Student", requestSchoolId);
        }
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    /** Summary — used in list, no parents */
    StudentResponse toSummaryResponse(Student s) {
        return baseBuilder(s).build();
    }

    /** Full response — used in single GET, includes parents */
    StudentResponse toFullResponse(Student s) {
        List<ParentResponse> parents = parentRepository
                .findByStudent_IdOrderByIsPrimaryDescCreatedAtAsc(s.getId())
                .stream().map(this::toParentResponse).collect(Collectors.toList());

        return baseBuilder(s).parents(parents).build();
    }

    private StudentResponse.StudentResponseBuilder baseBuilder(Student s) {
        String fullName = s.getFirstName() +
                (StringUtils.hasText(s.getLastName()) ? " " + s.getLastName() : "");

        return StudentResponse.builder()
                .id(s.getId())
                .classId(s.getSchoolClass() != null ? s.getSchoolClass().getId()      : null)
                .className(s.getSchoolClass() != null ? s.getSchoolClass().getName()    : null)
                .section(s.getSchoolClass() != null ? s.getSchoolClass().getSection()  : null)
                .rollNumber(s.getRollNumber())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .fullName(fullName)
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .photoUrl(s.getPhotoUrl())
                .admissionDate(s.getAdmissionDate())
                .admissionNumber(s.getAdmissionNumber())
                .bloodGroup(s.getBloodGroup())
                .address(s.getAddress())
                .isActive(s.getIsActive())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt());
    }

    ParentResponse toParentResponse(Parent p) {
        return ParentResponse.builder()
                .id(p.getId())
                .studentId(p.getStudent().getId())
                .name(p.getName())
                .relation(p.getRelation())
                .phone(p.getPhone())
                .whatsappNumber(p.getWhatsappNumber())
                .isPrimary(p.getIsPrimary())
                .whatsappOptOut(p.getWhatsappOptOut())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
