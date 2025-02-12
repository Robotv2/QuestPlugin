package fr.robotv2.questplugin.storage.loader;

import java.util.UUID;

public interface MultiServerLoader extends PlayerLoader {

    boolean canBeLoaded(UUID playerId);

    void setCanBeLoaded(UUID playerId, boolean value);

}
