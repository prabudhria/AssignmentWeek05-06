package com.ria.acmetesting.dtos;

import com.ria.acmetesting.dbentities.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private String statement;
    private ArrayList<String> options = new ArrayList<>();

    public QuestionDTO(Question question){
        this.statement = question.getStatement();
        this.options = question.getOptions();
    }
}
