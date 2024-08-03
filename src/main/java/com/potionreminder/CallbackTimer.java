package com.potionreminder;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;

public class CallbackTimer extends Timer
{
    @Getter private Duration duration;
    private final Runnable callback;
    private TimerTask currentTask;

    CallbackTimer(final Duration duration, Runnable callback)
    {
        this.duration = duration;
        this.callback = callback;
        scheduleTask(duration, callback);
    }

    private void scheduleTask(Duration duration, Runnable callback)
    {
        long delay = duration.toMillis();
        this.currentTask = new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        };
        this.schedule(this.currentTask, delay);
    }

    public void updateDuration(final Duration newDuration)
    {
        this.currentTask.cancel();
        this.duration = newDuration;
        scheduleTask(duration, callback);
    }

    public void cancelTimer()
    {
        this.cancel();
    }
}
