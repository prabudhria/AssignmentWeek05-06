package com.ria.acmetesting.controller;

import com.ria.acmetesting.dbentities.Question;
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

    @PostMapping(value = "/question")
    ResponseEntity<Question> addQuestion(@RequestBody Question question){
        return new ResponseEntity<>(acmeTestService.addQuestion(question), HttpStatus.CREATED);
    }
    @GetMapping(value = "/question")
    ResponseEntity<Question> getQuestionById(@RequestParam int questionId) {
        return new ResponseEntity<>(acmeTestService.getQuestionById(questionId), HttpStatus.FOUND);
    }
    @GetMapping(value = "/question/statement")
    ResponseEntity<Question> getQuestionByStatement(@RequestParam String questionStatement) {
        return new ResponseEntity<>(acmeTestService.getQuestionByStatement(questionStatement), HttpStatus.FOUND);
    }
    @PutMapping(value = "/question")
    ResponseEntity<Question> updateQuestion(@RequestBody Question question){
        return new ResponseEntity<>(acmeTestService.updateQuestion(question), HttpStatus.OK);
    }
    @DeleteMapping(value = "/question")
    ResponseEntity<Object> deleteQuestion(@RequestParam int questionId) {
        acmeTestService.deleteQuestion(questionId);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/admin/subject")
    ResponseEntity<Subject> addSubject(@RequestBody Subject subject){
        return new ResponseEntity<>(acmeTestService.addSubject(subject), HttpStatus.CREATED);
    }
    @GetMapping(value = "/admin/subject")
    ResponseEntity<Subject> getSubjectById(@RequestParam int subjectId){
        return new ResponseEntity<>(acmeTestService.getSubjectById(subjectId), HttpStatus.FOUND);
    }
    @GetMapping(value = "/admin/subject/name")
    ResponseEntity<Subject> getSubjectByName(@RequestParam String subjectName){
        return new ResponseEntity<>(acmeTestService.getSubjectByName(subjectName), HttpStatus.FOUND);
    }
    @PutMapping(value = "/admin/subject")
    ResponseEntity<Subject> updateSubject(@RequestBody Subject subject){
        return new ResponseEntity<>(acmeTestService.updateSubject(subject), HttpStatus.OK);
    }
    @DeleteMapping(value = "/admin/subject")
    ResponseEntity<Object> deleteSubject(@RequestParam int subjectId){
        acmeTestService.deleteSubject(subjectId);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }

}
