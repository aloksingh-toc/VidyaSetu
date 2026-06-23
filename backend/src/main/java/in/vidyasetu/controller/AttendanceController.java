package in.vidyasetu.controller;

import in.vidyasetu.dto.request.AttendanceBulkRequest;
import in.vidyasetu.dto.response.AttendanceResponse;
import in.vidyasetu.dto.response.AttendanceSummaryResponse;
import in.vidyasetu.service.AttendanceService;
import jakarta.validation.Valid;
import in.vidyasetu.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    // POST /v1/attendance/bulk
    @PostMapping("/v1/attendance/bulk")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'TEACHER')")
    public ResponseEntity<?> bulkMark(@Valid @RequestBody AttendanceBulkRequest req) {
        List<AttendanceResponse> data = attendanceService.bulkMark(req);
        return ResponseEntity.ok(ok(data, "Attendance marked for " + data.size() + " students"));
    }

    // GET /v1/attendance?classId=&date=
    @GetMapping("/v1/attendance")
    public ResponseEntity<?> getByClassAndDate(
            @RequestParam UUID classId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceResponse> data = attendanceService.getByClassAndDate(classId, date);
        return ResponseEntity.ok(ok(data, "Attendance fetched"));
    }

    // GET /v1/students/{studentId}/attendance?academicYearId=
    @GetMapping("/v1/students/{studentId}/attendance")
    public ResponseEntity<?> getByStudent(@PathVariable UUID studentId,
                                          @RequestParam UUID academicYearId) {
        List<AttendanceResponse> data = attendanceService.getByStudent(studentId, academicYearId);
        return ResponseEntity.ok(ok(data, "Attendance fetched"));
    }

    // GET /v1/students/{studentId}/attendance/summary?academicYearId=
    @GetMapping("/v1/students/{studentId}/attendance/summary")
    public ResponseEntity<?> summary(@PathVariable UUID studentId,
                                     @RequestParam UUID academicYearId) {
        AttendanceSummaryResponse data = attendanceService.summary(studentId, academicYearId);
        return ResponseEntity.ok(ok(data, "Attendance summary fetched"));
    }

    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
