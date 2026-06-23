package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ExamResponse {
    private UUID                     id;
    private UUID                     academicYearId;
    private String                   academicYearName;
    private String                   name;
    private String                   examType;
    private LocalDate                startDate;
    private LocalDate                endDate;
    private Boolean                  resultPublished;
    private LocalDateTime            publishedAt;
    private LocalDateTime            createdAt;
    private List<ExamSubjectResponse> subjects;   // populated on detail view
}
