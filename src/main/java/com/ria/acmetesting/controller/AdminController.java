package com.ria.acmetesting.controller;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.services.implementation.QuestionServiceImpl;
import com.ria.acmetesting.services.implementation.SubjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
public class AdminController {
    @Autowired
    SubjectServiceImpl subjectService;

    @Autowired
    QuestionServiceImpl questionService;

    @PostMapping(value = "/question")
    ResponseEntity<Question> addQuestion(@RequestBody Question question){
        return new ResponseEntity<>(questionService.addQuestion(question), HttpStatus.CREATED);
    }
    @GetMapping(value = "/question")
    ResponseEntity<Question> getQuestionById(@RequestParam int questionId) {
        return new ResponseEntity<>(questionService.getQuestionById(questionId), HttpStatus.FOUND);
    }
    @GetMapping(value = "/question/statement")
    ResponseEntity<Question> getQuestionByStatement(@RequestParam String questionStatement) {
        return new ResponseEntity<>(questionService.getQuestionByStatement(questionStatement), HttpStatus.FOUND);
    }
    @PutMapping(value = "/question")
    ResponseEntity<Question> updateQuestion(@RequestBody Question question){
        return new ResponseEntity<>(questionService.updateQuestion(question), HttpStatus.OK);
    }
    @DeleteMapping(value = "/question")
    ResponseEntity<Object> deleteQuestion(@RequestParam int questionId) {
        questionService.deleteQuestion(questionId);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }

    @PostMapping(value = "/subject")
    ResponseEntity<Subject> addSubject(@RequestBody Subject subject){
        return new ResponseEntity<>(subjectService.addSubject(subject), HttpStatus.CREATED);
    }
    @GetMapping(value = "/subject")
    ResponseEntity<Subject> getSubjectById(@RequestParam int subjectId){
        return new ResponseEntity<>(subjectService.getSubjectById(subjectId), HttpStatus.FOUND);
    }
    @GetMapping(value = "/subject/name")
    ResponseEntity<Subject> getSubjectByName(@RequestParam String subjectName){
        return new ResponseEntity<>(subjectService.getSubjectByName(subjectName), HttpStatus.FOUND);
    }
    @PutMapping(value = "/subject")
    ResponseEntity<Subject> updateSubject(@RequestBody Subject subject){
        return new ResponseEntity<>(subjectService.updateSubject(subject), HttpStatus.OK);
    }
    @DeleteMapping(value = "/subject")
    ResponseEntity<Object> deleteSubject(@RequestParam int subjectId){
        subjectService.deleteSubject(subjectId);
        return new ResponseEntity<>("Deleted successfully", HttpStatus.OK);
    }

}
