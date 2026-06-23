package in.vidyasetu.repository;

import in.vidyasetu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.school.id = :schoolId AND u.role <> 'OWNER' ORDER BY u.name ASC")
    List<User> findStaffBySchool(@Param("schoolId") UUID schoolId);

    boolean existsBySchool_IdAndPhone(UUID schoolId, String phone);

    @Query("SELECT u FROM User u WHERE u.school.id = :schoolId AND u.role IN ('OWNER', 'ADMIN') AND u.isActive = true")
    List<User> findOwnersAndAdminsBySchool(@Param("schoolId") UUID schoolId);
}
