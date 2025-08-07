package fr.robotv2.questplugin.quest.context.item;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.questplugin.quest.context.RunningQuestContext;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemContext<E extends Event> extends RunningQuestContext<XMaterial, E> {

    private final ItemStack item;

    public ItemContext(@NotNull Player initiator, @NotNull QuestType<?> type, @Nullable E triggered, @NotNull ItemStack target) {
        super(initiator, type, triggered, XMaterial.matchXMaterial(target));
        this.item = target;
    }

    public ItemContext(@NotNull Player initiator, @NotNull QuestType<?> type, @Nullable E triggered, @NotNull ItemStack target, int amount) {
        super(initiator, type, triggered, XMaterial.matchXMaterial(target), amount);
        this.item = target;
    }

    public ItemStack getItem() {
        return item;
    }
}
