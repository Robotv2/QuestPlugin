package fr.robotv2.questplugin.quest.context;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BlockContext<E extends BlockEvent> extends RunningQuestContext<Block, E> {

    public BlockContext(@NotNull Player initiator, @NotNull String type, @NotNull E triggered, @NotNull Block target) {
        super(initiator, type, triggered, target);
        Objects.requireNonNull(triggered);
        Objects.requireNonNull(target);
    }

    public Block getBlock() {
        return getTarget();
    }
}
