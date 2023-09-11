package com.ria.acmetesting.Services;

import com.ria.acmetesting.Entity.Question;
import com.ria.acmetesting.Entity.Student;

import java.util.ArrayList;

public interface ACMETestService {
    Student saveStudent(Student student);

    Integer getScore(Student student);

    ArrayList<Boolean> getRemainingSubject(Student student);

    Question getNexotQuestion(Student student);
}
