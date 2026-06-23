package in.vidyasetu.controller;

import in.vidyasetu.dto.request.FeePaymentRequest;
import in.vidyasetu.dto.request.VoidPaymentRequest;
import in.vidyasetu.dto.response.FeePaymentResponse;
import in.vidyasetu.service.FeePaymentService;
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
@RequestMapping("/v1/students/{studentId}/payments")
@RequiredArgsConstructor
public class FeePaymentController {

    private final FeePaymentService feePaymentService;

    // POST /v1/students/{studentId}/payments
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> collect(@PathVariable UUID studentId,
                                     @Valid @RequestBody FeePaymentRequest req) {
        FeePaymentResponse data = feePaymentService.collect(studentId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ok(data, "Payment recorded"));
    }

    // GET /v1/students/{studentId}/payments?academicYearId=...
    @GetMapping
    public ResponseEntity<?> list(@PathVariable UUID studentId,
                                  @RequestParam UUID academicYearId) {
        List<FeePaymentResponse> data = feePaymentService.listByStudent(studentId, academicYearId);
        return ResponseEntity.ok(ok(data, "Payments fetched"));
    }

    // PATCH /v1/students/{studentId}/payments/{paymentId}/void
    @PatchMapping("/{paymentId}/void")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> voidPayment(@PathVariable UUID studentId,
                                         @PathVariable UUID paymentId,
                                         @Valid @RequestBody VoidPaymentRequest req) {
        FeePaymentResponse data = feePaymentService.voidPayment(studentId, paymentId, req);
        return ResponseEntity.ok(ok(data, "Payment voided"));
    }

    private ApiResponse ok(Object data, String message) {
        return ApiResponse.ok(data, message);
    }
}
