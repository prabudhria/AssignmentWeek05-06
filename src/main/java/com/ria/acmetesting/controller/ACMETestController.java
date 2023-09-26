package com.ria.acmetesting.controller;

import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.exceptionhandling.ExamHasEndedException;
import com.ria.acmetesting.services.ACMETestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class ACMETestController {

    @Autowired
    ACMETestServiceImpl acmeTestService;
    @PostMapping(value = "/register")
    ResponseEntity<StudentDTO> register(@RequestBody Student student){
        return new ResponseEntity<>(acmeTestService.saveStudent(student), HttpStatus.OK);
    }

    @GetMapping(value = "/subject")
    ResponseEntity<List<String>> getRemainingSubjects(@RequestParam int studentId){
        return new ResponseEntity<>(acmeTestService.getRemainingSubjects(studentId), HttpStatus.OK);
    }

    @PostMapping(value = "/subject")
    ResponseEntity<StudentDTO> markSubject(@RequestParam int studentId, @RequestParam String subject){
        return new ResponseEntity<>(acmeTestService.markSubject(studentId, subject), HttpStatus.OK);
    }
    @GetMapping(value = "/subject/{subject}/test")
    ResponseEntity<QuestionDTO> startTest(@RequestParam int studentId, @PathVariable String subject){
        return new ResponseEntity<>(acmeTestService.starTest(studentId), HttpStatus.OK);
    }

    @PostMapping(value = "/subject/{subject}/test")
    ResponseEntity<QuestionDTO> getNextQuestion(@RequestParam int studentId,
                                                @RequestParam String selectedOption, @PathVariable String subject)
    throws ExamHasEndedException {
        QuestionDTO nextQuestion = acmeTestService.getNextQuestion(studentId, selectedOption);
        return new ResponseEntity<>(nextQuestion, HttpStatus.OK);
    }
    @GetMapping(value = "/score")
    ResponseEntity<Integer> getScore(@RequestParam int studentId){
        return new ResponseEntity<>(acmeTestService.getScore(studentId), HttpStatus.OK);
    }
}
