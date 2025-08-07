package fr.robotv2.questplugin.storage.dto;

import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.ActiveQuest;

import java.util.UUID;

public class ActiveQuestDto implements Identifiable<UUID> {

    private final UUID activeQuestUniqueId;

    private final UUID owner;

    private final String questId;

    private final String groupId;

    private final long nextReset;

    private final boolean started;

    private final int rerollCount;

    public ActiveQuestDto(ActiveQuest activeQuest) {
        this.activeQuestUniqueId = activeQuest.getUID();
        this.owner = activeQuest.getOwner();
        this.questId = activeQuest.getQuestId();
        this.groupId = activeQuest.getGroupId();
        this.nextReset = activeQuest.getNextReset();
        this.started = activeQuest.isStarted();
        this.rerollCount = activeQuest.getRerollCount();
    }

    @Override
    public UUID getUID() {
        return activeQuestUniqueId;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getQuestId() {
        return questId;
    }

    public String getGroupId() {
        return groupId;
    }

    public long getNextReset() {
        return nextReset;
    }

    public boolean isStarted() {
        return started;
    }

    public int getRerollCount() {
        return rerollCount;
    }
}
