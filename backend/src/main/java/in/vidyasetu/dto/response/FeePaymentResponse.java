package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FeePaymentResponse {
    private UUID          id;
    private UUID          studentId;
    private String        studentName;
    private UUID          feeTypeId;
    private String        feeTypeName;
    private UUID          academicYearId;
    private String        academicYearName;
    private BigDecimal    amountPaid;
    private BigDecimal    amountDue;
    private BigDecimal    amountWaived;
    private String        paymentMethod;
    private LocalDate     paymentDate;
    private String        forMonth;
    private String        receiptNumber;
    private String        receiptUrl;
    private String        status;
    private String        voidReason;
    private LocalDateTime voidedAt;
    private String        collectedByName;
    private String        notes;
    private String        transactionRef;
    private LocalDateTime createdAt;
}
