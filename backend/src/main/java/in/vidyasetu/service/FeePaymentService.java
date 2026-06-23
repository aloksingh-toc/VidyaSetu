package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.FeePaymentRequest;
import in.vidyasetu.dto.request.VoidPaymentRequest;
import in.vidyasetu.dto.response.FeePaymentResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeePaymentService {

    private final FeePaymentRepository  feePaymentRepository;
    private final StudentRepository     studentRepository;
    private final FeeTypeRepository     feeTypeRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository        userRepository;
    private final ParentRepository      parentRepository;
    private final AuditLogService       auditLogService;
    private final AppNotificationService appNotificationService;
    private final NotificationService   notificationService;

    // ── Collect payment ───────────────────────────────────────────────────────

    @Transactional
    public FeePaymentResponse collect(UUID studentId, FeePaymentRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        Student      student      = findStudent(studentId, schoolId);
        FeeType      feeType      = resolveFeeType(req.getFeeTypeId(), schoolId);
        AcademicYear academicYear = resolveAcademicYear(req.getAcademicYearId(), schoolId);
        User         collectedBy  = currentUser();

        FeePayment payment = FeePayment.builder()
                .school(student.getSchool())
                .student(student)
                .feeType(feeType)
                .academicYear(academicYear)
                .amountPaid(req.getAmountPaid())
                .amountDue(req.getAmountDue())
                .amountWaived(req.getAmountWaived() != null ? req.getAmountWaived() : BigDecimal.ZERO)
                .paymentMethod(req.getPaymentMethod())
                .paymentDate(req.getPaymentDate() != null ? req.getPaymentDate() : LocalDate.now())
                .forMonth(req.getForMonth())
                .receiptNumber(generateReceiptNumber())
                .notes(req.getNotes())
                .transactionRef(req.getTransactionRef())
                .collectedBy(collectedBy)
                .build();

        payment = feePaymentRepository.save(payment);
        log.info("FeePayment collected: ₹{} {} for student {} receipt {}",
                req.getAmountPaid(), feeType.getName(), studentId, payment.getReceiptNumber());

        auditLogService.log("FEE_PAYMENT_RECORDED", "FeePayment", payment.getId(), null,
                java.util.Map.of("amountPaid", payment.getAmountPaid(), "receiptNumber", payment.getReceiptNumber()));
        appNotificationService.notifyOwnersAndAdmins(schoolId, "FEE_PAYMENT",
                "Fee payment received",
                "₹" + req.getAmountPaid() + " collected from " + student.getFirstName()
                        + " (receipt " + payment.getReceiptNumber() + ")",
                "/students/" + studentId);

        FeePayment savedPayment = payment;
        parentRepository.findByStudent_IdAndIsPrimaryTrue(studentId).ifPresent(parent ->
                notificationService.sendFeeReceipt(schoolId, studentId, parent.getPhone(),
                        savedPayment.getReceiptNumber(), savedPayment.getAmountPaid().toString()));

        return toResponse(payment);
    }

    // ── List payments for a student ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<FeePaymentResponse> listByStudent(UUID studentId, UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);   // verify ownership

        return feePaymentRepository.findByStudentAndYear(schoolId, studentId, academicYearId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Void payment ──────────────────────────────────────────────────────────

    @Transactional
    public FeePaymentResponse voidPayment(UUID studentId, UUID paymentId, VoidPaymentRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);

        FeePayment payment = feePaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("FeePayment", paymentId));

        if (!payment.getStudent().getId().equals(studentId))
            throw new ResourceNotFoundException("FeePayment", paymentId);
        if (!payment.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("FeePayment", paymentId);
        if ("VOIDED".equals(payment.getStatus()))
            throw new BusinessRuleException("ALREADY_VOIDED", "This payment has already been voided.");

        payment.setStatus("VOIDED");
        payment.setVoidReason(req.getReason());
        payment.setVoidedAt(LocalDateTime.now());
        payment.setVoidedBy(currentUser());

        payment = feePaymentRepository.save(payment);
        log.info("FeePayment voided: id={} reason={}", paymentId, req.getReason());
        auditLogService.log("FEE_PAYMENT_VOIDED", "FeePayment", payment.getId(),
                java.util.Map.of("status", "ACTIVE"), java.util.Map.of("status", "VOIDED", "reason", req.getReason()));
        return toResponse(payment);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Student findStudent(UUID studentId, UUID schoolId) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        if (!s.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("Student", studentId);
        return s;
    }

    private FeeType resolveFeeType(UUID id, UUID schoolId) {
        FeeType ft = feeTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FeeType", id));
        if (!ft.getSchool().getId().equals(schoolId))
            throw new ResourceNotFoundException("FeeType", id);
        return ft;
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

    private String generateReceiptNumber() {
        String month = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "VS-" + month + "-" + suffix;
    }

    FeePaymentResponse toResponse(FeePayment p) {
        String studentName = p.getStudent().getFirstName() +
                (StringUtils.hasText(p.getStudent().getLastName())
                        ? " " + p.getStudent().getLastName() : "");

        return FeePaymentResponse.builder()
                .id(p.getId())
                .studentId(p.getStudent().getId())
                .studentName(studentName)
                .feeTypeId(p.getFeeType().getId())
                .feeTypeName(p.getFeeType().getName())
                .academicYearId(p.getAcademicYear().getId())
                .academicYearName(p.getAcademicYear().getName())
                .amountPaid(p.getAmountPaid())
                .amountDue(p.getAmountDue())
                .amountWaived(p.getAmountWaived())
                .paymentMethod(p.getPaymentMethod())
                .paymentDate(p.getPaymentDate())
                .forMonth(p.getForMonth())
                .receiptNumber(p.getReceiptNumber())
                .receiptUrl(p.getReceiptUrl())
                .status(p.getStatus())
                .voidReason(p.getVoidReason())
                .voidedAt(p.getVoidedAt())
                .collectedByName(p.getCollectedBy() != null ? p.getCollectedBy().getName() : null)
                .notes(p.getNotes())
                .transactionRef(p.getTransactionRef())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
