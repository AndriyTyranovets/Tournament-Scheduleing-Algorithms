package com.github.atyranovets.api.impl;

import com.github.atyranovets.api.IOutputStrategy;
import com.github.atyranovets.api.ITournamentScheduler;
import com.github.atyranovets.api.Match;
import com.github.atyranovets.api.SchedulingAlgorithm;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class SchurigTableTournamentScheduler implements ITournamentScheduler {
    private List<String> teams;
    private int laps;
    private List<LocalDateTime> dates;
    private Integer matchCount;

    private Map<Integer, List<Match>> schedule;
    private IOutputStrategy outputStrategy;

    protected SchurigTableTournamentScheduler(@Nonnull List<String> teams, int laps, @Nullable List<LocalDateTime> dates) {
        this.teams = teams;
        this.laps = laps;
        this.dates = dates;
    }

    @Override
    public void createSchedule() {
        if(CollectionUtils.isEmpty(teams)) {
            throw new IllegalStateException("Cannot create schedule without teams!");
        }

        int matchdaysInLap = teams.size() - 1 + (teams.size() & 1);

        if(CollectionUtils.isNotEmpty(dates) && dates.size() % (teams.size() - 1 + (teams.size() & 1)) == 0) {
            throw new IllegalStateException("Incorrect amount of dates! Dates amount must be a multiple of matchday amount");
        }

        schedule = Maps.newHashMap();
        String lastTeamIfEven = (teams.size() & 1) == 0 ? teams.get(teams.size() - 1) : null;
        Queue<String> homeTeams = Queues.newLinkedBlockingQueue(teams);
        if (lastTeamIfEven != null) {
            homeTeams.remove(lastTeamIfEven);
        }
        Queue<String> awayTeams = createReversedTeamQueue();
        for (int matchday = 0; matchday < matchdaysInLap; ++matchday) {
            createMatchday(matchday + 1, homeTeams, awayTeams, lastTeamIfEven, CollectionUtils.isNotEmpty(dates) ? dates.get(matchday) : null);
        }
    }

    private void createMatchday(int matchdayNumber, Queue<String> homeTeams, Queue<String> awayTeams, String lastTeamIfEven, LocalDateTime date) {
        int matchCount = teams.size() >> 1;
        List<Match> matchday = Lists.newLinkedList();
        if(lastTeamIfEven != null) {
            matchday.add(createMatch(pollAndAdd(homeTeams), lastTeamIfEven, (matchdayNumber & 1) == 0));
        }
        for(int i = 1; i < matchCount; ++i) {
            matchday.add(createMatch(pollAndAdd(homeTeams), pollAndAdd(awayTeams)));
        }
        schedule.put(matchdayNumber, matchday);
    }

    private Match createMatch(String home, String away) {
        return createMatch(home, away, false);
    }

    private Match createMatch(String home, String away, boolean doReverseLeg) {
        return new Match(doReverseLeg ? away : home, doReverseLeg ? home : away);
    }

    private Match reverseLegs(Match match) {
        return new Match(match.getAwayTeam(), match.getHomeTeam());
    }

    private LinkedBlockingQueue<String> createReversedTeamQueue() {
        LinkedList<String> reversed = new LinkedList<>(teams);
        if((reversed.size() & 1) == 0) {
            reversed.removeLast();
        }
        Collections.reverse(reversed);
        return Queues.newLinkedBlockingQueue(reversed);
    }

    private <T> T pollAndAdd(Queue<T> queue) {
        T element = queue.poll();
        queue.add(element);
        return element;
    }


    @Override
    public int getMatchdayCount() {
        return schedule.size();
    }

    @Override
    public int getMatchesCount() {
        if(this.matchCount == null) {
            this.matchCount = this.schedule.values().stream()
                    .flatMap(List::stream)
                    .map(match -> 1)
                    .reduce(Integer::sum)
                    .orElseThrow(() -> new IllegalStateException("Cannot get match count before creating schedule"));
        }
        return matchCount;
    }

    @Override
    public List<Match> getMatchday(int matchday) {
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
            this.outputStrategy.output(this.schedule);
        }
    }

    @Override
    public SchedulingAlgorithm getAlgorithm() {
        return SchedulingAlgorithm.SchurigTables;
    }
}
