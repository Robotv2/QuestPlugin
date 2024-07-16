package fr.robotv2.questplugin.quest.context.block;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener extends QuestProgressionEnhancer {

    public BlockBreakListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        updateQuestProgress(new BlockContext<>(event.getPlayer(), QuestType.BREAK_TYPE, event, event.getBlock()));
    }
}
