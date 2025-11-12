package com.solvd.schoolschedule;

import com.solvd.schoolschedule.service.impl.TimetableGeneratorServiceImpl;
import com.solvd.schoolschedule.service.interfaces.ITimetableGeneratorService;

public class Main {
    public static void main(String[] args) {

        ITimetableGeneratorService timetableGenerator = new TimetableGeneratorServiceImpl();

        timetableGenerator.generateAndDisplayTimetable();
    }
}
