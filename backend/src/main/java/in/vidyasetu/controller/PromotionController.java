package in.vidyasetu.controller;

import in.vidyasetu.dto.request.PromotionRequest;
import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<ApiResponse> promote(@Valid @RequestBody PromotionRequest req) {
        int count = promotionService.promote(req);
        return ResponseEntity.ok(ApiResponse.ok(
            Map.of("promoted", count),
            count + " student(s) promoted successfully"
        ));
    }
}
