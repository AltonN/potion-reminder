package com.potionreminder;

enum Status {
    STAMINA("Stamina"),
    ANTIFIRE("Antifire"),
    SUPER_ANTIFIRE("Super antifire"),
    ANTIPOISON("Antipoison"),
    ANTIVENOM("Anti-venom"),
    ;

    private final String statusName;

    Status(String statusName)
    {
        this.statusName = statusName;
    }

    public String getStatusName()
    {
        return this.statusName;
    }
}
