package in.vidyasetu.repository;

import in.vidyasetu.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {

    /** Primary parent first, then others */
    List<Parent> findByStudent_IdOrderByIsPrimaryDescCreatedAtAsc(UUID studentId);

    /** All parents of a school (for WhatsApp broadcast) */
    List<Parent> findBySchool_IdAndWhatsappOptOutFalse(UUID schoolId);

    /** All opted-in parents for a specific student */
    List<Parent> findByStudent_IdAndWhatsappOptOutFalse(UUID studentId);

    Optional<Parent> findByStudent_IdAndIsPrimaryTrue(UUID studentId);

    boolean existsByStudent_IdAndRelation(UUID studentId, String relation);

    long countBySchool_Id(UUID schoolId);
}
