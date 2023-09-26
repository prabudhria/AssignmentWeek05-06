package com.ria.acmetesting.respositories;

import com.ria.acmetesting.dbentities.Score;
import com.ria.acmetesting.key.ScoreKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<Score, ScoreKey> {
    @Modifying
    @Query(value = "insert into Score (student_id, subject_id, score) values (?, ?, ?)", nativeQuery = true)
    void initializeStudentScore(int studentId, int subjectId, int score);

    @Query(value = "select s.subject.id from Score s where s.student.id=?1")
//    @Query(value = "select subject_name from score where student_id=? and score = ?", nativeQuery = true)
//    @Query(value = "select s1.subject.name from Score s1 join Subject s2 on s2.name=s1.subject.name where s1.student.id=?1 and s1.score=?2")
    List<Integer> getAttemptedSubjects(int studentId);

    @Modifying
    @Query(value = "update score set score = score+? where student_id=? and subject_id=?", nativeQuery = true)
    void updateScore(int marksAwarded, int studentId, int subjectId);

//    @Query(value = "select sum(s.score) from Score s where s.studentId = ?1")
    @Query(value = "select sum(score) from score where student_id = ?", nativeQuery = true)
    Integer getTotalScore(int studentId);

//    select s2_0.name from score s1_0 join subject s2_0 on s2_0.id=s1_0.subject_name where s1_0.student_id=? and s1_0.score=?
}
