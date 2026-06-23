package in.vidyasetu.security;

import in.vidyasetu.config.TenantContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            extractTokenFromCookie(request, "access_token")
                    .filter(jwtUtil::isAccessTokenValid)
                    .ifPresent(token -> {
                        Claims claims = jwtUtil.parseAccessToken(token);
                        UUID userId   = jwtUtil.extractUserId(claims);
                        UUID schoolId = jwtUtil.extractSchoolId(claims);
                        String role   = jwtUtil.extractRole(claims);

                        // Set tenant context for this request thread
                        TenantContext.setSchoolId(schoolId);

                        // Set Spring Security authentication
                        var auth = new UsernamePasswordAuthenticationToken(
                                userId.toString(),
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + role))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    });

            filterChain.doFilter(request, response);

        } finally {
            // Always clean up ThreadLocal — prevents leaks in thread pool
            TenantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private Optional<String> extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}
