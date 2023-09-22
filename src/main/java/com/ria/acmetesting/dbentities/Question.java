package com.ria.acmetesting.dbentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int level;
    private String statement;
    private String subject;
    private String answer;
    private String options;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Student> attemptingStudent;

}
