package in.vidyasetu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "schools")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "institution_type")
    @Builder.Default
    private String institutionType = "SCHOOL";   // SCHOOL, COACHING_CENTER

    private String address;
    private String city;

    @Builder.Default
    private String state = "Uttar Pradesh";

    private String phone;

    @Column(unique = true)
    private String email;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "plan_type")
    @Builder.Default
    private String planType = "FREE";

    @Column(name = "plan_expires_at")
    private LocalDateTime planExpiresAt;

    @Column(name = "weekly_off_days")
    @Builder.Default
    private String weeklyOffDays = "SUNDAY";

    @Column(name = "grading_scale")
    @Builder.Default
    private String gradingScale = "STANDARD";

    @Column(name = "language_preference")
    @Builder.Default
    private String languagePreference = "hi";

    @Column(name = "whatsapp_phone_number_id")
    private String whatsappPhoneNumberId;

    @Column(name = "whatsapp_access_token")
    private String whatsappAccessToken;   // AES-256 encrypted at rest

    @Column(name = "whatsapp_enabled")
    @Builder.Default
    private Boolean whatsappEnabled = false;

    @Column(name = "sms_enabled")
    @Builder.Default
    private Boolean smsEnabled = true;

    @Column(name = "late_fee_enabled")
    @Builder.Default
    private Boolean lateFeeEnabled = false;

    @Column(name = "late_fee_amount")
    private BigDecimal lateFeeAmount;

    @Column(name = "fee_due_day")
    @Builder.Default
    private Integer feeDueDay = 10;

    @Column(name = "support_phone")
    private String supportPhone;

    private String gstin;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
