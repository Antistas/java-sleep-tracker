package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CountSleeplessNightAnalysisTest {

    private final CountSleeplessNightAnalysis analysis = new CountSleeplessNightAnalysis();

    @Test
    void shouldReturnZeroWhenEmptyList() {
        var result = analysis.apply(List.of());
        assertEquals("Количество бессонных ночей", result.description());
        assertEquals(0, result.value());
    }

    @Test
    void shouldBeZeroSleeplessNight() {
        List<SleepingSession> sessions = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            LocalDateTime start = LocalDateTime.of(2025,10, i,22,15);
            LocalDateTime end = LocalDateTime.of(2025,10,i + 1,8,0);
            sessions.add(new SleepingSession(start, end, SleepQuality.NORMAL));
        }

        var result = analysis.apply(sessions);
        assertEquals(0, result.value());
    }

    @Test
    void shouldReturnZeroWhenNoNightInLoggingInterval() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 10),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        var result = analysis.apply(sessions);
        assertEquals(0, result.value());
    }

    @Test
    void shouldCountOneSleeplessNight() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 2, 18, 0),
                        LocalDateTime.of(2025, 10, 2, 23, 50),
                        SleepQuality.BAD
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 10, 3, 14, 10),
                        LocalDateTime.of(2025, 10, 3, 15, 0),
                        SleepQuality.NORMAL
                )
        );

        var result = analysis.apply(sessions);
        assertEquals(1, result.value());
    }

    @Test
    void shouldCountTwoSleeplessNight() {
        List<SleepingSession> sessions = List.of(
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 2, 18, 0),
                        LocalDateTime.of(2025, 11, 2, 23, 59),
                        SleepQuality.BAD
                ),
                // одна бессоная ночь
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 3, 6, 10),
                        LocalDateTime.of(2025, 11, 3, 12, 0),
                        SleepQuality.NORMAL
                ),
                // вторая бессоная ночь
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 4, 23, 10),
                        LocalDateTime.of(2025, 11, 5, 7, 30),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 5, 23, 50),
                        LocalDateTime.of(2025, 11, 6, 6, 50),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 6, 14, 10),
                        LocalDateTime.of(2025, 11, 6, 15, 50),
                        SleepQuality.NORMAL
                ),
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 6, 23, 40),
                        LocalDateTime.of(2025, 11, 7, 8, 50),
                        SleepQuality.NORMAL
                )
        );

        var result = analysis.apply(sessions);
        assertEquals(2, result.value());
    }
}