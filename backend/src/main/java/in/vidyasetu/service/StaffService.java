package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.StaffRequest;
import in.vidyasetu.dto.response.StaffResponse;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.User;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final UserRepository  userRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StaffResponse> list() {
        UUID schoolId = TenantContext.getSchoolId();
        return userRepository.findStaffBySchool(schoolId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StaffResponse create(StaffRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new BusinessRuleException("PASSWORD_REQUIRED",
                    "Password is required when creating a staff member.");
        }
        if (userRepository.existsByPhone(req.getPhone())) {
            throw new BusinessRuleException("PHONE_ALREADY_REGISTERED",
                    "This phone number is already registered.");
        }

        School school = schoolRepository.getReferenceById(schoolId);

        User user = User.builder()
                .school(school)
                .name(req.getName().trim())
                .phone(req.getPhone())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public StaffResponse update(UUID staffId, StaffRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        User user = userRepository.findById(staffId)
                .filter(u -> u.getSchool().getId().equals(schoolId) && !"OWNER".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));

        user.setName(req.getName().trim());
        user.setEmail(req.getEmail());
        user.setRole(req.getRole());

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        }

        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void deactivate(UUID staffId) {
        UUID schoolId = TenantContext.getSchoolId();
        User user = userRepository.findById(staffId)
                .filter(u -> u.getSchool().getId().equals(schoolId) && !"OWNER".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    public void activate(UUID staffId) {
        UUID schoolId = TenantContext.getSchoolId();
        User user = userRepository.findById(staffId)
                .filter(u -> u.getSchool().getId().equals(schoolId) && !"OWNER".equals(u.getRole()))
                .orElseThrow(() -> new ResourceNotFoundException("Staff", staffId));

        user.setIsActive(true);
        userRepository.save(user);
    }

    private StaffResponse toResponse(User u) {
        return StaffResponse.builder()
                .id(u.getId())
                .name(u.getName())
                .phone(u.getPhone())
                .email(u.getEmail())
                .role(u.getRole())
                .isActive(u.getIsActive())
                .lastLoginAt(u.getLastLoginAt())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
