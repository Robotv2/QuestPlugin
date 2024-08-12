package fr.robotv2.questplugin.util.color;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class LegacyColorProvider implements ColorProvider {

    @Override
    @Contract("null -> null; _ -> !null")
    public String colorize(@Nullable String text) {

        if(text == null || text.isEmpty()) {
            return text;
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
