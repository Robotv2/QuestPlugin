package fr.robotv2.questplugin.conditions;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.conditions.impl.WorldCondition;
import fr.robotv2.questplugin.conditions.impl.entity.SheepColorCondition;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public class ConditionManager {

    private final QuestPlugin plugin;
    private final Map<String, Class<? extends Condition>> conditions = new HashMap<>();
    private boolean canRegister = true;

    public ConditionManager(QuestPlugin plugin) {
        this.plugin = plugin;
        registerDefaultConditions();
    }

    public void closeRegistration() {
        this.canRegister = false;
    }

    public void registerCondition(String key, Class<? extends Condition> conditionClazz) {

        if(!canRegister) {
            throw new IllegalStateException("Please register your condition in the JavaPlugin#onLoad method.");
        }

        if(this.checkConditionClass(conditionClazz)) {
            this.conditions.put(key, conditionClazz);
        } else {
            plugin.getLogger().warning("Couldn't register condition: " + key);
            plugin.getLogger().warning("Please, be sure that you're class have the required constructor.");
        }
    }

    public Optional<Condition> toInstance(ConfigurationSection parent, String key) {
        final Class<? extends Condition> conditionClazz = conditions.get(key);

        if(conditionClazz == null) {
            throw new NullPointerException(key + " is not a valid condition. Maybe it isn't registered ?");
        }

        try {
            final Constructor<? extends Condition> constructor = conditionClazz.getConstructor(String.class, ConfigurationSection.class);
            return Optional.of(constructor.newInstance(key, parent));
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred while creating condition for class: " + conditionClazz.getSimpleName(), exception);
        }

        return Optional.empty();
    }

    public void registerDefaultConditions() {

        registerCondition("required_worlds", WorldCondition.class);
        registerCondition("sheep_color", SheepColorCondition.class);

    }

    private boolean checkConditionClass(Class<? extends Condition> conditionClazz) {
        try {
            conditionClazz.getConstructor(String.class, ConfigurationSection.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
