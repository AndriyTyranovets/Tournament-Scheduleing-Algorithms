package com.github.atyranovets.api;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ITournamentScheduler {
    void createSchedule();
    int getMatchdayCount();
    int getMatchesCount();
    List<Match> getMatchday(int matchday);
    Map<LocalDate, Match> getMatchdayWithDates(int matchday);
    void setOutputStrategy(IOutputStrategy outputStrategy);
    void output();
    SchedulingAlgorithm getAlgorithm();
}
