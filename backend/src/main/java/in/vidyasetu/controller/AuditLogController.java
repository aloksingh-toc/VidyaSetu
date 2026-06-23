package in.vidyasetu.controller;

import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.dto.response.AuditLogResponse;
import in.vidyasetu.dto.response.PageResponse;
import in.vidyasetu.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    // GET /v1/audit-logs?page&size&action&entityType
    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size,
                                   @RequestParam(required = false) String action,
                                   @RequestParam(required = false) String entityType) {
        PageResponse<AuditLogResponse> data = auditLogService.list(page, size, action, entityType);
        return ResponseEntity.ok(ApiResponse.ok(data, "Audit logs fetched successfully"));
    }
}
