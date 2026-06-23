package in.vidyasetu.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Thin client for the Razorpay Orders API.
 * Inactive (throws RazorpayNotConfiguredException) until real key id/secret
 * are supplied — see {@code razorpay.*} keys in application.properties.
 * Gated as "Coming Soon" in the product (Settings → Billing) until then.
 */
@Component
@Slf4j
public class RazorpayClient {

    private static final String API_URL = "https://api.razorpay.com/v1/orders";

    @Value("${razorpay.key.id:}")
    private String keyId;

    @Value("${razorpay.key.secret:}")
    private String keySecret;

    @Value("${razorpay.webhook.secret:}")
    private String webhookSecret;

    private final RestClient restClient = RestClient.create();

    public boolean isConfigured() {
        return StringUtils.hasText(keyId) && StringUtils.hasText(keySecret);
    }

    /**
     * Creates a Razorpay order for the given amount (in rupees) and returns the
     * order id to hand to the frontend checkout widget.
     */
    public String createOrder(BigDecimal amountInRupees, String currency, String receiptRef) {
        if (!isConfigured()) {
            throw new RazorpayNotConfiguredException("Razorpay key id / secret not configured");
        }

        Map<String, Object> payload = Map.of(
                "amount", amountInRupees.multiply(BigDecimal.valueOf(100)).intValue(),  // paise
                "currency", currency,
                "receipt", receiptRef
        );

        Map<?, ?> response = restClient.post()
                .uri(API_URL)
                .headers(h -> h.setBasicAuth(keyId, keySecret))
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("id") == null) {
            throw new RazorpaySendException("Razorpay API returned no order id");
        }
        return String.valueOf(response.get("id"));
    }

    /** Verifies a Razorpay webhook signature (hex HMAC-SHA256 over the raw request body). */
    public boolean verifyWebhookSignature(String rawBody, String signatureHeader) {
        if (!StringUtils.hasText(webhookSecret) || !StringUtils.hasText(signatureHeader)) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] computed = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(computed).equalsIgnoreCase(signatureHeader);
        } catch (Exception e) {
            log.warn("Razorpay webhook signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static class RazorpayNotConfiguredException extends RuntimeException {
        public RazorpayNotConfiguredException(String message) { super(message); }
    }

    public static class RazorpaySendException extends RuntimeException {
        public RazorpaySendException(String message) { super(message); }
    }
}
