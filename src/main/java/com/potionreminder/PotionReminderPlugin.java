package com.potionreminder;

import com.google.inject.Provides;
import static com.potionreminder.Status.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Value;
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
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.RSTimeUnit;

@Slf4j
@PluginDescriptor(
	name = "Potion Reminder"
)
public class PotionReminderPlugin extends Plugin
{
	@Value static class InfoBoxPair { Timer timer; PotionInfoBox infoBox; }

	private final Map<Status, Timer> potionTimers = new HashMap<>();
	private final Map<Status, InfoBoxPair> infoBoxPairs = new HashMap<>();

	private static final int STAMINA_MULTIPLIER = 10;
	private static final int ANTIFIRE_MULTIPLIER = 30;
	private static final int SUPER_ANTIFIRE_MULTIPLIER = 20;
	private static final int ANTIPOISON_MULTIPLIER = 30;
	private static final int ANTIVENOM_MULTIPLIER = 30;

	private static final int VENOM_VALUE_CUTOFF = -38;

	@Inject
	private Client client;

	@Inject
	private PotionReminderConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		// Stamina
		if ((event.getVarbitId() == Varbits.STAMINA_EFFECT || event.getVarbitId() == Varbits.RING_OF_ENDURANCE_EFFECT)
				&& config.showStamina())
		{
			int staminaPotionEffect = client.getVarbitValue(Varbits.STAMINA_EFFECT);
			int enduranceRingEffect = client.getVarbitValue(Varbits.RING_OF_ENDURANCE_EFFECT);

			final int numTicks = (staminaPotionEffect + enduranceRingEffect) * STAMINA_MULTIPLIER;
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
				&& event.getValue() >= VENOM_VALUE_CUTOFF && event.getValue() <= 0)
		{
			final int numTicks = Math.abs(event.getValue()) * ANTIPOISON_MULTIPLIER;
			handlePotionTimer(ANTIPOISON, numTicks);
		}

		// Anti-venom
		if (event.getVarpId() == VarPlayer.POISON && config.showAntivenom()
				&& event.getValue() <= VENOM_VALUE_CUTOFF)
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
			resetPotionTimers();
		}
	}

	@Provides
    PotionReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PotionReminderConfig.class);
	}

	private void handlePotionTimer(final Status status, final int numTicks)
	{
		Timer potionTimer = potionTimers.get(status);
		Duration duration = Duration.of(numTicks, RSTimeUnit.GAME_TICKS).minusSeconds(config.notificationOffset());
		System.out.println(duration);

		if ((duration.isZero() || duration.isNegative()) && config.displayInfoBox())
		{
			createInfoBox(status);
		}
		else if (potionTimer == null || duration.compareTo(potionTimer.getDuration()) > 0)
		{
			createPotionTimer(status, duration);
		}
		else
		{
			potionTimer.updateDuration(duration);
		}
	}

	private void createPotionTimer(final Status status, final Duration duration)
	{
		if (infoBoxPairs.containsKey(status))
		{
			removeInfoBox(infoBoxPairs.get(status).getInfoBox());
		}
		cancelPotionTimer(status);

		Timer potionTimer = new Timer(duration, () -> handlePotionExpire(status));
		potionTimers.put(status, potionTimer);
	}

	private void cancelPotionTimer(final Status status)
	{
		final Timer timer = potionTimers.remove(status);
		if (timer != null)
		{
			timer.stop();
		}
	}

	private void resetPotionTimers()
	{
		for (Status key : potionTimers.keySet())
		{
			cancelPotionTimer(key);
		}
	}

	private void handlePotionExpire(final Status status)
	{
		notifier.notify(status.getStatusName() + " is expiring!");
	}

	private void createInfoBox(final Status status)
	{
		PotionInfoBox infoBox = new PotionInfoBox(this);
		infoBox.setImage(itemManager.getImage(status.getImageId()));
		infoBox.setTooltip(status.getStatusName() + " expired");

		Timer infoBoxTimer = new Timer(Duration.ofSeconds(config.infoBoxDuration()), () -> removeInfoBox(infoBox));
		infoBoxPairs.put(status, new InfoBoxPair(infoBoxTimer, infoBox));
		infoBoxManager.addInfoBox(infoBox);
	}

	private void removeInfoBox(PotionInfoBox infoBox)
	{
		infoBoxManager.removeInfoBox(infoBox);
	}
}
