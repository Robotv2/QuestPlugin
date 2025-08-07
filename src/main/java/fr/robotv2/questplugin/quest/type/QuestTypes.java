package fr.robotv2.questplugin.quest.type;

import com.cryptomorin.xseries.XEntityType;
import com.cryptomorin.xseries.XMaterial;
import fr.robotv2.questplugin.quest.context.block.BlockBreakListener;
import fr.robotv2.questplugin.quest.context.block.BlockPlaceListener;
import fr.robotv2.questplugin.quest.context.block.PumpkinCarveListener;
import fr.robotv2.questplugin.quest.context.entity.*;
import fr.robotv2.questplugin.quest.context.item.*;
import fr.robotv2.questplugin.quest.context.player.PlayerDeathListener;
import fr.robotv2.questplugin.quest.context.player.PlayerLocationListener;
import fr.robotv2.questplugin.util.McVersion;
import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuestTypes {

    public static final Map<String, QuestType<?>> TYPES = new HashMap<>();

    // block types
    public static final QuestType<XMaterial>    BREAK_TYPE = registerType(new QuestType<>("BREAK", XMaterial.class, BlockBreakListener.class));
    public static final QuestType<XMaterial>    PLACE_TYPE = registerType(new QuestType<>("PLACE", XMaterial.class, BlockPlaceListener.class));

    // entity types
    public static final QuestType<Void>         CARVE_TYPE = registerType(new QuestType<>("CARVE", Void.class, PumpkinCarveListener.class));
    public static final QuestType<XEntityType>  BREED_TYPE = registerType(new QuestType<>("BREED", XEntityType.class, EntityBreedListener.class), new McVersion(1, 10));
    public static final QuestType<XEntityType>  FISH_ENTITY_TYPE = registerType(new QuestType<>("FISH_ENTITY", XEntityType.class, EntityFishListener.class));
    public static final QuestType<XEntityType>  KILL_TYPE = registerType(new QuestType<>("KILL", XEntityType.class, EntityKillListener.class));
    public static final QuestType<Void>         MILK_TYPE = registerType(new QuestType<>("MILK", Void.class, EntityMilkListener.class));
    public static final QuestType<Void>         SHEAR_TYPE = registerType(new QuestType<>("SHEAR", Void.class, EntityShearListener.class));
    public static final QuestType<XEntityType>  TAME_TYPE = registerType(new QuestType<>("TAME", XEntityType.class, EntityTameListener.class));

    // item types
    public static final QuestType<XMaterial>    CONSUME_TYPE = registerType(new QuestType<>("CONSUME", XMaterial.class, ItemConsumeListener.class));
    public static final QuestType<XMaterial>    COOK_TYPE = registerType(new QuestType<>("COOK", XMaterial.class, ItemCookListener.class));
    public static final QuestType<XMaterial>    ENCHANT_TYPE = registerType(new QuestType<>("ENCHANT", XMaterial.class, ItemEnchantListener.class));
    public static final QuestType<XMaterial>    FISH_ITEM_TYPE = registerType(new QuestType<>("FISH_ITEM", XMaterial.class, ItemFishListener.class));
    public static final QuestType<XEntityType>  LAUNCH_TYPE = registerType(new QuestType<>("LAUNCH", XEntityType.class, ItemLaunchListener.class));
    public static final QuestType<XMaterial>    PICKUP_TYPE = registerType(new QuestType<>("PICKUP", XMaterial.class, ItemPickupListener.class), new McVersion(1, 9));
    public static final QuestType<XMaterial>    CRAFT_TYPE = registerType(new QuestType<>("CRAFT", XMaterial.class, ItemCraftListener.class));

    // player types
    public static final QuestType<EntityDamageEvent.DamageCause> PLAYER_DEATH_TYPE = registerType(new QuestType<>("DEATH", EntityDamageEvent.DamageCause.class, PlayerDeathListener.class));
    public static final QuestType<Location>     LOCATION_TYPE = registerType(new QuestType<>("LOCATION", Location.class, PlayerLocationListener.class,false));

    @Contract("_ -> _")
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
}
