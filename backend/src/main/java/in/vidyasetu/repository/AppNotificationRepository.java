package in.vidyasetu.repository;

import in.vidyasetu.entity.AppNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AppNotificationRepository extends JpaRepository<AppNotification, UUID> {

    Page<AppNotification> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUser_IdAndIsReadFalse(UUID userId);

    @Modifying
    @Query("UPDATE AppNotification a SET a.isRead = true WHERE a.user.id = :userId AND a.isRead = false")
    void markAllRead(@Param("userId") UUID userId);
}
