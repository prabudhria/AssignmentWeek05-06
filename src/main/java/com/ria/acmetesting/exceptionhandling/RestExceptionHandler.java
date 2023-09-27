package com.ria.acmetesting.exceptionhandling;

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
        return new ResponseEntity<>("The exam has ended", HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = StudentNameOrAgeNullException.class)
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
        return new ResponseEntity<>("Question not found", HttpStatus.BAD_REQUEST);
    }
}
