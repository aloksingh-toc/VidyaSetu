package in.vidyasetu.controller;

import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/fee-defaulters")
    public ResponseEntity<ApiResponse> getFeeDefaulters(@RequestParam UUID academicYearId) {
        return ResponseEntity.ok(ApiResponse.ok(
            reportService.getFeeDefaulters(academicYearId),
            "Fee defaulters fetched"
        ));
    }
}
