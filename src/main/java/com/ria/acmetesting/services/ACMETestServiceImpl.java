package com.ria.acmetesting.services;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.ExamHasEndedException;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.respositories.ScoreRepository;
import com.ria.acmetesting.respositories.StudentRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ACMETestServiceImpl implements ACMETestService{
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    ScoreRepository scoreRepository;

    @Override
    @Transactional
    public StudentDTO saveStudent(Student student){
        studentRepository.save(student);
        resetStudent(student.getId());
        return new StudentDTO(studentRepository.findById(student.getId()).get());
    }



    @Override
    public Integer getScore(int studentId) {
        return scoreRepository.getTotalScore(studentId);
    }

    @Override
    @Transactional
    public List<String> getRemainingSubjects(int studentId) {
        List<Integer> attemptedSubjects =  scoreRepository.getAttemptedSubjects(studentId);
        List<String> remainingSubjectNames = new ArrayList<>();
        List<Integer> allSubjects = subjectRepository.getAllSubjectIds();
        for(Integer subjectId: allSubjects) {
            boolean flag=true;
            for(Integer studentSubjectId : attemptedSubjects){
                if(subjectId.equals(studentSubjectId)) {
                    flag = false;
                    break;
                }
            }
            if(flag) remainingSubjectNames.add(subjectRepository.findById(subjectId).get().getName());
        }
        return remainingSubjectNames;
    }

    @Override
    @Transactional
    public QuestionDTO starTest(int studentId) {
        Student student = studentRepository.findById(studentId).get();

        int startingQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                student.getCurrentSubject(), student.getLevelQuestionId().get(0));
        student.setLevelOneQuestionId(startingQuestionId);
        student.setTotalQuestionsAttempted(1);
        studentRepository.save(student);
        return new QuestionDTO(questionRepository.getQuestion(startingQuestionId));
    }

    @Override
    @Transactional
    public QuestionDTO getNextQuestion(int studentId, String selectedOption) throws ExamHasEndedException {
        Student student = studentRepository.findById(studentId).get();
        int nextQuestionId=0;
        if(evaluateAnswer(student, selectedOption)) {
            if(student.getTotalQuestionsAttempted()==5){
                resetStudent(studentId);
                throw new ExamHasEndedException();
//                return null;
            }
            int currentLevelOfStudent=student.getCurrentLevel();
            if(currentLevelOfStudent+1>3) student.setCurrentLevel(1);
            else student.setCurrentLevel(currentLevelOfStudent+1);
            switch (student.getCurrentLevel()) {
                case 1 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelQuestionId().get(0));
                    student.setLevelOneQuestionId(nextQuestionId);
                }
                case 2 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelQuestionId().get(1));
                    student.setLevelTwoQuestionId(nextQuestionId);
                }
                case 3 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelQuestionId().get(2));
                    student.setLevelThreeQuestionId(nextQuestionId);
                }
            }

        }
        else{
            if(student.getTotalQuestionsAttempted()==5){
                resetStudent(studentId);
                throw new ExamHasEndedException();
            }
            student.setCurrentLevel(1);
            nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                    student.getCurrentSubject(), student.getLevelQuestionId().get(0));
            student.setLevelOneQuestionId(nextQuestionId);
        }
        student.setTotalQuestionsAttempted(student.getTotalQuestionsAttempted()+1);
        studentRepository.save(student);
        return new QuestionDTO(questionRepository.findById(nextQuestionId).get());
    }

    @Transactional
    private void resetStudent(int studentId) {
        Student student = studentRepository.findById(studentId).get();
        student.setCurrentSubject(null);
        student.setCurrentLevel(1);
        student.setTotalQuestionsAttempted(0);
        studentRepository.initializeQuestionLevels(studentId);
        studentRepository.save(student);
    }

    @Transactional
    private boolean evaluateAnswer(Student student, String selectedOption) {
        Question question = questionRepository.findById(getQuestionAnsweredId(student)).get();
        if(selectedOption.equals(question.getAnswer())){
            int subjectId=subjectRepository.getIdByName(student.getCurrentSubject());
            scoreRepository.updateScore(student.getCurrentLevel(), student.getId(), subjectId);
            return true;
        }
        return false;
    }

    private int getQuestionAnsweredId(Student student) {
        int currentQuestionLevelOfStudent=student.getCurrentLevel();
        return student.getLevelQuestionId().get(currentQuestionLevelOfStudent-1);
    }


    @Override
    @Transactional
    public StudentDTO markSubject(int studentId, String subject) {
        Student student = studentRepository.findById(studentId).get();
        student.setCurrentSubject(subject);
        scoreRepository.initializeStudentScore(studentId, subjectRepository.getIdByName(subject), 0);
        return new StudentDTO(studentRepository.save(student));
    }

    @Override
    public Question addQuestion(Question question) {
        return questionRepository.save(question);
    }


    @Override
    public Question getQuestion(@Param("questionId")int questionId){
        return questionRepository.getQuestion(questionId);
    }

    @Override
    public Subject getSubject(int subjectId) {
        return subjectRepository.getSubject(subjectId);
    }

    @Override
    public Subject addSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}
