package com.github.atyranovets.api.impl;

import com.github.atyranovets.api.ITournamentScheduler;
import com.github.atyranovets.api.ITournamentSchedulerFactory;
import com.github.atyranovets.api.SchedulingAlgorithm;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TournamentSchedulerFactory implements ITournamentSchedulerFactory {
    private SchedulingAlgorithm algorithm;
    private List<String> teams;
    private List<LocalDateTime> dates;
    private int laps;

    public TournamentSchedulerFactory() {
        this.algorithm = SchedulingAlgorithm.SchurigTables;
        this.laps = 1;
    }

    @Override
    public ITournamentScheduler create() {
        if(CollectionUtils.isEmpty(this.teams)) {
            throw new IllegalStateException("Teams are required!");
        }
        if(CollectionUtils.isNotEmpty(this.dates) || this.dates.size() % (this.teams.size() - 1) != 0) {
            throw new IllegalStateException("Dates amount should be multiple of matchday amounts!");
        }

        switch (algorithm) {
            case BergerTables:
                return new BergerTablesTournamentScheduler(this.teams, this.laps, this.dates);
            case CircleMethod:
                throw new IllegalStateException(algorithm.name() + " not yet implemented");
            case SchurigTables:
                return new SchurigTableTournamentScheduler(this.teams, this.laps, this.dates);
        }
        throw new IllegalStateException("Invalid data for scheduler!");
    }

    @Override
    public ITournamentSchedulerFactory algorithm(SchedulingAlgorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    @Override
    public ITournamentSchedulerFactory teams(String... teams) {
        if(teams.length < 2) {
            throw new IllegalStateException("Number of teams should be at least 3!");
        }
        this.teams = List.of(teams);
        return this;
    }

    @Override
    public ITournamentSchedulerFactory laps(int laps) {
        this.laps = laps;
        return this;
    }

    @Override
    public ITournamentSchedulerFactory setDates(LocalDateTime... dates) {
        this.dates = Lists.newArrayList(dates);
        return this;
    }
}
