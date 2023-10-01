package com.ria.acmetesting.respositories;

import com.ria.acmetesting.dbentities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
//    @Modifying
//    @Query(value = "update Student set current_level=1," +
//            " all_level_question_ids='{-1, 0, 0, 0}' where id=?1", nativeQuery = true)
//    void initializeQuestionLevelIds(int studentId);
    Optional<Student> findByUsername(String username);

    @Query(value = "select s.id from Student s where s.username = ?1")
    int getIdByUsername(String username);

    @Query(value = "select s.username from Student s where s.username = ?1")
    String findUsername(String username);
}
