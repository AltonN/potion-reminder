package com.potionreminder;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
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
	private enum Status
	{
		STAMINA,
		ANTIFIRE,
		ANTIPOISON,
		ANTIVENOM
	}

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
			handleTimer(Status.STAMINA, totalDuration, i -> i * STAMINA_MULTIPLIER);
		}
	}

	@Provides
    PotionReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PotionReminderConfig.class);
	}

	public void notifyClient()
	{
		notifier.notify("Stamina enhancement is expiring!");
	}

	private void handleTimer(Status status, final int varValue, final IntUnaryOperator tickDuration)
	{
		int durationTicks = tickDuration.applyAsInt(varValue);
		handleTimer(status, durationTicks);
	}

	private void handleTimer(Status status, final int ticks)
	{
		NotificationTimer timer = timers.get(status);

		if (ticks <= 0)
		{
			removeTimer(status);
		}
		else if (timer == null || ticks > timer.getTicks())
		{
			NotificationTimer newTimer = createTimer(ticks);
			timers.put(status, newTimer);
		}
		else
		{
			timer.setTicks(ticks);
		}
	}

	private NotificationTimer createTimer(final int ticks)
	{
		return new NotificationTimer(ticks, this);
	}

	private void removeTimer(Status status)
	{
		final NotificationTimer timer = timers.remove(status);
		if (timer != null)
		{
			timer.stop();
		}
	}
}
