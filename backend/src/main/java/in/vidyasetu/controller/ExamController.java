package in.vidyasetu.controller;

import in.vidyasetu.dto.request.ExamRequest;
import in.vidyasetu.dto.request.ExamSubjectRequest;
import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    /* ── Exams ─────────────────────────────────────────────────────────── */

    @GetMapping("/v1/exams")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse> list(
            @RequestParam UUID academicYearId) {
        return ResponseEntity.ok(ApiResponse.ok(examService.listExams(academicYearId)));
    }

    @GetMapping("/v1/exams/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(examService.getExam(id)));
    }

    @PostMapping("/v1/exams")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody ExamRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(examService.createExam(req), "Exam created successfully"));
    }

    @PutMapping("/v1/exams/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse> update(
            @PathVariable UUID id, @Valid @RequestBody ExamRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(examService.updateExam(id, req), "Exam updated successfully"));
    }

    @PatchMapping("/v1/exams/{id}/publish")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> publish(@PathVariable UUID id) {
        examService.publishResults(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/v1/exams/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }

    /* ── Subjects ───────────────────────────────────────────────────────── */

    @PostMapping("/v1/exams/{examId}/subjects")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse> addSubject(
            @PathVariable UUID examId, @Valid @RequestBody ExamSubjectRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(examService.addSubject(examId, req), "Subject added successfully"));
    }

    @PutMapping("/v1/exam-subjects/{subjectId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse> updateSubject(
            @PathVariable UUID subjectId, @Valid @RequestBody ExamSubjectRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(examService.updateSubject(subjectId, req), "Subject updated successfully"));
    }

    @DeleteMapping("/v1/exam-subjects/{subjectId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> deleteSubject(@PathVariable UUID subjectId) {
        examService.deleteSubject(subjectId);
        return ResponseEntity.noContent().build();
    }
}
