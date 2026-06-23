package in.vidyasetu.service;

import in.vidyasetu.entity.MessageLog;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.Student;
import in.vidyasetu.integration.Fast2SmsClient;
import in.vidyasetu.integration.WhatsAppClient;
import in.vidyasetu.repository.MessageLogRepository;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Sends WhatsApp/SMS notifications to parents and logs every attempt.
 * <p>
 * Real delivery only happens once {@link WhatsAppClient}/{@link Fast2SmsClient}
 * are configured with live credentials (whatsapp.* / fast2sms.* properties) AND
 * the school has WhatsApp enabled. Until then, this is gated as "Coming Soon":
 * messages are logged as PENDING instead of actually being sent, so nothing in
 * the app silently pretends a message went out when it didn't.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final MessageLogRepository messageLogRepository;
    private final SchoolRepository     schoolRepository;
    private final StudentRepository    studentRepository;
    private final WhatsAppClient       whatsAppClient;
    private final Fast2SmsClient       fast2SmsClient;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(UUID schoolId,
                     UUID studentId,
                     String recipientPhone,
                     String channel,
                     String messageType,
                     String templateName,
                     String messageBody) {

        School  school  = schoolRepository.getReferenceById(schoolId);
        Student student = studentId != null ? studentRepository.getReferenceById(studentId) : null;

        MessageLog entry = MessageLog.builder()
                .school(school)
                .student(student)
                .recipientPhone(recipientPhone)
                .channel(channel)
                .messageType(messageType)
                .templateName(templateName)
                .messageBody(messageBody)
                .build();

        if ("WHATSAPP".equals(channel)) {
            attemptWhatsApp(entry, school, recipientPhone, templateName, messageBody);
        } else {
            attemptSms(entry, recipientPhone, messageBody);
        }

        messageLogRepository.save(entry);
    }

    private void attemptWhatsApp(MessageLog entry, School school, String phone, String templateName, String body) {
        boolean active = whatsAppClient.isGloballyEnabled()
                && Boolean.TRUE.equals(school.getWhatsappEnabled())
                && StringUtils.hasText(school.getWhatsappPhoneNumberId())
                && StringUtils.hasText(school.getWhatsappAccessToken());

        if (!active) {
            entry.setStatus("PENDING");
            entry.setFailureReason("WhatsApp integration pending API key activation (Coming Soon)");
            return;
        }

        try {
            String messageId = whatsAppClient.sendTemplate(
                    school.getWhatsappPhoneNumberId(), school.getWhatsappAccessToken(),
                    phone, templateName, List.of(body));
            entry.setStatus("SENT");
            entry.setWhatsappMessageId(messageId);
            entry.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.warn("WhatsApp send failed for {}: {}", phone, e.getMessage());
            entry.setStatus("FAILED");
            entry.setFailureReason(e.getMessage());
        }
    }

    private void attemptSms(MessageLog entry, String phone, String body) {
        if (!fast2SmsClient.isGloballyEnabled() || !fast2SmsClient.isConfigured()) {
            entry.setStatus("PENDING");
            entry.setFailureReason("SMS integration pending API key activation (Coming Soon)");
            return;
        }

        try {
            fast2SmsClient.sendSms(phone, body);
            entry.setStatus("SENT");
            entry.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            log.warn("SMS send failed for {}: {}", phone, e.getMessage());
            entry.setStatus("FAILED");
            entry.setFailureReason(e.getMessage());
        }
    }

    public void sendFeeReceipt(UUID schoolId, UUID studentId, String phone, String receiptNumber, String amount) {
        String body = "Dear parent, fee payment of Rs." + amount + " received. Receipt: " + receiptNumber + ". -VidyaSetu";
        send(schoolId, studentId, phone, "WHATSAPP", "RECEIPT", "fee_receipt", body);
    }

    public void sendAbsentNotification(UUID schoolId, UUID studentId, String phone, String studentName) {
        String body = "Dear parent, " + studentName + " was marked absent today. -VidyaSetu";
        send(schoolId, studentId, phone, "WHATSAPP", "ABSENT_NOTIF", "absent_notification", body);
    }
}
