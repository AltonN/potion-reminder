package com.potionreminder;

import com.google.inject.Provides;
import static com.potionreminder.Status.*;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.VarPlayer;
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
	private static final int ANTIFIRE_MULTIPLIER = 30;
	private static final int SUPER_ANTIFIRE_MULTIPLIER = 20;
	private static final int ANTIPOISON_MULTIPLIER = 30;
	private static final int ANTIVENOM_MULTIPLIER = 30;

	private static final int VENOM_VALUE_CUTOFF = -38;

	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private PotionReminderConfig config;

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// Stamina
		if (event.getVarbitId() == Varbits.STAMINA_EFFECT && config.showStamina())
		{
			final int tickDuration = event.getValue() * STAMINA_MULTIPLIER;
			handleTimer(STAMINA, tickDuration);
		}

		// Antifire
		if (event.getVarbitId() == Varbits.ANTIFIRE && config.showAntifire())
		{
			final int tickDuration = event.getValue() * ANTIFIRE_MULTIPLIER;
			handleTimer(ANTIFIRE, tickDuration);
		}

		// Super antifire
		if (event.getVarbitId() == Varbits.SUPER_ANTIFIRE && config.showSuperAntifire())
		{
			final int tickDuration = event.getValue() * SUPER_ANTIFIRE_MULTIPLIER;
			handleTimer(SUPER_ANTIFIRE, tickDuration);
		}

		// Antipoison
		if (event.getVarpId() == VarPlayer.POISON && event.getValue() >= VENOM_VALUE_CUTOFF && event.getValue() < 0)
		{
			final int tickDuration = Math.abs(event.getValue()) * ANTIPOISON_MULTIPLIER;
			handleTimer(ANTIPOISON, tickDuration);
		}

		// Anti-venom
		if (event.getVarpId() == VarPlayer.POISON && event.getValue() < VENOM_VALUE_CUTOFF)
		{
			final int tickDuration = Math.abs(event.getValue() - VENOM_VALUE_CUTOFF) * ANTIVENOM_MULTIPLIER;
			handleTimer(ANTIVENOM, tickDuration);
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

	private void notifyClient(Status status)
	{
		String statusName = status.getStatusName();
		notifier.notify(statusName + " is expiring!");
	}

	private void handleTimer(final Status status, final int numTicks)
	{
		NotificationTimer timer = timers.get(status);

		if (numTicks <= 0)
		{
			removeTimer(status);
		}
		else if (timer == null || numTicks > timer.getTicks())
		{
			createTimer(status, numTicks);
		}
		else
		{
			timer.setTicks(numTicks);
		}
	}

	private void createTimer(final Status status, final int numTicks)
	{
		removeTimer(status);
		NotificationTimer newTimer = new NotificationTimer(numTicks, config, () -> notifyClient(status));
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

	private void resetTimers()
	{
		for (Status key : timers.keySet())
		{
			removeTimer(key);
		}
	}
}
