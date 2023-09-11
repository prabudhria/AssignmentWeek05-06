package com.ria.acmetesting.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;
    String name;
    int age;
    //index 0, 1 & 2 for Maths, English and Logic respectively
    ArrayList<Boolean> subjects = new ArrayList<>(3);
    String currentSubject;


}
