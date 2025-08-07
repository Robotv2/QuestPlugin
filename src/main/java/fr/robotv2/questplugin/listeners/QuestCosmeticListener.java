package fr.robotv2.questplugin.listeners;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.configurations.cosmetics.impl.ActionBarTitlesSection;
import fr.robotv2.questplugin.configurations.cosmetics.CosmeticType;
import fr.robotv2.questplugin.configurations.cosmetics.Cosmeticable;
import fr.robotv2.questplugin.event.QuestDoneEvent;
import fr.robotv2.questplugin.event.TaskIncrementEvent;
import fr.robotv2.questplugin.event.TaskDoneEvent;
import fr.robotv2.questplugin.util.ListActionProcessor;
import fr.robotv2.questplugin.util.placeholder.Placeholders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestCosmeticListener implements Listener {

    private final QuestPlugin plugin;

    public QuestCosmeticListener(QuestPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuestDone(QuestDoneEvent event) {
        // order is quest -> group > global configuration
        final ActionBarTitlesSection cosmetic = getFirstAvailableCosmetic(CosmeticType.QUEST_DONE,
                event.getQuest(),
                event.getQuest().getQuestGroup(),
                plugin.getQuestConfiguration()
        );

        if (cosmetic != null) {
            cosmetic.apply(Placeholders.ACTIVE_QUEST_PLACEHOLDER, event.getActiveQuest()).color().send(event.getPlayer());
        }

        new ListActionProcessor(event.getQuest().getRewards())
                .apply(Placeholders.ACTIVE_QUEST_PLACEHOLDER, event.getActiveQuest())
                .color()
                .process(event.getPlayer());
    }

    @EventHandler
    public void onTaskIncrement(TaskIncrementEvent event) {
        // order is task -> quest -> group > global configuration
        final ActionBarTitlesSection cosmetic = getFirstAvailableCosmetic(CosmeticType.TASK_INCREMENT,
                event.getTask(),
                event.getQuest(),
                event.getQuest().getQuestGroup(),
                plugin.getQuestConfiguration()
        );

        if (cosmetic != null) {
            cosmetic.apply(Placeholders.ACTIVE_TASK_PLACEHOLDER, event.getActiveTask()).color().send(event.getPlayer());
        }
    }

    @EventHandler
    public void onTaskDone(TaskDoneEvent event) {
        // order is task -> quest -> group > global configuration
        final ActionBarTitlesSection cosmetic = getFirstAvailableCosmetic(CosmeticType.TASK_DONE,
                event.getTask(),
                event.getQuest(),
                event.getQuest().getQuestGroup(),
                plugin.getQuestConfiguration()
        );

        if (cosmetic != null) {
            cosmetic.apply(Placeholders.ACTIVE_TASK_PLACEHOLDER, event.getActiveTask()).color().send(event.getPlayer());
        }

        new ListActionProcessor(event.getTask().getRewards())
                .apply(Placeholders.ACTIVE_TASK_PLACEHOLDER, event.getActiveTask())
                .color()
                .process(event.getPlayer());
    }

    private ActionBarTitlesSection getFirstAvailableCosmetic(CosmeticType type, Cosmeticable... cosmeticables) {
        for (Cosmeticable cosmeticable : cosmeticables) {
            final ActionBarTitlesSection section = cosmeticable.getCosmeticMap().get(type);
            if (section != null && !section.isEmpty()) {
                return section;
            }
        }
        return null;
    }
}
