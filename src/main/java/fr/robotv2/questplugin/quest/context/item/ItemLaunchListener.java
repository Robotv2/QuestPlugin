package fr.robotv2.questplugin.quest.context.item;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.context.entity.EntityContext;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ItemLaunchListener extends QuestProgressionEnhancer {

    public ItemLaunchListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectile(ProjectileLaunchEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity().getShooter();
        updateQuestProgress(new EntityContext<>(
                player,
                QuestTypes.LAUNCH_TYPE,
                event,
                event.getEntity()
        ));
    }
}
