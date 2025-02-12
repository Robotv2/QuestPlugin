package fr.robotv2.questplugin.event;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class TaskDoneEvent extends TaskEvent {

    private final static HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;

    public TaskDoneEvent(Player player, Quest quest, Task task, ActiveTask activeTask) {
        super(quest, task, activeTask);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
