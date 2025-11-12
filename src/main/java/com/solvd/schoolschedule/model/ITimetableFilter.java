package com.solvd.schoolschedule.model;

import java.util.List;

public interface ITimetableFilter {
    List<Lesson> filter(Timetable timetable);
}
