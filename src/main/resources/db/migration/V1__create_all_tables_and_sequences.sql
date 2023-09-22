create table question (
        id integer not null,
        answer varchar(255),
        level integer not null,
        options varchar(255),
        statement varchar(255),
        subject varchar(255),
        primary key (id)
);

create table score (
        student_id integer not null ,
        subject_id integer not null,
        score integer not null,
        primary key (student_id, subject_id)
    );

create table student (
        id integer not null ,
        name varchar(255),
        age integer not null,
        current_subject varchar(255),
        current_level integer,
        total_questions_attempted integer,
        level_one_question_id integer,
        level_two_question_id integer,
        level_three_question_id integer,
        primary key (id)
    );

create table student_to_question (
        student_id integer not null,
        question_id integer not null,
        primary key (student_id, question_id)
    );

create table subject (
        id integer not null,
        name varchar(255) unique,
        primary key (id)
    );


alter table if exists score
       add constraint student_score_fk
       foreign key (student_id)
       references student;

alter table if exists score
       add constraint subject_score_fk
       foreign key (subject_id)
       references subject;

alter table if exists student_to_question
       add constraint question_student_to_question_fk
       foreign key (question_id)
       references question;

alter table if exists student_to_question
       add constraint student_student_to_question_fk
       foreign key (student_id)
       references student;

