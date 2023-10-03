package com.ria.acmetesting.services.service;

import com.ria.acmetesting.dbentities.Question;

public interface QuestionService {
    Question addQuestion(Question question);

    Question getQuestionById(int questionId);

    Question getQuestionByStatement(String questionStatement);

    Question updateQuestion(Question question);

    void deleteQuestion(int questionId);
}
