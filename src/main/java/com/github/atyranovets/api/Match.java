package com.github.atyranovets.api;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

public final class Match {
    private String homeTeam;
    private String awayTeam;

    private LocalDateTime date;

    public Match() { /* Empty constructor */ }

    public Match(String homeTeam, String awayTeam) {
        this(homeTeam, awayTeam, null);
    }

    public Match(String homeTeam, String awayTeam, LocalDateTime date) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
    }


    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Match match = (Match) o;

        return new EqualsBuilder()
                .append(homeTeam, match.homeTeam)
                .append(awayTeam, match.awayTeam)
                .append(date, match.date)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(homeTeam)
                .append(awayTeam)
                .append(date)
                .toHashCode();
    }
}
