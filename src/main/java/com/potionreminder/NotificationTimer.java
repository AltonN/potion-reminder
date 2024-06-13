package com.potionreminder;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.runelite.client.util.RSTimeUnit;

public class NotificationTimer
{
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final PotionReminderPlugin plugin;
    private final PotionReminderConfig config;
    private final Status status;
    private Instant endTime;
    private int ticks;

    NotificationTimer(final int ticks, PotionReminderPlugin plugin, PotionReminderConfig config, Status status)
    {
        final Duration duration = Duration.of(ticks, RSTimeUnit.GAME_TICKS);
        this.endTime = Instant.now().plus(duration);
        this.plugin = plugin;
        this.config = config;
        this.status = status;
        this.ticks = ticks;

        scheduler.scheduleAtFixedRate(this::checkDuration, 0, 200, TimeUnit.MILLISECONDS);
    }

    private void checkDuration()
    {
        Duration remainingTime = Duration.between(Instant.now(), endTime);
        if (remainingTime.toMillis() <= config.notificationOffset())
        {
            plugin.notifyClient(status);
            stop();
        }
    }

    public int getTicks()
    {
        return this.ticks;
    }

    public void setTicks(final int ticks)
    {
        final Duration duration = Duration.of(ticks, RSTimeUnit.GAME_TICKS);
        this.endTime = Instant.now().plus(duration);
        this.ticks = ticks;
    }

    public void stop()
    {
        scheduler.shutdownNow();
    }
}
