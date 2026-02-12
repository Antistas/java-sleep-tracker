package ru.yandex.practicum.sleeptracker;

import java.time.Duration;
import java.time.LocalDateTime;

public class SleepingSession {

    private LocalDateTime fellAsleepAt;
    private LocalDateTime wokeUpAt;
    private SleepQuality sleepQuality;

    public SleepingSession(LocalDateTime fellAsleepAt, LocalDateTime wokeUpAt, SleepQuality sleepQuality) {
        this.fellAsleepAt = fellAsleepAt;
        this.wokeUpAt = wokeUpAt;
        this.sleepQuality = sleepQuality;
    }

    public LocalDateTime getFellAsleepAt() {
        return fellAsleepAt;
    }

    public void setFellAsleepAt(LocalDateTime fellAsleepAt) {
        this.fellAsleepAt = fellAsleepAt;
    }

    public LocalDateTime getWokeUpAt() {
        return wokeUpAt;
    }

    public void setWokeUpAt(LocalDateTime wokeUpAt) {
        this.wokeUpAt = wokeUpAt;
    }

    public SleepQuality getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(SleepQuality sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public Duration getDuration() {
        return Duration.between(fellAsleepAt, wokeUpAt);
    }

    public boolean isBad() {
        return (this.sleepQuality == SleepQuality.BAD);
    }

    @Override
    public String toString() {
        return "SleepingSession{" +
                "fellAsleepAt=" + fellAsleepAt +
                ", wokeUpAt=" + wokeUpAt +
                ", sleepQuality=" + sleepQuality +
                '}';
    }
}
