package in.vidyasetu.controller;

import in.vidyasetu.dto.request.ClassRequest;
import in.vidyasetu.dto.response.ClassResponse;
import in.vidyasetu.service.ClassService;
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
@RequestMapping("/v1/classes")
@RequiredArgsConstructor
public class ClassController {

    private final ClassService classService;

    // POST /v1/classes
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody ClassRequest req) {
        ClassResponse data = classService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Class created successfully"));
    }

    // GET /v1/classes?academicYearId=...
    @GetMapping
    public ResponseEntity<?> list(@RequestParam UUID academicYearId) {
        List<ClassResponse> data = classService.listByAcademicYear(academicYearId);
        return ResponseEntity.ok(ok(data, "Classes fetched successfully"));
    }

    // GET /v1/classes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        ClassResponse data = classService.getById(id);
        return ResponseEntity.ok(ok(data, "Class fetched successfully"));
    }

    // PUT /v1/classes/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody ClassRequest req) {
        ClassResponse data = classService.update(id, req);
        return ResponseEntity.ok(ok(data, "Class updated successfully"));
    }

    // DELETE /v1/classes/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        classService.delete(id);
        return ResponseEntity.ok(ok(null, "Class deleted successfully"));
    }

    // ── Standard response wrapper ─────────────────────────────────────────────
    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
