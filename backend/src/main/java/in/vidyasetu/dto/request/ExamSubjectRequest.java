package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExamSubjectRequest {

    @NotNull(message = "Class is required")
    private UUID classId;

    @NotBlank(message = "Subject name is required")
    @Size(max = 100)
    private String subject;

    @NotNull(message = "Max marks is required")
    @DecimalMin(value = "1")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal maxMarks;

    @DecimalMin(value = "0")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal passingMarks;

    private LocalDate examDate;
}
