package com.ria.acmetesting.dbentities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;

@Entity
@Getter
@Setter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_generator")
    @SequenceGenerator(name = "subject_generator", sequenceName = "subject_seq", allocationSize = 1)
    private int id;

    String name;

    int allowedAttempts;

    @OneToMany(mappedBy = "subject")
    Set<Score> score;
}

