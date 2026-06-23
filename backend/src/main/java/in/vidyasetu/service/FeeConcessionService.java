package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.FeeConcessionRequest;
import in.vidyasetu.dto.response.FeeConcessionResponse;
import in.vidyasetu.entity.*;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeConcessionService {

    private final FeeConcessionRepository feeConcessionRepository;
    private final StudentRepository       studentRepository;
    private final FeeTypeRepository       feeTypeRepository;
    private final AcademicYearRepository  academicYearRepository;
    private final UserRepository          userRepository;

    // ── Add concession for a student ─────────────────────────────────────────

    @Transactional
    public FeeConcessionResponse create(UUID studentId, FeeConcessionRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        Student student = findStudent(studentId, schoolId);

        FeeType      feeType      = resolveFeeType(req.getFeeTypeId(), schoolId);
        AcademicYear academicYear = resolveAcademicYear(req.getAcademicYearId(), schoolId);

        if (feeConcessionRepository.existsByStudent_IdAndFeeType_IdAndAcademicYear_IdAndIsActiveTrue(
                studentId, req.getFeeTypeId(), req.getAcademicYearId())) {
            throw new BusinessRuleException("DUPLICATE_CONCESSION",
                    "An active concession for this fee type and academic year already exists for this student.");
        }

        validateConcessionValue(req.getConcessionType(), req.getConcessionValue());

        User approvedBy = currentUser();

        FeeConcession concession = FeeConcession.builder()
                .school(student.getSchool())
                .student(student)
                .feeType(feeType)
                .academicYear(academicYear)
                .concessionType(req.getConcessionType())
                .concessionValue(req.getConcessionValue())
                .reason(req.getReason())
                .approvedBy(approvedBy)
                .build();

        concession = feeConcessionRepository.save(concession);
        log.info("FeeConcession created: {} {} {} for student {} in school {}",
                req.getConcessionType(), req.getConcessionValue(),
                feeType.getName(), studentId, schoolId);
        return toResponse(concession);
    }

    // ── List concessions for a student ───────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FeeConcessionResponse> list(UUID studentId, UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);   // verify ownership

        return feeConcessionRepository.findByStudentAndYear(schoolId, studentId, academicYearId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Deactivate concession ─────────────────────────────────────────────────

    @Transactional
    public void deactivate(UUID studentId, UUID concessionId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);

        FeeConcession concession = feeConcessionRepository.findById(concessionId)
                .orElseThrow(() -> new ResourceNotFoundException("FeeConcession", concessionId));

        if (!concession.getStudent().getId().equals(studentId))
            throw new ResourceNotFoundException("FeeConcession", concessionId);
        if (!concession.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("FeeConcession", concessionId);

        concession.setIsActive(false);
        feeConcessionRepository.save(concession);
        log.info("FeeConcession deactivated: id={}", concessionId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Student findStudent(UUID studentId, UUID schoolId) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        if (!s.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("Student", studentId);
        return s;
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

    private void validateConcessionValue(String type, BigDecimal value) {
        switch (type) {
            case "PERCENTAGE" -> {
                if (value == null) throw new BusinessRuleException("INVALID_CONCESSION",
                        "Concession value is required for PERCENTAGE type.");
                if (value.compareTo(BigDecimal.ONE) < 0 || value.compareTo(new BigDecimal("100")) > 0)
                    throw new BusinessRuleException("INVALID_CONCESSION",
                            "Percentage must be between 1 and 100.");
            }
            case "FIXED_AMOUNT" -> {
                if (value == null || value.compareTo(BigDecimal.ZERO) <= 0)
                    throw new BusinessRuleException("INVALID_CONCESSION",
                            "Concession value must be greater than 0 for FIXED_AMOUNT type.");
            }
            case "FULL_WAIVER" -> { /* value is ignored */ }
        }
    }

    private User currentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String s && StringUtils.hasText(s)) {
            return userRepository.findById(UUID.fromString(s)).orElse(null);
        }
        return null;
    }

    FeeConcessionResponse toResponse(FeeConcession fc) {
        String studentName = fc.getStudent().getFirstName() +
                (StringUtils.hasText(fc.getStudent().getLastName())
                        ? " " + fc.getStudent().getLastName() : "");

        return FeeConcessionResponse.builder()
                .id(fc.getId())
                .studentId(fc.getStudent().getId())
                .studentName(studentName)
                .feeTypeId(fc.getFeeType().getId())
                .feeTypeName(fc.getFeeType().getName())
                .academicYearId(fc.getAcademicYear().getId())
                .academicYearName(fc.getAcademicYear().getName())
                .concessionType(fc.getConcessionType())
                .concessionValue(fc.getConcessionValue())
                .reason(fc.getReason())
                .approvedById(fc.getApprovedBy() != null ? fc.getApprovedBy().getId() : null)
                .approvedByName(fc.getApprovedBy() != null ? fc.getApprovedBy().getName() : null)
                .isActive(fc.getIsActive())
                .createdAt(fc.getCreatedAt())
                .build();
    }
}
