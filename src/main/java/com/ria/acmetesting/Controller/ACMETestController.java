package com.ria.acmetesting.Controller;

import com.ria.acmetesting.Entity.Question;
import com.ria.acmetesting.Entity.Student;
import com.ria.acmetesting.Services.ACMETestSerivceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ACMETestController {

    @Autowired
    ACMETestSerivceImpl acmeTestSerivce;
    @PostMapping(value = "/register")
    ResponseEntity<Student> register(Student student){
        return new ResponseEntity<>(acmeTestSerivce.saveStudent(student), HttpStatus.OK);
    }

    @GetMapping(value = "/subject")
    ResponseEntity<Student> markSubject(Student student){
        return new ResponseEntity<>(acmeTestSerivce.markSubject(student))
    }



    @GetMapping(value = "/subject/{subject}/test")
    ResponseEntity<Question> getNextQuestion(@RequestParam String subject, Student student){
        return new ResponseEntity<Question>(acmeTestSerivce.getNexotQuestion(student), HttpStatus.OK);
    }

    @GetMapping(value = "/finished")
    ResponseEntity<ArrayList<Boolean>> getRemainingSubject(Student student){
        return new ResponseEntity<>(acmeTestSerivce.getRemainingSubject(student), HttpStatus.OK);
    }
    @GetMapping(value = "/score")
    ResponseEntity<Integer> getScore(Student student){
        return new ResponseEntity<Integer>(acmeTestSerivce.getScore(student), HttpStatus.OK);
    }
}
