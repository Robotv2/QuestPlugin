package fr.robotv2.questplugin.event;

import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.ActiveTask;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class TaskIncrementEvent extends TaskEvent implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;

    private final ActiveQuest activeQuest;

    private final ActiveTask activeTask;

    private boolean cancel = false;

    public TaskIncrementEvent(Quest quest, Task task, ActiveQuest activeQuest, ActiveTask activeTask, Player player) {
        super(quest, task, activeTask);
        this.player = player;
        this.activeQuest = activeQuest;
        this.activeTask = activeTask;
    }

    public Player getPlayer() {
        return player;
    }

    public ActiveQuest getActiveQuest() {
        return activeQuest;
    }

    public ActiveTask getActiveTask() {
        return activeTask;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
