package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.HolidayRequest;
import in.vidyasetu.dto.response.HolidayResponse;
import in.vidyasetu.entity.AcademicYear;
import in.vidyasetu.entity.Holiday;
import in.vidyasetu.entity.School;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.AcademicYearRepository;
import in.vidyasetu.repository.HolidayRepository;
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
public class HolidayService {

    private final HolidayRepository      holidayRepository;
    private final SchoolRepository       schoolRepository;
    private final AcademicYearRepository academicYearRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public HolidayResponse create(HolidayRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        if (holidayRepository.existsBySchool_IdAndDate(schoolId, req.getDate())) {
            throw new BusinessRuleException("DUPLICATE_HOLIDAY",
                    "A holiday already exists on " + req.getDate() + ".");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        AcademicYear academicYear = academicYearRepository.findById(req.getAcademicYearId())
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", req.getAcademicYearId()));

        assertBelongsToSchool(academicYear.getSchool().getId(), schoolId);

        Holiday holiday = Holiday.builder()
                .school(school)
                .academicYear(academicYear)
                .date(req.getDate())
                .name(req.getName())
                .type(req.getType())
                .build();

        holiday = holidayRepository.save(holiday);
        log.info("Holiday created: {} ({}) for school {}", holiday.getName(), holiday.getDate(), schoolId);
        return toResponse(holiday);
    }

    // ── List by academic year ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<HolidayResponse> listByAcademicYear(UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        return holidayRepository
                .findBySchool_IdAndAcademicYear_IdOrderByDateAsc(schoolId, academicYearId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Get one ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public HolidayResponse getById(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday", id));

        assertBelongsToSchool(holiday.getSchool().getId(), schoolId);
        return toResponse(holiday);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public HolidayResponse update(UUID id, HolidayRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday", id));

        assertBelongsToSchool(holiday.getSchool().getId(), schoolId);

        // Check duplicate date only if date changed
        if (!holiday.getDate().equals(req.getDate()) &&
                holidayRepository.existsBySchool_IdAndDate(schoolId, req.getDate())) {
            throw new BusinessRuleException("DUPLICATE_HOLIDAY",
                    "A holiday already exists on " + req.getDate() + ".");
        }

        holiday.setDate(req.getDate());
        holiday.setName(req.getName());
        holiday.setType(req.getType());
        holiday = holidayRepository.save(holiday);
        return toResponse(holiday);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Holiday", id));

        assertBelongsToSchool(holiday.getSchool().getId(), schoolId);

        holidayRepository.delete(holiday);
        log.info("Holiday deleted: {} ({}) for school {}", holiday.getName(), holiday.getDate(), schoolId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void assertBelongsToSchool(UUID ownerSchoolId, UUID requestSchoolId) {
        if (!ownerSchoolId.equals(requestSchoolId)) {
            throw new ResourceNotFoundException("Holiday", requestSchoolId);
        }
    }

    private HolidayResponse toResponse(Holiday h) {
        return HolidayResponse.builder()
                .id(h.getId())
                .academicYearId(h.getAcademicYear() != null ? h.getAcademicYear().getId() : null)
                .date(h.getDate())
                .name(h.getName())
                .type(h.getType())
                .createdAt(h.getCreatedAt())
                .build();
    }
}
