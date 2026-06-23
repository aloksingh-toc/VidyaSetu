package in.vidyasetu.controller;

import in.vidyasetu.dto.request.ExamMarkBulkRequest;
import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.service.ExamMarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ExamMarkController {

    private final ExamMarkService examMarkService;

    @GetMapping("/v1/exam-subjects/{subjectId}/marks")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse> getBySubject(
            @PathVariable UUID subjectId) {
        return ResponseEntity.ok(ApiResponse.ok(examMarkService.getBySubject(subjectId)));
    }

    @PostMapping("/v1/exam-subjects/{subjectId}/marks")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse> bulkSave(
            @PathVariable UUID subjectId,
            @Valid @RequestBody ExamMarkBulkRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(examMarkService.bulkSave(subjectId, req), "Marks saved successfully"));
    }

    @GetMapping("/v1/students/{studentId}/exam-marks")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse> getByStudent(
            @PathVariable UUID studentId,
            @RequestParam UUID examId) {
        return ResponseEntity.ok(ApiResponse.ok(examMarkService.getByStudent(studentId, examId)));
    }
}
