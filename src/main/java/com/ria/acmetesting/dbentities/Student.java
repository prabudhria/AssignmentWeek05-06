package com.ria.acmetesting.dbentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private int age;
    private String currentSubject;
    private int currentLevel;
    private int totalQuestionsAttempted;
    private ArrayList<Integer> levelQuestionId = new ArrayList<>();
//    private int levelOneQuestionId;
//    private int levelTwoQuestionId;
//    private int levelThreeQuestionId;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "student_to_question", joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id"))
    private Set<Question> questionsAttempted;

    @OneToMany(mappedBy = "student")
    private Set<Score> score;

    public void setLevelQuestionId(int questionId, int level){
        this.levelQuestionId.set(level-1, questionId);
    }
    public void setLevelOneQuestionId(int questionId){
        this.levelQuestionId.set(0, questionId);
    }
    public void setLevelTwoQuestionId(int questionId){
        this.levelQuestionId.set(1, questionId);
    }
    public void setLevelThreeQuestionId(int questionId){
        this.levelQuestionId.set(2, questionId);
    }
}
