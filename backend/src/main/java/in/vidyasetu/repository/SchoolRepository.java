package in.vidyasetu.repository;

import in.vidyasetu.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchoolRepository extends JpaRepository<School, UUID> {
    Optional<School> findByEmail(String email);
    boolean existsByEmail(String email);
}
