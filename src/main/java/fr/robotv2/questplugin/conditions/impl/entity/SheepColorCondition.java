package fr.robotv2.questplugin.conditions.impl.entity;

import com.google.common.base.Enums;
import fr.robotv2.questplugin.conditions.Condition;
import fr.robotv2.questplugin.quest.context.RunningQuestContext;
import fr.robotv2.questplugin.quest.context.entity.EntityContext;
import org.bukkit.DyeColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;

import java.util.Objects;

public class SheepColorCondition implements Condition {

    private final DyeColor color;

    public SheepColorCondition(String key, ConfigurationSection parent) {
        final String dyeColorString = Objects.requireNonNull(parent.getString(key));
        this.color = Enums.getIfPresent(DyeColor.class, dyeColorString).orNull();
    }

    @Override
    public boolean isMet(RunningQuestContext<?, ?> context) {
        if(context instanceof EntityContext<?> entityContext) {
            final Entity entity = entityContext.getEntity();
            if(entity instanceof Sheep sheep && this.color != null) {
                return sheep.getColor() == this.color;
            }
        }

        return true;
    }
}
