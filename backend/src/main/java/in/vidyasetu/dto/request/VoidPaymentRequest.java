package in.vidyasetu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VoidPaymentRequest {

    @NotBlank(message = "Void reason is required")
    @Size(max = 500)
    private String reason;
}
