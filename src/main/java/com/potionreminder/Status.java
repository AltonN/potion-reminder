package com.potionreminder;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
enum Status {
    STAMINA("Stamina", ItemID.STAMINA_POTION4),
    ANTIFIRE("Antifire", ItemID.ANTIFIRE_POTION4),
    SUPER_ANTIFIRE("Super antifire", ItemID.SUPER_ANTIFIRE_POTION4),
    ANTIPOISON("Antipoison", ItemID.ANTIPOISON4),
    ANTIVENOM("Anti-venom", ItemID.ANTIVENOM4),
    ;

    private final String statusName;
    private final int imageId;

    Status(String statusName, int imageId)
    {
        this.statusName = statusName;
        this.imageId = imageId;
    }
}
