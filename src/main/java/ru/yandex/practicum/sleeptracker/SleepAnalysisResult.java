package ru.yandex.practicum.sleeptracker;

public final class SleepAnalysisResult<T> {
    private final String description;
    private final T value;

    public SleepAnalysisResult(String description, T value) {
        this.description = description;
        this.value = value;
    }

    public String description() {
        return description;
    }

    public T value() {
        return value;
    }

    public String formatForConsole() {
        return description + ": " + value;
    }
}
