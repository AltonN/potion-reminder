package com.potionreminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Units;

@ConfigGroup("example")
public interface PotionReminderConfig extends Config
{
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
	default int infoBoxDuration() { return 15; }

	@ConfigSection(
			name = "Potion Options",
			description = "",
			position = 4
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
}