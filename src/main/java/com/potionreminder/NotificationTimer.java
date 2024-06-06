package com.potionreminder;

import javax.inject.Inject;
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
    private final Instant endTime;
    private int ticks;

    @Inject
    private PotionReminderConfig config;

    NotificationTimer(final int ticks, PotionReminderPlugin plugin)
    {
        final Duration duration = Duration.of(ticks, RSTimeUnit.GAME_TICKS);
        this.endTime = Instant.now().plus(duration);
        this.plugin = plugin;
        this.ticks = ticks;

        scheduler.scheduleAtFixedRate(this::checkDuration, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void checkDuration()
    {
        Duration remainingTime = Duration.between(Instant.now(), endTime);
        if (remainingTime.toMillis() <= config.notificationOffset())
        {
            plugin.notifyClient();
            scheduler.shutdown();
        }
    }

    public int getTicks()
    {
        // Implement me
        return 0;
    }

    public void setTicks(final int ticks)
    {
        // Implement me
    }

    public void stop()
    {
        // Implement me
    }
}
