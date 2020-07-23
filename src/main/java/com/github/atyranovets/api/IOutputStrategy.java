package com.github.atyranovets.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface IOutputStrategy {
    void output(Map<Integer, List<Match>> schedule);
}
