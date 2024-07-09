package fr.robotv2.questplugin.quest.context;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RunningQuestContext<T, E extends Event> {

    @NotNull
    private final Player initiator;

    @NotNull
    private final String type;

    @Nullable
    private final E triggered;

    @Nullable
    private final T target;

    @NotNull
    private final Number amount;

    protected RunningQuestContext(@NotNull Player initiator, @NotNull String type, @Nullable E triggered, @Nullable T target) {
        this(initiator, type, triggered, target, 1);
    }

    protected RunningQuestContext(@NotNull Player initiator, @NotNull String type, @Nullable E triggered, @Nullable T target, @NotNull Number amount) {
        this.initiator = initiator;
        this.type = type;
        this.triggered = triggered;
        this.target = target;
        this.amount = amount;
    }

    public @NotNull Player getInitiator() {
        return initiator;
    }

    public @NotNull String getType() {
        return type;
    }

    public @Nullable E getTriggeredEvent() {
        return triggered;
    }

    protected @Nullable T getTarget() {
        return target;
    }

    public @NotNull Number getAmount() {
        return amount;
    }
}
