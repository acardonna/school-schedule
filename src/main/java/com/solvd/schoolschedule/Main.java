package com.solvd.schoolschedule;

import com.solvd.schoolschedule.model.Group;
import com.solvd.schoolschedule.model.Lesson;
import com.solvd.schoolschedule.model.SchoolConfig;
import com.solvd.schoolschedule.model.Timetable;
import com.solvd.schoolschedule.service.impl.*;
import com.solvd.schoolschedule.service.interfaces.*;
import com.solvd.schoolschedule.view.TimetableView;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        ITimetableGeneratorService timetableGenerator = new TimetableGeneratorServiceImpl();

        timetableGenerator.generateAndDisplayTimetable();

    }
}
