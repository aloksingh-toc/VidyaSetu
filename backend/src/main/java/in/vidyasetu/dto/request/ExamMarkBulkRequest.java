package in.vidyasetu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ExamMarkBulkRequest {

    @NotEmpty(message = "At least one mark entry is required")
    @Valid
    private List<MarkEntry> entries;

    @Data
    public static class MarkEntry {
        @NotNull(message = "Student ID is required")
        private UUID studentId;

        @DecimalMin(value = "0")
        @Digits(integer = 3, fraction = 2)
        private BigDecimal marksObtained;   // null if absent

        private Boolean isAbsent;

        @Size(max = 200)
        private String remarks;
    }
}
