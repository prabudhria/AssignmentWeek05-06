package com.ria.acmetesting.dbentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int age;
    private String currentSubject;
    private int currentLevel;
    private int totalQuestionsAttempted;
    private ArrayList<Integer> levelQuestionId = new ArrayList<>();

    @OneToMany(mappedBy = "student")
    private Set<Score> score;

    public void setLevelQuestionId(int questionId, int level){
        this.levelQuestionId.set(level-1, questionId);
    }
}

