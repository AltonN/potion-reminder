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
import net.runelite.client.config.Notification;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
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

		// Divine super attack
		if (event.getVarbitId() == Varbits.DIVINE_SUPER_ATTACK && config.showDivineSuperAttack())
		{
			final int numTicks = event.getValue();
			if (numTicks < client.getVarbitValue(Varbits.DIVINE_SUPER_COMBAT))
			{
				return;
			}
			handlePotionTimer(DIVINE_SUPER_ATTACK, numTicks);
		}

		// Divine super strength
		if (event.getVarbitId() == Varbits.DIVINE_SUPER_STRENGTH && config.showDivineSuperStrength())
		{
			final int numTicks = event.getValue();
			if (numTicks < client.getVarbitValue(Varbits.DIVINE_SUPER_COMBAT))
			{
				return;
			}
			handlePotionTimer(DIVINE_SUPER_STRENGTH, numTicks);
		}

		// Divine super defence
		if (event.getVarbitId() == Varbits.DIVINE_SUPER_DEFENCE && config.showDivineSuperDefence())
		{
			final int numTicks = event.getValue();
			if (numTicks < client.getVarbitValue(Varbits.DIVINE_SUPER_COMBAT)
					|| numTicks < client.getVarbitValue(Varbits.DIVINE_BASTION)
					|| numTicks < client.getVarbitValue(Varbits.DIVINE_BATTLEMAGE))
			{
				return;
			}
			handlePotionTimer(DIVINE_SUPER_DEFENCE, numTicks);
		}

		// Divine super combat
		if (event.getVarbitId() == Varbits.DIVINE_SUPER_COMBAT && config.showDivineSuperCombat())
		{
			final int numTicks = event.getValue();
			if (numTicks == client.getVarbitValue(Varbits.DIVINE_SUPER_ATTACK))
			{
				cancelPotionTimer(DIVINE_SUPER_ATTACK);
				removeInfoBox(DIVINE_SUPER_ATTACK);
			}
			if (numTicks == client.getVarbitValue(Varbits.DIVINE_SUPER_STRENGTH))
			{
				cancelPotionTimer(DIVINE_SUPER_STRENGTH);
				removeInfoBox(DIVINE_SUPER_STRENGTH);
			}
			if (numTicks == client.getVarbitValue(Varbits.DIVINE_SUPER_DEFENCE))
			{
				cancelPotionTimer(DIVINE_SUPER_DEFENCE);
				removeInfoBox(DIVINE_SUPER_DEFENCE);
			}
			handlePotionTimer(DIVINE_SUPER_COMBAT, numTicks);
		}

		// Divine ranging
		if (event.getVarbitId() == Varbits.DIVINE_RANGING && config.showDivineRanging())
		{
			final int numTicks = event.getValue();
			if (numTicks < client.getVarbitValue(Varbits.DIVINE_BASTION))
			{
				return;
			}
			handlePotionTimer(DIVINE_RANGING, numTicks);
		}

		// Divine magic
		if (event.getVarbitId() == Varbits.DIVINE_MAGIC && config.showDivineMagic())
		{
			final int numTicks = event.getValue();
			if (numTicks < client.getVarbitValue(Varbits.DIVINE_BATTLEMAGE))
			{
				return;
			}
			handlePotionTimer(DIVINE_MAGIC, numTicks);
		}

		// Divine bastion
		if (event.getVarbitId() == Varbits.DIVINE_BASTION && config.showDivineBastion())
		{
			final int numTicks = event.getValue();
			if (numTicks == client.getVarbitValue(Varbits.DIVINE_RANGING))
			{
				cancelPotionTimer(DIVINE_RANGING);
				removeInfoBox(DIVINE_RANGING);
			}
			if (numTicks == client.getVarbitValue(Varbits.DIVINE_SUPER_DEFENCE))
			{
				cancelPotionTimer(DIVINE_SUPER_DEFENCE);
				removeInfoBox(DIVINE_SUPER_DEFENCE);
			}
			handlePotionTimer(DIVINE_BASTION, numTicks);
		}

		// Divine battlemage
		if (event.getVarbitId() == Varbits.DIVINE_BATTLEMAGE && config.showDivineBattlemage())
		{
			final int numTicks = event.getValue();
			if (numTicks == client.getVarbitValue(Varbits.DIVINE_MAGIC))
			{
				cancelPotionTimer(DIVINE_MAGIC);
				removeInfoBox(DIVINE_MAGIC);
			}
			if (numTicks == client.getVarbitValue(Varbits.DIVINE_SUPER_DEFENCE))
			{
				cancelPotionTimer(DIVINE_SUPER_DEFENCE);
				removeInfoBox(DIVINE_SUPER_DEFENCE);
			}
			handlePotionTimer(DIVINE_BATTLEMAGE, numTicks);
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(PotionReminderConfig.CONFIG_GROUP))
		{
			return;
		}

		if (config.expirationNotification() == Notification.OFF)
		{
			cancelPotionTimers();
		}

		if (!config.displayInfoBox())
		{
			removeInfoBoxes();
		}

		Status status = null;
		String key = event.getKey();
		switch (key)
		{
			case "showStamina":
				status = STAMINA;
				break;
			case "showAntifire":
				status = ANTIFIRE;
				break;
			case "showSuperAntifire":
				status = SUPER_ANTIFIRE;
				break;
			case "showAntipoison":
				status = ANTIPOISON;
				break;
			case "showAntivenom":
				status = ANTIVENOM;
				break;
			case "showDivineSuperAttack":
				status = DIVINE_SUPER_ATTACK;
				break;
			case "showDivineSuperStrength":
				status = DIVINE_SUPER_STRENGTH;
				break;
			case "showDivineSuperDefence":
				status = DIVINE_SUPER_DEFENCE;
				break;
			case "showDivineSuperCombat":
				status = DIVINE_SUPER_COMBAT;
				break;
			case "showDivineRanging":
				status = DIVINE_RANGING;
				break;
			case "showDivineMagic":
				status = DIVINE_MAGIC;
				break;
			case "showDivineBastion":
				status = DIVINE_BASTION;
				break;
			case "showDivineBattlemage":
				status = DIVINE_BATTLEMAGE;
				break;
		}

		if (status != null)
		{
			cancelPotionTimer(status);
			removeInfoBox(status);
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			cancelPotionTimers();
			removeInfoBoxes();
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

		if ((duration.isZero() || duration.isNegative()) && config.displayInfoBox())
		{
			createInfoBox(status);
		}
		else if (potionTimer == null || duration.compareTo(potionTimer.getDuration()) > 0)
		{
			createPotionTimer(status, duration);
			removeInfoBox(status);
		}
		else
		{
			potionTimer.updateDuration(duration);
		}
	}

	private void createPotionTimer(final Status status, final Duration duration)
	{
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

	private void handlePotionExpire(final Status status)
	{
		notifier.notify(config.expirationNotification(),status.getStatusName() + " is expiring!");
	}

	private void createInfoBox(final Status status)
	{
		Timer infoBoxTimer = new Timer(Duration.ofSeconds(config.infoBoxDuration()), () -> removeInfoBox(status));
		PotionInfoBox infoBox = new PotionInfoBox(this);
		infoBox.setImage(itemManager.getImage(status.getImageId()));
		infoBox.setTooltip(status.getStatusName() + " expired");

		removeInfoBox(status);
		infoBoxPairs.put(status, new InfoBoxPair(infoBoxTimer, infoBox));
		infoBoxManager.addInfoBox(infoBox);
	}

	private void removeInfoBox(Status status)
	{
		final InfoBoxPair infoBoxPair = infoBoxPairs.remove(status);
		if (infoBoxPair != null)
		{
			infoBoxPair.getTimer().stop();
			infoBoxManager.removeInfoBox(infoBoxPair.getInfoBox());
		}
	}

	private void cancelPotionTimers()
	{
		for (Status key : Status.values())
		{
			cancelPotionTimer(key);
		}
	}

	private void removeInfoBoxes()
	{
		for (Status key : Status.values())
		{
			removeInfoBox(key);
		}
	}
}
