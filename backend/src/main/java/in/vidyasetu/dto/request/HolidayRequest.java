package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class HolidayRequest {

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Holiday name is required")
    @Size(max = 100, message = "Holiday name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(PUBLIC|SCHOOL|HALF_DAY)$",
             message = "Type must be PUBLIC, SCHOOL, or HALF_DAY")
    private String type;
}
