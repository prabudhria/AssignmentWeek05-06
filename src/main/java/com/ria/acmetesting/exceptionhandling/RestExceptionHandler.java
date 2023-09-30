package com.ria.acmetesting.exceptionhandling;

import com.ria.acmetesting.exceptionhandling.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = ExamHasEndedException.class)
    public ResponseEntity<Object> examEndedExceptionHandler(){
        return new ResponseEntity<>("The exam has ended, please select next subject or finish", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = RequiredStudentFieldNullException.class)
    public ResponseEntity<Object> studentNameOrAgeNullExceptionHandler(){
        return new ResponseEntity<>("The Name or Age cannot be null", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = StudentNotFoundException.class)
    public ResponseEntity<Object> studentNotFoundExceptionHandler(){
        return new ResponseEntity<>("Student not found", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value =WrongOptionSelectedException.class)
    public ResponseEntity<Object> wrongOptionSelectedExceptionHandler(){
        return new ResponseEntity<>("Wrong option selected, please select A, B, C or D", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value =SubjectNotFoundException.class)
    public ResponseEntity<Object> subjectNotFoundExceptionHandler(){
        return new ResponseEntity<>("Subject requested does not exist", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value =RequiredQuestionFieldNullException.class)
    public ResponseEntity<Object> requiredQuestionFieldNullExceptionHandler(){
        return new ResponseEntity<>("Question statement, subject, options and answer cannot be null", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = LevelDoesNotExistException.class)
    public ResponseEntity<Object> levelDoesNotExistExceptionHandler(){
        return new ResponseEntity<>("Requested question level does not exist", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = QuestionNotFoundException.class)
    public ResponseEntity<Object> questionNotFoundExceptionHandler(){
        return new ResponseEntity<>("Question not found", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = RequiredSubjectFieldNullException.class)
    public ResponseEntity<Object> requiredSubjectFieldNullExceptionHandler(){
        return new ResponseEntity<>("Subject name and allowed-attempts cannot be null", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = NoMoreQuestionsForTheSubjectException.class)
    public ResponseEntity<Object> noMoreQuestionsForTheSubjectExceptionHandler(){
        return new ResponseEntity<>("can't find more question for the subject", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = NoQuestionsExistForTheSubjectException.class)
    public ResponseEntity<Object> noQuestionsExistForTheSubjectExceptionHandler(){
        return new ResponseEntity<>("can't find question for the subject", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = WrongSubjectRequestedException.class)
    public ResponseEntity<Object> wrongSubjectRequestedExceptionHandler(){
        return new ResponseEntity<>("Wrong subject requested", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = TestAlreadyStartedException.class)
    public ResponseEntity<Object> testAlreadyStartedExceptionHandler(){
        return new ResponseEntity<>("The test has already started", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = SubjectNotSelectedException.class)
    public ResponseEntity<Object> subjectNotSelectedExceptionHandler(){
        return new ResponseEntity<>("Select the subject first", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = TestNotStartedException.class)
    public ResponseEntity<Object> testNotStartedExceptionHandler(){
        return new ResponseEntity<>("Kindly start the test first", HttpStatus.BAD_REQUEST);
    }

}
