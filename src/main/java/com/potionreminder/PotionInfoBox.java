package com.potionreminder;

import java.awt.Color;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class PotionInfoBox extends InfoBox
{
    public PotionInfoBox(PotionReminderPlugin plugin)
    {
        super(null, plugin);
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
}
