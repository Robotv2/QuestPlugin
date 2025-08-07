package fr.robotv2.questplugin.quest.context.item;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public class ItemFishListener extends QuestProgressionEnhancer {
    public ItemFishListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if(event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Entity entity = event.getCaught();

        if(entity == null) {
            return;
        }

        if(entity instanceof Item) {
            final ItemContext<PlayerFishEvent> context = new ItemContext<>(event.getPlayer(), QuestTypes.FISH_ITEM_TYPE, event, ((Item) entity).getItemStack());
            updateQuestProgress(context);
        }
    }
}
