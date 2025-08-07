package fr.robotv2.questplugin.quest.context.entity;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class EntityShearListener extends QuestProgressionEnhancer {

    public EntityShearListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onShear(final PlayerShearEntityEvent event) {
        updateQuestProgress(new EntityContext<>(
                event.getPlayer(),
                QuestTypes.SHEAR_TYPE,
                event,
                event.getEntity()
        ));
    }
}
