package in.vidyasetu.controller;

import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.dto.response.AppNotificationResponse;
import in.vidyasetu.dto.response.PageResponse;
import in.vidyasetu.service.AppNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class AppNotificationController {

    private final AppNotificationService appNotificationService;

    // GET /v1/notifications?page&size
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "20") int size) {
        PageResponse<AppNotificationResponse> data = appNotificationService.listForCurrentUser(page, size);
        return ResponseEntity.ok(ApiResponse.ok(data, "Notifications fetched successfully"));
    }

    // GET /v1/notifications/unread-count
    @GetMapping("/unread-count")
    public ResponseEntity<?> unreadCount() {
        long count = appNotificationService.unreadCount();
        return ResponseEntity.ok(ApiResponse.ok(Map.of("count", count), "Unread count fetched"));
    }

    // PATCH /v1/notifications/{id}/read
    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable UUID id) {
        appNotificationService.markRead(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Notification marked as read"));
    }

    // PATCH /v1/notifications/read-all
    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllRead() {
        appNotificationService.markAllRead();
        return ResponseEntity.ok(ApiResponse.ok(null, "All notifications marked as read"));
    }
}
