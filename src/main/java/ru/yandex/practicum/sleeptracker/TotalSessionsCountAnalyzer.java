package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class TotalSessionsCountAnalyzer implements Function<List<SleepSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepSession> sessions) {
        long count = sessions != null ? sessions.size() : 0;
        return new SleepAnalysisResult("Общее количество сессий сна", count);
    }
}