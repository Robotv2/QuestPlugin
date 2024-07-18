package fr.robotv2.questplugin.util;

import org.bukkit.ChatColor;

import java.text.DecimalFormat;

public class DecimalUtil {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.##");

    public static String format(double value) {
        return DECIMAL_FORMAT.format(value);
    }

    public static String percentage(double currentValue, double maximumValue) {
        return percentage(currentValue, maximumValue, 100);
    }

    public static String percentage(double currentValue, double maximumValue, double scale) {
        return format(Math.min(currentValue * scale / maximumValue, scale));
    }

    public static String processBar(double amount, double required, int barNumber) {
        final StringBuilder builder = new StringBuilder();

        final double greenBarNumber = amount * barNumber / required;
        final double grayBarNumber = barNumber - greenBarNumber;

        for (int i = 0; i < greenBarNumber; i++) {
            builder.append(ChatColor.GREEN).append("|");
        }

        for (int i = 0; i < grayBarNumber; i++) {
            builder.append(ChatColor.GRAY).append("|");
        }

        return builder.toString();
    }

    public static String processBar(double amount, double required) {
        return processBar(amount, required, 20);
    }

}
