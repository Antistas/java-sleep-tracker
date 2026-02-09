package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CountSessionsAnalysisTest {

    private final CountSessionsAnalysis analysis = new CountSessionsAnalysis();

    @Test
    void shouldReturnZeroWhenEmptyList() {
        var result = analysis.apply(List.of());
        assertEquals("Всего сессий сна", result.description());
        assertEquals(0, result.value());
    }

    @Test
    void shouldCountSessionsCorrectly() {
        var sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025,10,1,22,15),
                        LocalDateTime.of(2025,10,2,8,0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025,10,3,14,30),
                        LocalDateTime.of(2025,10,3,15,20), SleepQuality.NORMAL)
        );

        var result = analysis.apply(sessions);
        assertEquals(2, result.value());
    }
}
