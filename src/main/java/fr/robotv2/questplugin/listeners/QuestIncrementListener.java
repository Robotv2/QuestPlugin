package fr.robotv2.questplugin.listeners;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.event.QuestDoneEvent;
import fr.robotv2.questplugin.event.QuestIncrementEvent;
import fr.robotv2.questplugin.util.placeholder.Placeholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestIncrementListener implements Listener {

    private final QuestPlugin plugin;

    public QuestIncrementListener(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onIncrement(QuestIncrementEvent event) {
        plugin.getQuestConfiguration().getQuestIncrementCosmetics()
                .apply(Placeholders.ACTIVE_TASK_PLACEHOLDER, event.getActiveTask())
                .color()
                .send(event.getPlayer());
    }

    @EventHandler
    public void onDone(QuestDoneEvent event) {
        plugin.getQuestConfiguration().getQuestDoneCosmetics()
                .apply(Placeholders.ACTIVE_QUEST_PLACEHOLDER, event.getActiveQuest())
                .color()
                .send(event.getPlayer());
    }
}
