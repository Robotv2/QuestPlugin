package fr.robotv2.questplugin.quest.context.entity;

import com.cryptomorin.xseries.XEntityType;
import fr.robotv2.questplugin.quest.context.RunningQuestContext;
import fr.robotv2.questplugin.quest.type.QuestType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Objects;

public class EntityContext<E extends Event> extends RunningQuestContext<XEntityType, E> {

    private final Entity entity;

    public EntityContext(Player initiator, QuestType<?> type, E triggered, Entity target) {
        super(initiator, type, triggered, XEntityType.of(target.getType()));
        Objects.requireNonNull(triggered);
        this.entity = Objects.requireNonNull(target);
    }

    public Entity getEntity() {
        return entity;
    }
}
