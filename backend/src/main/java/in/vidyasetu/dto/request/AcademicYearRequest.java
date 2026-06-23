package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AcademicYearRequest {

    @NotBlank(message = "Name is required (e.g. 2025-2026)")
    @Pattern(regexp = "^\\d{4}-\\d{4}$",
             message = "Name must be in format YYYY-YYYY (e.g. 2025-2026)")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Boolean makeCurrent = false;
}
