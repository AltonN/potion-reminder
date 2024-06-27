package com.potionreminder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NotificationTimer
{
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PotionReminderConfig config;
    private final Runnable callback;
    private Duration duration;
    private Instant endTime;

    NotificationTimer(PotionReminderConfig config, Runnable callback, final Duration duration)
    {
        this.config = config;
        this.callback = callback;
        this.duration = duration;
        this.endTime = Instant.now().plus(duration);

        scheduler.scheduleAtFixedRate(this::checkDuration, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void checkDuration()
    {
        Duration remainingTime = Duration.between(Instant.now(), endTime);
        if (remainingTime.toSeconds() <= config.notificationOffset())
        {
            callback.run();
            stop();
        }
    }

    public Duration getDuration()
    {
        return this.duration;
    }

    public void setDuration(final Duration duration)
    {
        this.duration = duration;
        this.endTime = Instant.now().plus(duration);
    }

    public void stop()
    {
        scheduler.shutdownNow();
    }
}
