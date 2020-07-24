package com.github.atyranovets.api.impl;

import com.github.atyranovets.api.IOutputStrategy;
import com.github.atyranovets.api.Match;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BergerTablesTournamentSchedulerTest {
    private static final Path resourcePath = Paths.get("src", "test", "resources", "com.github.atyranovets.api.impl");
    private static final String resultResource = "BergerTablesTournamentSchedulerTest.results";

    private final List<String> TEAMS = ImmutableList.of("A", "B", "C", "D", "E", "F", "G", "H");
    private final int LAPS = 1;
    private final List<LocalDateTime> DATES = null;

    private Supplier<String> result = () -> null;

    private IOutputStrategy testOutputStrategy = schedule -> {
        var builder = new StringBuilder();

        int teamCount = getTeamCount(schedule);
        int[][] table = new int[teamCount][teamCount];
        for(int i : schedule.keySet()) {
            List<Match> matchday = schedule.get(i);
            for(var match : matchday) {
                table[getTeamOrdinal(match.getHomeTeam())][getTeamOrdinal(match.getAwayTeam())] = i;
                table[getTeamOrdinal(match.getAwayTeam())][getTeamOrdinal(match.getHomeTeam())] = i;
            }
        }

        builder.append(' ');
        builder.append('|');
        TEAMS.forEach(builder::append);
        builder.append('\n');
        IntStream.range(0, TEAMS.size() + 2).forEach(i -> builder.append('_'));
        builder.append('\n');

        for (int i = 0; i < table.length; i++) {
            var row = table[i];
            builder.append(TEAMS.get(i));
            builder.append('|');
            for (int j = 0; j < row.length; j++) {
                if (i == j) {
                    builder.append('x');
                }
                else {
                    builder.append(row[j]);
                }
            }
            builder.append('\n');
        }

        result = builder::toString;
    };


    @Test
    public void testBergerTablesTournamentScheduler() throws Exception {
        var scheduler = new BergerTablesTournamentScheduler(TEAMS, LAPS, DATES);
        scheduler.setOutputStrategy(testOutputStrategy);
        scheduler.createSchedule();
        scheduler.output();
        var result = this.result.get();
        Assert.assertNotNull(result);
        System.out.println(result);
        Assert.assertEquals(getResourceAsString(resultResource), result);
    }

    /**
     * [Team Count] = [Rounds Count] + 1 - ([Games per Round] & 0b0001 / parity check/),
     * because [Rounds Count] = [Team Count] - 1 /when team count is even/ or [Rounds Count] = [Team Count] /when team count is odd/
     * and [Games per Round] = [Team Count] / 2 /when team count is even/ or [Games per Round] = ([Team Count] + 1) / 2 /when team count is odd/
     */
    private static int getTeamCount(Map<Integer, List<Match>> schedule) {
        final int roundsCount = schedule.size();
        final int gamesPerRound = schedule.values().stream().findFirst().orElseThrow().size();
        return roundsCount + 1 - (gamesPerRound & 1);
    }

    private static int getTeamOrdinal(String team) {
        return team.charAt(0) - 'A';
    }

    private static String getResourceAsString(String resourceName) throws IOException {
        return Files.readString(new File(resourcePath.resolve(resourceName).toString()).getAbsoluteFile().toPath());
    }
}
