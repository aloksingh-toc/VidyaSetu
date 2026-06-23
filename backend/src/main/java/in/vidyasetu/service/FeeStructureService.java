package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.FeeStructureRequest;
import in.vidyasetu.dto.response.FeeStructureResponse;
import in.vidyasetu.entity.*;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.*;
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
public class FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final SchoolRepository       schoolRepository;
    private final SchoolClassRepository  classRepository;
    private final FeeTypeRepository      feeTypeRepository;
    private final AcademicYearRepository academicYearRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public FeeStructureResponse create(FeeStructureRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        if (feeStructureRepository.existsBySchoolClass_IdAndFeeType_IdAndAcademicYear_Id(
                req.getClassId(), req.getFeeTypeId(), req.getAcademicYearId())) {
            throw new BusinessRuleException("DUPLICATE_FEE_STRUCTURE",
                    "A fee structure for this class, fee type, and academic year already exists.");
        }

        School       school      = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));
        SchoolClass  schoolClass = resolveClass(req.getClassId(), schoolId);
        FeeType      feeType     = resolveFeeType(req.getFeeTypeId(), schoolId);
        AcademicYear academicYear = resolveAcademicYear(req.getAcademicYearId(), schoolId);

        FeeStructure fs = FeeStructure.builder()
                .school(school)
                .schoolClass(schoolClass)
                .feeType(feeType)
                .academicYear(academicYear)
                .amount(req.getAmount())
                .frequency(req.getFrequency())
                .dueDay(req.getDueDay())
                .build();

        fs = feeStructureRepository.save(fs);
        log.info("FeeStructure created: {} / {} ₹{} ({}) for school {}",
                schoolClass.getName(), feeType.getName(), req.getAmount(), req.getFrequency(), schoolId);
        return toResponse(fs);
    }

    // ── List by academic year ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FeeStructureResponse> listByYear(UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        return feeStructureRepository.findBySchoolAndYear(schoolId, academicYearId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── List by class ─────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FeeStructureResponse> listByClass(UUID classId) {
        UUID schoolId = TenantContext.getSchoolId();
        return feeStructureRepository.findBySchoolAndClass(schoolId, classId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public FeeStructureResponse update(UUID id, FeeStructureRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        FeeStructure fs = findOwned(id, schoolId);

        boolean keyChanged = !fs.getSchoolClass().getId().equals(req.getClassId())
                          || !fs.getFeeType().getId().equals(req.getFeeTypeId())
                          || !fs.getAcademicYear().getId().equals(req.getAcademicYearId());

        if (keyChanged && feeStructureRepository.existsBySchoolClass_IdAndFeeType_IdAndAcademicYear_Id(
                req.getClassId(), req.getFeeTypeId(), req.getAcademicYearId())) {
            throw new BusinessRuleException("DUPLICATE_FEE_STRUCTURE",
                    "A fee structure for this class, fee type, and academic year already exists.");
        }

        fs.setSchoolClass(resolveClass(req.getClassId(), schoolId));
        fs.setFeeType(resolveFeeType(req.getFeeTypeId(), schoolId));
        fs.setAcademicYear(resolveAcademicYear(req.getAcademicYearId(), schoolId));
        fs.setAmount(req.getAmount());
        fs.setFrequency(req.getFrequency());
        fs.setDueDay(req.getDueDay());

        fs = feeStructureRepository.save(fs);
        log.info("FeeStructure updated: id={}", id);
        return toResponse(fs);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(UUID id) {
        UUID schoolId = TenantContext.getSchoolId();
        FeeStructure fs = findOwned(id, schoolId);
        feeStructureRepository.delete(fs);
        log.info("FeeStructure deleted: id={}", id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private FeeStructure findOwned(UUID id, UUID schoolId) {
        FeeStructure fs = feeStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeStructure", id));
        if (!fs.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("FeeStructure", id);
        return fs;
    }

    private SchoolClass resolveClass(UUID classId, UUID schoolId) {
        SchoolClass sc = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class", classId));
        if (!sc.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("Class", classId);
        return sc;
    }

    private FeeType resolveFeeType(UUID feeTypeId, UUID schoolId) {
        FeeType ft = feeTypeRepository.findById(feeTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", feeTypeId));
        if (!ft.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("FeeType", feeTypeId);
        return ft;
    }

    private AcademicYear resolveAcademicYear(UUID yearId, UUID schoolId) {
        AcademicYear ay = academicYearRepository.findById(yearId)
                .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", yearId));
        if (!ay.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("AcademicYear", yearId);
        return ay;
    }

    FeeStructureResponse toResponse(FeeStructure fs) {
        return FeeStructureResponse.builder()
                .id(fs.getId())
                .classId(fs.getSchoolClass().getId())
                .className(fs.getSchoolClass().getName())
                .classSection(fs.getSchoolClass().getSection())
                .feeTypeId(fs.getFeeType().getId())
                .feeTypeName(fs.getFeeType().getName())
                .academicYearId(fs.getAcademicYear().getId())
                .academicYearName(fs.getAcademicYear().getName())
                .amount(fs.getAmount())
                .frequency(fs.getFrequency())
                .dueDay(fs.getDueDay())
                .createdAt(fs.getCreatedAt())
                .build();
    }
}
