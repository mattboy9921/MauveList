package net.mattlabs.mauvelist.util;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class MauvePlayerData {

    private boolean isNew = false;

    public MauvePlayerData() {}

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}
