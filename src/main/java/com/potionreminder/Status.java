package com.potionreminder;

import lombok.Getter;
import net.runelite.api.ItemID;

@Getter
enum Status {
    STAMINA("Stamina", ItemID.STAMINA_POTION4, false),
    ANTIFIRE("Antifire", ItemID.ANTIFIRE_POTION4, false),
    SUPER_ANTIFIRE("Super antifire", ItemID.SUPER_ANTIFIRE_POTION4, false),
    ANTIPOISON("Antipoison", ItemID.ANTIPOISON4, false),
    ANTIVENOM("Anti-venom", ItemID.ANTIVENOM4, false),
    DIVINE_SUPER_ATTACK("Divine super attack", ItemID.DIVINE_SUPER_ATTACK_POTION4, false),
    DIVINE_SUPER_STRENGTH("Divine super strength", ItemID.DIVINE_SUPER_STRENGTH_POTION4, false),
    DIVINE_SUPER_DEFENCE("Divine super defence", ItemID.DIVINE_SUPER_DEFENCE_POTION4, false),
    DIVINE_SUPER_COMBAT("Divine super combat", ItemID.DIVINE_SUPER_COMBAT_POTION4, false),
    DIVINE_RANGING("Divine ranging", ItemID.DIVINE_RANGING_POTION4, false),
    DIVINE_MAGIC("Divine magic", ItemID.DIVINE_MAGIC_POTION4, false),
    DIVINE_BASTION("Divine bastion", ItemID.DIVINE_BASTION_POTION4, false),
    DIVINE_BATTLEMAGE("Divine battlemage", ItemID.DIVINE_BATTLEMAGE_POTION4, false),
    IMBUED_HEART("Imbued heart", ItemID.IMBUED_HEART, false)
    ;

    private final String statusName;
    private final int imageId;
    private final boolean removedOnDeath;

    Status(String statusName, int imageId, boolean removedOnDeath)
    {
        this.statusName = statusName;
        this.imageId = imageId;
        this.removedOnDeath = removedOnDeath;
    }
}
