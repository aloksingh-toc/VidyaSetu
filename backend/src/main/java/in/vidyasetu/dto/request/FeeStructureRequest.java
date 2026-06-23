package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class FeeStructureRequest {

    @NotNull(message = "Class is required")
    private UUID classId;

    @NotNull(message = "Fee type is required")
    private UUID feeTypeId;

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal amount;

    @NotBlank(message = "Frequency is required")
    @Pattern(regexp = "MONTHLY|QUARTERLY|ANNUAL|ONE_TIME",
             message = "Frequency must be MONTHLY, QUARTERLY, ANNUAL, or ONE_TIME")
    private String frequency;

    @Min(1) @Max(28)
    private Integer dueDay;
}
