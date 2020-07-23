package com.github.atyranovets.api;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ITournamentSchedulerFactory {
    ITournamentScheduler create();

    /** <b>Mandatory</b> */
    ITournamentSchedulerFactory algorithm(SchedulingAlgorithm algorithm);

    /** <b>Mandatory</b> */
    ITournamentSchedulerFactory teams(String... teams);

    /**
     * <i>Optional</i>
     * How many times teams play each other teams.<br>
     * e.g. 1 => Each team plays each opponent once, 2 => twice, 3 => 3 times and so on.
     * @param laps defaults to 1
     */
    ITournamentSchedulerFactory laps(int laps);

    /** <i>Optional</i> */
    ITournamentSchedulerFactory setDates(LocalDateTime... dates);
}
