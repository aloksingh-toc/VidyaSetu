package in.vidyasetu.repository;

import in.vidyasetu.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, UUID> {

    @Query("SELECT fs FROM FeeStructure fs " +
           "JOIN FETCH fs.schoolClass c " +
           "JOIN FETCH fs.feeType ft " +
           "JOIN FETCH fs.academicYear ay " +
           "WHERE fs.school.id = :schoolId AND fs.academicYear.id = :academicYearId " +
           "ORDER BY c.displayOrder ASC, ft.name ASC")
    List<FeeStructure> findBySchoolAndYear(@Param("schoolId") UUID schoolId,
                                           @Param("academicYearId") UUID academicYearId);

    @Query("SELECT fs FROM FeeStructure fs " +
           "JOIN FETCH fs.feeType ft " +
           "JOIN FETCH fs.academicYear ay " +
           "WHERE fs.school.id = :schoolId AND fs.schoolClass.id = :classId " +
           "ORDER BY ft.name ASC")
    List<FeeStructure> findBySchoolAndClass(@Param("schoolId") UUID schoolId,
                                            @Param("classId") UUID classId);

    boolean existsBySchoolClass_IdAndFeeType_IdAndAcademicYear_Id(
            UUID classId, UUID feeTypeId, UUID academicYearId);
}
