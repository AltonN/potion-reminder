package com.potionreminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;
import net.runelite.client.config.Units;

@ConfigGroup(PotionReminderConfig.CONFIG_GROUP)
public interface PotionReminderConfig extends Config
{
	String CONFIG_GROUP = "potionReminder";

	@ConfigItem(
			keyName = "expirationNotification",
			name = "Expiration Notification",
			description = "Send a notification when potion is expiring.",
			position = 0
	)
	default Notification expirationNotification() { return Notification.ON; }

	@ConfigItem(
			keyName = "notificationOffset",
			name = "Notification Offset",
			description = "The amount of seconds before potion expires to notify.",
			position = 1
	)
	@Units(Units.SECONDS)
	default int notificationOffset()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "displayInfoBox",
			name = "Display InfoBox",
			description = "Display an infobox when potion is no longer active.",
			position = 2
	)
	default boolean displayInfoBox() { return true; }

	@ConfigItem(
			keyName = "infoBoxDuration",
			name = "InfoBox Duration",
			description = "The amount of seconds the infobox lasts before expiring.",
			position = 3
	)
	@Units(Units.SECONDS)
	default int infoBoxDuration() { return 30; }

	@ConfigItem(
			keyName = "hideWhenOutOfCombat",
			name = "Hide When Out of Combat",
			description = "Hide InfoBoxes when out of combat",
			position = 4
	)
	default boolean hideWhenOutOfCombat() { return false; }

	@ConfigSection(
			name = "Potion Options",
			description = "",
			position = 5
	)
	String potionOptions = "potionOptions";

	@ConfigItem(
			keyName = "showStamina",
			name = "Stamina",
			description = "",
			section = potionOptions
	)
	default boolean showStamina()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAntifire",
			name = "Antifire",
			description = "",
			section = potionOptions
	)
	default boolean showAntifire()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showSuperAntifire",
			name = "Super Antifire",
			description = "",
			section = potionOptions
	)
	default boolean showSuperAntifire()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAntipoison",
			name = "Antipoison",
			description = "",
			section = potionOptions
	)
	default boolean showAntipoison()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAntivenom",
			name = "Antivenom",
			description = "",
			section = potionOptions
	)
	default boolean showAntivenom()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineSuperAttack",
			name = "Divine Super Attack",
			description = "",
			section = potionOptions
	)
	default boolean showDivineSuperAttack()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineSuperStrength",
			name = "Divine Super Strength",
			description = "",
			section = potionOptions
	)
	default boolean showDivineSuperStrength()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineSuperDefence",
			name = "Divine Super Defence",
			description = "",
			section = potionOptions
	)
	default boolean showDivineSuperDefence()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineSuperCombat",
			name = "Divine Super Combat",
			description = "",
			section = potionOptions
	)
	default boolean showDivineSuperCombat()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineRanging",
			name = "Divine Ranging",
			description = "",
			section = potionOptions
	)
	default boolean showDivineRanging()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineMagic",
			name = "Divine Magic",
			description = "",
			section = potionOptions
	)
	default boolean showDivineMagic()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineBastion",
			name = "Divine Bastion",
			description = "",
			section = potionOptions
	)
	default boolean showDivineBastion()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showDivineBattlemage",
			name = "Divine Battlemage",
			description = "",
			section = potionOptions
	)
	default boolean showDivineBattlemage()
	{
		return true;
	}
}