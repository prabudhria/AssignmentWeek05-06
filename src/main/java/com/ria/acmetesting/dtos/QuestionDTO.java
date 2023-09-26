package com.ria.acmetesting.dtos;

import com.ria.acmetesting.dbentities.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO {
    private String statement;
    private String options;

    public QuestionDTO(Question question){
        this.statement = question.getStatement();
        this.options = question.getOptions();
    }
}
