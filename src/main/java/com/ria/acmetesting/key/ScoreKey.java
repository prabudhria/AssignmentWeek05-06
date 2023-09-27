package com.ria.acmetesting.key;
import com.ria.acmetesting.dbentities.Student;
import com.ria.acmetesting.dbentities.Subject;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

//import javax.persistence.*;
import java.util.ArrayList;
import java.util.Set;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class ScoreKey implements Serializable {

    @Column(name = "student_id")
    int studentId;
    @Column(name = "subject_id")
    int subjectId;
}

