package in.vidyasetu.repository;

import in.vidyasetu.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, UUID> {

    List<AcademicYear> findBySchool_IdOrderByStartDateDesc(UUID schoolId);

    Optional<AcademicYear> findBySchool_IdAndIsCurrentTrue(UUID schoolId);

    boolean existsBySchool_IdAndName(UUID schoolId, String name);

    // Unmark all "current" rows for this school except the given one
    @Modifying
    @Query("UPDATE AcademicYear a SET a.isCurrent = false " +
           "WHERE a.school.id = :schoolId AND a.id <> :exceptId")
    void unmarkAllCurrentExcept(UUID schoolId, UUID exceptId);
}
