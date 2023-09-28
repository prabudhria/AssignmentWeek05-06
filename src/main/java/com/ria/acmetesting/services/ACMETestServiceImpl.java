package com.ria.acmetesting.services;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.exceptions.*;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.respositories.ScoreRepository;
import com.ria.acmetesting.respositories.StudentRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
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
        if(student.getName() == null || student.getAge()==0) throw new RequiredStudentFieldNullException();
        studentRepository.save(student);
        resetStudent(student.getId());
        log.info(student.toString());
        return new StudentDTO(studentRepository.findById(student.getId())
                .orElseThrow(StudentNotFoundException::new));
    }



    @Override
    public Integer getScore(int studentId) {
        return scoreRepository.getTotalScore(studentId);
    }

    @Override
    @Transactional
    public List<String> getRemainingSubjectsOfStudent(int studentId){
        studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        List<Integer> attemptedSubjects = scoreRepository.getAttemptedSubjectsByStudent(studentId);

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
            if(flag) remainingSubjectNames.add(subjectRepository.findById(subjectId)
                    .orElseThrow(SubjectNotFoundException::new).getName());
        }
        return remainingSubjectNames;
    }

    @Override
    @Transactional
    public QuestionDTO starTest(int studentId, String subject){
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        if(student.getCurrentSubject() == null) {
            if (scoreRepository.isAttemptedSubject(subjectRepository.getIdByName(subject), studentId) == null)
                throw new SubjectNotSelectedException();
            else throw new ExamHasEndedException();
        }
        if(student.getTotalQuestionsAttemptedOfSubject() != 0) throw new TestAlreadyStartedException();
        if(!student.getCurrentSubject().equals(subject)) throw new WrongSubjectRequestedException();

        //always starting test with level 1
        Integer startingQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                student.getCurrentSubject(), student.getAllLevelQuestionIds().get(1));
        if(startingQuestionId==null) throw new NoQuestionsExistForTheSubjectException();

        student.setAllLevelQuestionIds(startingQuestionId, 1);
        student.setTotalQuestionsAttemptedOfSubject(1);

        studentRepository.save(student);
        return new QuestionDTO(questionRepository.findById(startingQuestionId)
                .orElseThrow(QuestionNotFoundException::new));
    }

    @Override
    @Transactional
    public QuestionDTO getNextQuestion(int studentId, String selectedOption, String subject) {
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        if(!student.getCurrentSubject().equals(subject)) throw new WrongSubjectRequestedException();


        if(!selectedOption.equals("a") && !selectedOption.equals("b")
                && !selectedOption.equals("c") && !selectedOption.equals("d")) {
            throw new WrongOptionSelectedException();
        }

        Integer nextQuestionId=0;
        int totalQuestionsAttemptedOfSubject = student.getTotalQuestionsAttemptedOfSubject();
        String currentSubjectOfStudent = student.getCurrentSubject();
        int maxLevel = questionRepository.getMaxQuestionLevelOfSubject(currentSubjectOfStudent);
        int currentLevelOfStudent=student.getCurrentLevel();
        int totalAllowedAttemptsOfSubject = subjectRepository.getAttemptsAllowedOfSubject(currentSubjectOfStudent);

        if(evaluateAnswer(student, selectedOption)) {
            if(totalQuestionsAttemptedOfSubject==totalAllowedAttemptsOfSubject){
                resetStudent(studentId);
                return null;
            }

            if(currentLevelOfStudent+1 > maxLevel) student.setCurrentLevel(1);
            else student.setCurrentLevel(currentLevelOfStudent+1);

            currentLevelOfStudent = student.getCurrentLevel();

            if(currentLevelOfStudent < 1 || currentLevelOfStudent > maxLevel) throw new LevelDoesNotExistException();

            nextQuestionId = questionRepository.getNextQuestionId(currentLevelOfStudent,
                    currentSubjectOfStudent, student.getAllLevelQuestionIds().get(currentLevelOfStudent));
            if(nextQuestionId==null) throw new NoMoreQuestionsForTheSubjectException();
            student.setAllLevelQuestionIds(nextQuestionId, currentLevelOfStudent);
        }
        else{
            if(totalQuestionsAttemptedOfSubject==totalAllowedAttemptsOfSubject){
                resetStudent(studentId);
                return null;
            }
            student.setCurrentLevel(1);
            nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                    student.getCurrentSubject(), student.getAllLevelQuestionIds().get(1));
            if(nextQuestionId==null) throw new NoMoreQuestionsForTheSubjectException();
            student.setAllLevelQuestionIds(nextQuestionId, 1);
        }

        student.setTotalQuestionsAttemptedOfSubject(student.getTotalQuestionsAttemptedOfSubject()+1);
        studentRepository.save(student);
        return new QuestionDTO(questionRepository.findById(nextQuestionId)
                .orElseThrow(QuestionNotFoundException::new));
    }

    @Transactional
    private void resetStudent(int studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        student.setCurrentSubject(null);
        student.setCurrentLevel(1);
        student.setTotalQuestionsAttemptedOfSubject(0);
        studentRepository.initializeQuestionLevelIds(student.getId());
        studentRepository.save(student);
    }

    @Transactional
    private boolean evaluateAnswer(Student student, String selectedOption) {
        Question question = questionRepository.findById(getQuestionAnsweredId(student))
                .orElseThrow(QuestionNotFoundException::new);
        if(selectedOption.equals(question.getAnswer())){
            int subjectId=subjectRepository.getIdByName(student.getCurrentSubject());
            scoreRepository.updateScore(student.getCurrentLevel(), student.getId(), subjectId);
            return true;
        }
        return false;
    }

    private int getQuestionAnsweredId(Student student) {
        int currentQuestionLevelOfStudent=student.getCurrentLevel();
        return student.getAllLevelQuestionIds().get(currentQuestionLevelOfStudent);
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
        if(question.getSubject()==null || question.getLevel()==0 || question.getOptions()==null
                || question.getAnswer()==null) throw new RequiredQuestionFieldNullException();
        return questionRepository.save(question);
    }


    @Override
    public Question getQuestionById(int questionId) {
        return questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);
    }

    @Override
    public Question getQuestionByStatement(String questionStatement) {
        Question question = questionRepository.findQuestionByStatement(questionStatement);
        if(question==null) throw new QuestionNotFoundException();
        else return question;
    }

    @Override
    public Question updateQuestion(Question question) {
        if(question.getId() == 0 || question.getSubject()==null
                || question.getLevel()==0 || question.getOptions()==null
                || question.getAnswer()==null) throw new RequiredQuestionFieldNullException();
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(int questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);
        questionRepository.deleteById(questionId);

    }

    @Override
    public Subject addSubject(Subject subject) {
        if(subject.getName()==null || subject.getAllowedAttempts()==0) throw new RequiredSubjectFieldNullException();
        return subjectRepository.save(subject);
    }

    @Override
    public Subject getSubjectById(int subjectId){
        return subjectRepository.findById(subjectId).orElseThrow(SubjectNotFoundException::new);
    }

    @Override
    public Subject getSubjectByName(String subjectName) {
        Subject subject = subjectRepository.findSubjectByName(subjectName);
        if(subject==null) throw new SubjectNotFoundException();
        else return subject;
    }

    @Override
    public Subject updateSubject(Subject subject) {
        if(subject.getId()==0 || subject.getName()==null
                || subject.getAllowedAttempts()==0) throw new RequiredSubjectFieldNullException();
        return subjectRepository.save(subject);
    }

    @Override
    public void deleteSubject(int subjectId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(SubjectNotFoundException::new);
        subjectRepository.deleteById(subjectId);
    }
}
