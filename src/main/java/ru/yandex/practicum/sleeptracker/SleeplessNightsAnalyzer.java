package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.time.temporal.ChronoUnit;

public class SleeplessNightsAnalyzer implements Function<List<SleepSession>, SleepAnalysisResult> {
    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime NOON = LocalTime.of(12, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }
        SleepSession firstSession = sessions.stream().min(Comparator.comparing(SleepSession::getStartTime)).orElseThrow();
        SleepSession lastSession = sessions.stream().max(Comparator.comparing(SleepSession::getEndTime)).orElseThrow();

        LocalDate startDateToCheck;
        if (firstSession.getStartTime().toLocalTime().isAfter(NOON)) {
            startDateToCheck = firstSession.getStartTime().toLocalDate().minusDays(1);
        } else {
            startDateToCheck = firstSession.getStartTime().toLocalDate();
        }
        LocalDate endDateToCheck = lastSession.getEndTime().toLocalDate();
        if (endDateToCheck.isBefore(startDateToCheck)) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }
        long totalDays = ChronoUnit.DAYS.between(startDateToCheck, endDateToCheck) + 1;
        long sleeplessCount = IntStream.range(0, (int) totalDays).mapToObj(i -> startDateToCheck.plusDays(i)).filter(date -> isNightSleepless(date, sessions)).count();
        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessCount);
    }


    private boolean isNightSleepless(LocalDate date, List<SleepSession> sessions) {
        LocalDateTime intervalStart = date.atTime(NIGHT_START);
        LocalDateTime intervalEnd = date.atTime(NIGHT_END);

        boolean hasRelevantSleep = sessions.stream().anyMatch(session -> {
            LocalDateTime startTime = session.getStartTime();
            LocalDateTime endTime = session.getEndTime();

            boolean intersectsNightInterval = startTime.isBefore(intervalEnd) && endTime.isAfter(intervalStart);
            boolean startedThisDay = startTime.toLocalDate().equals(date);

            return intersectsNightInterval || startedThisDay;
        });

        return !hasRelevantSleep;
    }

}
