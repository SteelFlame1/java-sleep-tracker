package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class BadQualitySessionsAnalyzer implements Function<List<SleepSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepSession> sessions) {
        long count = sessions.stream().filter(s -> s.getDurationMinutes() < 360).count();
        return new SleepAnalysisResult("Количество сессий с плохим качеством сна (<6 часов)", count);
    }
}