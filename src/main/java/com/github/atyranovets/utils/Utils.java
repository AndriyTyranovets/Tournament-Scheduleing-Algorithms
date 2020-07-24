package com.github.atyranovets.utils;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.BiFunction;

public class Utils {
    public static String formatMatch(String home, String away) {
        return new StringBuilder().append(home).append(" vs. ").append(away).toString();
    }

    public static String formatMatchdayTitle(int matchday) {
        return formatMatchdayTitle(matchday, null);
    }

    public static String formatMatchdayTitle(int matchday, LocalDateTime date) {
        StringBuilder builder = new StringBuilder();
        builder.append("Matchday ").append(matchday);
        if(date != null) {
            builder.append(" (").append(date.getDayOfMonth()).append(" ").append(date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)).append(")");
        }
        return builder.toString();
    }
}
