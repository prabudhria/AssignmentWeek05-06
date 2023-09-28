package com.ria.acmetesting.respositories;

import com.ria.acmetesting.dbentities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Modifying
    @Query(value = "update Student set current_level=1," +
            " all_level_question_ids='{-1, 0, 0, 0}' where id=?1", nativeQuery = true)
    void initializeQuestionLevelIds(int studentId);
}
