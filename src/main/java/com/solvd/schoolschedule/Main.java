package com.solvd.schoolschedule;

import com.solvd.schoolschedule.service.impl.*;
import com.solvd.schoolschedule.service.interfaces.*;

public class Main {
    public static void main(String[] args) {

        ITimetableGeneratorService timetableGenerator = new TimetableGeneratorServiceImpl();

        timetableGenerator.generateAndDisplayTimetable();

    }
}
