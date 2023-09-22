package com.ria.acmetesting.dbentities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ria.acmetesting.key.ScoreKey;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Score {


    @EmbeddedId
    ScoreKey id;

    @JsonIgnore
    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    Student student;

    @JsonIgnore
    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id")
    Subject subject;

    int score;
}

