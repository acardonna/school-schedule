package com.solvd.schoolschedule.service;

import com.solvd.schoolschedule.model.*;

import java.util.List;

/**
 * Service for display info about timetables and schedules
 */
public class DisplayService {

    /**
     * Displays on console a summary of the timetable
     *
     * @param timetable timetable
     * @param populationService populationService
     */
    public static void timetableSummary(Timetable timetable, PopulationService populationService) {
        System.out.println("\n=== TIMETABLE SUMMARY ===");

        for (Group group : populationService.getGroups()) {
            DisplayService.groupSchedule(timetable, group);
        }

        System.out.println("\n=== TEACHER SCHEDULES ===");
        for (Teacher teacher : populationService.getTeachers()) {
            DisplayService.teacherSchedule(timetable, teacher);
        }
    }

    /**
     * Displays on console the weekly schedule of a specific group
     *
     * @param timetable timetable
     * @param group group
     */
    public static void groupSchedule(Timetable timetable, Group group) {
        System.out.println("\nGroup: " + group.getName());
        int max = maxNumberOfDayLessons(timetable, group);
        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
            if (!dayLessons.isEmpty()) {
                System.out.print(formatedDay(day));
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
     * Displays on console the weekly schedule of a specific teacher
     *
     * @param timetable timetable
     * @param teacher teacher
     */
    public static void teacherSchedule(Timetable timetable, Teacher teacher) {
        String subjectName = teacher.getSubject().getDisplayName();
        System.out.println("\n" + teacher.getName() + " (" + subjectName + " - " + abbreviate(subjectName) + "):");
        int max = maxNumberOfDayLessons(timetable, teacher);
        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);
            if (!dayLessons.isEmpty()) {
                System.out.print(formatedDay(day));
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
     * Fills the String of each day with spaces, so every day has the same length
     *
     * @param day day as an integer (0 Mon - 4 Fri)
     * @return formatedString
     */
    private static String formatedDay(int day) {
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
    private static int maxNumberOfDayLessons(Timetable timetable, Group group) {
        int max = 0;
        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForGroupOnDay(group, day);
            int number = dayLessons.size();
            max = Math.max(number,max);
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
    private static int maxNumberOfDayLessons(Timetable timetable, Teacher teacher) {
        int max = 0;
        for (int day = 0; day < SchoolConfig.WORKING_DAYS_PER_WEEK; day++) {
            List<Lesson> dayLessons = timetable.getLessonsForTeacherOnDay(teacher, day);
            int number = dayLessons.size();
            max = Math.max(number,max);
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
    public static String abbreviate(String string) {
        if (!string.contains(" ")) {
            return string.substring(0, 4);
        } else {
            String[] splitString = string.split(" ");
            return splitString[0].charAt(0) + splitString[1].substring(0, 3);
        }

    }


}
