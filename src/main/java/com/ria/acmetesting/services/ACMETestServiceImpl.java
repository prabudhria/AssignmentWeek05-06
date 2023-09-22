package com.ria.acmetesting.services;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.dtos.QuestionResponseDTO;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.respositories.ScoreRepository;
import com.ria.acmetesting.respositories.StudentRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    public Student saveStudent(Student student){
        studentRepository.save(student);
        resetStudent(student.getId());
        List<Integer> subjects = subjectRepository.getAllSubjects();
        for(Integer subjectId : subjects) scoreRepository.initializeStudentScore(student.getId(), subjectId, 0);
        return studentRepository.findById(student.getId()).get();
    }



    @Override
    public Integer getScore(int studentId) {
        return null;
    }

    @Override
    @Transactional
    public List<String> getRemainingSubjects(int studentId) {
        List<Integer> remainingSubjectIds =  scoreRepository.getRemainingSubjects(studentId);
        List<String> remainingSubjectNames = new ArrayList<>();
        for(Integer subjectId: remainingSubjectIds) {
            remainingSubjectNames.add(subjectRepository.findById(subjectId).get().getName());
        }
        return remainingSubjectNames;
    }

    @Override
    @Transactional
    public Question starTest(int studentId) {
        Student student = studentRepository.findById(studentId).get();
        int startingQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                student.getCurrentSubject(), student.getLevelOneQuestionId());
        student.setLevelOneQuestionId(startingQuestionId);
        student.setTotalQuestionsAttempted(1);
        studentRepository.save(student);

        return questionRepository.getQuestion(startingQuestionId);
    }

    @Override
    @Transactional
    public Question getNextQuestion(int studentId, String selectedOption) {
        Student student = studentRepository.findById(studentId).get();
        int nextQuestionId=0;
        if(evaluateAnswer(student, selectedOption)) {
            if(student.getTotalQuestionsAttempted()==5) return resetStudent(studentId);
            int currentLevelOfStudent=student.getCurrentLevel();
            if(currentLevelOfStudent+1>3) student.setCurrentLevel(1);
            else student.setCurrentLevel(currentLevelOfStudent+1);
            switch (student.getCurrentLevel()) {
                case 1 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelOneQuestionId());
                    student.setLevelOneQuestionId(nextQuestionId);
                }
                case 2 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelTwoQuestionId());
                    student.setLevelTwoQuestionId(nextQuestionId);
                }
                case 3 -> {
                    nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                            student.getCurrentSubject(), student.getLevelThreeQuestionId());
                    student.setLevelThreeQuestionId(nextQuestionId);
                }
            }

        }
        else{
            if(student.getTotalQuestionsAttempted()==5) return resetStudent(studentId);
            student.setCurrentLevel(1);
            nextQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                    student.getCurrentSubject(), student.getLevelOneQuestionId());
            student.setLevelOneQuestionId(nextQuestionId);
        }
        student.setTotalQuestionsAttempted(student.getTotalQuestionsAttempted()+1);
        studentRepository.save(student);
        return questionRepository.findById(nextQuestionId).get();
//        Question question = questionRepository.findById(nextQuestionId).get();
//        return new QuestionResponseDTO(question.getStatement(), question.getOptions());
    }

    @Transactional
    private Question resetStudent(int studentId) {
        Student student = studentRepository.findById(studentId).get();
        student.setCurrentSubject(null);
        student.setCurrentLevel(1);
        student.setTotalQuestionsAttempted(0);
        student.setLevelOneQuestionId(0);
        student.setLevelTwoQuestionId(0);
        student.setLevelThreeQuestionId(0);
        studentRepository.save(student);
        return null;
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
        return switch (currentQuestionLevelOfStudent) {
            case 1 -> student.getLevelOneQuestionId();
            case 2 -> student.getLevelTwoQuestionId();
            case 3 -> student.getLevelThreeQuestionId();
            default -> 0;
        };
    }


    @Override
    @Transactional
    public Student markSubject(int studentId, String subject) {
        Student student = studentRepository.findById(studentId).get();
        student.setCurrentSubject(subject);
        return studentRepository.save(student);
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
