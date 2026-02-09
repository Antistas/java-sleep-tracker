package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 1) Приложение должно принимать на вход как аргумент командной строки путь к файлу с логом сна,
// 3) добавить функцию, которая будет вычислять, сколько раз за всё время логирования сна пользователь не спал ночью.
/*
Бессонной ночью считается ночь, когда не было ни одной сессии сна, пересекающей интервал от 0:00 до 6:00.
То есть, если пользователь спал с 23:00 до 3:00, ночь не будет считаться бессонной,
также как если он спал с 2:00 до 7:00.
А вот если сон был только с 7:00 до 11:00, такую ночь мы запишем в бессонные.
Также будем считать, что если первая сессия сна в файле началась после 12 дня,
потенциальной ночью для сна считается следующая ночь, а если до 12 — то предыдущая.
 */
// 4) вам нужно написать не менее четырёх юнит-тестов.
// 5) Добавьте в приложение возможность определять, к какому хронотипу относится пользователь.
/*
Для каждой ночи на основе времени засыпания и пробуждения определите, относится ночь к типу «сова», «жаворонок» или «голубь».
«Сова» — если время засыпания было после 23:00, а время пробуждения — после 9:00.
«Жаворонок» — если время засыпания было до 22:00, а время пробуждения до — 7:00.
«Голубь» — во всех остальных случаях.
Бессонные ночи и дневные сессии сна в подсчёте должны игнорироваться.
Посчитайте количество ночей каждого типа и выберите, какой встречается чаще всего.
Именно к этому типу нужно отнести пользователя. Если есть сомнения, например, количество ночей двух типов совпадает,
считайте, что пользователь относится к «голубям».
 */
// 6) Добавить 4 теста

public class SleepTrackerApp {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
    private static final String SESSIONS_FILE_PATH = "src/main/resources/sleep_log.txt";
    static final String SYSTEM_LOG = "sleep_tracker_system.log";

    private final List<SleepAnalysisFunction> analyses = List.of(
            new CountSessionsAnalysis(),
            new MinSleepSessionsAnalysis(),
            new MaxSleepSessionsAnalysis(),
            new AvgSleepSessionsAnalysis(),
            new CountBadSleepSessions()
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

