package fr.robotv2.questplugin.event;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class QuestDoneEvent extends QuestEvent {

    private final static HandlerList HANDLER_LIST = new HandlerList();
    private final ActiveQuest activeQuest;
    private final Player player;

    public QuestDoneEvent(Quest quest, ActiveQuest activeQuest, Player player) {
        super(quest);
        this.activeQuest = activeQuest;
        this.player = player;
    }

    public ActiveQuest getActiveQuest() {
        return activeQuest;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
