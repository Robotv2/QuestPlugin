package fr.robotv2.questplugin.quest.context.entity;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;

public class EntityFishListener extends QuestProgressionEnhancer {

    public EntityFishListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFish(final PlayerFishEvent event) {
        if(event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }

        final Player player = event.getPlayer();
        final Entity entity = event.getCaught();

        if(entity == null) {
            return;
        }

        updateQuestProgress(new EntityContext<>(
                player,
                QuestTypes.FISH_ENTITY_TYPE,
                event,
                entity
        ));
    }
}
