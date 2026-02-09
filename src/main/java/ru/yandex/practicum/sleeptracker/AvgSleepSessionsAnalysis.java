package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class AvgSleepSessionsAnalysis implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<String> apply(List<SleepingSession> sessions) {
        // немного странный вариант от chatGPT - если есть альтернатива, буду рад услышать
        OptionalDouble averageSleepSession = sessions.stream()
                .map(SleepingSession::getDuration)
                .mapToLong(Duration::toMinutes)
                .average();

        if (averageSleepSession.isPresent()) {
            long d = Math.round(averageSleepSession.getAsDouble());
            long hours = d / 60;
            long minutes = d % 60;

            return new SleepAnalysisResult<>(
                    "Средняя продолжительность сна", hours + "ч. "
                    + minutes + "мин.");
        } else {
            return new SleepAnalysisResult<>("Средняя продолжительность сна", "не определена");
        }
    }
}