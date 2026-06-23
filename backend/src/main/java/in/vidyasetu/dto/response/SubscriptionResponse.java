package in.vidyasetu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private String planType;
    private LocalDateTime planExpiresAt;
    private String status;
    private boolean billingEnabled;   // false until Razorpay keys are configured — drives "Coming Soon" UI
}
