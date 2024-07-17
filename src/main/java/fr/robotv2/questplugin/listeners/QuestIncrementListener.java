package fr.robotv2.questplugin.listeners;

import com.cryptomorin.xseries.messages.ActionBar;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.event.QuestIncrementEvent;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.task.Task;
import fr.robotv2.questplugin.util.DecimalUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestIncrementListener implements Listener {

    @EventHandler
    public void onIncrement(QuestIncrementEvent event) {

        final Quest quest = event.getQuest();
        final Task task = event.getTask();

        final String processBar = DecimalUtil.processBar(event.getProgress().doubleValue(), task.getRequiredAmount());
        final String percentage = DecimalUtil.percentage(event.getProgress().doubleValue(), task.getRequiredAmount());

        ActionBar.sendActionBar(
                QuestPlugin.instance(),
                event.getPlayer(),
                ChatColor.GREEN + "Quest " + quest.getQuestId() + " | Task " + task.getTaskId() + " | " + processBar + " | " + percentage + "%"
        );
    }
}
