package com.ria.acmetesting.respositories;

import com.ria.acmetesting.dbentities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    @Query(value = "select s from Subject s WHERE s.id = ?1")
    Subject getSubject(int questionId);

    @Query(value = "select s.id from Subject s")
    List<Integer> getAllSubjectIds();

    @Query(value = "select s.id from Subject s where s.name=?1")
    Integer getIdByName(String subject);

    @Query(value = "select s.allowedAttempts from Subject s where s.name = ?1")
    int getAttemptsAllowedOfSubject(String currentSubject);

    Optional<Subject> findSubjectByName(String subjectName);
}
