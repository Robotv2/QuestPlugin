package fr.robotv2.questplugin.quest.type;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.quest.context.QuestProgressionEnhancer;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class QuestType<T> {

    protected final String literal;

    protected final Class<T> tClass;

    protected final Class<? extends QuestProgressionEnhancer> listener;

    protected final boolean numerical;

    public QuestType(String literal, Class<T> tClass, Class<? extends QuestProgressionEnhancer> listener) {
        this(literal, tClass, listener, true);
    }

    public QuestType(String literal, Class<T> tClass, Class<? extends QuestProgressionEnhancer> listener, boolean numerical) {
        this.literal = literal;
        this.tClass = tClass;
        this.listener = listener;
        this.numerical = numerical;
    }

    public String getLiteral() {
        return literal;
    }

    public Class<T> getRequiredClass() {
        return tClass;
    }

    public Class<? extends QuestProgressionEnhancer> getListener() {
        return listener;
    }

    public boolean isNumerical() {
        return numerical;
    }

    public boolean isLoaded() {
        return QuestTypes.getLoadedTypes().contains(this);
    }

    public void registerListener() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (this.listener == null) {
            return;
        }

        final Constructor<? extends QuestProgressionEnhancer> cstr = listener.getDeclaredConstructor(QuestPlugin.class);
        final QuestProgressionEnhancer enhancer = cstr.newInstance(QuestPlugin.instance());
        Bukkit.getPluginManager().registerEvents(enhancer, QuestPlugin.instance());
    }

    @Override
    public String toString() {
        return "QuestType{" +
                "literal='" + literal + '\'' +
                ", tClass=" + tClass +
                ", numerical=" + numerical +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestType<?> type)) return false;
        return Objects.equals(literal, type.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(literal);
    }
}
