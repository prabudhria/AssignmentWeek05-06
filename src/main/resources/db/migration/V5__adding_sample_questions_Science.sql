--level one questions for science
INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (16, 1, 'Science', 'A snail movies by its muscular', ARRAY['Wings', 'Foot', 'Fins', 'None of the these'], 'b');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (17, 1, 'Science', 'The muscles cover the ', ARRAY['Skin', 'Bones', 'Both (a) and (b)', 'None of the these'], 'b');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (18, 1, 'Science', 'The skull covers and protects the', ARRAY['Eyes', 'Nose', 'Brain', 'None of the these'], 'c');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (19, 1, 'Science', 'To which bone are thigh bones attached', ARRAY['Pelvic bone',
 'Hinge joint', 'Shoulder bone', 'Backbone'], 'a');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (20, 2, 'Science', 'How many shoulder bone are there', ARRAY['One ', 'Three', 'Two', 'None of the these'], 'c');

--level two questions for science
INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (21, 2, 'Science', 'During the process of respiration the ribs move',
ARRAY['down and inwards', 'up and inwards', 'down and outwards', 'up and outward'], 'a');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (22, 2, 'Science', 'Name the organ of body in which the blood is oxygenated',
 ARRAY['Heart', 'Lungs', 'Lever', 'Pancreas'], 'b');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (23, 2, 'Science', 'After heavy exercise, due to the accumulation of which substance we get muscle cramps',
 ARRAY['Malaic acid', 'Fumaric acid', 'Lipoic acid', 'Lactic acid'], 'd');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (24, 2, 'Science', 'Yeasts are used in', ARRAY['wine and beer industry', 'bakery', 'in both', 'None of the these'], 'c');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (25, 2, 'Science', 'Breathing rate in human beings in normal condition is',
 ARRAY['12-15 times in a minute', '15-18 times in a minute', '18-22 times in a minute', '22-25 times in a minute'], 'b');

--level three questions for Science
INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (26, 3, 'Science', 'In which case the matter intermolecular force is stronger',
 ARRAY['Solid', 'Liquid', 'Gas', 'Plasma'], 'a');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (27, 3, 'Science', 'Which of the following process converts solid into gas',
ARRAY['Sublimation', 'Vaporization', 'Deposition', 'Fusion'], 'a');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (28, 3, 'Science', 'How many types of the mixture are present in nature', ARRAY['Six', 'Ten', 'Two', 'Five'], 'c');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (29, 3, 'Science', 'Which of the following substance is present in large quantities in the solution',
 ARRAY['Solute', 'Solvent', 'Water', 'Benzene'], 'b');

INSERT INTO question (id, level, subject, statement, options, answer)
VALUES (30, 3, 'Science', 'Name the process by which we can obtain various gases from the air',
 ARRAY['Distillation', 'Hydrolysis', 'Fractional Distillation', 'Froth Flotation'], 'c');

ALTER SEQUENCE question_seq RESTART 31;

