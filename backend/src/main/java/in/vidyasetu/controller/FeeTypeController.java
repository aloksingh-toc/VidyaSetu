package in.vidyasetu.controller;

import in.vidyasetu.dto.request.FeeTypeRequest;
import in.vidyasetu.dto.response.FeeTypeResponse;
import in.vidyasetu.service.FeeTypeService;
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
@RequestMapping("/v1/fee-types")
@RequiredArgsConstructor
public class FeeTypeController {

    private final FeeTypeService feeTypeService;

    // POST /v1/fee-types
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody FeeTypeRequest req) {
        FeeTypeResponse data = feeTypeService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Fee type created"));
    }

    // GET /v1/fee-types?activeOnly=true
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "true") boolean activeOnly) {
        List<FeeTypeResponse> data = feeTypeService.list(activeOnly);
        return ResponseEntity.ok(ok(data, "Fee types fetched"));
    }

    // PUT /v1/fee-types/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody FeeTypeRequest req) {
        FeeTypeResponse data = feeTypeService.update(id, req);
        return ResponseEntity.ok(ok(data, "Fee type updated"));
    }

    // DELETE /v1/fee-types/{id}  (soft deactivate)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> deactivate(@PathVariable UUID id) {
        feeTypeService.deactivate(id);
        return ResponseEntity.ok(ok(null, "Fee type deactivated"));
    }

    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
