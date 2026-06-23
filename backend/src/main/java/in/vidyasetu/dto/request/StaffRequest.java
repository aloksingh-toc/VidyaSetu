package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StaffRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "[6-9][0-9]{9}", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @Email
    private String email;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "TEACHER|ADMIN", message = "Role must be TEACHER or ADMIN")
    private String role;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;   // required for create, optional for update
}
