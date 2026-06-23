package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class ClassRequest {

    @NotNull(message = "Academic year ID is required")
    private UUID academicYearId;

    @NotBlank(message = "Class name is required")
    @Size(max = 100, message = "Class name cannot exceed 100 characters")
    private String name;

    @Size(max = 10, message = "Section cannot exceed 10 characters")
    private String section;

    private UUID classTeacherId;

    private Integer displayOrder = 0;
}
