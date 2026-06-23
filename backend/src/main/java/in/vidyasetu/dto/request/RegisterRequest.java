package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    // ── School details ────────────────────────────────────────
    @NotBlank(message = "School name is required")
    @Size(min = 2, max = 255, message = "School name must be 2–255 characters")
    private String schoolName;

    private String institutionType = "SCHOOL";   // SCHOOL or COACHING_CENTER

    private String city;
    private String state = "Uttar Pradesh";

    // ── Owner (first user) details ────────────────────────────
    @NotBlank(message = "Owner name is required")
    @Size(min = 2, max = 255)
    private String ownerName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
