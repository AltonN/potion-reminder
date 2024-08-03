package com.potionreminder;

import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;

public class CallbackTimer extends Timer
{
    @Getter private long duration;
    private final Runnable callback;
    private TimerTask currentTask;

    CallbackTimer(long duration, Runnable callback)
    {
        this.duration = duration;
        this.callback = callback;
        scheduleTask(duration, callback);
    }

    private void scheduleTask(long duration, Runnable callback)
    {
        this.currentTask = new TimerTask() {
            @Override
            public void run() {
                callback.run();
            }
        };
        this.schedule(this.currentTask, duration);
    }

    public void updateDuration(final long newDuration)
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
