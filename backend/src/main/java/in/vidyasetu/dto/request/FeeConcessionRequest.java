package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class FeeConcessionRequest {

    @NotNull(message = "Fee type is required")
    private UUID feeTypeId;

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;

    @NotBlank(message = "Concession type is required")
    @Pattern(regexp = "PERCENTAGE|FIXED_AMOUNT|FULL_WAIVER",
             message = "concessionType must be PERCENTAGE, FIXED_AMOUNT, or FULL_WAIVER")
    private String concessionType;

    // Required for PERCENTAGE (1–100) and FIXED_AMOUNT (>0); null for FULL_WAIVER
    @DecimalMin(value = "0.01", message = "Concession value must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal concessionValue;

    @Size(max = 200)
    private String reason;
}
