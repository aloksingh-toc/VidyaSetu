package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AttendanceSummaryResponse {
    private UUID   studentId;
    private String studentName;
    private UUID   academicYearId;
    private String academicYearName;
    private long   totalPresent;
    private long   totalAbsent;
    private long   totalLate;
    private long   totalLeave;
    private long   totalMarked;
    private double attendancePercent;  // present / totalMarked * 100
}
