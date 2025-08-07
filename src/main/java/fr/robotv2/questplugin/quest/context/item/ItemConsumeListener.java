package fr.robotv2.questplugin.quest.context.item;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ItemConsumeListener extends QuestProgressionEnhancer {

    public ItemConsumeListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        updateQuestProgress(new ItemContext<>(
                event.getPlayer(),
                QuestTypes.CONSUME_TYPE,
                event,
                event.getItem()
        ));
    }
}
