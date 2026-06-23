package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class StudentResponse {
    private UUID   id;

    // Class info (denormalised for convenience)
    private UUID   classId;
    private String className;
    private String section;

    private String rollNumber;
    private String firstName;
    private String lastName;
    private String fullName;        // firstName + " " + lastName

    private LocalDate dateOfBirth;
    private String    gender;
    private String    photoUrl;
    private LocalDate admissionDate;
    private String    admissionNumber;
    private String    bloodGroup;
    private String    address;
    private Boolean   isActive;

    /** Populated only on GET /students/{id} */
    private List<ParentResponse> parents;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
