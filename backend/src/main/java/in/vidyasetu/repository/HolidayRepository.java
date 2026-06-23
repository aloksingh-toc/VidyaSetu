package in.vidyasetu.repository;

import in.vidyasetu.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, UUID> {

    List<Holiday> findBySchool_IdAndAcademicYear_IdOrderByDateAsc(
            UUID schoolId, UUID academicYearId);

    List<Holiday> findBySchool_IdAndDateBetweenOrderByDateAsc(
            UUID schoolId, LocalDate from, LocalDate to);

    boolean existsBySchool_IdAndDate(UUID schoolId, LocalDate date);
}
