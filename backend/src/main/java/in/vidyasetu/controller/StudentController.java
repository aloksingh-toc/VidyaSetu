package in.vidyasetu.controller;

import in.vidyasetu.dto.request.ParentRequest;
import in.vidyasetu.dto.request.StudentRequest;
import in.vidyasetu.dto.response.PageResponse;
import in.vidyasetu.dto.response.ParentResponse;
import in.vidyasetu.dto.response.StudentImportResultResponse;
import in.vidyasetu.dto.response.StudentResponse;
import in.vidyasetu.service.ParentService;
import in.vidyasetu.service.StudentCsvService;
import in.vidyasetu.service.StudentService;
import jakarta.validation.Valid;
import in.vidyasetu.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService    studentService;
    private final ParentService     parentService;
    private final StudentCsvService studentCsvService;

    // ── Student CRUD ──────────────────────────────────────────────────────────

    // POST /v1/students
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody StudentRequest req) {
        StudentResponse data = studentService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Student added successfully"));
    }

    /**
     * GET /v1/students
     *   ?page=0&size=20
     *   &classId=<uuid>          (optional — filter by class)
     *   &search=Aarav            (optional — name / roll / admission no.)
     *   &activeOnly=true         (default true)
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0")    int     page,
            @RequestParam(defaultValue = "20")   int     size,
            @RequestParam(required = false)      UUID    classId,
            @RequestParam(required = false)      String  search,
            @RequestParam(defaultValue = "true") boolean activeOnly
    ) {
        PageResponse<StudentResponse> data = studentService.list(page, size, classId, search, activeOnly);
        return ResponseEntity.ok(ok(data, "Students fetched successfully"));
    }

    // GET /v1/students/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        StudentResponse data = studentService.getById(id);
        return ResponseEntity.ok(ok(data, "Student fetched successfully"));
    }

    // PUT /v1/students/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody StudentRequest req) {
        StudentResponse data = studentService.update(id, req);
        return ResponseEntity.ok(ok(data, "Student updated successfully"));
    }

    // DELETE /v1/students/{id}  →  soft deactivate
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> deactivate(@PathVariable UUID id) {
        studentService.deactivate(id);
        return ResponseEntity.ok(ok(null, "Student deactivated successfully"));
    }

    // PATCH /v1/students/{id}/reactivate
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> reactivate(@PathVariable UUID id) {
        StudentResponse data = studentService.reactivate(id);
        return ResponseEntity.ok(ok(data, "Student reactivated successfully"));
    }

    // ── CSV import / export ───────────────────────────────────────────────────

    // GET /v1/students/export?classId=<uuid>&activeOnly=true
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false)      UUID    classId,
            @RequestParam(defaultValue = "true") boolean activeOnly
    ) {
        byte[] csv = studentCsvService.exportCsv(classId, activeOnly);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"students.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    // POST /v1/students/import  (multipart file upload)
    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) {
        StudentImportResultResponse data = studentCsvService.importCsv(file);
        return ResponseEntity.ok(ok(data, "Import completed: " + data.getSuccessCount() + " of "
                + data.getTotalRows() + " row(s) imported"));
    }

    // ── Parent sub-resource ───────────────────────────────────────────────────

    // GET /v1/students/{id}/parents
    @GetMapping("/{id}/parents")
    public ResponseEntity<?> listParents(@PathVariable UUID id) {
        List<ParentResponse> data = parentService.listByStudent(id);
        return ResponseEntity.ok(ok(data, "Parents fetched successfully"));
    }

    // POST /v1/students/{id}/parents
    @PostMapping("/{id}/parents")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> addParent(@PathVariable UUID id,
                                       @Valid @RequestBody ParentRequest req) {
        ParentResponse data = parentService.addParent(id, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Parent added successfully"));
    }

    // PUT /v1/students/{id}/parents/{parentId}
    @PutMapping("/{id}/parents/{parentId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> updateParent(@PathVariable UUID id,
                                          @PathVariable UUID parentId,
                                          @Valid @RequestBody ParentRequest req) {
        ParentResponse data = parentService.update(id, parentId, req);
        return ResponseEntity.ok(ok(data, "Parent updated successfully"));
    }

    // PATCH /v1/students/{id}/parents/{parentId}/whatsapp-opt-out
    @PatchMapping("/{id}/parents/{parentId}/whatsapp-opt-out")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> toggleWhatsappOptOut(@PathVariable UUID id,
                                                  @PathVariable UUID parentId) {
        ParentResponse data = parentService.toggleWhatsappOptOut(id, parentId);
        return ResponseEntity.ok(ok(data, "WhatsApp preference updated"));
    }

    // DELETE /v1/students/{id}/parents/{parentId}
    @DeleteMapping("/{id}/parents/{parentId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> deleteParent(@PathVariable UUID id,
                                          @PathVariable UUID parentId) {
        parentService.delete(id, parentId);
        return ResponseEntity.ok(ok(null, "Parent removed successfully"));
    }

    // ── Standard response wrapper ─────────────────────────────────────────────
    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
