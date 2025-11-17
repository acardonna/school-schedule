-- -----------------------------------------------------
-- Some inserts to populate the db. (Commented out since we now populated it from the program)
-- -----------------------------------------------------

-- INSERT INTO subject (subject_code, display_name, weekly_lessons) VALUES
--     ('MATH', 'Mathematics', 5),
--     ('PHYSICS', 'Physics', 4),
--     ('INFORMATICS', 'Informatics', 3),
--     ('PHYSICAL_CULTURE', 'Physical Culture', 2);

-- INSERT INTO student_group (name, student_count) VALUES
--     ('Group 1', 30),
--     ('Group 2', 30),
--     ('Group 3', 30),
--     ('Group 4', 30);

-- INSERT INTO teacher (name, subject_code) VALUES
--     ('Mr. Smith', 'MATH'),
--     ('Ms. Johnson', 'PHYSICS'),
--     ('Dr. Brown', 'INFORMATICS'),
--     ('Mrs. Davis', 'PHYSICAL_CULTURE');

-- INSERT INTO classroom (name, room_type) VALUES
--     ('Room 101', 'GENERAL'),
--     ('Room 102', 'GENERAL'),
--     ('Room 103', 'GENERAL'),
--     ('Physics Lab', 'PHYSICS_LAB'),
--     ('Computer Lab', 'COMPUTER_LAB');

-- INSERT INTO classroom_subject_capability (classroom_id, subject_code) VALUES
--     (1, 'MATH'),
--     (1, 'PHYSICS'),
--     (1, 'PHYSICAL_CULTURE'),
--     (2, 'MATH'),
--     (2, 'PHYSICS'),
--     (2, 'PHYSICAL_CULTURE'),
--     (3, 'MATH'),
--     (3, 'PHYSICS'),
--     (3, 'PHYSICAL_CULTURE'),
--     (4, 'PHYSICS'),
--     (5, 'INFORMATICS');

-- -----------------------------------------------------
-- Select statements to view all tables
-- -----------------------------------------------------

SELECT * FROM subject;

SELECT * FROM student_group;

SELECT * FROM teacher;

SELECT * FROM classroom;

SELECT * FROM classroom_subject_capability;

SELECT * FROM timetable;

SELECT * FROM lesson;
