package fr.robotv2.questplugin.util.color;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModernColorProvider implements ColorProvider {

    public static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    @Override
    @Contract("null -> null; _ -> !null")
    public String colorize(@Nullable String text) {

        if(text == null || text.isEmpty()) {
            return text;
        }

        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);

        while (matcher.find()) {
            final String color = text.substring(matcher.start() + 1, matcher.end());
            text = text.replace("&".concat(color), ChatColor.of(color) + "");
            matcher = HEX_COLOR_PATTERN.matcher(text);
        }

        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
