package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.request.PromotionRequest;
import in.vidyasetu.entity.SchoolClass;
import in.vidyasetu.entity.Student;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.SchoolClassRepository;
import in.vidyasetu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final StudentRepository     studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final AuditLogService       auditLogService;
    private final AppNotificationService appNotificationService;

    @Transactional
    public int promote(PromotionRequest req) {
        UUID schoolId = TenantContext.getSchoolId();
        int count = 0;

        for (PromotionRequest.PromotionEntry entry : req.getEntries()) {
            SchoolClass fromClass = schoolClassRepository.findById(entry.getFromClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", entry.getFromClassId()));
            if (!fromClass.getSchool().getId().equals(schoolId))
                throw new ResourceNotFoundException("Class", entry.getFromClassId());

            SchoolClass toClass = schoolClassRepository.findById(entry.getToClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class", entry.getToClassId()));
            if (!toClass.getSchool().getId().equals(schoolId))
                throw new ResourceNotFoundException("Class", entry.getToClassId());

            List<Student> students = studentRepository
                .findBySchool_IdAndSchoolClass_IdAndIsActiveTrueOrderByRollNumberAsc(
                    schoolId, entry.getFromClassId());

            for (Student s : students) {
                s.setSchoolClass(toClass);
                studentRepository.save(s);
                count++;
            }
        }

        log.info("Promoted {} students (school={})", count, schoolId);
        auditLogService.log("PROMOTION_COMPLETED", "School", schoolId, null,
                java.util.Map.of("studentsPromoted", count));
        appNotificationService.notifyOwnersAndAdmins(schoolId, "PROMOTION_COMPLETED",
                "Student promotion completed",
                count + " student(s) promoted to their new class.", "/promotions");
        return count;
    }
}
