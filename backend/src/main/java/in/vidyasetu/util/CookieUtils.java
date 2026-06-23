package in.vidyasetu.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

@Component
public class CookieUtils {

    @Value("${cookie.secure:false}")
    private boolean secure;

    public void addAccessTokenCookie(HttpServletResponse response, String token, long maxAgeMs) {
        addCookie(response, "access_token", token, (int) (maxAgeMs / 1000));
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String token, long maxAgeMs) {
        addCookie(response, "refresh_token", token, (int) (maxAgeMs / 1000));
    }

    public void clearAuthCookies(HttpServletResponse response) {
        addCookie(response, "access_token",  "", 0);
        addCookie(response, "refresh_token", "", 0);
    }

    public Optional<String> getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    // Uses ResponseCookie (Spring 5.1+) which natively supports SameSite.
    // response.addHeader is used so multiple Set-Cookie headers coexist correctly.
    // The old approach (addCookie + setHeader) overwrote all prior Set-Cookie headers
    // with a single one, silently dropping the second cookie on every login/logout.
    private void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
