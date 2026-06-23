package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardStatsResponse {
    private long       totalStudents;
    private BigDecimal feesCollectedThisMonth;
    private BigDecimal feesCollectedThisYear;
    private long       presentToday;
    private long       absentToday;
    private double     attendancePercent;
    private long       upcomingExams;
    private long       totalClasses;
}
