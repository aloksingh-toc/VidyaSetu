package in.vidyasetu.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class StudentRequest {

    @NotNull(message = "Class ID is required")
    private UUID classId;

    @Size(max = 20, message = "Roll number cannot exceed 20 characters")
    private String rollNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;

    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$",
             message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    private LocalDate admissionDate;

    @Size(max = 50, message = "Admission number cannot exceed 50 characters")
    private String admissionNumber;

    @Size(max = 5, message = "Blood group cannot exceed 5 characters")
    private String bloodGroup;

    private String address;

    /** Optional — create parents along with student in a single call */
    @Valid
    private List<ParentRequest> parents;
}
