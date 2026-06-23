package in.vidyasetu.dto.response;

import java.time.Instant;
import java.util.Map;

/**
 * Standard success envelope for every API response.
 *
 * Shape: { "success": true, "data": <payload>, "message": <text>, "timestamp": <iso8601> }
 *
 * Use the static factories instead of constructing directly so the shape stays
 * consistent across all controllers. Errors use {@link in.vidyasetu.exception.GlobalExceptionHandler}.
 */
public record ApiResponse(boolean success, Object data, String message, String timestamp) {

    public static ApiResponse ok(Object data, String message) {
        return new ApiResponse(true, data != null ? data : Map.of(), message, Instant.now().toString());
    }

    public static ApiResponse ok(Object data) {
        return ok(data, "Success");
    }
}
