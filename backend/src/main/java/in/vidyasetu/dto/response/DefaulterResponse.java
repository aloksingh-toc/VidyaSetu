package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class DefaulterResponse {
    private UUID id;
    private String fullName;
    private String rollNumber;
    private String admissionNumber;
    private UUID classId;
    private String className;
    private String classSection;
}
