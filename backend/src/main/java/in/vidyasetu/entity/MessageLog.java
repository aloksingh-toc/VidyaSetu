package in.vidyasetu.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_logs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "recipient_phone", nullable = false, length = 15)
    private String recipientPhone;

    @Column(length = 10)
    @Builder.Default
    private String channel = "WHATSAPP";   // WHATSAPP, SMS

    @Column(name = "message_type", length = 50)
    private String messageType;   // FEE_REMINDER, RECEIPT, ABSENT_NOTIF, BROADCAST, REPORT_CARD

    @Column(name = "template_name", length = 100)
    private String templateName;

    @Column(name = "message_body", columnDefinition = "TEXT")
    private String messageBody;

    @Column(length = 20)
    @Builder.Default
    private String status = "PENDING";   // PENDING, SENT, DELIVERED, FAILED

    @Column(name = "whatsapp_message_id", length = 200)
    private String whatsappMessageId;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
