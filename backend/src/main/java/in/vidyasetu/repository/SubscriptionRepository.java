package in.vidyasetu.repository;

import in.vidyasetu.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    Optional<Subscription> findFirstBySchool_IdOrderByCreatedAtDesc(UUID schoolId);

    List<Subscription> findBySchool_IdOrderByCreatedAtDesc(UUID schoolId);

    Optional<Subscription> findByRazorpaySubId(String razorpaySubId);
}
