package com.ria.acmetesting.dbentities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_generator")
    @SequenceGenerator(name = "student_generator", sequenceName = "student_seq", allocationSize = 1)
    private int id;
    private String username;
    private String name;
    private int age;
    private String currentSubject;
    private int currentLevel;
    private int totalQuestionsAttemptedOfSubject;
    private int nextQuestionId;
    private ArrayList<Integer> allLevelQuestionIds = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private Set<Score> score;

    public void setLevelQuestionId(int questionId, int level){
        this.allLevelQuestionIds.set(level, questionId);
    }
}

