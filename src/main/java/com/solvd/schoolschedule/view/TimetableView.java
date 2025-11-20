package com.solvd.schoolschedule.view;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solvd.schoolschedule.model.*;
import com.solvd.schoolschedule.model.interfaces.ITimetableFilter;

/**
 * View class responsible for displaying timetable information to the console
 */
public class TimetableView {

    private static final Logger LOGGER = LogManager.getLogger(TimetableView.class);

    /**
     * Displays the header for the timetable summary
     */
    public void displayTimetableSummaryHeader() {
        LOGGER.info("=== TIMETABLE SUMMARY ===");
    }

    /**
     * Displays the header for teacher schedules section
     */
    public void displayTeacherSchedulesHeader() {
        LOGGER.info("=== TEACHER SCHEDULES ===");
    }

    /**
     * Displays the header for classroom schedules section
     */
    public void displayClassroomSchedulesHeader() {
        LOGGER.info("=== CLASSROOM SCHEDULES ===");
    }

    /**
     * Displays the weekly schedule of a specific group
     *
     * @param timetable timetable
     * @param group     group
     */
    public void displayGroupSchedule(Timetable timetable, Group group) {
        LOGGER.info("Group: " + group.getName());

        int max = Math.max(6, maxNumberOfDayLessons(timetable, group));

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsOnDayFor(group, day);
            if (!dayLessons.isEmpty()) {
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append(formatDay(day));

                int periodOfFirstLesson = dayLessons.getFirst().getTimeSlot().getPeriod();
                String spaceStringPrefix = " ".repeat(periodOfFirstLesson * 11);
                lineBuilder.append(spaceStringPrefix);

                for (Lesson lesson : dayLessons) {
                    String name = lesson.getSubject().getDisplayName();
                    String groupString = "Gr" + lesson.getGroup().getId();
                    lineBuilder.append(lesson.getTimeSlot().getPeriod() + "." + abbreviate(name) + "-" + groupString + " ");
                }


                String spaceStringSufix = " ".repeat((max - periodOfFirstLesson - dayLessons.size()) * 11);

                lineBuilder.append(spaceStringSufix + "(" + dayLessons.size() + " lessons)");
                LOGGER.info(lineBuilder.toString());
            }
        }
    }

    /**
     * Displays the weekly schedule of a specific teacher
     *
     * @param timetable timetable
     * @param teacher   teacher
     */
    public void displayTeacherSchedule(Timetable timetable, Teacher teacher) {
        String subjectName = teacher.getSubject().getDisplayName();

        LOGGER.info("" + teacher.getName() + " (" + subjectName + " - " + abbreviate(subjectName) + "):");

        int max = Math.max(6, maxNumberOfDayLessons(timetable, teacher));

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsOnDayFor(teacher, day);

            if (!dayLessons.isEmpty()) {
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append(formatDay(day));

                int periodOfFirstLesson = dayLessons.getFirst().getTimeSlot().getPeriod();
                String spaceStringPrefix = " ".repeat(periodOfFirstLesson * 13);
                lineBuilder.append(spaceStringPrefix);

                for (Lesson lesson : dayLessons) {
                    String name = "Gr" + lesson.getGroup().getId();
                    String className = formatClassroom(lesson.getClassroom());
                    lineBuilder.append(lesson.getTimeSlot().getPeriod() + "." + className + "-" + name + " ");
                }

                String spaceStringSufix = " ".repeat((max - periodOfFirstLesson - dayLessons.size()) * 13);
                lineBuilder.append(spaceStringSufix + "(" + dayLessons.size() + " lessons)");
                LOGGER.info(lineBuilder.toString());
            }
        }
    }

    /**
     * Displays the weekly schedule of a specific classroom
     *
     * @param timetable timetable
     * @param classroom classroom
     */
    public void displayClassroomSchedule(Timetable timetable, Classroom classroom) {
        LOGGER.info("Classroom: " + classroom.getName() + " - " + formatClassroom(classroom));

        int max = Math.max(6, maxNumberOfDayLessons(timetable, classroom));

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsOnDayFor(classroom, day);
            if (!dayLessons.isEmpty()) {
                StringBuilder lineBuilder = new StringBuilder();
                lineBuilder.append(formatDay(day));

                int periodOfFirstLesson = dayLessons.getFirst().getTimeSlot().getPeriod();
                String spaceStringPrefix = " ".repeat(periodOfFirstLesson * 11);
                lineBuilder.append(spaceStringPrefix);

                for (Lesson lesson : dayLessons) {
                    String subjectString = lesson.getSubject().getDisplayName();
                    String groupString = "Gr" + lesson.getGroup().getId();
                    lineBuilder.append(lesson.getTimeSlot().getPeriod() + "." + abbreviate(subjectString) + "-" + groupString + " ");
                }

                String spaceStringSufix = " ".repeat((max - periodOfFirstLesson - dayLessons.size()) * 11);

                lineBuilder.append(spaceStringSufix + "(" + dayLessons.size() + " lessons)");
                LOGGER.info(lineBuilder.toString());
            }
        }
    }

    /**
     * Displays the generation progress
     *
     * @param generation generation number
     * @param fitness    best fitness value
     */
    public void displayGenerationProgress(int generation, double fitness) {
        double percentage = fitness/2000*100;
        String percentageString = percentage<0? "0%": String.format("%.2f%%", percentage);
        LOGGER.info("Generation " + generation + " - Best Fitness: " + fitness + "/2000 " + percentageString);
    }

    /**
     * Displays the final results header
     */
    public void displayFinalResultsHeader() {
        LOGGER.info("=== FINAL RESULTS ===");
    }

    /**
     * Displays the best fitness value
     *
     * @param fitness fitness value
     */
    public void displayBestFitness(double fitness) {
        LOGGER.info("Best Fitness: " + fitness);
    }

    /**
     * Displays the total number of lessons
     *
     * @param totalLessons total lessons count
     */
    public void displayTotalLessons(int totalLessons) {
        LOGGER.info("Total Lessons: " + totalLessons);
    }

    /**
     * Fills the String of each day with spaces, so every day has the same length
     *
     * @param day day as an integer (0 Mon - 4 Fri)
     * @return formatted string
     */
    private String formatDay(int day) {
        String dayString = SchoolConfig.DAY_NAMES[day];
        String spaceString = " ".repeat(11 - dayString.length());
        return dayString + ":" + spaceString;
    }

    /**
     * For an ITimetableFilter object, calculates the number of lessons for each day, and returns the max number.
     *
     * @param timetable timetable
     * @param object    ITimetableFilter object
     * @return max
     */
    private int maxNumberOfDayLessons(Timetable timetable, ITimetableFilter object) {
        int max = 0;

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsOnDayFor(object, day);
            int number = dayLessons.size();
            int periodOfFirstLesson = dayLessons.isEmpty() ? 0 : dayLessons.getFirst().getTimeSlot().getPeriod();
            max = Math.max(number + periodOfFirstLesson, max);
        }
        return max;
    }

    /**
     * Abbreviates the string into a string of length 4.
     * If it's one word, takes the first 4 characters.
     * If it's more than one word, takes the first character of the first word, and 3 characters of the second word.
     *
     * @param string string
     * @return abbreviation
     */
    private String abbreviate(String string) {
        if (!string.contains(" ")) {
            return string.substring(0, 4);
        } else {
            String[] splitString = string.split(" ");
            return splitString[0].charAt(0) + splitString[1].substring(0, 3);
        }
    }

    /**
     * Abbreviates the classroom name into a string of length 6.
     *
     * @param classroom classroom
     * @return formated classroom name
     */
    private String formatClassroom(Classroom classroom) {
        String name = classroom.getName();
        if (name.contains("Room")) {
            String[] words = name.split(" ");
            return "Cr#" + words[1];
        }
        if (name.contains("Lab")) {
            return name.substring(0, 3) + "Lab";
        }
        return name.substring(0, 6);
    }

    /**
     * Displays a message
     *
     * @param string message
     */
    public void displayMessage(String string) {
        LOGGER.info(string);
    }
}
