package fr.robotv2.questplugin.quest.context.item;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemPickupListener extends QuestProgressionEnhancer {

    public ItemPickupListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPick(final EntityPickupItemEvent event) {

        if(!(event.getEntity() instanceof Player player)) {
            return;
        }

        final Item item = event.getItem();

//        if(this.getGlitchChecker().isMarked(item)) {
//            return;
//        }

        updateQuestProgress(new ItemContext<>(
                player,
                QuestTypes.PICKUP_TYPE,
                event,
                item.getItemStack(),
                item.getItemStack().getAmount()
        ));
    }
}
