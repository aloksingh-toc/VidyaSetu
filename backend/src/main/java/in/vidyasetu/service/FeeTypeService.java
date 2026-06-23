package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.FeeTypeRequest;
import in.vidyasetu.dto.response.FeeTypeResponse;
import in.vidyasetu.entity.FeeType;
import in.vidyasetu.entity.School;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.FeeTypeRepository;
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
public class FeeTypeService {

    private final FeeTypeRepository feeTypeRepository;
    private final SchoolRepository  schoolRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public FeeTypeResponse create(FeeTypeRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        if (feeTypeRepository.existsBySchool_IdAndNameIgnoreCase(schoolId, req.getName())) {
            throw new BusinessRuleException("DUPLICATE_FEE_TYPE",
                    "A fee type named '" + req.getName() + "' already exists.");
        }

        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        FeeType feeType = FeeType.builder()
                .school(school)
                .name(req.getName().trim())
                .description(req.getDescription())
                .build();

        feeType = feeTypeRepository.save(feeType);
        log.info("FeeType created: '{}' for school {}", feeType.getName(), schoolId);
        return toResponse(feeType);
    }

    // ── List ──────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FeeTypeResponse> list(boolean activeOnly) {
        UUID schoolId = TenantContext.getSchoolId();
        List<FeeType> types = activeOnly
                ? feeTypeRepository.findBySchool_IdAndIsActiveTrueOrderByNameAsc(schoolId)
                : feeTypeRepository.findBySchool_IdOrderByNameAsc(schoolId);
        return types.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public FeeTypeResponse update(UUID id, FeeTypeRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        FeeType feeType = findOwned(id, schoolId);

        if (!feeType.getName().equalsIgnoreCase(req.getName()) &&
                feeTypeRepository.existsBySchool_IdAndNameIgnoreCase(schoolId, req.getName())) {
            throw new BusinessRuleException("DUPLICATE_FEE_TYPE",
                    "A fee type named '" + req.getName() + "' already exists.");
        }

        feeType.setName(req.getName().trim());
        feeType.setDescription(req.getDescription());
        feeType = feeTypeRepository.save(feeType);
        log.info("FeeType updated: '{}' (id={})", feeType.getName(), id);
        return toResponse(feeType);
    }

    // ── Deactivate (soft delete) ──────────────────────────────────────────────

    @Transactional
    public void deactivate(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        FeeType feeType = findOwned(id, schoolId);
        feeType.setIsActive(false);
        feeTypeRepository.save(feeType);
        log.info("FeeType deactivated: '{}' (id={})", feeType.getName(), id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private FeeType findOwned(UUID id, UUID schoolId) {
        FeeType ft = feeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", id));
        if (!ft.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("FeeType", id);
        return ft;
    }

    FeeTypeResponse toResponse(FeeType ft) {
        return FeeTypeResponse.builder()
                .id(ft.getId())
                .name(ft.getName())
                .description(ft.getDescription())
                .isActive(ft.getIsActive())
                .createdAt(ft.getCreatedAt())
                .build();
    }
}
