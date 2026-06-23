package in.vidyasetu.controller;

import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.dto.response.PlanResponse;
import in.vidyasetu.dto.response.SubscriptionResponse;
import in.vidyasetu.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    // GET /v1/billing/subscription
    @GetMapping("/subscription")
    public ResponseEntity<?> getSubscription() {
        SubscriptionResponse data = billingService.getCurrentSubscription();
        return ok(data, "Subscription fetched successfully");
    }

    // GET /v1/billing/plans
    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        List<PlanResponse> data = billingService.getPlans();
        return ok(data, "Plans fetched successfully");
    }

    // POST /v1/billing/checkout  { "planType": "PRO" }
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> checkout(@RequestBody Map<String, String> body) {
        String orderId = billingService.initiateCheckout(body.get("planType"));
        return ok(Map.of("orderId", orderId), "Checkout order created");
    }

    // POST /v1/billing/webhook  (Razorpay server-to-server callback — no auth, signature-verified)
    @PostMapping("/webhook")
    public ResponseEntity<?> webhook(@RequestBody String rawBody,
                                      @RequestHeader("X-Razorpay-Signature") String signature) {
        billingService.handleWebhook(rawBody, signature);
        return ok(null, "Webhook processed");
    }

    private ResponseEntity<?> ok(Object data, String message) {
        return ResponseEntity.ok(ApiResponse.ok(data, message));
    }
}
