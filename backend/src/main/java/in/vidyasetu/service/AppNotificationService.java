package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.response.AppNotificationResponse;
import in.vidyasetu.dto.response.PageResponse;
import in.vidyasetu.entity.AppNotification;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.User;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.AppNotificationRepository;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.stream.Collectors;

/** In-app notification bell — separate from the parent-facing WhatsApp/SMS NotificationService. */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppNotificationService {

    private final AppNotificationRepository appNotificationRepository;
    private final SchoolRepository          schoolRepository;
    private final UserRepository            userRepository;

    // Notifies every active OWNER/ADMIN in the school. Own transaction so a
    // notification failure never rolls back the business action that triggered it.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyOwnersAndAdmins(UUID schoolId, String type, String title, String body, String actionUrl) {
        try {
            School school = schoolRepository.findById(schoolId).orElse(null);
            if (school == null) return;

            for (User recipient : userRepository.findOwnersAndAdminsBySchool(schoolId)) {
                AppNotification n = AppNotification.builder()
                        .school(school)
                        .user(recipient)
                        .type(type)
                        .title(title)
                        .body(body)
                        .actionUrl(actionUrl)
                        .build();
                appNotificationRepository.save(n);
            }
        } catch (Exception e) {
            log.warn("Failed to create app notification type={}: {}", type, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<AppNotificationResponse> listForCurrentUser(int page, int size) {
        UUID userId = currentUserId();
        Page<AppNotification> result = appNotificationRepository.findByUser_IdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return PageResponse.<AppNotificationResponse>builder()
                .content(result.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return appNotificationRepository.countByUser_IdAndIsReadFalse(currentUserId());
    }

    @Transactional
    public void markRead(UUID id) {
        UUID userId = currentUserId();
        AppNotification n = appNotificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", id));
        if (!n.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification", id);
        }
        n.setIsRead(true);
        appNotificationRepository.save(n);
    }

    @Transactional
    public void markAllRead() {
        appNotificationRepository.markAllRead(currentUserId());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UUID currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String s && StringUtils.hasText(s)) {
            return UUID.fromString(s);
        }
        throw new ResourceNotFoundException("User", TenantContext.getSchoolId());
    }

    private AppNotificationResponse toResponse(AppNotification n) {
        return AppNotificationResponse.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .actionUrl(n.getActionUrl())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
