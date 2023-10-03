package com.ria.acmetesting.services.implementation;

import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.exceptionhandling.exceptions.StudentNotFoundException;
import com.ria.acmetesting.respositories.ScoreRepository;
import com.ria.acmetesting.respositories.StudentRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import com.ria.acmetesting.services.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {
    @Autowired
    ScoreRepository scoreRepository;
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Override
    public Integer getScore(String studentUsername) {
        Student student = studentRepository.findByUsername(studentUsername).orElseThrow(StudentNotFoundException::new);
        if(student.getTotalQuestionsAttemptedOfSubject()
                ==subjectRepository.getAttemptsAllowedOfSubject(student.getCurrentSubject())){

        }
        Integer totalScore = scoreRepository.getTotalScore(studentRepository.getIdByUsername(studentUsername));
        if(totalScore==null) return 0;
        else return totalScore;
    }
}
