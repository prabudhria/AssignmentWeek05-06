package com.ria.acmetesting.services.service;

import com.ria.acmetesting.dbentities.Subject;

public interface SubjectService {
    Subject getSubjectById(int subjectId);

    Subject addSubject(Subject subject);
    Subject getSubjectByName(String subjectName);

    Subject updateSubject(Subject subject);

    void deleteSubject(int subjectId);
}
