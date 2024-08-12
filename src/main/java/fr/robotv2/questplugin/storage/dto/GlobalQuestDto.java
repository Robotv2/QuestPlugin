package fr.robotv2.questplugin.storage.dto;

import fr.robotv2.questplugin.storage.Identifiable;

import java.util.UUID;

public class GlobalQuestDto implements Identifiable<UUID> {

    private final UUID globalQuestId;

    private final String groupId;

    private final String questId;

    public GlobalQuestDto(UUID uuid, String groupId, String questId) {
        this.globalQuestId = uuid;
        this.groupId = groupId;
        this.questId = questId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getQuestId() {
        return questId;
    }

    @Override
    public UUID getId() {
        return globalQuestId;
    }
}
