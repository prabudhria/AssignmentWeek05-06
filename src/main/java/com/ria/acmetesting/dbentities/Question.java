package com.ria.acmetesting.dbentities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_generator")
    @SequenceGenerator(name = "question_generator", sequenceName = "question_seq", allocationSize = 1)
    private int id;
    private int level;
    private String statement;
    private String subject;
    private ArrayList<String> options = new ArrayList<>();
    private String correctOption;

    public Question(int level, String statement, String subject, ArrayList<String> options, String correctOption) {
        this.level = level;
        this.statement = statement;
        this.subject = subject;
        this.options = options;
        this.correctOption = correctOption;
    }
}
