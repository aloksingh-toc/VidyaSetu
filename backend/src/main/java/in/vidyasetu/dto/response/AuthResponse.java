package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private UUID userId;
    private UUID schoolId;
    private String name;
    private String role;
    private String schoolName;
    private String institutionType;
    private String planType;
    private String languagePreference;
}
