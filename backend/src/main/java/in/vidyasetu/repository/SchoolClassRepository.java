package in.vidyasetu.repository;

import in.vidyasetu.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, UUID> {

    List<SchoolClass> findBySchool_IdAndAcademicYear_IdOrderByDisplayOrderAsc(
            UUID schoolId, UUID academicYearId);

    List<SchoolClass> findBySchool_IdOrderByDisplayOrderAsc(UUID schoolId);

    boolean existsBySchool_IdAndAcademicYear_IdAndNameAndSection(
            UUID schoolId, UUID academicYearId, String name, String section);

    long countBySchool_Id(UUID schoolId);
}
