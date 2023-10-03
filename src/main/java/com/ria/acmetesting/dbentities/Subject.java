package com.ria.acmetesting.dbentities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_generator")
    @SequenceGenerator(name = "subject_generator", sequenceName = "subject_seq", allocationSize = 1)
    private int id;

    String name;

    int allowedAttempts;

    @OneToMany(mappedBy = "subject")
    Set<Score> score;

    public Subject(String name, int allowedAttempts, Set<Score> score) {
        this.name = name;
        this.allowedAttempts = allowedAttempts;
        this.score = score;
    }
}

