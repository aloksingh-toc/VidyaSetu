package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class FeePaymentRequest {

    @NotNull(message = "Fee type is required")
    private UUID feeTypeId;

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(value = "0.01", message = "Amount paid must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amountPaid;

    @NotNull(message = "Amount due is required")
    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amountDue;

    @DecimalMin(value = "0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amountWaived;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "CASH|UPI|CHEQUE|ONLINE",
             message = "paymentMethod must be CASH, UPI, CHEQUE, or ONLINE")
    private String paymentMethod;

    private LocalDate paymentDate;   // defaults to today in service

    @Pattern(regexp = "\\d{4}-\\d{2}", message = "forMonth must be in YYYY-MM format, e.g. 2025-06")
    private String forMonth;

    @Size(max = 500)
    private String notes;

    @Size(max = 100)
    private String transactionRef;
}
