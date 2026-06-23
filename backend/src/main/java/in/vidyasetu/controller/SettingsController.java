package in.vidyasetu.controller;

import in.vidyasetu.dto.request.ChangePasswordRequest;
import in.vidyasetu.dto.request.SchoolSettingsRequest;
import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.service.SettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ResponseEntity<ApiResponse> get() {
        return ResponseEntity.ok(ApiResponse.ok(settingsService.getSettings(), "Settings fetched"));
    }

    @PutMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ApiResponse> update(@Valid @RequestBody SchoolSettingsRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(settingsService.updateSettings(req), "Settings updated"));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        settingsService.changePassword(req);
        return ResponseEntity.ok(ApiResponse.ok(null, "Password changed successfully"));
    }
}
