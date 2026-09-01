package ru.yandex.practicum.sleeptracker;


import java.util.List;
import java.util.function.Function;


public class AverageDurationAnalyzer implements Function<List<SleepSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Средняя длительность сна (мин)", 0.0);
        }

        double average = sessions.stream()
                .mapToLong(SleepSession::getDurationMinutes)
                .average()
                .orElse(0.0);

        return new SleepAnalysisResult("Средняя длительность сна (мин)", average);
    }
}