package in.vidyasetu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class AttendanceBulkRequest {

    @NotNull(message = "Class is required")
    private UUID classId;

    @NotNull(message = "Academic year is required")
    private UUID academicYearId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotEmpty(message = "At least one attendance entry is required")
    @Valid
    private List<AttendanceEntry> entries;

    @Data
    public static class AttendanceEntry {
        @NotNull(message = "Student ID is required")
        private UUID studentId;

        @NotBlank(message = "Status is required")
        @Pattern(regexp = "PRESENT|ABSENT|LATE|LEAVE",
                 message = "Status must be PRESENT, ABSENT, LATE, or LEAVE")
        private String status;
    }
}
