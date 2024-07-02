package com.potionreminder;

import java.awt.Color;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class PotionInfoBox extends InfoBox
{
    private final Client client;
    private final PotionReminderConfig config;
    private final PotionReminderPlugin plugin;

    public PotionInfoBox(Client client, PotionReminderConfig config, PotionReminderPlugin plugin)
    {
        super(null, plugin);
        this.client = client;
        this.config = config;
        this.plugin = plugin;

        setPriority(InfoBoxPriority.HIGH);
    }

    @Override
    public String getText()
    {
        return "?";
    }

    @Override
    public Color getTextColor()
    {
        return Color.RED;
    }

    @Override
    public boolean render()
    {
        final Player localPlayer = client.getLocalPlayer();
        return !(localPlayer.getHealthScale() == -1 && config.hideWhenOutOfCombat());
    }
}
