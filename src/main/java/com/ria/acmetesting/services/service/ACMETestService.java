package com.ria.acmetesting.services;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;

import java.util.List;

public interface ACMETestService {

    StudentDTO saveStudent(Student student);

    Integer getScore(String studentUsername);

    List<String> getRemainingSubjectsOfStudent(String studentUsername) ;

    void evalutateStudentAnswer(String studentUsername, String selectedOption);

    QuestionDTO starTest(String studentUsername);

    void markSubject(String studentUsername, String subject);

    Question addQuestion(Question question);

    Question getQuestionById(int questionId);

    Subject getSubjectById(int subjectId);

    Subject addSubject(Subject subject);

    Question getQuestionByStatement(String questionStatement);

    Question updateQuestion(Question question);

    void deleteQuestion(int questionId);

    Subject getSubjectByName(String subjectName);

    Subject updateSubject(Subject subject);

    void deleteSubject(int subjectId);

    QuestionDTO getNextQuestion(String studentUsername);
}
