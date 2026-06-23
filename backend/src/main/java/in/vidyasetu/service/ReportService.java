package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.response.DefaulterResponse;
import in.vidyasetu.entity.Student;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.AcademicYearRepository;
import in.vidyasetu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentRepository      studentRepository;
    private final AcademicYearRepository academicYearRepository;

    @Transactional(readOnly = true)
    public List<DefaulterResponse> getFeeDefaulters(UUID academicYearId) {
        UUID schoolId = TenantContext.getSchoolId();

        academicYearRepository.findById(academicYearId)
            .filter(ay -> ay.getSchool().getId().equals(schoolId))
            .orElseThrow(() -> new ResourceNotFoundException("AcademicYear", academicYearId));

        return studentRepository.findDefaulters(schoolId, academicYearId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    private DefaulterResponse toResponse(Student s) {
        String fullName = s.getFirstName() +
            (StringUtils.hasText(s.getLastName()) ? " " + s.getLastName() : "");
        return DefaulterResponse.builder()
            .id(s.getId())
            .fullName(fullName)
            .rollNumber(s.getRollNumber())
            .admissionNumber(s.getAdmissionNumber())
            .classId(s.getSchoolClass() != null ? s.getSchoolClass().getId() : null)
            .className(s.getSchoolClass() != null ? s.getSchoolClass().getName() : null)
            .classSection(s.getSchoolClass() != null ? s.getSchoolClass().getSection() : null)
            .build();
    }
}
