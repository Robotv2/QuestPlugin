package fr.robotv2.questplugin.quest.context.block;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.questplugin.quest.context.RunningQuestContext;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlockContext<E extends BlockEvent> extends RunningQuestContext<XMaterial, E> {

    private final Block block;

    public BlockContext(@NotNull Player initiator, @NotNull QuestType<?> type, @NotNull E triggered, @NotNull Block target) {
        super(initiator, type, triggered, XMaterial.matchXMaterial(target.getType()));
        Objects.requireNonNull(triggered);
        this.block = Objects.requireNonNull(target);
    }

    public Block getBlock() {
        return block;
    }
}
