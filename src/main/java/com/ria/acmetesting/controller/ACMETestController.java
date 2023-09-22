package com.ria.acmetesting.controller;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.services.ACMETestServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ACMETestController {

    @Autowired
    ACMETestServiceImpl acmeTestService;
    @PostMapping(value = "/register")
    ResponseEntity<Student> register(@RequestBody Student student){
        return new ResponseEntity<>(acmeTestService.saveStudent(student), HttpStatus.OK);
    }

    @GetMapping(value = "/subject")
    ResponseEntity<List<String>> getRemainingSubjects(@RequestParam int studentId){
        return new ResponseEntity<>(acmeTestService.getRemainingSubjects(studentId), HttpStatus.OK);
    }

    @PostMapping(value = "/subject")
    ResponseEntity<Student> markSubject(@RequestParam int studentId, @RequestParam String subject){
        return new ResponseEntity<>(acmeTestService.markSubject(studentId, subject), HttpStatus.OK);
    }
    @GetMapping(value = "/subject/{subject}/test")
    ResponseEntity<Question> startTest(@RequestParam int studentId, @PathVariable String subject){
        return new ResponseEntity<Question>(acmeTestService.starTest(studentId), HttpStatus.OK);
    }

    @PostMapping(value = "/subject/{subject}/test")
    ResponseEntity<Question> getNextQuestion(@RequestParam int studentId, @RequestParam String selectedOption, @PathVariable String subject){
        return new ResponseEntity<Question>(acmeTestService.getNextQuestion(studentId, selectedOption), HttpStatus.OK);
    }
    @GetMapping(value = "/score")
    ResponseEntity<Integer> getScore(@RequestParam int studentId){
        return new ResponseEntity<Integer>(acmeTestService.getScore(studentId), HttpStatus.OK);
    }
}
