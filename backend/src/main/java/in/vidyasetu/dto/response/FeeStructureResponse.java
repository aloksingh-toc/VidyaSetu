package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FeeStructureResponse {
    private UUID          id;
    private UUID          classId;
    private String        className;
    private String        classSection;
    private UUID          feeTypeId;
    private String        feeTypeName;
    private UUID          academicYearId;
    private String        academicYearName;
    private BigDecimal    amount;
    private String        frequency;
    private Integer       dueDay;
    private LocalDateTime createdAt;
}
