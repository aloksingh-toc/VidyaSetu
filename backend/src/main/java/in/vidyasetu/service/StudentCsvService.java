package in.vidyasetu.service;

import in.vidyasetu.config.TenantContext;
import in.vidyasetu.dto.response.StudentImportResultResponse;
import in.vidyasetu.entity.School;
import in.vidyasetu.entity.SchoolClass;
import in.vidyasetu.entity.Student;
import in.vidyasetu.exception.ResourceNotFoundException;
import in.vidyasetu.repository.SchoolClassRepository;
import in.vidyasetu.repository.SchoolRepository;
import in.vidyasetu.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentCsvService {

    private static final String[] HEADERS = {
            "Roll Number", "First Name", "Last Name", "Class", "Section",
            "Date of Birth", "Gender", "Admission Date", "Admission Number",
            "Blood Group", "Address"
    };

    private final StudentRepository     studentRepository;
    private final SchoolClassRepository classRepository;
    private final SchoolRepository      schoolRepository;
    private final AuditLogService       auditLogService;

    // ── Export ────────────────────────────────────────────────────────────────

    public byte[] exportCsv(UUID classId, boolean activeOnly) {
        UUID schoolId = TenantContext.getSchoolId();
        List<Student> students = activeOnly
                ? studentRepository.findBySchool_IdAndIsActiveTrueOrderByFirstNameAsc(schoolId)
                : studentRepository.findWithFilters(schoolId, classId, null, false,
                        org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getContent();

        if (classId != null) {
            students = students.stream()
                    .filter(s -> s.getSchoolClass() != null && classId.equals(s.getSchoolClass().getId()))
                    .collect(Collectors.toList());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader(HEADERS).build())) {

            for (Student s : students) {
                printer.printRecord(
                        s.getRollNumber(),
                        s.getFirstName(),
                        s.getLastName(),
                        s.getSchoolClass() != null ? s.getSchoolClass().getName() : null,
                        s.getSchoolClass() != null ? s.getSchoolClass().getSection() : null,
                        s.getDateOfBirth(),
                        s.getGender(),
                        s.getAdmissionDate(),
                        s.getAdmissionNumber(),
                        s.getBloodGroup(),
                        s.getAddress()
                );
            }
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate CSV export", e);
        }
        return out.toByteArray();
    }

    // ── Import ────────────────────────────────────────────────────────────────

    public StudentImportResultResponse importCsv(MultipartFile file) {
        UUID schoolId = TenantContext.getSchoolId();
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School", schoolId));

        Map<String, SchoolClass> classesByKey = classRepository.findBySchool_IdOrderByDisplayOrderAsc(schoolId)
                .stream()
                .collect(Collectors.toMap(this::classKey, c -> c, (a, b) -> a));

        List<StudentImportResultResponse.RowError> errors = new ArrayList<>();
        int total = 0;
        int success = 0;

        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader().setSkipHeaderRecord(true).setTrim(true).setIgnoreSurroundingSpaces(true)
                .build()
                .parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            for (CSVRecord record : parser) {
                total++;
                int rowNum = (int) record.getRecordNumber() + 1; // +1 for header row

                try {
                    String firstName = get(record, "First Name");
                    if (!StringUtils.hasText(firstName)) {
                        throw new IllegalArgumentException("First Name is required");
                    }

                    String className = get(record, "Class");
                    String section   = get(record, "Section");
                    if (!StringUtils.hasText(className)) {
                        throw new IllegalArgumentException("Class is required");
                    }
                    SchoolClass schoolClass = classesByKey.get(key(className, section));
                    if (schoolClass == null) {
                        throw new IllegalArgumentException(
                                "Class '" + className + (StringUtils.hasText(section) ? " " + section : "") + "' not found");
                    }

                    String rollNumber = get(record, "Roll Number");
                    if (StringUtils.hasText(rollNumber) &&
                            studentRepository.existsBySchool_IdAndSchoolClass_IdAndRollNumber(
                                    schoolId, schoolClass.getId(), rollNumber)) {
                        throw new IllegalArgumentException(
                                "Roll number '" + rollNumber + "' already exists in class " + className);
                    }

                    Student student = Student.builder()
                            .school(school)
                            .schoolClass(schoolClass)
                            .rollNumber(rollNumber)
                            .firstName(firstName)
                            .lastName(get(record, "Last Name"))
                            .dateOfBirth(parseDate(get(record, "Date of Birth")))
                            .gender(normalizeGender(get(record, "Gender")))
                            .admissionDate(parseDateOrToday(get(record, "Admission Date")))
                            .admissionNumber(get(record, "Admission Number"))
                            .bloodGroup(get(record, "Blood Group"))
                            .address(get(record, "Address"))
                            .build();

                    studentRepository.save(student);
                    success++;
                } catch (Exception rowEx) {
                    errors.add(StudentImportResultResponse.RowError.builder()
                            .row(rowNum).message(rowEx.getMessage()).build());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV file", e);
        }

        log.info("CSV import for school {}: {} total, {} succeeded, {} failed",
                schoolId, total, success, errors.size());
        auditLogService.log("STUDENTS_IMPORTED", "Student", null, null,
                Map.of("total", total, "success", success, "failed", errors.size()));

        return StudentImportResultResponse.builder()
                .totalRows(total)
                .successCount(success)
                .errorCount(errors.size())
                .errors(errors)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String get(CSVRecord record, String header) {
        if (!record.isMapped(header)) return null;
        String v = record.get(header);
        return StringUtils.hasText(v) ? v.trim() : null;
    }

    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date '" + value + "' (expected YYYY-MM-DD)");
        }
    }

    private LocalDate parseDateOrToday(String value) {
        LocalDate parsed = parseDate(value);
        return parsed != null ? parsed : LocalDate.now();
    }

    private String normalizeGender(String value) {
        if (!StringUtils.hasText(value)) return null;
        String upper = value.trim().toUpperCase();
        if (!upper.equals("MALE") && !upper.equals("FEMALE") && !upper.equals("OTHER")) {
            throw new IllegalArgumentException("Gender must be MALE, FEMALE, or OTHER (got '" + value + "')");
        }
        return upper;
    }

    private String classKey(SchoolClass c) {
        return key(c.getName(), c.getSection());
    }

    private String key(String name, String section) {
        return (name == null ? "" : name.trim().toLowerCase()) + "|" +
               (section == null ? "" : section.trim().toLowerCase());
    }
}
