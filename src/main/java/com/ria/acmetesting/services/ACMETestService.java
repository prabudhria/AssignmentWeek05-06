package com.ria.acmetesting.services;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;

import java.util.List;

public interface ACMETestService {

    Student saveStudent(Student student);

    Integer getScore(int studentId);

    List<String> getRemainingSubjects(int studentId);

    Question getNextQuestion(int studentId, String subject);

    Question starTest(int studentId);

    Student markSubject(int studentId, String subject);

    Question addQuestion(Question question);

    Question getQuestion(int questionId);

    Subject getSubject(int subjectId);

    Subject addSubject(Subject subject);
}
