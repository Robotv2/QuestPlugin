package fr.robotv2.questplugin.event;

import fr.robotv2.questplugin.quest.Quest;
import org.bukkit.event.Event;

public abstract class QuestEvent extends Event {

    private final Quest quest;

    protected QuestEvent(Quest quest) {
        this.quest = quest;
    }

    public Quest getQuest() {
        return quest;
    }
}
