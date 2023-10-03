package com.ria.acmetesting.services.implementation;

import com.ria.acmetesting.dbentities.Subject;
import com.ria.acmetesting.exceptionhandling.exceptions.RequiredSubjectFieldNullException;
import com.ria.acmetesting.exceptionhandling.exceptions.SubjectNotFoundException;
import com.ria.acmetesting.respositories.SubjectRepository;
import com.ria.acmetesting.services.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    SubjectRepository subjectRepository;
    @Override
    public Subject addSubject(Subject subject) {
        if(subject.getName()==null || subject.getAllowedAttempts()==0) throw new RequiredSubjectFieldNullException();
        return subjectRepository.save(subject);
    }

    @Override
    public Subject getSubjectById(int subjectId){
        return subjectRepository.findById(subjectId).orElseThrow(SubjectNotFoundException::new);
    }

    @Override
    public Subject getSubjectByName(String subjectName) {
        return subjectRepository.findSubjectByName(subjectName).orElseThrow(SubjectNotFoundException::new);
    }

    @Override
    public Subject updateSubject(Subject subject) {
        if(subject.getId()==0 || subject.getName()==null
                || subject.getAllowedAttempts()==0) throw new RequiredSubjectFieldNullException();
        return subjectRepository.save(subject);
    }

    @Override
    public void deleteSubject(int subjectId) {
        subjectRepository.findById(subjectId).orElseThrow(SubjectNotFoundException::new);
        subjectRepository.deleteById(subjectId);
    }
}
