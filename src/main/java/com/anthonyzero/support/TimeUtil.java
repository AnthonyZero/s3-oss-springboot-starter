package com.anthonyzero.support;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static Date formDuration(Duration duration) {
        return Date.from(Instant.now().plus(duration));
    }

    public static Date formDuration(Integer time, TimeUnit timeUnit) {
        return Date.from(Instant.now().plus(getDuration(time, timeUnit)));
    }

    public static Date formLocalDateTime(Integer time, TimeUnit timeUnit) {
        return Date.from(getLocalDateTime(time, timeUnit).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Duration getDuration(Integer time, TimeUnit timeUnit) {
        switch (timeUnit) {
            case DAYS:
                return Duration.ofDays(time);
            case HOURS:
                return Duration.ofHours(time);
            case MINUTES:
                return Duration.ofMinutes(time);
            case SECONDS:
                return Duration.ofSeconds(time);
            case MILLISECONDS:
                return Duration.ofMillis(time);
            case NANOSECONDS:
                return Duration.ofNanos(time);
            default:
                throw new UnsupportedOperationException("Man, use a real TimeUnit unit");
        }
    }

    public static LocalDateTime getLocalDateTime(Integer time, TimeUnit timeUnit) {
        switch (timeUnit) {
            case DAYS:
                return LocalDateTime.now().plusDays(time);
            case HOURS:
                return LocalDateTime.now().plusHours(time);
            case MINUTES:
                return LocalDateTime.now().plusMinutes(time);
            case SECONDS:
                return LocalDateTime.now().plusSeconds(time);
            default:
                throw new UnsupportedOperationException("Man, use a real TimeUnit unit");
        }
    }
}
