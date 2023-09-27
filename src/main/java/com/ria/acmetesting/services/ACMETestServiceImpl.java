package com.ria.acmetesting.services;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.*;
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

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
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
    public StudentDTO saveStudent(Student student) {
        if(student.getName() == null || student.getAge()==0) throw new StudentNameOrAgeNullException();
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
    public List<String> getRemainingSubjects(int studentId){
        List<Integer> attemptedSubjects;
        try{
            attemptedSubjects =  scoreRepository.getAttemptedSubjects(studentId);
        }
        catch (Exception e){
            throw new StudentNotFoundException();
        }

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
    public QuestionDTO starTest(int studentId){
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        int startingQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                student.getCurrentSubject(), student.getLevelQuestionId().get(0));
        student.setLevelQuestionId(startingQuestionId, 1);
        student.setTotalQuestionsAttempted(1);
        studentRepository.save(student);
        return new QuestionDTO(questionRepository.getQuestion(startingQuestionId));
    }

    @Override
    @Transactional
    public QuestionDTO getNextQuestion(int studentId, String selectedOption) {
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        if(!selectedOption.equals("a") && !selectedOption.equals("b") && !selectedOption.equals("c") && !selectedOption.equals("d")) {
            throw new WrongOptionSelectedException();
        }
        int nextQuestionId=0;
        if(evaluateAnswer(student, selectedOption)) {
            if(student.getTotalQuestionsAttempted()==5){
                resetStudent(studentId);
                throw new ExamHasEndedException();
            }
            int currentLevelOfStudent=student.getCurrentLevel();

            if(currentLevelOfStudent+1>3) student.setCurrentLevel(1);
            else student.setCurrentLevel(currentLevelOfStudent+1);

            if(currentLevelOfStudent < 1 || currentLevelOfStudent > 3) throw new LevelDoesNotExistException();

            switch (student.getCurrentLevel()) {
                case 1 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelQuestionId().get(0));
                    student.setLevelQuestionId(nextQuestionId, 1);
                }
                case 2 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelQuestionId().get(1));
                    student.setLevelQuestionId(nextQuestionId, 2);
                }
                case 3 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelQuestionId().get(2));
                    student.setLevelQuestionId(nextQuestionId, 3);
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
            student.setLevelQuestionId(nextQuestionId, 1);
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
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        student.setCurrentSubject(subject);
        int subjectId;
        try{
            subjectId = subjectRepository.getIdByName(subject);
        }
        catch (Exception e){
            throw new SubjectNotFoundException();
        }
        scoreRepository.initializeStudentScore(studentId, subjectId, 0);
        return new StudentDTO(studentRepository.save(student));
    }

    @Override
    public Question addQuestion(Question question){
        try{
            return questionRepository.save(question);
        }
        catch (Exception e){
            throw new RequiredQuestionFieldNullException();
        }
    }


    @Override
    public Question getQuestion(int questionId) {
        return questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);
    }

    @Override
    public Subject getSubject(int subjectId){
        return subjectRepository.findById(subjectId).orElseThrow(SubjectNotFoundException::new);
    }

    @Override
    public Subject addSubject(Subject subject) {
        return subjectRepository.save(subject);
    }
}
