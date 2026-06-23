package in.vidyasetu.controller;

import in.vidyasetu.dto.request.AcademicYearRequest;
import in.vidyasetu.dto.response.AcademicYearResponse;
import in.vidyasetu.service.AcademicYearService;
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
@RequestMapping("/v1/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    // POST /v1/academic-years
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody AcademicYearRequest req) {
        AcademicYearResponse data = academicYearService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Academic year created successfully"));
    }

    // GET /v1/academic-years
    @GetMapping
    public ResponseEntity<?> list() {
        List<AcademicYearResponse> data = academicYearService.list();
        return ResponseEntity.ok(ok(data, "Academic years fetched successfully"));
    }

    // GET /v1/academic-years/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        AcademicYearResponse data = academicYearService.getById(id);
        return ResponseEntity.ok(ok(data, "Academic year fetched successfully"));
    }

    // PUT /v1/academic-years/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody AcademicYearRequest req) {
        AcademicYearResponse data = academicYearService.update(id, req);
        return ResponseEntity.ok(ok(data, "Academic year updated successfully"));
    }

    // DELETE /v1/academic-years/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        academicYearService.delete(id);
        return ResponseEntity.ok(ok(null, "Academic year deleted successfully"));
    }

    // ── Standard response wrapper ─────────────────────────────────────────────
    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
