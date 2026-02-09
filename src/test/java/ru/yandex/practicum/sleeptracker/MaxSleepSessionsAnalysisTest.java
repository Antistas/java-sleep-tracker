package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MaxSleepSessionsAnalysisTest {

    private final MaxSleepSessionsAnalysis analysis = new MaxSleepSessionsAnalysis();

    @Test
    void shouldReturnZeroWhenEmptyList() {
        var result = analysis.apply(List.of());
        assertEquals("Максимальная продолжительность сна", result.description());
        assertEquals("не определена", result.value());
    }

    @Test
    void shouldCountSessionsCorrectly() {
        var sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025,10,1,22,15),
                        LocalDateTime.of(2025,10,2,8,0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025,10,3,14,30),
                        LocalDateTime.of(2025,10,3,15,20), SleepQuality.NORMAL),
                new SleepingSession(LocalDateTime.of(2025,10,3,22,30),
                        LocalDateTime.of(2025,10,4,9,20), SleepQuality.NORMAL),
                new SleepingSession(LocalDateTime.of(2025,10,4,23,30),
                        LocalDateTime.of(2025,10,5,7,15), SleepQuality.BAD)
        );

        var result = analysis.apply(sessions);
        assertEquals("10ч. 50мин.", result.value());
    }
}