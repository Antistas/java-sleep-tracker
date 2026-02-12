package ru.yandex.practicum.sleeptracker;

import java.util.List;

public class CountBadSleepSessions implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Long> apply(List<SleepingSession> sessions) {
        long countBadSleepSessions = sessions.stream().filter(SleepingSession::isBad).count();
        return new SleepAnalysisResult<>("Количество сессий с плохим качеством сна", countBadSleepSessions);
    }

}