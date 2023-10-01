package com.ria.acmetesting.dbentities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@ToString
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
    private String answer;
    private ArrayList<String> options = new ArrayList<>();

}
