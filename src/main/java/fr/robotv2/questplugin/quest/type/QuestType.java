package fr.robotv2.questplugin.quest.type;

import org.bukkit.Material;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class QuestType<T> {

    public static final Map<String, QuestType<?>> TYPES = new HashMap<>();

    public static final QuestType<Material> BREAK_TYPE = registerType(new QuestType<>("BLOCK", Material.class));

    public static final QuestType<Material> PLACE_TYPE = registerType(new QuestType<>("PLACE", Material.class));

    @Contract("_ -> _;")
    public static <T> QuestType<T> registerType(QuestType<T> type) {
        TYPES.put(type.literal.toLowerCase(), type);
        return type;
    }

    @Nullable
    public static QuestType<?> getByLiteral(String literal) {
        return TYPES.get(literal.toLowerCase());
    }

    private final String literal;

    private final Class<T> tClass;

    private final boolean numerical;

    public QuestType(String literal, Class<T> tClass) {
        this(literal, tClass, true);
    }

    public QuestType(String literal, Class<T> tClass, boolean numerical) {
        this.literal = literal;
        this.tClass = tClass;
        this.numerical = numerical;
    }

    public String getLiteral() {
        return literal;
    }

    public Class<T> getRequiredClass() {
        return tClass;
    }

    public boolean isNumerical() {
        return numerical;
    }
}
