package com.ria.acmetesting.dbentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

//import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
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
