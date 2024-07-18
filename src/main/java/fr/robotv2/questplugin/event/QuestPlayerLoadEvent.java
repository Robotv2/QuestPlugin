package fr.robotv2.questplugin.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class QuestPlayerLoadEvent extends PlayerEvent {

    private final static HandlerList HANDLER_LIST = new HandlerList();
    private final boolean success;

    public QuestPlayerLoadEvent(Player who, boolean success) {
        super(who);
        this.success = success;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public boolean isSuccess() {
        return success;
    }
}
