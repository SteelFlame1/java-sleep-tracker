package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.OptionalLong;
import java.util.function.Function;

public class MaxDurationAnalyzer implements Function<List<SleepSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepSession> sessions) {
        OptionalLong max = sessions.stream()
                .mapToLong(SleepSession::getDurationMinutes)
                .max();

        long maxValue = max.orElse(0L);
        return new SleepAnalysisResult("Максимальная длительность сна (мин)", maxValue);
    }
}