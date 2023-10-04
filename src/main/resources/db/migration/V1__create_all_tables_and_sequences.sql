DROP TABLE IF EXISTS question;
CREATE TABLE question (
        id INTEGER NOT NULL,
        correct_option VARCHAR(255) NOT NULL,
        level INTEGER NOT NULL,
        options VARCHAR[] NOT NULL,
        statement VARCHAR(255) NOT NULL UNIQUE,
        subject VARCHAR(255) NOT NULL,
        PRIMARY KEY(id)
);

DROP TABLE IF EXISTS score;
CREATE TABLE score (
        student_id INTEGER NOT NULL ,
        subject_id INTEGER NOT NULL,
        score INTEGER NOT NULL,
        PRIMARY KEY(student_id, subject_id)
    );

DROP TABLE IF EXISTS student;
CREATE TABLE student (
        id INTEGER NOT NULL ,
        username VARCHAR(255) NOT NULL UNIQUE,
        name VARCHAR(255) NOT NULL,
        age INTEGER NOT NULL,
        current_subject VARCHAR(255),
        current_level INTEGER,
        total_questions_attempted_of_subject INTEGER,
        next_question_id INTEGER,
        all_level_question_ids INTEGER[],
        PRIMARY KEY (id)
    );

DROP TABLE IF EXISTS subject;
CREATE TABLE subject (
        id INTEGER NOT NULL,
        name VARCHAR(255) UNIQUE NOT NULL,
        allowed_attempts INTEGER NOT NULL,
        PRIMARY KEY (id)
    );


ALTER TABLE IF EXISTS score
       ADD CONSTRAINT student_score_fk
       FOREIGN KEY (student_id)
       REFERENCES student;

ALTER TABLE IF EXISTS score
       ADD CONSTRAINT subject_score_fk
       FOREIGN KEY (subject_id)
       REFERENCES subject;
