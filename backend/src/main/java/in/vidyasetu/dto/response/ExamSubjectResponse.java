package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExamSubjectResponse {
    private UUID          id;
    private UUID          examId;
    private UUID          classId;
    private String        className;
    private String        classSection;
    private String        subject;
    private BigDecimal    maxMarks;
    private BigDecimal    passingMarks;
    private LocalDate     examDate;
    private LocalDateTime createdAt;
}
