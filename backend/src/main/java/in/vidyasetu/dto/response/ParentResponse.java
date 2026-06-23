package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ParentResponse {
    private UUID   id;
    private UUID   studentId;
    private String name;
    private String relation;
    private String phone;
    private String whatsappNumber;
    private Boolean isPrimary;
    private Boolean whatsappOptOut;
    private LocalDateTime createdAt;
}
