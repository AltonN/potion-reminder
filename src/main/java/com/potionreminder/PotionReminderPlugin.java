package com.potionreminder;

import com.google.inject.Provides;
import static com.potionreminder.Status.*;
import java.time.Duration;
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
import net.runelite.client.util.RSTimeUnit;

@Slf4j
@PluginDescriptor(
	name = "Potion Reminder"
)
public class PotionReminderPlugin extends Plugin
{
	private final Map<Status, Timer> timers = new HashMap<>();
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
			final int numTicks = event.getValue() * STAMINA_MULTIPLIER;
			handlePotionTimer(STAMINA, numTicks);
		}

		// Antifire
		if (event.getVarbitId() == Varbits.ANTIFIRE && config.showAntifire())
		{
			final int numTicks = event.getValue() * ANTIFIRE_MULTIPLIER;
			handlePotionTimer(ANTIFIRE, numTicks);
		}

		// Super antifire
		if (event.getVarbitId() == Varbits.SUPER_ANTIFIRE && config.showSuperAntifire())
		{
			final int numTicks = event.getValue() * SUPER_ANTIFIRE_MULTIPLIER;
			handlePotionTimer(SUPER_ANTIFIRE, numTicks);
		}

		// Antipoison
		if (event.getVarpId() == VarPlayer.POISON && config.showAntipoison()
				&& event.getValue() >= VENOM_VALUE_CUTOFF && event.getValue() < 0)
		{
			final int numTicks = Math.abs(event.getValue()) * ANTIPOISON_MULTIPLIER;
			handlePotionTimer(ANTIPOISON, numTicks);
		}

		// Anti-venom
		if (event.getVarpId() == VarPlayer.POISON && config.showAntivenom()
				&& event.getValue() < VENOM_VALUE_CUTOFF)
		{
			final int numTicks = Math.abs(event.getValue() - VENOM_VALUE_CUTOFF) * ANTIVENOM_MULTIPLIER;
			handlePotionTimer(ANTIVENOM, numTicks);
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

	private void handlePotionExpire(Status status)
	{
		String statusName = status.getStatusName();
		notifier.notify(statusName + " is expiring!");
	}

	private void handlePotionTimer(final Status status, final int numTicks)
	{
		Timer timer = timers.get(status);
		Duration duration = Duration.of(numTicks, RSTimeUnit.GAME_TICKS).minusSeconds(config.notificationOffset());

		if (duration.isZero())
		{
			removeTimer(status);
		}
		else if (timer == null || duration.compareTo(timer.getDuration()) > 0)
		{
			createTimer(status, duration);
		}
		else
		{
			timer.updateDuration(duration);
		}
	}

	private void createTimer(final Status status, final Duration duration)
	{
		removeTimer(status);
		Timer newTimer = new Timer(duration, () -> handlePotionExpire(status));
		timers.put(status, newTimer);
	}

	private void removeTimer(final Status status)
	{
		final Timer timer = timers.remove(status);
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
