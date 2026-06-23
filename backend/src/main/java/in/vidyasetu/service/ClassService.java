package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.ClassRequest;
import in.vidyasetu.dto.response.ClassResponse;
import in.vidyasetu.entity.AcademicYear;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.SchoolClass;
import in.vidyasetu.entity.User;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.AcademicYearRepository;
import in.vidyasetu.repository.SchoolClassRepository;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassService {

    private final SchoolClassRepository  classRepository;
    private final SchoolRepository       schoolRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository         userRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public ClassResponse create(ClassRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        if (classRepository.existsBySchool_IdAndAcademicYear_IdAndNameAndSection(
                schoolId, req.getAcademicYearId(), req.getName(), req.getSection())) {
            throw new BusinessRuleException("DUPLICATE_CLASS",
                    "A class '" + req.getName() +
                    (req.getSection() != null ? " " + req.getSection() : "") +
                    "' already exists for this academic year.");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        AcademicYear academicYear = academicYearRepository.findById(req.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", req.getAcademicYearId()));

        assertBelongsToSchool(academicYear.getSchool().getId(), schoolId, "AcademicYear");

        User classTeacher = null;
        if (req.getClassTeacherId() != null) {
            classTeacher = userRepository.findById(req.getClassTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", req.getClassTeacherId()));
        }

        SchoolClass sc = SchoolClass.builder()
                .school(school)
                .academicYear(academicYear)
                .name(req.getName())
                .section(req.getSection())
                .classTeacher(classTeacher)
                .displayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0)
                .build();

        sc = classRepository.save(sc);
        log.info("Class created: {}{} for school {}",
                sc.getName(), sc.getSection() != null ? " " + sc.getSection() : "", schoolId);
        return toResponse(sc);
    }

    // ── List by academic year ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ClassResponse> listByAcademicYear(UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        return classRepository
                .findBySchool_IdAndAcademicYear_IdOrderByDisplayOrderAsc(schoolId, academicYearId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get one ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ClassResponse getById(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        SchoolClass sc = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", id));

        assertBelongsToSchool(sc.getSchool().getId(), schoolId, "Class");
        return toResponse(sc);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public ClassResponse update(UUID id, ClassRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        SchoolClass sc = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", id));

        assertBelongsToSchool(sc.getSchool().getId(), schoolId, "Class");

        // Check duplicate only if name/section changed
        boolean nameChanged    = !sc.getName().equals(req.getName());
        boolean sectionChanged = !Objects.equals(sc.getSection(), req.getSection());
        if ((nameChanged || sectionChanged) &&
                classRepository.existsBySchool_IdAndAcademicYear_IdAndNameAndSection(
                        schoolId, req.getAcademicYearId(), req.getName(), req.getSection())) {
            throw new BusinessRuleException("DUPLICATE_CLASS",
                    "A class with this name and section already exists.");
        }

        User classTeacher = null;
        if (req.getClassTeacherId() != null) {
            classTeacher = userRepository.findById(req.getClassTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", req.getClassTeacherId()));
        }

        sc.setName(req.getName());
        sc.setSection(req.getSection());
        sc.setClassTeacher(classTeacher);
        if (req.getDisplayOrder() != null) sc.setDisplayOrder(req.getDisplayOrder());

        sc = classRepository.save(sc);
        return toResponse(sc);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        SchoolClass sc = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", id));

        assertBelongsToSchool(sc.getSchool().getId(), schoolId, "Class");

        classRepository.delete(sc);
        log.info("Class deleted: {} {} for school {}", sc.getName(), sc.getSection(), schoolId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void assertBelongsToSchool(UUID ownerSchoolId, UUID requestSchoolId, String resource) {
        if (!ownerSchoolId.equals(requestSchoolId)) {
            throw new ResourceNotFoundException(resource, requestSchoolId);
        }
    }

    private ClassResponse toResponse(SchoolClass sc) {
        return ClassResponse.builder()
                .id(sc.getId())
                .academicYearId(sc.getAcademicYear() != null ? sc.getAcademicYear().getId()   : null)
                .academicYearName(sc.getAcademicYear() != null ? sc.getAcademicYear().getName() : null)
                .name(sc.getName())
                .section(sc.getSection())
                .classTeacherId(sc.getClassTeacher()   != null ? sc.getClassTeacher().getId()   : null)
                .classTeacherName(sc.getClassTeacher() != null ? sc.getClassTeacher().getName() : null)
                .displayOrder(sc.getDisplayOrder())
                .createdAt(sc.getCreatedAt())
                .build();
    }
}
