package in.vidyasetu.controller;

import in.vidyasetu.dto.request.StaffRequest;
import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse> list() {
        return ResponseEntity.ok(ApiResponse.ok(staffService.list()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse> create(
            @Valid @RequestBody StaffRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.create(req), "Staff member added successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse> update(
            @PathVariable UUID id, @Valid @RequestBody StaffRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(staffService.update(id, req), "Staff member updated successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        staffService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        staffService.activate(id);
        return ResponseEntity.noContent().build();
    }
}
