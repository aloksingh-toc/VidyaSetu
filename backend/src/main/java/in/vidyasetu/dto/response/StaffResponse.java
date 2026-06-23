package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StaffResponse {
    private UUID          id;
    private String        name;
    private String        phone;
    private String        email;
    private String        role;
    private Boolean       isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
