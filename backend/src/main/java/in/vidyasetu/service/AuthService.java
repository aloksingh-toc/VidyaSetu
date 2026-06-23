package in.vidyasetu.service;

import in.vidyasetu.dto.request.LoginRequest;
import in.vidyasetu.dto.request.RegisterRequest;
import in.vidyasetu.dto.response.AuthResponse;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.User;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.UserRepository;
import in.vidyasetu.security.JwtUtil;
import in.vidyasetu.util.CookieUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final SchoolRepository schoolRepository;
    private final UserRepository   userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtUtil          jwtUtil;
    private final CookieUtils      cookieUtils;

    // ── Register ──────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest req, HttpServletResponse response) {
        // Validate no duplicate phone
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new BusinessRuleException("PHONE_ALREADY_REGISTERED",
                    "This phone number is already registered. Please login.");
        }

        // Treat blank email as absent so Postgres' unique constraint (which allows
        // multiple NULLs but not multiple "") doesn't reject the second blank-email signup
        String email = (req.getEmail() != null && !req.getEmail().isBlank()) ? req.getEmail() : null;

        // Create school
        School school = School.builder()
                .name(req.getSchoolName())
                .institutionType(req.getInstitutionType() != null ? req.getInstitutionType() : "SCHOOL")
                .city(req.getCity())
                .state(req.getState() != null ? req.getState() : "Uttar Pradesh")
                .phone(req.getPhone())
                .email(email)
                .planType("FREE")
                .build();
        school = schoolRepository.save(school);

        // Create owner user
        User owner = User.builder()
                .school(school)
                .name(req.getOwnerName())
                .phone(req.getPhone())
                .email(email)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role("OWNER")
                .build();
        owner = userRepository.save(owner);

        log.info("New school registered: {} (id={})", school.getName(), school.getId());

        // Issue tokens and set cookies
        issueTokenCookies(owner, school, response);

        return buildAuthResponse(owner, school);
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse login(LoginRequest req, HttpServletResponse response) {
        User user = userRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new BusinessRuleException("INVALID_CREDENTIALS",
                        "Phone number or password is incorrect."));

        if (!user.getIsActive()) {
            throw new BusinessRuleException("ACCOUNT_DISABLED",
                    "Your account has been disabled. Contact support.");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("INVALID_CREDENTIALS",
                    "Phone number or password is incorrect.");
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        School school = user.getSchool();
        issueTokenCookies(user, school, response);

        return buildAuthResponse(user, school);
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtils.getCookieValue(request, "refresh_token")
                .orElseThrow(() -> new BusinessRuleException("NO_REFRESH_TOKEN",
                        "No refresh token found. Please login again."));

        if (!jwtUtil.isRefreshTokenValid(refreshToken)) {
            throw new BusinessRuleException("INVALID_REFRESH_TOKEN",
                    "Session expired. Please login again.");
        }

        Claims claims = jwtUtil.parseRefreshToken(refreshToken);
        UUID userId = jwtUtil.extractUserId(claims);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!user.getIsActive()) {
            throw new BusinessRuleException("ACCOUNT_DISABLED", "Account disabled.");
        }

        School school = user.getSchool();

        // Issue new access token only
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), school.getId(), user.getRole());
        cookieUtils.addAccessTokenCookie(response, newAccessToken, jwtUtil.getAccessExpirationMs());

        return buildAuthResponse(user, school);
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    public void logout(HttpServletResponse response) {
        cookieUtils.clearAuthCookies(response);
    }

    // ── Me ────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AuthResponse me(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return buildAuthResponse(user, user.getSchool());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void issueTokenCookies(User user, School school, HttpServletResponse response) {
        String accessToken  = jwtUtil.generateAccessToken(user.getId(), school.getId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        cookieUtils.addAccessTokenCookie(response,  accessToken,  jwtUtil.getAccessExpirationMs());
        cookieUtils.addRefreshTokenCookie(response, refreshToken, jwtUtil.getRefreshExpirationMs());
    }

    private AuthResponse buildAuthResponse(User user, School school) {
        return AuthResponse.builder()
                .userId(user.getId())
                .schoolId(school.getId())
                .name(user.getName())
                .role(user.getRole())
                .schoolName(school.getName())
                .institutionType(school.getInstitutionType())
                .planType(school.getPlanType())
                .languagePreference(school.getLanguagePreference())
                .build();
    }
}
