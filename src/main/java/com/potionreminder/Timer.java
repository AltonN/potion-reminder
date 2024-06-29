package com.potionreminder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

public class Timer
{
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    @Getter private Duration duration;
    private final Runnable callback;
    private Instant endTime;

    Timer(final Duration duration, Runnable callback)
    {
        this.duration = duration;
        this.callback = callback;
        this.endTime = Instant.now().plus(duration);

        scheduler.scheduleAtFixedRate(this::checkDuration, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void checkDuration()
    {
        if (Instant.now().compareTo(endTime) >= 0)
        {
            callback.run();
            stop();
        }
    }

    public void updateDuration(final Duration duration)
    {
        this.duration = duration;
        this.endTime = Instant.now().plus(duration);
    }

    public void stop()
    {
        scheduler.shutdownNow();
    }
}
