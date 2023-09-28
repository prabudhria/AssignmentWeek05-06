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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    String name;

    int allowedAttempts;

    @OneToMany(mappedBy = "subject")
    Set<Score> score;
}

