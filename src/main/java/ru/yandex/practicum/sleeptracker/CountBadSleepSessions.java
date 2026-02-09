package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CountBadSleepSessions implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long countBadSleepSessions = sessions.stream().filter(SleepingSession::isBad).count();
        return new SleepAnalysisResult<>("Количество сессий с плохим качеством сна", countBadSleepSessions);
    }

}