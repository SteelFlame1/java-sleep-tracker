package ru.yandex.practicum.sleeptracker;

import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class ChronotypeAnalyzer implements Function<List<SleepSession>, Chronotype> {
    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime NOON = LocalTime.of(12, 0);
    private static final LocalTime OWL_SLEEP_LIMIT = LocalTime.of(23, 0);
    private static final LocalTime OWL_WAKE_LIMIT = LocalTime.of(9, 0);
    private static final LocalTime LARK_SLEEP_LIMIT = LocalTime.of(22, 0);
    private static final LocalTime LARK_WAKE_LIMIT = LocalTime.of(7, 0);

    @Override
    public Chronotype apply(List<SleepSession> sessions) {
        if (sessions.isEmpty()) {
            return Chronotype.PIGEON;
        }
        SleepSession firstSession = sessions.stream().min(Comparator.comparing(SleepSession::getStartTime)).orElseThrow();
        SleepSession lastSession = sessions.stream().max(Comparator.comparing(SleepSession::getEndTime)).orElseThrow();
        LocalDate startDateToCheck = firstSession.getStartTime().toLocalTime().isAfter(NOON) ? firstSession.getStartTime().toLocalDate().minusDays(1) : firstSession.getStartTime().toLocalDate();
        LocalDate endDateToCheck = lastSession.getEndTime().toLocalDate();
        if (endDateToCheck.isBefore(startDateToCheck)) {
            return Chronotype.PIGEON;
        }
        long totalDays = ChronoUnit.DAYS.between(startDateToCheck, endDateToCheck) + 1;
        return IntStream.range(0, (int) totalDays).mapToObj(i -> startDateToCheck.plusDays(i)).filter(date -> hasSleepInNight(date, sessions)).map(date -> findCoveringSession(date, sessions)).map(this::classifyNight).collect(() -> new Stats(), Stats::add, Stats::combine).getResult();
    }

    private boolean hasSleepInNight(LocalDate date, List<SleepSession> sessions) {
        LocalDateTime intervalStart = date.atTime(NIGHT_START);
        LocalDateTime intervalEnd = date.atTime(NIGHT_END);
        return sessions.stream().anyMatch(session -> session.getStartTime().isBefore(intervalEnd) && session.getEndTime().isAfter(intervalStart));
    }

    private SleepSession findCoveringSession(LocalDate date, List<SleepSession> sessions) {
        LocalDateTime intervalStart = date.atTime(NIGHT_START);
        LocalDateTime intervalEnd = date.atTime(NIGHT_END);
        return sessions.stream().filter(session -> session.getStartTime().isBefore(intervalEnd) && session.getEndTime().isAfter(intervalStart)).findFirst().orElseThrow();
    }

    private Chronotype classifyNight(SleepSession session) {
        LocalTime sleepTime = session.getStartTime().toLocalTime();
        LocalTime wakeTime = session.getEndTime().toLocalTime();
        boolean isOwl = sleepTime.isAfter(OWL_SLEEP_LIMIT) && wakeTime.isAfter(OWL_WAKE_LIMIT);
        boolean isLark = sleepTime.isBefore(LARK_SLEEP_LIMIT) && wakeTime.isBefore(LARK_WAKE_LIMIT);
        if (isOwl) return Chronotype.OWL;
        if (isLark) return Chronotype.LARK;
        return Chronotype.PIGEON;
    }

    private static class Stats {
        private int owlCount = 0;
        private int larkCount = 0;
        private int pigeonCount = 0;

        void add(Chronotype type) {
            switch (type) {
                case OWL -> owlCount++;
                case LARK -> larkCount++;
                case PIGEON -> pigeonCount++;
            }
        }

        void combine(Stats other) {
            this.owlCount += other.owlCount;
            this.larkCount += other.larkCount;
            this.pigeonCount += other.pigeonCount;
        }

        Chronotype getResult() {
            int max = Math.max(owlCount, Math.max(larkCount, pigeonCount));

            boolean owlIsMax = owlCount == max;
            boolean larkIsMax = larkCount == max;
            boolean pigeonIsMax = pigeonCount == max;

            if (pigeonIsMax) {
                return Chronotype.PIGEON;
            }
            if (owlIsMax && !larkIsMax) {
                return Chronotype.OWL;
            }
            if (larkIsMax && !owlIsMax) {
                return Chronotype.LARK;
            }

            return Chronotype.PIGEON;
        }
    }
}