package com.ria.acmetesting.respositories;

import com.ria.acmetesting.dbentities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
//    @Modifying
//    @Query(value = "update Student s set s.currentSubject = ?1 where s.id = ?2")
//    void markSubject(String subject, int studentId);

    @Modifying
//    @Query(value = "update Student s set s.currentLevel=1, s.levelQuestionId=ARRAY[0, 0, 0] where s.id=?1")
    @Query(value = "update Student set current_level=1, level_question_id='{0, 0, 0}' where id=?1", nativeQuery = true)
    void initializeQuestionLevels(int studentId);
}
