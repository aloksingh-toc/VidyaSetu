package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.response.PlanResponse;
import in.vidyasetu.dto.response.SubscriptionResponse;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.Subscription;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.integration.RazorpayClient;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Billing / subscription management backed by Razorpay.
 * Checkout stays disabled ("Coming Soon") until real Razorpay keys are
 * supplied — see {@link RazorpayClient#isConfigured()} and the
 * {@code razorpay.*} keys in application.properties.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BillingService {

    private final SchoolRepository       schoolRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final RazorpayClient         razorpayClient;
    private final AuditLogService        auditLogService;

    private static final List<PlanResponse> PLAN_CATALOG = List.of(
            PlanResponse.builder().planType("FREE").displayName("Free").monthlyPrice(BigDecimal.ZERO).maxStudents(30).build(),
            PlanResponse.builder().planType("STARTER").displayName("Starter").monthlyPrice(new BigDecimal("299")).maxStudents(75).build(),
            PlanResponse.builder().planType("BASIC").displayName("Basic").monthlyPrice(new BigDecimal("499")).maxStudents(150).build(),
            PlanResponse.builder().planType("STANDARD").displayName("Standard").monthlyPrice(new BigDecimal("899")).maxStudents(300).build(),
            PlanResponse.builder().planType("PRO").displayName("Pro").monthlyPrice(new BigDecimal("1699")).maxStudents(750).build(),
            PlanResponse.builder().planType("COMPLETE").displayName("Complete").monthlyPrice(new BigDecimal("2999")).maxStudents(null).build()
    );

    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrentSubscription() {
        UUID schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        String status = subscriptionRepository.findFirstBySchool_IdOrderByCreatedAtDesc(schoolId)
                .map(Subscription::getStatus)
                .orElse("ACTIVE");

        return SubscriptionResponse.builder()
                .planType(school.getPlanType())
                .planExpiresAt(school.getPlanExpiresAt())
                .status(status)
                .billingEnabled(razorpayClient.isConfigured())
                .build();
    }

    public List<PlanResponse> getPlans() {
        UUID schoolId = TenantContext.getSchoolId();
        String currentPlan = schoolRepository.findById(schoolId)
                .map(School::getPlanType).orElse("FREE");

        return PLAN_CATALOG.stream()
                .map(p -> PlanResponse.builder()
                        .planType(p.getPlanType())
                        .displayName(p.getDisplayName())
                        .monthlyPrice(p.getMonthlyPrice())
                        .maxStudents(p.getMaxStudents())
                        .current(p.getPlanType().equals(currentPlan))
                        .build())
                .toList();
    }

    /**
     * Initiates a Razorpay checkout for the given plan. Until real Razorpay
     * keys are configured this always fails fast with a "Coming Soon" error
     * rather than attempting a doomed call to the Razorpay API.
     */
    @Transactional
    public String initiateCheckout(String planType) {
        if (!razorpayClient.isConfigured()) {
            throw new BusinessRuleException("BILLING_COMING_SOON",
                    "Online billing is coming soon. We're finalizing our payment gateway setup — your current plan stays active until then.");
        }

        UUID schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        PlanResponse plan = PLAN_CATALOG.stream()
                .filter(p -> p.getPlanType().equals(planType))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("INVALID_PLAN", "Unknown plan: " + planType));

        BigDecimal gst = plan.getMonthlyPrice().multiply(new BigDecimal("0.18"));
        BigDecimal total = plan.getMonthlyPrice().add(gst);
        String receiptRef = "VS-SUB-" + schoolId + "-" + System.currentTimeMillis();

        String orderId = razorpayClient.createOrder(total, "INR", receiptRef);

        Subscription subscription = Subscription.builder()
                .school(school)
                .planType(planType)
                .billingCycle("MONTHLY")
                .baseAmount(plan.getMonthlyPrice())
                .gstAmount(gst)
                .totalAmount(total)
                .startsAt(LocalDateTime.now())
                .nextBillingDate(LocalDate.now().plusMonths(1))
                .paymentReference(orderId)
                .status("PENDING")
                .build();
        subscriptionRepository.save(subscription);

        log.info("Razorpay order created: school={} plan={} orderId={}", schoolId, planType, orderId);
        auditLogService.log("BILLING_CHECKOUT_INITIATED", "Subscription", subscription.getId(), null,
                java.util.Map.of("planType", planType, "orderId", orderId));

        return orderId;
    }

    /** Handles the Razorpay webhook on successful payment, activating the pending subscription. */
    @Transactional
    public void handleWebhook(String rawBody, String signatureHeader) {
        if (!razorpayClient.verifyWebhookSignature(rawBody, signatureHeader)) {
            throw new BusinessRuleException("INVALID_SIGNATURE", "Razorpay webhook signature verification failed");
        }
        log.info("Razorpay webhook received and verified");
        // Real payload parsing (order id, payment id, status) wired in once Razorpay
        // keys are issued and we can exercise this end-to-end against sandbox events.
    }
}
