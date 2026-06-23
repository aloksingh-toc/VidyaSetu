package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ClassResponse {
    private UUID id;
    private UUID academicYearId;
    private String academicYearName;
    private String name;
    private String section;
    private UUID classTeacherId;
    private String classTeacherName;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
