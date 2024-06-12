package com.potionreminder;

import com.google.inject.Provides;
import static com.potionreminder.Status.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.Varbits;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Potion Reminder"
)
public class PotionReminderPlugin extends Plugin
{
	private final Map<Status, NotificationTimer> timers = new HashMap<>();
	private static final int STAMINA_MULTIPLIER = 10;

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private PotionReminderConfig config;

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == Varbits.STAMINA_EFFECT && config.showStamina())
		{
			final int totalDuration = client.getVarbitValue(Varbits.STAMINA_EFFECT);
			handleTimer(STAMINA, totalDuration, i -> i * STAMINA_MULTIPLIER);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			resetTimers();
		}

	}

	@Provides
    PotionReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PotionReminderConfig.class);
	}

	public void notifyClient(Status status)
	{
		String statusName = status.getStatusName();
		notifier.notify(statusName + " is expiring!");
	}

	private void handleTimer(final Status status, final int varValue, final IntUnaryOperator tickDuration)
	{
		int durationTicks = tickDuration.applyAsInt(varValue);
		handleTimer(status, durationTicks);
	}

	private void handleTimer(final Status status, final int ticks)
	{
		NotificationTimer timer = timers.get(status);

		if (ticks <= 0)
		{
			removeTimer(status);
		}
		else if (timer == null || ticks > timer.getTicks())
		{
			createTimer(status, ticks);
		}
		else
		{
			timer.setTicks(ticks);
		}
	}

	private void resetTimers()
	{
		for (Status key : timers.keySet())
		{
			removeTimer(key);
		}
	}

	private void createTimer(final Status status, final int ticks)
	{
		removeTimer(status);
		NotificationTimer newTimer = new NotificationTimer(ticks, this, config, status);
		timers.put(status, newTimer);
	}

	private void removeTimer(final Status status)
	{
		final NotificationTimer timer = timers.remove(status);
		if (timer != null)
		{
			timer.stop();
		}
	}
}
