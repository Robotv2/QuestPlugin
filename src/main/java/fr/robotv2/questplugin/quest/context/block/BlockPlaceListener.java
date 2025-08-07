package fr.robotv2.questplugin.quest.context.block;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener extends QuestProgressionEnhancer {

    public BlockPlaceListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        updateQuestProgress(new BlockContext<>(event.getPlayer(), QuestTypes.PLACE_TYPE, event, event.getBlock()));
    }
}
