package in.vidyasetu.repository;

import in.vidyasetu.entity.FeeConcession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FeeConcessionRepository extends JpaRepository<FeeConcession, UUID> {

    @Query("SELECT fc FROM FeeConcession fc " +
           "JOIN FETCH fc.feeType ft " +
           "JOIN FETCH fc.academicYear ay " +
           "LEFT JOIN FETCH fc.approvedBy ab " +
           "WHERE fc.school.id = :schoolId AND fc.student.id = :studentId " +
           "AND fc.academicYear.id = :academicYearId " +
           "ORDER BY ft.name ASC")
    List<FeeConcession> findByStudentAndYear(@Param("schoolId") UUID schoolId,
                                             @Param("studentId") UUID studentId,
                                             @Param("academicYearId") UUID academicYearId);

    boolean existsByStudent_IdAndFeeType_IdAndAcademicYear_IdAndIsActiveTrue(
            UUID studentId, UUID feeTypeId, UUID academicYearId);
}
