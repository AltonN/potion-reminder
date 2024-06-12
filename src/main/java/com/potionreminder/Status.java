package com.potionreminder;

enum Status {
    STAMINA("Stamina"),
    ANTIFIRE("Antifire"),
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
