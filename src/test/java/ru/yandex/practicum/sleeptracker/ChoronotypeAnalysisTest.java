package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ChoronotypeAnalysisTest {

    private final ChronotypeAnalysis analysis = new ChronotypeAnalysis();

    @Test
    void shouldBeUndefinedChronotypeWhenEmptyList() {
        var result = analysis.apply(List.of());
        assertEquals("Хронотип пользователя", result.description());
        assertEquals(Chronotype.UNDEFINED, result.value());
    }

    @Test
    void shouldReturnOwl() {
        var sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025,10,1,23,15),
                        LocalDateTime.of(2025,10,2,9,1), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025,10,3,23,55),
                        LocalDateTime.of(2025,10,4,10,10), SleepQuality.NORMAL),
                new SleepingSession(LocalDateTime.of(2025,10,4,20,30),
                    LocalDateTime.of(2025,10,5,10,20), SleepQuality.NORMAL)
        );

        var result = analysis.apply(sessions);
        assertEquals(Chronotype.OWL, result.value());
    }

    @Test
    void shouldReturnLark() {
        var sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025,10,1,23,15),
                        LocalDateTime.of(2025,10,2,9,1), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025,10,3,21,55),
                        LocalDateTime.of(2025,10,4,6,59), SleepQuality.NORMAL),
                new SleepingSession(LocalDateTime.of(2025,10,4,20,30),
                        LocalDateTime.of(2025,10,5,6,20), SleepQuality.NORMAL)
        );

        var result = analysis.apply(sessions);
        assertEquals(Chronotype.LARK, result.value());
    }

    @Test
    void shouldReturnPigeonWhenOneLarkOneOwl() {
        var sessions = List.of(
                new SleepingSession(LocalDateTime.of(2025,10,1,23,15),
                        LocalDateTime.of(2025,10,2,9,1), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025,10,3,21,55),
                        LocalDateTime.of(2025,10,4,6,59), SleepQuality.NORMAL)
        );

        var result = analysis.apply(sessions);
        assertEquals(Chronotype.PIGEON, result.value());
    }


    @Test
    void shouldReturnPigeonWhenOneLarkOneOwlOnePigeonWithDaySleep() {
        List<SleepingSession> sessions = List.of(
                // undefined
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 2, 18, 0),
                        LocalDateTime.of(2025, 11, 2, 23, 59),
                        SleepQuality.BAD
                ),
                // undefined
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 3, 6, 10),
                        LocalDateTime.of(2025, 11, 3, 12, 0),
                        SleepQuality.NORMAL
                ),
                // pigeon
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 4, 23, 10),
                        LocalDateTime.of(2025, 11, 5, 7, 30),
                        SleepQuality.NORMAL
                ),
                // lark
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 5, 21, 50),
                        LocalDateTime.of(2025, 11, 6, 6, 50),
                        SleepQuality.NORMAL
                ),
                // undefined
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 6, 14, 10),
                        LocalDateTime.of(2025, 11, 6, 15, 50),
                        SleepQuality.NORMAL
                ),
                // owl
                new SleepingSession(
                        LocalDateTime.of(2025, 11, 7, 0, 40),
                        LocalDateTime.of(2025, 11, 7, 9, 50),
                        SleepQuality.NORMAL
                )
        );

        var result = analysis.apply(sessions);
        assertEquals(Chronotype.PIGEON, result.value());
    }
}
