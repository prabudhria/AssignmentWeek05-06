package com.ria.acmetesting.respositories;

import com.ria.acmetesting.dbentities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query(value = "select q from Question q where q.id = ?1")
    Question getQuestion(int questionId);


//    @Query(value = "select q from Question q where q.id=(select min(q1.id) from Question q1 where q1.subject=?1 and ) ")
//    Question startTest(String currentSubject);

    @Query(value = "select min(q.id) from Question q where q.level=?1 and  q.subject=?2 and q.id > ?3")
    int getNextQuestionId(int studentLevel, String currentSubject, int currentQuestionId);
}
