package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ExamRequest {

    @NotBlank(message = "Exam name is required")
    @Size(max = 100)
    private String name;

    @Pattern(regexp = "UNIT_TEST|HALF_YEARLY|ANNUAL|PRACTICAL|INTERNAL",
             message = "examType must be UNIT_TEST, HALF_YEARLY, ANNUAL, PRACTICAL, or INTERNAL")
    private String examType;

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;

    private LocalDate startDate;
    private LocalDate endDate;
}
