package com.ria.acmetesting.respositories;

import com.ria.acmetesting.dbentities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "select min(q.id) from Question q where q.level=?1 and  q.subject=?2 and q.id > ?3")
    Integer getNextQuestionId(int studentLevel, String currentSubject, int currentQuestionId) ;

    @Query(value = "select max(q.level) from Question q where q.subject = ?1")
    Integer getMaxQuestionLevelOfSubject(String subject);


    Question findQuestionByStatement(String questionStatement);
}
