package in.vidyasetu.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SchoolSettingsRequest {
    @NotBlank
    private String name;
    private String institutionType;
    private String city;
    private String state;
    private String address;
    private String phone;
    private String email;
    private String gstin;
    private Integer feeDueDay;
    private Boolean lateFeeEnabled;
    private BigDecimal lateFeeAmount;
    private String weeklyOffDays;
    private String languagePreference;
}
