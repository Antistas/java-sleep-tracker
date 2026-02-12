package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ChronotypeAnalysis implements SleepAnalysisFunction {

    @Override
    public SleepAnalysisResult<Chronotype> apply(List<SleepingSession> sessions) {
        if (sessions == null || sessions.isEmpty()) {
            return new SleepAnalysisResult<>("Хронотип пользователя", Chronotype.UNDEFINED);
        }

        // определяем ночные сессии (пересекают окно 0 - 6)
        Set<LocalDate> nightsWithSleep = sessions.stream()
                .flatMap(this::nightDatesOverlappedBySession)
                .collect(Collectors.toSet());

        Map<Chronotype, Long> chronotypeLongMap = nightsWithSleep.stream()
                // для каждой ночной сессии определяем дату
                .map(night -> pickNightSession(sessions, night)
                // классифицируем
                .map(session -> classifyNight(night, session))) // Optional<Chronotype>
                // если есть
                .filter(Optional::isPresent)
                // получаем
                .map(Optional::get) // SleepingSession
                // делаем мапу - хронотип, количество
                .collect(Collectors.groupingBy(ct -> ct, Collectors.counting()));

        long owls = chronotypeLongMap.getOrDefault(Chronotype.OWL, 0L);
        long larks = chronotypeLongMap.getOrDefault(Chronotype.LARK, 0L);
        long pigeons = chronotypeLongMap.getOrDefault(Chronotype.PIGEON, 0L);

        Chronotype chronotype = chooseChronotype(owls, larks, pigeons);

        return new SleepAnalysisResult<>(
                "Хронотип пользователя", chronotype);
    }

    private Chronotype chooseChronotype(long owls, long larks, long pigeons) {
        long max = Math.max(owls, Math.max(larks, pigeons));

        // если максимум у голубя (или совы = жаворонки),
        // то все равно отдаем голубя,
        // а если жаворонки = совам, то пусть идут первые жаворонки
        if (max == pigeons || (larks == owls)) {
            return Chronotype.PIGEON;
        } else if (larks == max) {
            return Chronotype.LARK;
        }

        return Chronotype.OWL;
    }

    private Chronotype classifyNight(LocalDate nightDate, SleepingSession s) {
        LocalDateTime owlSleepAfter = nightDate.minusDays(1).atTime(23, 0);
        LocalDateTime owlWakeAfter = nightDate.atTime(9, 0);

        LocalDateTime larkSleepBefore = nightDate.minusDays(1).atTime(22, 0);
        LocalDateTime larkWakeBefore = nightDate.atTime(7, 0);

        boolean owl = s.getFellAsleepAt().isAfter(owlSleepAfter) && s.getWokeUpAt().isAfter(owlWakeAfter);
        if (owl) return Chronotype.OWL;

        boolean lark = s.getFellAsleepAt().isBefore(larkSleepBefore) && s.getWokeUpAt().isBefore(larkWakeBefore);
        if (lark) return Chronotype.LARK;

        return Chronotype.PIGEON;
    }

    private Optional<SleepingSession> pickNightSession(List<SleepingSession> sessions, LocalDate nightDate) {
        LocalDateTime nightStart = nightDate.atStartOfDay();
        LocalDateTime nightEnd = nightStart.plusHours(6);

        // если вдруг несколько сессий пересекают ночь, берём ту, у которой пробуждение позже (обычно это “основной сон”)
        return sessions.stream()
                .filter(s -> overlaps(s.getFellAsleepAt(), s.getWokeUpAt(), nightStart, nightEnd))
                .max(Comparator.comparing(SleepingSession::getWokeUpAt));
    }

    private Stream<LocalDate> nightDatesOverlappedBySession(SleepingSession s) {
        LocalDate startDate = s.getFellAsleepAt().toLocalDate();
        LocalDate endDate = s.getWokeUpAt().toLocalDate();

        return startDate.datesUntil(endDate.plusDays(1))
                .filter(d -> overlapsNight(d, s.getFellAsleepAt(), s.getWokeUpAt()));
    }

    private boolean overlapsNight(LocalDate nightDate, LocalDateTime sessionStart, LocalDateTime sessionEnd) {
        LocalDateTime nightStart = nightDate.atStartOfDay(); // 00:00
        LocalDateTime nightEnd = nightStart.plusHours(6); // 06:00
        return overlaps(sessionStart, sessionEnd, nightStart, nightEnd);
    }

    private boolean overlaps(LocalDateTime sessionStart, LocalDateTime sessionEnd, LocalDateTime start, LocalDateTime end) {
        return sessionStart.isBefore(end) && sessionEnd.isAfter(start);
    }


}
