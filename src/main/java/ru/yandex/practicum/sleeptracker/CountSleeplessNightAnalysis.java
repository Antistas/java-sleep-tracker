package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountSleeplessNightAnalysis implements SleepAnalysisFunction {

    private static final LocalTime NOON = LocalTime.NOON;

    @Override
    public SleepAnalysisResult<Integer> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Количество бессонных ночей", 0);
        }
        // берем первую и последнюю запись для определения границ лога
        LocalDateTime startLogging = sessions.getFirst().getFellAsleepAt();
        LocalDateTime endLogging = sessions.getLast().getWokeUpAt();

        LocalDate startNightDate = firstPotentialNightDate(startLogging);
        LocalDate endNightDate = endLogging.toLocalDate();

        // Вычисляем ночи, где человек точно спал
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .flatMap(this::nightDatesOverlappedBySession)
                .collect(Collectors.toSet());

        // генерируем все даты ОТ начала и ДО конца лога
        long sleepless = startNightDate.datesUntil(endNightDate.plusDays(1))
                // считаем только ночи, которые пересекаются с интервалом логирования
                .filter(d -> nightIntersectsLogging(d, startLogging, endLogging))
                // оставляем только те даты, в которых были сессии (иначе данных нет)
                .filter(d -> hasAnySleepInfoForDate(d, sessions))
                // убираем даты, где пользователь точно ночью спал
                .filter(d -> !nightsWithSleep.contains(d))
                .count();

        return new SleepAnalysisResult<>("Количество бессонных ночей", (int) sleepless);
    }

    // Есть ли какие-нибудь сессии в указанное окно date и date + 1 день?
    private boolean hasAnySleepInfoForDate(LocalDate date, List<SleepingSession> sessions) {
        return sessions.stream().anyMatch(s ->
                overlaps(s.getFellAsleepAt(), s.getWokeUpAt(), date.atStartOfDay(), date.atStartOfDay().plusDays(1))
        );
    }

    private Stream<LocalDate> nightDatesOverlappedBySession(SleepingSession s) {
        LocalDate startDate = s.getFellAsleepAt().toLocalDate();
        LocalDate endDate = s.getWokeUpAt().toLocalDate();
        // Генерируются 2 даты, возвращаем пустой стрим, если нет ночного сна. Или с датой (сл. день) если есть.
        return startDate.datesUntil(endDate.plusDays(1))
                .filter(d -> overlapsNight(d, s.getFellAsleepAt(), s.getWokeUpAt()));
    }

    // пересекает ли эта сессия ночное окно
    private boolean overlapsNight(LocalDate nightDate, LocalDateTime sessionStart, LocalDateTime sessionEnd) {
        LocalDateTime nightStart = nightDate.atStartOfDay(); // 00:00
        LocalDateTime nightEnd = nightStart.plusHours(6); // 06:00
        return overlaps(sessionStart, sessionEnd, nightStart, nightEnd);
    }

    private boolean overlaps(LocalDateTime sessionStart, LocalDateTime sessionEnd, LocalDateTime start, LocalDateTime end) {
        return sessionStart.isBefore(end) && sessionEnd.isAfter(start);
    }

    // определяем первую ночь
    private LocalDate firstPotentialNightDate(LocalDateTime firstSessionStart) {
        LocalDate d = firstSessionStart.toLocalDate();
        return firstSessionStart.toLocalTime().isAfter(NOON) ? d.plusDays(1) : d;
    }

    private boolean nightIntersectsLogging(LocalDate nightDate, LocalDateTime startLogging, LocalDateTime endLogging) {
        LocalDateTime nightStart = nightDate.atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6);
        return overlaps(nightStart, nightEnd, startLogging, endLogging);
    }
}