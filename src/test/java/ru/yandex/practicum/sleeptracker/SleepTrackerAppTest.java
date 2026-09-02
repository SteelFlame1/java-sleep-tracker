package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {
    private final SleeplessNightsAnalyzer analyzer = new SleeplessNightsAnalyzer();

    @Test
    void shouldCorrectlyHandleOvernightSessionAndDateShift() {

        List<SleepSession> sessions = List.of(new SleepSession(LocalDateTime.of(2026, 9, 1, 23, 0), LocalDateTime.of(2026, 9, 2, 3, 0)));

        var result = analyzer.apply(sessions);
        assertEquals(1L, result.getValue(), "Должна быть 1 бессонная ночь (31.08), остальные покрыты сном");
    }

    @Test
    void shouldCorrectlyHandleSessionStartingExactlyAtNoon() {

        List<SleepSession> sessions = List.of(new SleepSession(LocalDateTime.of(2026, 9, 1, 12, 0), LocalDateTime.of(2026, 9, 1, 14, 0)));

        var result = analyzer.apply(sessions);
        assertEquals(0L, result.getValue(), "Сессия началась ровно в полдень, сдвига нет, ночь покрыта.");
    }

    @Test
    void shouldCountNightAsSleeplessIfOnlyDaytimeSleepExists() {

        List<SleepSession> sessions = List.of(new SleepSession(LocalDateTime.of(2026, 9, 1, 14, 0), LocalDateTime.of(2026, 9, 1, 16, 0)));

        var result = analyzer.apply(sessions);
        assertEquals(1L, result.getValue(), "Ночь должна считаться бессонной, так как в интервале 00:00-06:00 сна не было.");
    }

    @Test
    void shouldCountMultipleSleeplessNightsInGap() {

        List<SleepSession> sessions = List.of(
                new SleepSession(LocalDateTime.of(2026, 10, 1, 23, 0), LocalDateTime.of(2026, 10, 2, 3, 0)),
                new SleepSession(LocalDateTime.of(2026, 10, 10, 23, 0), LocalDateTime.of(2026, 10, 11, 3, 0)));

        var result = analyzer.apply(sessions);
        assertEquals(8L, result.getValue(), "Должно быть 8 бессонных ночей: все ночи с 03.10 по 10.10 включительно.");
    }

    @Test
    void shouldDetectLarkWhenAllNightsAreEarly() {
        List<SleepSession> sessions = List.of(
                new SleepSession(LocalDateTime.of(2026, 9, 1, 21, 0), LocalDateTime.of(2026, 9, 2, 6, 0)),
                new SleepSession(LocalDateTime.of(2026, 9, 2, 21, 30), LocalDateTime.of(2026, 9, 3, 6, 30)));
        var result = new ChronotypeAnalyzer().apply(sessions);
        assertEquals(Chronotype.LARK, result, "Должен быть жаворонок, так как все ночи ранние");
    }

}