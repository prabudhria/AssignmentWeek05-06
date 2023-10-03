package com.ria.acmetesting.services.implementation;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.exceptions.TestNotStartedException;
import com.ria.acmetesting.exceptionhandling.exceptions.*;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.respositories.ScoreRepository;
import com.ria.acmetesting.respositories.StudentRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import com.ria.acmetesting.services.service.ACMETestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class ACMETestServiceImpl implements ACMETestService {
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
        if(student.getName() == null
                || student.getAge()==0
                || student.getUsername()==null) throw new RequiredStudentFieldNullException();
        else if (studentRepository.findUsername(student.getUsername())!=null) throw new UsernameAlreadyTakenException();
        Student initialisedStudent = resetStudent(student);
        return new StudentDTO(studentRepository.save(initialisedStudent));
    }



    @Override
    @Transactional
    public List<String> getRemainingSubjectsOfStudent(String studentUsername){
        studentRepository.findByUsername(studentUsername).orElseThrow(StudentNotFoundException::new);
        List<Integer> attemptedSubjects = scoreRepository.getAttemptedSubjectsByStudent(studentUsername);

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
    public QuestionDTO starTest(String studentUsername){
        Student student = studentRepository.findByUsername(studentUsername).orElseThrow(StudentNotFoundException::new);
        if(student.getCurrentSubject() == null) throw new SubjectNotSelectedException();
        if(checkIfAllowedToContinueTest(student)){
            if(student.getTotalQuestionsAttemptedOfSubject()==0){
                Integer startingQuestionId = questionRepository.getNextQuestionId(student.getCurrentLevel(),
                        student.getCurrentSubject(), student.getAllLevelQuestionIds().get(1));
                if(startingQuestionId==null) throw new NoQuestionsExistForTheSubjectException();

                student.setLevelQuestionId(startingQuestionId, 1);
                studentRepository.save(student);

                return new QuestionDTO(questionRepository.findById(startingQuestionId)
                        .orElseThrow(QuestionNotFoundException::new));
            }
            else throw new TestAlreadyStartedException();
        }
        else throw new TestHasEndedException();
    }

    @Override
    @Transactional
    public void evalutateStudentAnswer(String studentUsername, String selectedOption) {
        Student student = studentRepository.findByUsername(studentUsername).orElseThrow(StudentNotFoundException::new);

        if(student.getAllLevelQuestionIds().get(1)==0) throw new TestNotStartedException();

        if(!selectedOption.equals("a") && !selectedOption.equals("b")
                && !selectedOption.equals("c") && !selectedOption.equals("d")) {
            throw new WrongOptionSelectedException();
        }

        Integer nextQuestionId=0;

        if(checkIfAllowedToContinueTest(student)){
            if(isCorrectlyAnswered(student, selectedOption)){
                nextQuestionId = nextQuestionIdIfCorrectAnswer(student);
            }
            else nextQuestionId = nextQuestionIdIfIncorrectAnswer(student);
        }
        else throw new TestHasEndedException();

        student.setNextQuestionId(nextQuestionId);
        studentRepository.save(student);
    }

    @Transactional
    @Override
    public QuestionDTO getNextQuestion(String studentUsername) {
        Student student = studentRepository.findByUsername(studentUsername).orElseThrow(StudentNotFoundException::new);

        if(student.getAllLevelQuestionIds().get(1)==0) throw new TestNotStartedException();

        if(checkIfAllowedToContinueTest(student)) {
            int nextQuestionId = student.getNextQuestionId();
            return new QuestionDTO(questionRepository.findById(nextQuestionId).orElseThrow(QuestionNotFoundException::new));
        }
        else throw new TestHasEndedException();
    }

    private boolean checkIfAllowedToContinueTest(Student student) {
        return !(student.getTotalQuestionsAttemptedOfSubject() ==
                subjectRepository.getAttemptsAllowedOfSubject(student.getCurrentSubject()));
    }

    @Transactional
    private Integer nextQuestionIdIfIncorrectAnswer(Student student) {
        student.setCurrentLevel(1);
        Integer nextQuestionId = fetchNextQuestionId(1,
                student.getCurrentSubject(), student.getAllLevelQuestionIds().get(1));

        student.setLevelQuestionId(nextQuestionId, 1);
        student.setTotalQuestionsAttemptedOfSubject(student.getTotalQuestionsAttemptedOfSubject()+1);

        studentRepository.save(student);
        return nextQuestionId;
    }

    @Transactional
    private Integer nextQuestionIdIfCorrectAnswer(Student student) {
        int currentLevelOfStudent=student.getCurrentLevel();
        int maxLevel = questionRepository.getMaxQuestionLevelOfSubject(student.getCurrentSubject());


        if(currentLevelOfStudent+1 > maxLevel) student.setCurrentLevel(1);
        else student.setCurrentLevel(currentLevelOfStudent+1);

        currentLevelOfStudent = student.getCurrentLevel();

        if(currentLevelOfStudent < 1 || currentLevelOfStudent > maxLevel) throw new LevelDoesNotExistException();

        Integer nextQuestionId = fetchNextQuestionId(currentLevelOfStudent, student.getCurrentSubject(),
                student.getAllLevelQuestionIds().get(currentLevelOfStudent));

        student.setLevelQuestionId(nextQuestionId, currentLevelOfStudent);
        student.setTotalQuestionsAttemptedOfSubject(student.getTotalQuestionsAttemptedOfSubject()+1);

        studentRepository.save(student);
        return nextQuestionId;
    }

    private Integer fetchNextQuestionId(int currentLevel, String currentSubject, Integer currentLevelQuestionId) {
        Integer nextQuestionId = questionRepository.getNextQuestionId(currentLevel,
                currentSubject, currentLevelQuestionId);
        if(nextQuestionId==null) throw new NoMoreQuestionsForTheSubjectException();
        return nextQuestionId;
    }

    private Student resetStudent(Student student) {
        student.setCurrentLevel(1);
        student.setTotalQuestionsAttemptedOfSubject(0);
        student.setNextQuestionId(0);
        student.setAllLevelQuestionIds(new ArrayList<>(Arrays.asList(-1, 0, 0, 0)));
        return student;
    }

    @Transactional
    private boolean isCorrectlyAnswered(Student student, String selectedOption) {
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
    public void markSubject(String studentUsername, String subject) {
        Student student = studentRepository.findByUsername(studentUsername).orElseThrow(StudentNotFoundException::new);

        if(student.getCurrentSubject()!=null){
            if (checkIfAllowedToContinueTest(student)) throw new PreviousTestNotFinishedException();
        }

        Student resetStudent = resetStudent(student);
        resetStudent.setCurrentSubject(subject);

        Integer subjectId=subjectRepository.getIdByName(subject);

        if(subjectId==null) throw new SubjectNotFoundException();
        else {
            scoreRepository.initializeStudentScore(studentRepository.getIdByUsername(studentUsername),
                    subjectId, 0);
            studentRepository.save(resetStudent);
        }

    }


}
