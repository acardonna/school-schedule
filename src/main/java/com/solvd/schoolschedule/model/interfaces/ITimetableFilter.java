package com.solvd.schoolschedule.model.interfaces;

import com.solvd.schoolschedule.model.Lesson;
import com.solvd.schoolschedule.model.Timetable;

import java.util.List;
import java.util.stream.Collectors;

public interface ITimetableFilter {

    /**
     * Get the ITimetableFilter object from a lesson.
     *
     * @param lesson lesson
     * @return object ITimetableFilter
     */
    ITimetableFilter getFromLesson(Lesson lesson);

    default List<Lesson> filter(Timetable timetable) {
        return timetable.getLessons().stream()
                .filter(lesson -> lesson.get(this).equals(this))
                .collect(Collectors.toList());
    }

}
