package com.ria.acmetesting.dbentities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;

//import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Getter
@Setter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    String name;

    @OneToMany(mappedBy = "subject")
    Set<Score> score;
}

