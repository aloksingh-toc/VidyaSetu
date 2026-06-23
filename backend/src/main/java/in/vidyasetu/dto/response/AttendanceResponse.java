package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AttendanceResponse {
    private UUID          id;
    private UUID          studentId;
    private String        studentName;
    private UUID          classId;
    private String        className;
    private UUID          academicYearId;
    private LocalDate     date;
    private String        status;     // PRESENT, ABSENT, LATE, LEAVE
    private String        markedByName;
    private LocalDateTime createdAt;
}
