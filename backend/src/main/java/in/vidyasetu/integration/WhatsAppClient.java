package in.vidyasetu.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Thin client for the Meta WhatsApp Business Graph API.
 * Inactive (throws WhatsAppNotConfiguredException) until a real phone-number-id
 * and access token are supplied — see {@code whatsapp.*} keys in application.properties.
 * Gated as "Coming Soon" in the product until those credentials are acquired.
 */
@Component
@Slf4j
public class WhatsAppClient {

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.enabled:false}")
    private boolean globallyEnabled;

    private final RestClient restClient = RestClient.create();

    public boolean isGloballyEnabled() {
        return globallyEnabled;
    }

    /**
     * Sends a pre-approved WhatsApp template message.
     *
     * @return the WhatsApp message id assigned by Meta
     */
    public String sendTemplate(String phoneNumberId, String accessToken, String recipientPhone,
                                String templateName, List<String> bodyParams) {
        if (!StringUtils.hasText(phoneNumberId) || !StringUtils.hasText(accessToken)) {
            throw new WhatsAppNotConfiguredException("WhatsApp phone number id / access token not configured");
        }

        Map<String, Object> template = Map.of(
                "name", templateName,
                "language", Map.of("code", "en_US"),
                "components", bodyParams == null || bodyParams.isEmpty()
                        ? List.of()
                        : List.of(Map.of(
                                "type", "body",
                                "parameters", bodyParams.stream()
                                        .map(p -> Map.of("type", "text", "text", p))
                                        .toList()))
        );

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", normalizePhone(recipientPhone),
                "type", "template",
                "template", template
        );

        Map<?, ?> response = restClient.post()
                .uri(apiUrl + "/" + phoneNumberId + "/messages")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(Map.class);

        List<?> messages = response != null ? (List<?>) response.get("messages") : null;
        if (messages == null || messages.isEmpty()) {
            throw new WhatsAppSendException("WhatsApp API returned no message id");
        }
        Map<?, ?> first = (Map<?, ?>) messages.get(0);
        return String.valueOf(first.get("id"));
    }

    private String normalizePhone(String phone) {
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        return digitsOnly.startsWith("91") || digitsOnly.length() > 10 ? digitsOnly : "91" + digitsOnly;
    }

    public static class WhatsAppNotConfiguredException extends RuntimeException {
        public WhatsAppNotConfiguredException(String message) { super(message); }
    }

    public static class WhatsAppSendException extends RuntimeException {
        public WhatsAppSendException(String message) { super(message); }
    }
}
