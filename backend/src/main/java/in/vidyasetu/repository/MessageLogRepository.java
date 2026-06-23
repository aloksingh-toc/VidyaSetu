package in.vidyasetu.repository;

import in.vidyasetu.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageLogRepository extends JpaRepository<MessageLog, UUID> {

    List<MessageLog> findBySchool_IdAndStudent_IdOrderByCreatedAtDesc(UUID schoolId, UUID studentId);

    List<MessageLog> findBySchool_IdAndStatusOrderByCreatedAtDesc(UUID schoolId, String status);
}
