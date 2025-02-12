package fr.robotv2.questplugin.quest.type;

import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.questplugin.util.McVersion;
import org.bukkit.Location;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class QuestType<T> {

    public static final Map<String, QuestType<?>> TYPES = new HashMap<>();

    public static final QuestType<XMaterial> BREAK_TYPE = registerType(new QuestType<>("BREAK", XMaterial.class));

    public static final QuestType<XMaterial> PLACE_TYPE = registerType(new QuestType<>("PLACE", XMaterial.class));

    public static final QuestType<Location> LOCATION_TYPE = registerType(new QuestType<>("LOCATION", Location.class));

    @Contract("_ -> _;")
    public static <T> QuestType<T> registerType(QuestType<T> type) {
        TYPES.put(type.literal.toLowerCase(), type);
        return type;
    }

    @Nullable
    public static <T> QuestType<T> registerType(QuestType<T> type, McVersion required) {
        return McVersion.current().isAtLeast(required) ? registerType(type) : null;
    }

    @Nullable
    public static QuestType<?> getByLiteral(String literal) {
        return TYPES.get(literal.toLowerCase());
    }

    @UnmodifiableView
    public static Collection<QuestType<?>> getLoadedTypes() {
        return Collections.unmodifiableCollection(TYPES.values());
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

    public boolean isLoaded() {
        return getLoadedTypes().contains(this);
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
        if (!(o instanceof QuestType)) return false;
        QuestType<?> questType = (QuestType<?>) o;
        return Objects.equals(literal, questType.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(literal);
    }
}
