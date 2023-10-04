package com.ria.acmetesting.services.implementation;

import com.ria.acmetesting.dbentities.Question;
import com.ria.acmetesting.exceptionhandling.exceptions.QuestionNotFoundException;
import com.ria.acmetesting.exceptionhandling.exceptions.RequiredQuestionFieldNullException;
import com.ria.acmetesting.exceptionhandling.exceptions.SubjectNotFoundException;
import com.ria.acmetesting.respositories.QuestionRepository;
import com.ria.acmetesting.respositories.SubjectRepository;
import com.ria.acmetesting.services.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    SubjectRepository subjectRepository;
    @Override
    public Question addQuestion(Question question){
        if(question.getSubject()==null || question.getLevel()==0 || question.getOptions()==null
                || question.getCorrectOption()==null) throw new RequiredQuestionFieldNullException();

        String subjectOfQuestion = question.getSubject();
        subjectRepository.findSubjectByName(subjectOfQuestion).orElseThrow(SubjectNotFoundException::new);
        return questionRepository.save(question);
    }


    @Override
    public Question getQuestionById(int questionId) {
        return questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);
    }

    @Override
    public Question getQuestionByStatement(String questionStatement) {
        return questionRepository.findQuestionByStatement(questionStatement).orElseThrow(QuestionNotFoundException::new);
    }

    @Override
    public Question updateQuestion(Question question) {
        if(question.getId() == 0 || question.getSubject()==null
                || question.getLevel()==0 || question.getOptions()==null
                || question.getCorrectOption()==null) throw new RequiredQuestionFieldNullException();
        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(int questionId) {
        questionRepository.findById(questionId).orElseThrow(QuestionNotFoundException::new);
        questionRepository.deleteById(questionId);

    }
}
