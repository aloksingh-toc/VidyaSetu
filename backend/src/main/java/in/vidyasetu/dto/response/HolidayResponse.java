package in.vidyasetu.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HolidayResponse {
    private UUID id;
    private UUID academicYearId;
    private LocalDate date;
    private String name;
    private String type;
    private LocalDateTime createdAt;
}
