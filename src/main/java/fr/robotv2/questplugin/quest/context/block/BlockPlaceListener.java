package fr.robotv2.questplugin.quest.context.block;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener extends QuestProgressionEnhancer {

    public BlockPlaceListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent event) {
        updateQuestProgress(new BlockContext<>(event.getPlayer(), QuestType.PLACE_TYPE, event, event.getBlock()));
    }
}
