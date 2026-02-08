package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MaxSleepSessionsAnalysis implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<String> apply(List<SleepingSession> sessions) {

        Optional<SleepingSession> session = sessions.stream().max(Comparator.comparing(SleepingSession::getDuration));
        if (session.isPresent()) {
            Duration d = session.get().getDuration();
            return new SleepAnalysisResult<>("Максимальная продолжительность сна", d.toHours() + "ч. "
                    + d.toMinutesPart() + "мин.");
        } else {
            return new SleepAnalysisResult<>("Максимальная продолжительность", "не определена");
        }
    }
}