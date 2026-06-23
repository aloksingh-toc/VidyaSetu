package in.vidyasetu.controller;

import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse> stats(
            @RequestParam(required = false) UUID academicYearId) {
        return ResponseEntity.ok(ApiResponse.ok(dashboardService.stats(academicYearId)));
    }
}
