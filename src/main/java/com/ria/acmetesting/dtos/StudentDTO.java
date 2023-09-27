package com.ria.acmetesting.dtos;

import com.ria.acmetesting.dbentities.Student;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private String name;
    private int age;

    public StudentDTO(Student student){
        this.name = student.getName();
        this.age = student.getAge();
    }
}
