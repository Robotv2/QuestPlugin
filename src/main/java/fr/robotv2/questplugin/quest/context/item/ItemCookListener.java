package fr.robotv2.questplugin.quest.context.item;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemCookListener extends QuestProgressionEnhancer {
    public ItemCookListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFurnaceExtract(FurnaceExtractEvent event) {
        updateQuestProgress(new ItemContext<>(
                event.getPlayer(),
                QuestTypes.COOK_TYPE,
                event,
                new ItemStack(event.getItemType(), event.getItemAmount()),
                event.getItemAmount()
        ));
    }
}
