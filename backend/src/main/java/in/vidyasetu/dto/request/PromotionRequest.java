package in.vidyasetu.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class PromotionRequest {

    @NotEmpty
    private List<PromotionEntry> entries;

    @Data
    public static class PromotionEntry {
        private UUID fromClassId;
        private UUID toClassId;
    }
}
