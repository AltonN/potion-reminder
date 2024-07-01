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
    DIVINE_SUPER_ATTACK("Divine super attack", ItemID.DIVINE_SUPER_ATTACK_POTION4),
    DIVINE_SUPER_STRENGTH("Divine super strength", ItemID.DIVINE_SUPER_STRENGTH_POTION4),
    DIVINE_SUPER_DEFENCE("Divine super defence", ItemID.DIVINE_SUPER_DEFENCE_POTION4),
    DIVINE_SUPER_COMBAT("Divine super combat", ItemID.DIVINE_SUPER_COMBAT_POTION4),
    DIVINE_RANGING("Divine ranging", ItemID.DIVINE_RANGING_POTION4),
    DIVINE_MAGIC("Divine magic", ItemID.DIVINE_MAGIC_POTION4),
    DIVINE_BASTION("Divine bastion", ItemID.DIVINE_BASTION_POTION4),
    DIVINE_BATTLEMAGE("Divine battlemage", ItemID.DIVINE_BATTLEMAGE_POTION4),
    ;

    private final String statusName;
    private final int imageId;

    Status(String statusName, int imageId)
    {
        this.statusName = statusName;
        this.imageId = imageId;
    }
}
