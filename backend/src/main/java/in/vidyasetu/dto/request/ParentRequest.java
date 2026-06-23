package in.vidyasetu.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ParentRequest {

    @NotBlank(message = "Parent name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Relation is required")
    @Pattern(regexp = "^(FATHER|MOTHER|GUARDIAN)$",
             message = "Relation must be FATHER, MOTHER, or GUARDIAN")
    private String relation;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
             message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    /** If blank, defaults to phone */
    @Pattern(regexp = "^[6-9]\\d{9}$",
             message = "Enter a valid 10-digit Indian mobile number")
    private String whatsappNumber;

    private Boolean isPrimary = false;
}
