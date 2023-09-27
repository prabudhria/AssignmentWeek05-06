create table question (
        id integer not null,
        answer varchar(255) not null,
        level integer not null,
        options varchar(255) not null,
        statement varchar(255) not null,
        subject varchar(255) not null,
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
        name varchar(255) not null,
        age integer not null,
        current_subject varchar(255),
        current_level integer,
        total_questions_attempted integer,
        level_question_id integer[],
        primary key (id)
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
