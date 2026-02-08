package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SleepTrackerApp {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private static final String SESSIONS_FILE_PATH = "src/main/resources/sleep_log.txt";
    static final String SYSTEM_LOG = "sleep_tracker_system.log";

    private final List<SleepAnalysisFunction> analyses = List.of(
            new CountSessionsAnalysis()
    );

    public static void main(String[] args) {

        SleepTrackerApp app = new SleepTrackerApp();
        AppLogger sysLog = null;

        try {
            sysLog = AppLogger.system(SYSTEM_LOG);
            final AppLogger log = sysLog; // костыль конечно ((

            sysLog.info("SleepTracker started. Sessions file: " + SESSIONS_FILE_PATH);
            List<SleepingSession> sessions = app.readSessions(Path.of(SESSIONS_FILE_PATH));
            sysLog.info("Loaded sessions: " + sessions.size());

            app.analyses.stream()
                    .map(fn -> fn.apply(sessions))
                    .map(SleepAnalysisResult::formatForConsole)
                    .forEach(line -> {
                        log.info(line);
                        System.out.println(line);
                    });
            sysLog.info("SleepTracker finished successfully");

        } catch (LogFileException e) {
            System.err.println("Ошибка работы с лог-файлом: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка в исходных данных " + e.getMessage());
            if (sysLog != null) {
                sysLog.error("Ошибка в исходных данных", e);
            }
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл. " + e.getMessage());
            sysLog.error("Не удалось прочитать файл.", e);
        } catch (RuntimeException e) {
            System.err.println("Ошибка обработки данных. " + e. getMessage());
            if (sysLog != null) {
                sysLog.error("Ошибка обработки данных.", e);
            }
        } finally {
            try {
                if (sysLog != null) {
                    sysLog.close();
                }
            } catch (Exception closeError) {
                System.err.println("Не удалось корректно закрыть лог-файл: " + closeError.getMessage());
            }
        }
    }

    List<SleepingSession> readSessions(Path file) throws IOException {
        try (var lines = Files.lines(file)) {
            return lines
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(this::parseLine)
                    .toList();
        }
    }

    private SleepingSession parseLine(String line) throws IllegalArgumentException  {

        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректная строка: " + line);
        }

        LocalDateTime fellAsleep = LocalDateTime.parse(parts[0].trim(), DT);
        LocalDateTime wokeUp = LocalDateTime.parse(parts[1].trim(), DT);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

        if (wokeUp.isBefore(fellAsleep)) {
            throw new IllegalArgumentException("Пробуждение раньше засыпания: " + line);
        }

        return new SleepingSession(fellAsleep, wokeUp, quality);
    }
}

