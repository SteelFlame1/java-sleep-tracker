package ru.yandex.practicum.sleeptracker;

import java.util.function.Function;
import java.util.List;
import java.util.OptionalLong;

public class MinDurationAnalyzer implements Function<List<SleepSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Минимальная продолжительность сессии (мин)", 0L);
        }
        OptionalLong min = sessions.stream().mapToLong(SleepSession::getDurationMinutes).min();
        return new SleepAnalysisResult("Минимальная продолжительность сессии (мин)", min.orElse(0L));
    }
}