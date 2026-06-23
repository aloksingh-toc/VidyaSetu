package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FeeConcessionResponse {
    private UUID          id;
    private UUID          studentId;
    private String        studentName;
    private UUID          feeTypeId;
    private String        feeTypeName;
    private UUID          academicYearId;
    private String        academicYearName;
    private String        concessionType;
    private BigDecimal    concessionValue;
    private String        reason;
    private UUID          approvedById;
    private String        approvedByName;
    private Boolean       isActive;
    private LocalDateTime createdAt;
}
