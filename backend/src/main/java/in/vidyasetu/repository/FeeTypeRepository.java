package in.vidyasetu.repository;

import in.vidyasetu.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeeTypeRepository extends JpaRepository<FeeType, UUID> {

    List<FeeType> findBySchool_IdAndIsActiveTrueOrderByNameAsc(UUID schoolId);

    List<FeeType> findBySchool_IdOrderByNameAsc(UUID schoolId);

    boolean existsBySchool_IdAndNameIgnoreCase(UUID schoolId, String name);
}
