package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.dto.GlobalQuestDto;

import java.util.UUID;

public class GlobalQuest implements Identifiable<UUID> {

    private final UUID globalQuestId;

    private final String groupId;

    private final String questId;

    public GlobalQuest(Quest quest) {
        this(UUID.randomUUID(), quest.getQuestGroup().getGroupId(), quest.getQuestId());
    }

    public GlobalQuest(GlobalQuestDto dto) {
        this(dto.getId(), dto.getGroupId(), dto.getQuestId());
    }

    public GlobalQuest(UUID uuid, String groupId, String questId) {
        this.globalQuestId = uuid;
        this.groupId = groupId;
        this.questId = questId;
    }

    @Override
    public UUID getId() {
        return globalQuestId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getQuestId() {
        return questId;
    }
}
