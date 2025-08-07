package fr.robotv2.questplugin.quest.context.item;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class ItemEnchantListener extends QuestProgressionEnhancer {
    public ItemEnchantListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        updateQuestProgress(new ItemContext<>(
                event.getEnchanter(),
                QuestTypes.ENCHANT_TYPE,
                event,
                event.getItem(),
                event.getItem().getAmount()
        ));
    }
}
