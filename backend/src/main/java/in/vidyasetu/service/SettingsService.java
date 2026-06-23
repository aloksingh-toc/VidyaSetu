package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.ChangePasswordRequest;
import in.vidyasetu.dto.request.SchoolSettingsRequest;
import in.vidyasetu.dto.response.SchoolSettingsResponse;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.User;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SchoolRepository schoolRepository;
    private final UserRepository   userRepository;
    private final PasswordEncoder  passwordEncoder;
    private final AuditLogService  auditLogService;

    @Transactional(readOnly = true)
    public SchoolSettingsResponse getSettings() {
        return toResponse(currentSchool());
    }

    @Transactional
    public SchoolSettingsResponse updateSettings(SchoolSettingsRequest req) {
        School school = currentSchool();

        school.setName(req.getName());
        if (req.getInstitutionType()     != null) school.setInstitutionType(req.getInstitutionType());
        if (req.getCity()                != null) school.setCity(req.getCity());
        if (req.getState()               != null) school.setState(req.getState());
        if (req.getAddress()             != null) school.setAddress(req.getAddress());
        if (req.getPhone()               != null) school.setPhone(req.getPhone());
        if (req.getEmail()               != null) school.setEmail(req.getEmail());
        if (req.getGstin()               != null) school.setGstin(req.getGstin());
        if (req.getFeeDueDay()           != null) school.setFeeDueDay(req.getFeeDueDay());
        if (req.getLateFeeEnabled()      != null) school.setLateFeeEnabled(req.getLateFeeEnabled());
        if (req.getLateFeeAmount()       != null) school.setLateFeeAmount(req.getLateFeeAmount());
        if (req.getWeeklyOffDays()       != null) school.setWeeklyOffDays(req.getWeeklyOffDays());
        if (req.getLanguagePreference()  != null) school.setLanguagePreference(req.getLanguagePreference());

        School saved = schoolRepository.save(school);
        auditLogService.log("SETTINGS_UPDATED", "School", saved.getId(), null,
                java.util.Map.of("name", saved.getName()));
        return toResponse(saved);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest req) {
        UUID userId = UUID.fromString(
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("WRONG_PASSWORD", "Current password is incorrect.");
        }

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
        auditLogService.log("PASSWORD_CHANGED", "User", user.getId(), null, null);
    }

    private School currentSchool() {
        UUID schoolId = TenantContext.getSchoolId();
        return schoolRepository.findById(schoolId)
            .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));
    }

    private SchoolSettingsResponse toResponse(School s) {
        return SchoolSettingsResponse.builder()
            .id(s.getId())
            .name(s.getName())
            .institutionType(s.getInstitutionType())
            .city(s.getCity())
            .state(s.getState())
            .address(s.getAddress())
            .phone(s.getPhone())
            .email(s.getEmail())
            .gstin(s.getGstin())
            .feeDueDay(s.getFeeDueDay())
            .lateFeeEnabled(s.getLateFeeEnabled())
            .lateFeeAmount(s.getLateFeeAmount())
            .weeklyOffDays(s.getWeeklyOffDays())
            .languagePreference(s.getLanguagePreference())
            .planType(s.getPlanType())
            .logoUrl(s.getLogoUrl())
            .build();
    }
}
