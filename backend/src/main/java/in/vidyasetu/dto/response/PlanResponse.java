package in.vidyasetu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {
    private String planType;
    private String displayName;
    private BigDecimal monthlyPrice;
    private Integer maxStudents;
    private boolean current;
}
