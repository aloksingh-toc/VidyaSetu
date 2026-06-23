package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.AcademicYearRequest;
import in.vidyasetu.dto.response.AcademicYearResponse;
import in.vidyasetu.entity.AcademicYear;
import in.vidyasetu.entity.School;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.AcademicYearRepository;
import in.vidyasetu.repository.SchoolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;
    private final SchoolRepository       schoolRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public AcademicYearResponse create(AcademicYearRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        if (academicYearRepository.existsBySchool_IdAndName(schoolId, req.getName())) {
            throw new BusinessRuleException("DUPLICATE_ACADEMIC_YEAR",
                    "Academic year '" + req.getName() + "' already exists.");
        }

        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BusinessRuleException("INVALID_DATE_RANGE",
                    "End date must be after start date.");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        AcademicYear year = AcademicYear.builder()
                .school(school)
                .name(req.getName())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .isCurrent(Boolean.TRUE.equals(req.getMakeCurrent()))
                .build();

        year = academicYearRepository.save(year);

        // Unmark all other years as current for this school
        if (Boolean.TRUE.equals(req.getMakeCurrent())) {
            academicYearRepository.unmarkAllCurrentExcept(schoolId, year.getId());
        }

        log.info("Academic year created: {} for school {}", year.getName(), schoolId);
        return toResponse(year);
    }

    // ── List ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> list() {
        UUID schoolId = TenantContext.getSchoolId();
        return academicYearRepository.findBySchool_IdOrderByStartDateDesc(schoolId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get one ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AcademicYearResponse getById(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));

        assertBelongsToSchool(year.getSchool().getId(), schoolId);
        return toResponse(year);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public AcademicYearResponse update(UUID id, AcademicYearRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));

        assertBelongsToSchool(year.getSchool().getId(), schoolId);

        if (req.getEndDate().isBefore(req.getStartDate())) {
            throw new BusinessRuleException("INVALID_DATE_RANGE", "End date must be after start date.");
        }

        // Check name uniqueness only if changed
        if (!year.getName().equals(req.getName()) &&
                academicYearRepository.existsBySchool_IdAndName(schoolId, req.getName())) {
            throw new BusinessRuleException("DUPLICATE_ACADEMIC_YEAR",
                    "Academic year '" + req.getName() + "' already exists.");
        }

        year.setName(req.getName());
        year.setStartDate(req.getStartDate());
        year.setEndDate(req.getEndDate());

        if (Boolean.TRUE.equals(req.getMakeCurrent())) {
            year.setIsCurrent(true);
            year = academicYearRepository.save(year);
            academicYearRepository.unmarkAllCurrentExcept(schoolId, year.getId());
        } else {
            year = academicYearRepository.save(year);
        }

        return toResponse(year);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        AcademicYear year = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", id));

        assertBelongsToSchool(year.getSchool().getId(), schoolId);

        if (Boolean.TRUE.equals(year.getIsCurrent())) {
            throw new BusinessRuleException("CANNOT_DELETE_CURRENT",
                    "Cannot delete the current academic year. Make another year current first.");
        }

        academicYearRepository.delete(year);
        log.info("Academic year deleted: {} for school {}", year.getName(), schoolId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void assertBelongsToSchool(UUID ownerSchoolId, UUID requestSchoolId) {
        if (!ownerSchoolId.equals(requestSchoolId)) {
            throw new ResourceNotFoundException("AcademicYear", requestSchoolId);
        }
    }

    private AcademicYearResponse toResponse(AcademicYear y) {
        return AcademicYearResponse.builder()
                .id(y.getId())
                .name(y.getName())
                .startDate(y.getStartDate())
                .endDate(y.getEndDate())
                .isCurrent(y.getIsCurrent())
                .isArchived(y.getIsArchived())
                .createdAt(y.getCreatedAt())
                .build();
    }
}
