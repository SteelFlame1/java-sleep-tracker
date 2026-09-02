package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class GoodNightsCountAnalyzer implements Function<List<SleepSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepSession> sessions) {
        long count = sessions.stream()
                .filter(s -> s.getDurationMinutes() > 420)
                .count();

        return new SleepAnalysisResult("Количество ночей с хорошим сном (>7 часов)", count);
    }
}