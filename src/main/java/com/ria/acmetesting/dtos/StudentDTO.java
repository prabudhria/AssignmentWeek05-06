package com.ria.acmetesting.dtos;

import com.ria.acmetesting.dbentities.Student;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO {
    private String name;
    private int age;

    public StudentDTO(Student student){
        this.name = student.getName();
        this.age = student.getAge();
    }
}
