package fr.robotv2.questplugin.quest.context.item;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ComplexRecipe;
import org.bukkit.inventory.ItemStack;

public class ItemCraftListener extends QuestProgressionEnhancer {

    public ItemCraftListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftItem(final CraftItemEvent event) {
        final ItemStack item = event.getRecipe() instanceof ComplexRecipe complex ?
                new ItemStack(Material.valueOf(complex.getKey().getKey())) :
                event.getCurrentItem() != null ? event.getCurrentItem().clone() : null;

        if(item == null) {
            return;
        }

        updateQuestProgress(new ItemContext<>(
                (Player) event.getWhoClicked(),
                QuestTypes.CRAFT_TYPE,
                event,
                item
        ));
    }
}
