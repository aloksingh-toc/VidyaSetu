package in.vidyasetu.repository;

import in.vidyasetu.entity.ExamMark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExamMarkRepository extends JpaRepository<ExamMark, UUID> {

    @Query("SELECT em FROM ExamMark em " +
           "JOIN FETCH em.student s " +
           "LEFT JOIN FETCH em.enteredBy eb " +
           "WHERE em.examSubject.id = :examSubjectId " +
           "ORDER BY s.firstName ASC, s.lastName ASC")
    List<ExamMark> findByExamSubjectId(@Param("examSubjectId") UUID examSubjectId);

    @Query("SELECT em FROM ExamMark em " +
           "JOIN FETCH em.examSubject es " +
           "WHERE em.student.id = :studentId AND es.exam.id = :examId " +
           "ORDER BY es.subject ASC")
    List<ExamMark> findByStudentAndExam(@Param("studentId") UUID studentId,
                                        @Param("examId") UUID examId);

    Optional<ExamMark> findByStudent_IdAndExamSubject_Id(UUID studentId, UUID examSubjectId);
}
