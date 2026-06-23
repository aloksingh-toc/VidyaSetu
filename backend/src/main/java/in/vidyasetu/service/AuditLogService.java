package in.vidyasetu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.response.AuditLogResponse;
import in.vidyasetu.dto.response.PageResponse;
import in.vidyasetu.entity.AuditLog;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.User;
import in.vidyasetu.repository.AuditLogRepository;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Writes immutable audit trail entries (audit_logs table). Application DB user
 * has INSERT-only privileges on this table by design — see V1__init.sql:381.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final SchoolRepository   schoolRepository;
    private final UserRepository     userRepository;
    private final ObjectMapper       objectMapper;

    // Runs in its own transaction so a logging failure never rolls back the
    // business operation that triggered it.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String entityType, UUID entityId, Object oldValue, Object newValue) {
        try {
            UUID schoolId = TenantContext.getSchoolId();
            School school = schoolId != null ? schoolRepository.findById(schoolId).orElse(null) : null;
            User   user   = currentUser();

            AuditLog entry = AuditLog.builder()
                    .school(school)
                    .user(user)
                    .userName(user != null ? user.getName() : "System")
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(toJson(oldValue))
                    .newValue(toJson(newValue))
                    .ipAddress(currentRequestIp())
                    .userAgent(currentRequestUserAgent())
                    .build();

            auditLogRepository.save(entry);
        } catch (Exception e) {
            log.warn("Failed to write audit log for action={}: {}", action, e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> list(int page, int size, String action, String entityType) {
        UUID schoolId = TenantContext.getSchoolId();
        Page<AuditLog> result = auditLogRepository.findWithFilters(
                schoolId, blankToNull(action), blankToNull(entityType),
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        return PageResponse.<AuditLogResponse>builder()
                .content(result.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .first(result.isFirst())
                .last(result.isLast())
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String blankToNull(String s) {
        return StringUtils.hasText(s) ? s : null;
    }

    private String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }

    private User currentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof String s && StringUtils.hasText(s)) {
            try {
                return userRepository.findById(UUID.fromString(s)).orElse(null);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private String currentRequestIp() {
        HttpServletRequest request = currentRequest();
        if (request == null) return null;
        String forwarded = request.getHeader("X-Forwarded-For");
        return StringUtils.hasText(forwarded) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    private String currentRequestUserAgent() {
        HttpServletRequest request = currentRequest();
        return request != null ? request.getHeader("User-Agent") : null;
    }

    private HttpServletRequest currentRequest() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private AuditLogResponse toResponse(AuditLog a) {
        return AuditLogResponse.builder()
                .id(a.getId())
                .userName(a.getUserName())
                .action(a.getAction())
                .entityType(a.getEntityType())
                .entityId(a.getEntityId())
                .oldValue(a.getOldValue())
                .newValue(a.getNewValue())
                .ipAddress(a.getIpAddress())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
