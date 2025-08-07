package fr.robotv2.questplugin.quest.context.block;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PumpkinCarveListener extends QuestProgressionEnhancer {
    public PumpkinCarveListener(QuestPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPumpkinCarve(final PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if(event.getItem() == null || event.getItem().getType() != XMaterial.SHEARS.parseMaterial()) {
            return;
        }

        if(event.getClickedBlock() == null || event.getClickedBlock().getType() != XMaterial.PUMPKIN.parseMaterial()) {
            return;
        }

        updateQuestProgress(new BlockContext<>(
                event.getPlayer(),
                QuestTypes.CARVE_TYPE,
                event,
                event.getClickedBlock()
        ));
    }
}
