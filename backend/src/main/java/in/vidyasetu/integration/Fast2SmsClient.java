package in.vidyasetu.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Thin client for the Fast2SMS bulk SMS API — used as the fallback channel
 * when WhatsApp isn't available. Inactive until {@code fast2sms.api.key} is
 * supplied; gated as "Coming Soon" in the product until then.
 */
@Component
@Slf4j
public class Fast2SmsClient {

    private static final String API_URL = "https://www.fast2sms.com/dev/bulkV2";

    @Value("${fast2sms.api.key:}")
    private String apiKey;

    @Value("${fast2sms.enabled:false}")
    private boolean globallyEnabled;

    private final RestClient restClient = RestClient.create();

    public boolean isGloballyEnabled() {
        return globallyEnabled;
    }

    public boolean isConfigured() {
        return StringUtils.hasText(apiKey);
    }

    public String sendSms(String recipientPhone, String message) {
        if (!isConfigured()) {
            throw new Fast2SmsNotConfiguredException("Fast2SMS API key not configured");
        }

        String uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("route", "q")
                .queryParam("message", message)
                .queryParam("language", "english")
                .queryParam("flash", 0)
                .queryParam("numbers", normalizePhone(recipientPhone))
                .toUriString();

        Map<?, ?> response = restClient.get()
                .uri(uri)
                .header("authorization", apiKey)
                .retrieve()
                .body(Map.class);

        if (response == null || !Boolean.TRUE.equals(response.get("return"))) {
            throw new Fast2SmsSendException("Fast2SMS API did not confirm delivery: " + response);
        }

        Object requestId = response.get("request_id");
        return requestId != null ? requestId.toString() : null;
    }

    private String normalizePhone(String phone) {
        return phone.replaceAll("[^0-9]", "").replaceFirst("^91(?=\\d{10}$)", "");
    }

    public static class Fast2SmsNotConfiguredException extends RuntimeException {
        public Fast2SmsNotConfiguredException(String message) { super(message); }
    }

    public static class Fast2SmsSendException extends RuntimeException {
        public Fast2SmsSendException(String message) { super(message); }
    }
}
