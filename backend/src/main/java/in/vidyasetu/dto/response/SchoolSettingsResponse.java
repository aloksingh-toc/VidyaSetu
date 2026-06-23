package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class SchoolSettingsResponse {
    private UUID id;
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
    private String planType;
    private String logoUrl;
}
