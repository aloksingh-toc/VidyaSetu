package in.vidyasetu.repository;

import in.vidyasetu.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface FeePaymentRepository extends JpaRepository<FeePayment, UUID> {

    @Query("SELECT p FROM FeePayment p " +
           "JOIN FETCH p.feeType ft " +
           "JOIN FETCH p.academicYear ay " +
           "LEFT JOIN FETCH p.collectedBy cb " +
           "WHERE p.school.id = :schoolId AND p.student.id = :studentId " +
           "AND p.academicYear.id = :academicYearId " +
           "ORDER BY p.paymentDate DESC, p.createdAt DESC")
    List<FeePayment> findByStudentAndYear(@Param("schoolId") UUID schoolId,
                                          @Param("studentId") UUID studentId,
                                          @Param("academicYearId") UUID academicYearId);

    @Query("SELECT COALESCE(SUM(p.amountPaid), 0) FROM FeePayment p " +
           "WHERE p.school.id = :schoolId AND p.academicYear.id = :academicYearId " +
           "AND p.status = 'ACTIVE'")
    java.math.BigDecimal totalCollectedForYear(@Param("schoolId") UUID schoolId,
                                               @Param("academicYearId") UUID academicYearId);

    @Query(value = "SELECT COALESCE(SUM(amount_paid), 0) FROM fee_payments " +
                   "WHERE school_id = :schoolId AND status = 'ACTIVE' " +
                   "AND DATE_TRUNC('month', payment_date) = DATE_TRUNC('month', CURRENT_DATE)",
           nativeQuery = true)
    java.math.BigDecimal sumThisMonth(@Param("schoolId") UUID schoolId);
}
