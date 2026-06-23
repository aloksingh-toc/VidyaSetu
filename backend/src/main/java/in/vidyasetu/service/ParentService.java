package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.ParentRequest;
import in.vidyasetu.dto.response.ParentResponse;
import in.vidyasetu.entity.Parent;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.Student;
import in.vidyasetu.exception.BusinessRuleException;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.ParentRepository;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParentService {

    private final ParentRepository  parentRepository;
    private final StudentRepository studentRepository;
    private final SchoolRepository  schoolRepository;

    // ── Add parent to student ─────────────────────────────────────────────────

    @Transactional
    public ParentResponse addParent(UUID studentId, ParentRequest req) {
        UUID schoolId = TenantContext.getSchoolId();

        Student student = findStudent(studentId, schoolId);
        School school   = student.getSchool();

        // If marked primary, unmark existing primary first
        if (Boolean.TRUE.equals(req.getIsPrimary())) {
            unmarkExistingPrimary(studentId);
        }

        Parent parent = Parent.builder()
                .school(school)
                .student(student)
                .name(req.getName())
                .relation(req.getRelation())
                .phone(req.getPhone())
                .whatsappNumber(StringUtils.hasText(req.getWhatsappNumber())
                        ? req.getWhatsappNumber() : req.getPhone())
                .isPrimary(Boolean.TRUE.equals(req.getIsPrimary()))
                .build();

        parent = parentRepository.save(parent);
        log.info("Parent added: {} ({}) for student {}", parent.getName(), parent.getRelation(), studentId);
        return toResponse(parent);
    }

    // ── List parents of a student ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ParentResponse> listByStudent(UUID studentId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);   // verify ownership

        return parentRepository.findByStudent_IdOrderByIsPrimaryDescCreatedAtAsc(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Update parent ─────────────────────────────────────────────────────────

    @Transactional
    public ParentResponse update(UUID studentId, UUID parentId, ParentRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);   // verify student ownership

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", parentId));

        // Must belong to the same student
        if (!parent.getStudent().getId().equals(studentId)) {
            throw new ResourceNotFoundException("Parent", parentId);
        }

        // If promoting to primary, demote current primary
        if (Boolean.TRUE.equals(req.getIsPrimary()) && !Boolean.TRUE.equals(parent.getIsPrimary())) {
            unmarkExistingPrimary(studentId);
        }

        parent.setName(req.getName());
        parent.setRelation(req.getRelation());
        parent.setPhone(req.getPhone());
        parent.setWhatsappNumber(StringUtils.hasText(req.getWhatsappNumber())
                ? req.getWhatsappNumber() : req.getPhone());
        parent.setIsPrimary(Boolean.TRUE.equals(req.getIsPrimary()));

        parent = parentRepository.save(parent);
        return toResponse(parent);
    }

    // ── Toggle WhatsApp opt-out ───────────────────────────────────────────────

    @Transactional
    public ParentResponse toggleWhatsappOptOut(UUID studentId, UUID parentId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", parentId));

        if (!parent.getStudent().getId().equals(studentId)) {
            throw new ResourceNotFoundException("Parent", parentId);
        }

        parent.setWhatsappOptOut(!Boolean.TRUE.equals(parent.getWhatsappOptOut()));
        parent = parentRepository.save(parent);
        return toResponse(parent);
    }

    // ── Delete parent ─────────────────────────────────────────────────────────

    @Transactional
    public void delete(UUID studentId, UUID parentId) {
        UUID schoolId = TenantContext.getSchoolId();
        findStudent(studentId, schoolId);

        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent", parentId));

        if (!parent.getStudent().getId().equals(studentId)) {
            throw new ResourceNotFoundException("Parent", parentId);
        }

        boolean wasPrimary = Boolean.TRUE.equals(parent.getIsPrimary());

        List<Parent> allParents = parentRepository
                .findByStudent_IdOrderByIsPrimaryDescCreatedAtAsc(studentId);

        if (allParents.size() <= 1) {
            throw new BusinessRuleException("CANNOT_DELETE_ONLY_PARENT",
                    "Cannot delete the only parent. Add another parent first.");
        }

        parentRepository.delete(parent);
        log.info("Parent deleted: {} (id={})", parent.getName(), parentId);

        // Auto-promote the earliest remaining parent when the primary is deleted
        if (wasPrimary) {
            allParents.stream()
                    .filter(p -> !p.getId().equals(parentId))
                    .findFirst()
                    .ifPresent(p -> {
                        p.setIsPrimary(true);
                        parentRepository.save(p);
                        log.info("Auto-promoted {} as primary contact for student {}",
                                p.getName(), studentId);
                    });
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Student findStudent(UUID studentId, UUID schoolId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        if (!student.getSchool().getId().equals(schoolId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        return student;
    }

    private void unmarkExistingPrimary(UUID studentId) {
        parentRepository.findByStudent_IdAndIsPrimaryTrue(studentId)
                .ifPresent(p -> {
                    p.setIsPrimary(false);
                    parentRepository.save(p);
                });
    }

    private ParentResponse toResponse(Parent p) {
        return ParentResponse.builder()
                .id(p.getId())
                .studentId(p.getStudent().getId())
                .name(p.getName())
                .relation(p.getRelation())
                .phone(p.getPhone())
                .whatsappNumber(p.getWhatsappNumber())
                .isPrimary(p.getIsPrimary())
                .whatsappOptOut(p.getWhatsappOptOut())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
