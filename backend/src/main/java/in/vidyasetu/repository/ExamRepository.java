package in.vidyasetu.repository;

import in.vidyasetu.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {

    List<Exam> findBySchool_IdAndAcademicYear_IdOrderByStartDateAsc(
            UUID schoolId, UUID academicYearId);

    long countBySchool_IdAndAcademicYear_IdAndStartDateGreaterThanEqual(
            UUID schoolId, UUID academicYearId, LocalDate date);
}
