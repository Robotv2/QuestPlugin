package fr.robotv2.questplugin.storage.model;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.Identifiable;

public class SimpleQuestRecord implements java.io.Serializable, Identifiable<String> {

    private final String id;
    private final String groupId;

    public SimpleQuestRecord(Quest quest) {
        this.id = quest.getQuestId();
        this.groupId = quest.getQuestGroup().getGroupId();
    }

    public SimpleQuestRecord(String id, String groupId) {
        this.id = id;
        this.groupId = groupId;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }
}
