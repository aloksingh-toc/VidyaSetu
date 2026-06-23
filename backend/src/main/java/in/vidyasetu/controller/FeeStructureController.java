package in.vidyasetu.controller;

import in.vidyasetu.dto.request.FeeStructureRequest;
import in.vidyasetu.dto.response.FeeStructureResponse;
import in.vidyasetu.service.FeeStructureService;
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
@RequestMapping("/v1/fee-structures")
@RequiredArgsConstructor
public class FeeStructureController {

    private final FeeStructureService feeStructureService;

    // POST /v1/fee-structures
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody FeeStructureRequest req) {
        FeeStructureResponse data = feeStructureService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Fee structure created"));
    }

    // GET /v1/fee-structures?academicYearId=...
    @GetMapping
    public ResponseEntity<?> listByYear(@RequestParam UUID academicYearId) {
        List<FeeStructureResponse> data = feeStructureService.listByYear(academicYearId);
        return ResponseEntity.ok(ok(data, "Fee structures fetched"));
    }

    // GET /v1/fee-structures/by-class/{classId}
    @GetMapping("/by-class/{classId}")
    public ResponseEntity<?> listByClass(@PathVariable UUID classId) {
        List<FeeStructureResponse> data = feeStructureService.listByClass(classId);
        return ResponseEntity.ok(ok(data, "Fee structures fetched"));
    }

    // PUT /v1/fee-structures/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody FeeStructureRequest req) {
        FeeStructureResponse data = feeStructureService.update(id, req);
        return ResponseEntity.ok(ok(data, "Fee structure updated"));
    }

    // DELETE /v1/fee-structures/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        feeStructureService.delete(id);
        return ResponseEntity.ok(ok(null, "Fee structure deleted"));
    }

    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
