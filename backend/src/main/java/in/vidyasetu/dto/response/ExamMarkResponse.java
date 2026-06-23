package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ExamMarkResponse {
    private UUID          id;
    private UUID          studentId;
    private String        studentName;
    private String        rollNumber;
    private UUID          examSubjectId;
    private String        subject;
    private BigDecimal    marksObtained;
    private BigDecimal    maxMarks;
    private Boolean       isAbsent;
    private String        remarks;
    private String        grade;           // computed: A+, A, B, C, F
    private String        enteredByName;
    private LocalDateTime updatedAt;
}
