package fr.robotv2.questplugin.quest.context.entity;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class EntityMilkListener extends QuestProgressionEnhancer {

    public EntityMilkListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityMilk(final PlayerInteractEntityEvent event) {
        if(event.getPlayer().getInventory().getItemInHand().getType() != XMaterial.BUCKET.parseMaterial()) {
            return;
        }

        if(event.getRightClicked().getType() != EntityType.COW) {
            return;
        }

        updateQuestProgress(new EntityContext<>(
                event.getPlayer(),
                QuestTypes.MILK_TYPE,
                event,
                event.getRightClicked()
        ));
    }
}
