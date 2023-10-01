package com.ria.acmetesting.controller;

import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dtos.QuestionDTO;
import com.ria.acmetesting.dtos.StudentDTO;
import com.ria.acmetesting.services.ACMETestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
@Slf4j
public class ACMETestController {

    @Autowired
    ACMETestServiceImpl acmeTestService;
    @PostMapping(value = "/register")
    ResponseEntity<StudentDTO> register(@RequestBody Student student) {
        return new ResponseEntity<>(acmeTestService.saveStudent(student), HttpStatus.CREATED);
    }

    @GetMapping(value = "/subject")
    ResponseEntity<List<String>> getRemainingSubjects(@RequestParam String studentUsername) {
        return new ResponseEntity<>(acmeTestService.getRemainingSubjectsOfStudent(studentUsername), HttpStatus.OK);
    }

    @PostMapping(value = "/subject")
    ResponseEntity<Object> markSubject(@RequestParam String studentUsername, @RequestParam String subject) {
        acmeTestService.markSubject(studentUsername, subject);
        return new ResponseEntity<>("Subject \"" + subject + "\" is selected successfully", HttpStatus.OK);
    }
    @GetMapping(value = "/starttest")
    ResponseEntity<QuestionDTO> startTest(@RequestParam String studentUsername)
    {
        return new ResponseEntity<>(acmeTestService.starTest(studentUsername), HttpStatus.OK);
    }

    @PostMapping(value = "/test")
    ResponseEntity<Object> evaluateStudentAnswer(@RequestParam String studentUsername,
                                                 @RequestParam String selectedOption) {
        acmeTestService.evalutateStudentAnswer(studentUsername, selectedOption);
        return new ResponseEntity<>("Answer evaluated successfully", HttpStatus.OK);
    }

    @GetMapping(value = "/test")
    ResponseEntity<QuestionDTO> getNextQuestion(@RequestParam String studentUsername) {
        QuestionDTO nextQuestion = acmeTestService.getNextQuestion(studentUsername);
        return new ResponseEntity<>(nextQuestion, HttpStatus.OK);
    }
    @GetMapping(value = "/score")
    ResponseEntity<Integer> getScore(@RequestParam String studentUsername){
        return new ResponseEntity<>(acmeTestService.getScore(studentUsername), HttpStatus.OK);
    }
}
