package in.vidyasetu.repository;

import in.vidyasetu.entity.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExamSubjectRepository extends JpaRepository<ExamSubject, UUID> {

    @Query("SELECT es FROM ExamSubject es " +
           "JOIN FETCH es.schoolClass c " +
           "WHERE es.exam.id = :examId " +
           "ORDER BY c.displayOrder ASC, es.subject ASC")
    List<ExamSubject> findByExamId(@Param("examId") UUID examId);

    List<ExamSubject> findByExam_IdAndSchoolClass_IdOrderBySubjectAsc(UUID examId, UUID classId);
}
