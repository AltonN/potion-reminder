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
			position = 2
	)
	@Units(Units.MILLISECONDS)
	default int notificationOffset()
	{
		return 0;
	}

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