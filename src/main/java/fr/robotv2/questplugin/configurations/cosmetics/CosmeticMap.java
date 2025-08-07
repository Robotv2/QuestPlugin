package fr.robotv2.questplugin.configurations.cosmetics;

import fr.robotv2.questplugin.configurations.cosmetics.impl.ActionBarTitlesSection;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.EnumMap;

public class CosmeticMap extends EnumMap<CosmeticType, ActionBarTitlesSection> {
    public CosmeticMap(ConfigurationSection section) {
        super(CosmeticType.class);

        Arrays.stream(CosmeticType.values()).forEach((type) -> {
            if (section == null) {
                put(type, ActionBarTitlesSection.EMPTY);
            } else {
                final String key = type.name().toLowerCase();
                final ConfigurationSection cosmeticSection = section.getConfigurationSection(key);
                put(type, new ActionBarTitlesSection(cosmeticSection));
            }
        });
    }
}
