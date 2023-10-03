package com.ria.acmetesting.services.service;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;

import java.util.List;

public interface ACMETestService {

    StudentDTO saveStudent(Student student);

    List<String> getRemainingSubjectsOfStudent(String studentUsername) ;

    void evalutateStudentAnswer(String studentUsername, String selectedOption);

    QuestionDTO starTest(String studentUsername);

    void markSubject(String studentUsername, String subject);


    QuestionDTO getNextQuestion(String studentUsername);
}
