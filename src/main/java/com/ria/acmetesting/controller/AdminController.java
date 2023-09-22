package com.ria.acmetesting.controller;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.services.ACMETestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {
    @Autowired
    ACMETestServiceImpl acmeTestService;

    @PostMapping(value = "/addquestion")
    ResponseEntity<Question> addQuestion(@RequestBody Question question){
        return new ResponseEntity<>(acmeTestService.addQuestion(question), HttpStatus.OK);
    }

    @GetMapping(value = "/getquestion")
    ResponseEntity<Question> getQuestion(@RequestParam int questionId){
        return new ResponseEntity<>(acmeTestService.getQuestion(questionId), HttpStatus.OK);
    }

    @GetMapping(value = "/getsubject")
    ResponseEntity<Subject> getSubject(@RequestParam int subjectId){
        return new ResponseEntity<>(acmeTestService.getSubject(subjectId), HttpStatus.OK);
    }

    @PostMapping(value = "/addsubject")
    ResponseEntity<Subject> addSubject(@RequestBody Subject subject){
        return new ResponseEntity<>(acmeTestService.addSubject(subject), HttpStatus.OK);
    }

}
