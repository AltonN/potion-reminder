package com.potionreminder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("example")
public interface PotionReminderConfig extends Config
{
	@ConfigItem(
			keyName = "notificationOffset",
			name = "Notification Offset",
			description = "The amount of seconds before potion expires to notify.",
			position = -4
	)
	@Units(Units.SECONDS)
	default int notificationOffset()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "displayInfobox",
			name = "Display Infobox",
			description = "Display an infobox when potion is no longer active.",
			position = -3
	)
	default boolean displayInfobox() { return true; }

	@ConfigItem(
			keyName = "infoboxDuration",
			name = "Infobox Duration",
			description = "The amount of seconds the infobox lasts before expiring.",
			position = -2
	)
	@Units(Units.SECONDS)
	default int infoboxDuration() { return 15; }

	@ConfigItem(
			keyName = "showStamina",
			name = "Stamina",
			description = ""
	)
	default boolean showStamina()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAntifire",
			name = "Antifire",
			description = ""
	)
	default boolean showAntifire()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showSuperAntifire",
			name = "Super Antifire",
			description = ""
	)
	default boolean showSuperAntifire()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAntipoison",
			name = "Antipoison",
			description = ""
	)
	default boolean showAntipoison()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAntivenom",
			name = "Antivenom",
			description = ""
	)
	default boolean showAntivenom()
	{
		return true;
	}
}