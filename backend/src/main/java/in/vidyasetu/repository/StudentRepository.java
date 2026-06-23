package in.vidyasetu.repository;

import in.vidyasetu.entity.FeePayment;
import in.vidyasetu.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    /**
     * Flexible list with optional classId filter, search (name/roll/admission),
     * and activeOnly flag. Single query handles all combinations.
     */
    @Query("SELECT s FROM Student s " +
           "WHERE s.school.id = :schoolId " +
           "AND (:activeOnly = false OR s.isActive = true) " +
           "AND (:classId IS NULL OR s.schoolClass.id = :classId) " +
           "AND (:search IS NULL " +
           "     OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(s.lastName)  LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR s.rollNumber       = :search " +
           "     OR s.admissionNumber  = :search)")
    Page<Student> findWithFilters(@Param("schoolId")   UUID schoolId,
                                  @Param("classId")    UUID classId,
                                  @Param("search")     String search,
                                  @Param("activeOnly") boolean activeOnly,
                                  Pageable pageable);

    /** All active students in a class (used by attendance, fee calculation) */
    List<Student> findBySchool_IdAndSchoolClass_IdAndIsActiveTrueOrderByRollNumberAsc(
            UUID schoolId, UUID classId);

    /** All active students (no pagination — for bulk ops) */
    List<Student> findBySchool_IdAndIsActiveTrueOrderByFirstNameAsc(UUID schoolId);

    boolean existsBySchool_IdAndSchoolClass_IdAndRollNumber(
            UUID schoolId, UUID classId, String rollNumber);

    long countBySchool_IdAndIsActiveTrue(UUID schoolId);

    /** Students in a year's classes who have zero active fee payments — fee defaulters */
    @Query("SELECT s FROM Student s " +
           "WHERE s.school.id = :schoolId " +
           "AND s.isActive = true " +
           "AND s.schoolClass.academicYear.id = :academicYearId " +
           "AND s.id NOT IN (" +
           "  SELECT fp.student.id FROM FeePayment fp " +
           "  WHERE fp.school.id = :schoolId " +
           "  AND fp.academicYear.id = :academicYearId " +
           "  AND fp.status = 'ACTIVE'" +
           ") " +
           "ORDER BY s.schoolClass.name, s.rollNumber, s.firstName")
    List<Student> findDefaulters(@Param("schoolId") UUID schoolId,
                                 @Param("academicYearId") UUID academicYearId);
}
