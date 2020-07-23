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
import java.util.function.Supplier;

public class SchurigTableTournamentSchedulerTest {
    private final List<String> TEAMS = ImmutableList.of("A", "B", "C", "D", "E", "F", "G", "H");
    private final int LAPS = 1;
    private final List<LocalDateTime> DATES = null;
    private static final Path resourcePath = Paths.get("src", "test", "resources", "com.github.atyranovets.api.impl");
    private final String resultResource = "SchurigTableTournamentSchedulerTest.results";

    private Supplier<String> result = () -> null;

    private IOutputStrategy testOutputStrategy = schedule -> {
        var builder = new StringBuilder();
        for(int i : schedule.keySet()) {
            builder.append(i).append('|');
            List<Match> matchday = schedule.get(i);
            for(var match : matchday) {
                builder.append(match.getHomeTeam()).append(match.getAwayTeam()).append('|');
            }
            builder.append('\n');
        }
        result = builder::toString;
    };

    @Test
    public void testSchurigTableTournamentScheduler() throws Exception {
        var scheduler = new SchurigTableTournamentScheduler(TEAMS, LAPS, DATES);
        scheduler.setOutputStrategy(testOutputStrategy);
        scheduler.createSchedule();
        scheduler.output();
        var result = this.result.get();
        System.out.println(result);
        Assert.assertNotNull(result);
        Assert.assertEquals(getResourceAsString(resultResource), result);
    }

    private static String getResourceAsString(String resourceName) throws IOException {
        return Files.readString(new File(resourcePath.resolve(resourceName).toString()).getAbsoluteFile().toPath());
    }
}
