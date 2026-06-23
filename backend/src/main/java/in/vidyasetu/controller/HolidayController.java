package in.vidyasetu.controller;

import in.vidyasetu.dto.request.HolidayRequest;
import in.vidyasetu.dto.response.HolidayResponse;
import in.vidyasetu.service.HolidayService;
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
@RequestMapping("/v1/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final HolidayService holidayService;

    // POST /v1/holidays
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody HolidayRequest req) {
        HolidayResponse data = holidayService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Holiday created successfully"));
    }

    // GET /v1/holidays?academicYearId=...
    @GetMapping
    public ResponseEntity<?> list(@RequestParam UUID academicYearId) {
        List<HolidayResponse> data = holidayService.listByAcademicYear(academicYearId);
        return ResponseEntity.ok(ok(data, "Holidays fetched successfully"));
    }

    // GET /v1/holidays/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        HolidayResponse data = holidayService.getById(id);
        return ResponseEntity.ok(ok(data, "Holiday fetched successfully"));
    }

    // PUT /v1/holidays/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @Valid @RequestBody HolidayRequest req) {
        HolidayResponse data = holidayService.update(id, req);
        return ResponseEntity.ok(ok(data, "Holiday updated successfully"));
    }

    // DELETE /v1/holidays/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        holidayService.delete(id);
        return ResponseEntity.ok(ok(null, "Holiday deleted successfully"));
    }

    // ── Standard response wrapper ─────────────────────────────────────────────
    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
