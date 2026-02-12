package ru.yandex.practicum.sleeptracker;

import java.util.List;

public final class CountSessionsAnalysis implements SleepAnalysisFunction {
    @Override
    public SleepAnalysisResult<Integer> apply(List<SleepingSession> sessions) {
        int count = (sessions == null) ? 0 : sessions.size();
        return new SleepAnalysisResult<>("Всего сессий сна", count);
    }
}
