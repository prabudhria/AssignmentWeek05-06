package com.ria.acmetesting.Services;

import com.ria.acmetesting.Entity.Question;
import com.ria.acmetesting.Entity.Student;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ACMETestSerivceImpl implements ACMETestService{
    @Override
    public Student saveStudent(Student student) {
        return null;
    }

    @Override
    public Integer getScore(Student student) {
        return null;
    }

    @Override
    public ArrayList<Boolean> getRemainingSubject(Student student) {
        return null;
    }

    @Override
    public Question getNexotQuestion(Student student) {
        return null;
    }
}
