-- -----------------------------------------------------
-- Schema school_schedule
-- -----------------------------------------------------

CREATE DATABASE IF NOT EXISTS school_schedule;
USE school_schedule;

SET foreign_key_checks = 0;

-- -----------------------------------------------------
-- Table subject
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS subject (
  subject_code VARCHAR(20) NOT NULL,
  display_name VARCHAR(50) NOT NULL,
  weekly_lessons TINYINT NOT NULL,
  PRIMARY KEY (subject_code));

-- -----------------------------------------------------
-- Table student_group
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS student_group (
  group_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  student_count TINYINT NOT NULL DEFAULT 30,
  PRIMARY KEY (group_id));

-- -----------------------------------------------------
-- Table teacher
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS teacher (
  teacher_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  subject_code VARCHAR(20) NOT NULL,
  PRIMARY KEY (teacher_id),
    FOREIGN KEY (subject_code)
    REFERENCES subject (subject_code));

-- -----------------------------------------------------
-- Table classroom
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS classroom (
  classroom_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  room_type VARCHAR(20) NULL,
  PRIMARY KEY (classroom_id));

-- -----------------------------------------------------
-- Table classroom_subject_capability
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS classroom_subject_capability (
  classroom_id INT,
  subject_code VARCHAR(20),
  PRIMARY KEY (classroom_id, subject_code),
    FOREIGN KEY (classroom_id)
    REFERENCES classroom (classroom_id),
    FOREIGN KEY (subject_code)
    REFERENCES subject (subject_code));

-- -----------------------------------------------------
-- Table timetable
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS timetable (
  timetable_id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) NULL,
  fitness_score DECIMAL(10,2) NOT NULL,
  generation_number INT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (timetable_id));

-- -----------------------------------------------------
-- Table lesson
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS lesson (
  lesson_id INT NOT NULL AUTO_INCREMENT,
  timetable_id INT NOT NULL,
  group_id INT NOT NULL,
  subject_code VARCHAR(20) NOT NULL,
  teacher_id INT NOT NULL,
  classroom_id INT NOT NULL,
  day_of_week TINYINT NOT NULL,
  period_number TINYINT NOT NULL,
  PRIMARY KEY (lesson_id),
    FOREIGN KEY (timetable_id)
    REFERENCES timetable (timetable_id),
    FOREIGN KEY (group_id)
    REFERENCES student_group (group_id),
    FOREIGN KEY (subject_code)
    REFERENCES subject (subject_code),
    FOREIGN KEY (teacher_id)
    REFERENCES teacher (teacher_id),
    FOREIGN KEY (classroom_id)
    REFERENCES classroom (classroom_id));

SET foreign_key_checks = 1;
