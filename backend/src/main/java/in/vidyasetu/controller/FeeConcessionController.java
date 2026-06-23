package in.vidyasetu.controller;

import in.vidyasetu.dto.request.FeeConcessionRequest;
import in.vidyasetu.dto.response.FeeConcessionResponse;
import in.vidyasetu.service.FeeConcessionService;
import jakarta.validation.Valid;
import in.vidyasetu.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/students/{studentId}/concessions")
@RequiredArgsConstructor
public class FeeConcessionController {

    private final FeeConcessionService feeConcessionService;

    // POST /v1/students/{studentId}/concessions
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> create(@PathVariable UUID studentId,
                                    @Valid @RequestBody FeeConcessionRequest req) {
        FeeConcessionResponse data = feeConcessionService.create(studentId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Concession added"));
    }

    // GET /v1/students/{studentId}/concessions?academicYearId=...
    @GetMapping
    public ResponseEntity<?> list(@PathVariable UUID studentId,
                                  @RequestParam UUID academicYearId) {
        List<FeeConcessionResponse> data = feeConcessionService.list(studentId, academicYearId);
        return ResponseEntity.ok(ok(data, "Concessions fetched"));
    }

    // DELETE /v1/students/{studentId}/concessions/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> deactivate(@PathVariable UUID studentId,
                                        @PathVariable UUID id) {
        feeConcessionService.deactivate(studentId, id);
        return ResponseEntity.ok(ok(null, "Concession deactivated"));
    }

    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
