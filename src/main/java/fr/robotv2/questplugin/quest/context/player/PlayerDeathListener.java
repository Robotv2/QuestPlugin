package fr.robotv2.questplugin.quest.context.player;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.context.RunningQuestContext;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDeathListener extends QuestProgressionEnhancer {

    public PlayerDeathListener(QuestPlugin plugin) {
        super(plugin);
    }

    public static class PlayerDeathContext extends RunningQuestContext<EntityDamageEvent.DamageCause, PlayerDeathEvent> {
        public PlayerDeathContext(@NotNull Player initiator, @NotNull PlayerDeathEvent triggered, EntityDamageEvent.@NotNull DamageCause target) {
            super(initiator, QuestTypes.PLAYER_DEATH_TYPE, triggered, target);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {

        final EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
        final EntityDamageEvent.DamageCause cause = entityDamageEvent != null ? entityDamageEvent.getCause() : EntityDamageEvent.DamageCause.CUSTOM;

        updateQuestProgress(new PlayerDeathContext(
                event.getEntity(),
                event,
                cause
        ));
    }
}
