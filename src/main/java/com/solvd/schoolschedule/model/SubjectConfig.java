package com.solvd.schoolschedule.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for weekly lesson frequencies per subject
 */
public class SubjectConfig {
    private static final Map<Subject, Integer> WEEKLY_LESSONS = new HashMap<>();

    static {
        // Define how many times per week each subject is taught
        WEEKLY_LESSONS.put(Subject.MATH, 5);              
        WEEKLY_LESSONS.put(Subject.PHYSICS, 4);          
        WEEKLY_LESSONS.put(Subject.INFORMATICS, 3);     
        WEEKLY_LESSONS.put(Subject.PHYSICAL_CULTURE, 2);
    }

    /**
     * Get the number of weekly lessons for a subject
     * @param subject the subject
     * @return number of lessons per week
     */
    public static int getWeeklyLessons(Subject subject) {
        return WEEKLY_LESSONS.getOrDefault(subject, 1);
    }

    /**
     * Get total lessons per week for all subjects
     * @return total weekly lessons
     */
    public static int getTotalWeeklyLessons() {
        return WEEKLY_LESSONS.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Get the average lessons per day
     * @return average daily lessons
     */
    public static double getAverageLessonsPerDay() {
        return getTotalWeeklyLessons() / 5.0;
    }
}