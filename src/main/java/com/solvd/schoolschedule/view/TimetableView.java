package com.solvd.schoolschedule.view;

import java.util.List;

import com.solvd.schoolschedule.model.*;

/**
 * View class responsible for displaying timetable information to the console
 */
public class TimetableView {

    /**
     * Displays the header for the timetable summary
     */
    public void displayTimetableSummaryHeader() {
        System.out.println("\n=== TIMETABLE SUMMARY ===");
    }

    /**
     * Displays the header for teacher schedules section
     */
    public void displayTeacherSchedulesHeader() {
        System.out.println("\n=== TEACHER SCHEDULES ===");
    }

    /**
     * Displays the weekly schedule of a specific group
     *
     * @param timetable timetable
     * @param group group
     */
    public void displayGroupSchedule(Timetable timetable, Group group) {
        System.out.println("\nGroup: " + group.getName());

        int max = maxNumberOfDayLessons(timetable, group);

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
            if (!dayLessons.isEmpty()) {
                System.out.print(formatDay(day));

                for (Lesson lesson : dayLessons) {
                    String name = lesson.getSubject().getDisplayName();
                    System.out.print(lesson.getTimeSlot().getPeriod() + "." + abbreviate(name) + " ");
                }

                String spaceString = " ".repeat((max - dayLessons.size()) * 7);

                System.out.println(spaceString + "(" + dayLessons.size() + " lessons)");
            }
        }
    }

    /**
     * Displays the weekly schedule of a specific teacher
     *
     * @param timetable timetable
     * @param teacher teacher
     */
    public void displayTeacherSchedule(Timetable timetable, Teacher teacher) {
        String subjectName = teacher.getSubject().getDisplayName();

        System.out.println("\n" + teacher.getName() + " (" + subjectName + " - " + abbreviate(subjectName) + "):");

        int max = maxNumberOfDayLessons(timetable, teacher);

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);

            if (!dayLessons.isEmpty()) {
                System.out.print(formatDay(day));

                for (Lesson lesson : dayLessons) {
                    String name = "Gr" + lesson.getGroup().getId();
                    System.out.print(lesson.getTimeSlot().getPeriod() + "." + name + " ");
                }

                String spaceString = " ".repeat((max - dayLessons.size()) * 6);
                System.out.println(spaceString + "(" + dayLessons.size() + " lessons)");
            }
        }
    }

    /**
     * Displays the generation progress
     *
     * @param generation generation number
     * @param fitness best fitness value
     */
    public void displayGenerationProgress(int generation, double fitness) {
        System.out.println("Generation " + generation + " - Best Fitness: " + fitness);
    }

    /**
     * Displays the final results header
     */
    public void displayFinalResultsHeader() {
        System.out.println("\n=== FINAL RESULTS ===");
    }

    /**
     * Displays the best fitness value
     *
     * @param fitness fitness value
     */
    public void displayBestFitness(double fitness) {
        System.out.println("Best Fitness: " + fitness);
    }

    /**
     * Displays the total number of lessons
     *
     * @param totalLessons total lessons count
     */
    public void displayTotalLessons(int totalLessons) {
        System.out.println("Total Lessons: " + totalLessons);
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
     * For the group, calculates the number of lessons for each day, and returns the max number.
     *
     * @param timetable timetable
     * @param group group
     * @return max
     */
    private int maxNumberOfDayLessons(Timetable timetable, Group group) {
        int max = 0;

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
            int number = dayLessons.size();
            max = Math.max(number, max);
        }
        return max;
    }

    /**
     * For the teacher, calculates the number of lessons for each day, and returns the max number.
     *
     * @param timetable timetable
     * @param teacher teacher
     * @return max
     */
    private int maxNumberOfDayLessons(Timetable timetable, Teacher teacher) {
        int max = 0;

        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);
            int number = dayLessons.size();
            max = Math.max(number, max);
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
}
