package in.vidyasetu.repository;

import in.vidyasetu.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.student s " +
           "LEFT JOIN FETCH a.markedBy mb " +
           "WHERE a.school.id = :schoolId AND a.schoolClass.id = :classId " +
           "AND a.date = :date " +
           "ORDER BY s.firstName ASC, s.lastName ASC")
    List<Attendance> findByClassAndDate(@Param("schoolId") UUID schoolId,
                                        @Param("classId") UUID classId,
                                        @Param("date") LocalDate date);

    Optional<Attendance> findByStudent_IdAndDate(UUID studentId, LocalDate date);

    @Query("SELECT a FROM Attendance a " +
           "WHERE a.school.id = :schoolId AND a.student.id = :studentId " +
           "AND a.academicYear.id = :academicYearId " +
           "ORDER BY a.date ASC")
    List<Attendance> findByStudentAndYear(@Param("schoolId") UUID schoolId,
                                          @Param("studentId") UUID studentId,
                                          @Param("academicYearId") UUID academicYearId);

    long countBySchool_IdAndStudent_IdAndAcademicYear_IdAndStatus(
            UUID schoolId, UUID studentId, UUID academicYearId, String status);

    long countBySchool_IdAndDateAndStatus(UUID schoolId, LocalDate date, String status);

    long countBySchool_IdAndDate(UUID schoolId, LocalDate date);
}
