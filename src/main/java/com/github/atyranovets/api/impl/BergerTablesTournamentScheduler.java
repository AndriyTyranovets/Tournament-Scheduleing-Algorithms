package com.github.atyranovets.api.impl;

import com.github.atyranovets.api.IOutputStrategy;
import com.github.atyranovets.api.ITournamentScheduler;
import com.github.atyranovets.api.Match;
import com.github.atyranovets.api.SchedulingAlgorithm;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BergerTablesTournamentScheduler implements ITournamentScheduler {
    private IOutputStrategy outputStrategy;

    private List<String> teams;
    private int laps;
    private List<LocalDateTime> dates;
    private int[][] table;
    private Map<Integer, List<Match>> schedule;
    private Integer matchCount;

    protected BergerTablesTournamentScheduler(List<String> teams, int laps, List<LocalDateTime> dates) {
        this.teams = teams;
        this.laps = laps;
        this.dates = dates;
    }

    @Override
    public void createSchedule() {
        this.table = new int[this.teams.size()][this.teams.size()];

        var teamsToCover = this.teams.size() - 1 + (this.teams.size() & 1);

        fillTableUpToLastOddTeam(teamsToCover);
        fillLastTeamIfEven(teamsToCover);
        fillSchedule();
    }

    private int fillTableUpToLastOddTeam(int teamsToCover) {
        var roundCounter = 0;
        for(var i = 0; i < teamsToCover; ++i) {
            for(var j = 0; j < teamsToCover; ++j) {
                this.table[i][j] = roundCounter++ % teamsToCover + 1;
            }
            roundCounter++;
        }
        return teamsToCover;
    }

    private void fillLastTeamIfEven(int teamsToCover) {
        if (teamsToCover != teams.size()) {
            var lastTeam = this.teams.size() - 1;
            for(var i = 0; i < teams.size(); ++i) {
                var lastTeamMatchday = this.table[i][i];
                this.table[lastTeam][i] = this.table[i][lastTeam] = lastTeamMatchday;
            }
        }
    }

    private void fillSchedule() {
        this.schedule = Maps.newHashMap();
        for(var i = 0; i < teams.size(); ++i) {
            for(var j = i + 1; j < teams.size(); ++j) {
                if (i != j) {
                    var md = this.table[i][j];
                    //TODO: Revamp or remove dates from scheduling
                    var mdDate = CollectionUtils.isNotEmpty(this.dates) ? this.dates.get(md) : null;
                    var match = createMatch(this.teams.get(i), this.teams.get(j), mdDate);
                    List<Match> matchdayList = this.schedule.computeIfAbsent(md, ArrayList::new);
                    matchdayList.add(match);
                }
            }
        }
    }

    private Match createMatch(@Nonnull String home, @Nonnull String away, @Nullable LocalDateTime date) {
        final Match match = new Match(home, away);
        if(date != null) {
            match.setDate(date);
        }
        return match;
    }

    @Override
    public int getMatchdayCount() {
        if(schedule == null) {
            throw new IllegalStateException("Cannot get matchday count before schedule created!");
        }
        return schedule.size();
    }

    @Override
    public int getMatchesCount() {
        if(schedule == null) {
            throw new IllegalStateException("Cannot get matchday count before schedule created!");
        }
        if(matchCount == null) {
            this.matchCount = this.schedule.values().stream()
                    .flatMap(List::stream)
                    .map(match -> 1)
                    .reduce(Integer::sum)
                    .orElseThrow(() -> new IllegalStateException("Cannot get match count before schedule created!"));
        }
        return matchCount;
    }

    @Override
    public List<Match> getMatchday(int matchday) {
        if(schedule == null) {
            throw new IllegalStateException("Cannot get matchday count before schedule created!");
        }
        return schedule.get(matchday);
    }

    @Override
    public Map<LocalDate, Match> getMatchdayWithDates(int matchday) {
        return null;
    }

    @Override
    public void setOutputStrategy(IOutputStrategy outputStrategy) {
        this.outputStrategy = outputStrategy;
    }

    @Override
    public void output() {
        if(this.outputStrategy != null) {
            this.outputStrategy.output(schedule);
        }
    }

    @Override
    public SchedulingAlgorithm getAlgorithm() {
        return SchedulingAlgorithm.BergerTables;
    }
}
