package com.ria.acmetesting.services;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.*;

import java.util.List;

public interface ACMETestService {

    StudentDTO saveStudent(Student student);

    Integer getScore(int studentId);

    List<String> getRemainingSubjects(int studentId) ;

    QuestionDTO getNextQuestion(int studentId, String subject);

    QuestionDTO starTest(int studentId);

    StudentDTO markSubject(int studentId, String subject);

    Question addQuestion(Question question);

    Question getQuestion(int questionId);

    Subject getSubject(int subjectId);

    Subject addSubject(Subject subject);
}
