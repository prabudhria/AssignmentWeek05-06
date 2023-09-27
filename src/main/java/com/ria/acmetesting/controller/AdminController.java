package com.ria.acmetesting.controller;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.exceptionhandling.QuestionNotFoundException;
import com.ria.acmetesting.exceptionhandling.RequiredQuestionFieldNullException;
import com.ria.acmetesting.exceptionhandling.SubjectNotFoundException;
import com.ria.acmetesting.services.ACMETestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {
    @Autowired
    ACMETestServiceImpl acmeTestService;

    @PostMapping(value = "/question")
    ResponseEntity<Question> addQuestion(@RequestBody Question question){
        return new ResponseEntity<>(acmeTestService.addQuestion(question), HttpStatus.OK);
    }

    @GetMapping(value = "/question")
    ResponseEntity<Question> getQuestion(@RequestParam int questionId) {
        return new ResponseEntity<>(acmeTestService.getQuestion(questionId), HttpStatus.OK);
    }

    @GetMapping(value = "/admin/subject")
    ResponseEntity<Subject> getSubject(@RequestParam int subjectId){
        return new ResponseEntity<>(acmeTestService.getSubject(subjectId), HttpStatus.OK);
    }

    @PostMapping(value = "/admin/subject")
    ResponseEntity<Subject> addSubject(@RequestBody Subject subject){
        return new ResponseEntity<>(acmeTestService.addSubject(subject), HttpStatus.OK);
    }

}
