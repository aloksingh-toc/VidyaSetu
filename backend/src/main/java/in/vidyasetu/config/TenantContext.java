package in.vidyasetu.config;

import java.util.UUID;

/**
 * ThreadLocal store for the current request's school_id (tenant).
 *
 * Set by JwtFilter after validating the JWT.
 * Read by all services/repositories to scope every query to the correct school.
 * Cleared after each request (via JwtFilter finally block).
 */
public class TenantContext {

    private static final ThreadLocal<UUID> CURRENT_SCHOOL = new ThreadLocal<>();

    private TenantContext() {}

    public static void setSchoolId(UUID schoolId) {
        CURRENT_SCHOOL.set(schoolId);
    }

    public static UUID getSchoolId() {
        return CURRENT_SCHOOL.get();
    }

    public static void clear() {
        CURRENT_SCHOOL.remove();
    }
}
