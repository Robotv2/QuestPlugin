package fr.robotv2.questplugin.configurations.time;

import org.bukkit.configuration.ConfigurationSection;

public class TimeFormatConfiguration {

    private final String daySuffix;

    private final String hourSuffix;

    private final String minuteSuffix;

    private final String secondSuffix;

    private final String format;

    public TimeFormatConfiguration(ConfigurationSection section) {
        this.daySuffix = section.getString("days");
        this.hourSuffix = section.getString("hours");
        this.minuteSuffix = section.getString("minutes");
        this.secondSuffix = section.getString("seconds");
        this.format = section.getString("format");
    }

    public String getDay() {
        return daySuffix;
    }

    public String getHour() {
        return hourSuffix;
    }

    public String getMinute() {
        return minuteSuffix;
    }

    public String getSecond() {
        return secondSuffix;
    }

    public String getFormat() {
        return format;
    }

    public String format(long milli) {
        long days = milli / 86400000;
        long hours = (milli % 86400000) / 3600000;
        long minutes = (milli % 3600000) / 60000;
        long seconds = (milli % 60000) / 1000;
        return format
                .replace("%days%", days > 0 ? days + daySuffix : "")
                .replace("%hours%", hours > 0 ? hours + hourSuffix : "")
                .replace("%minutes%", minutes > 0 ? minutes + minuteSuffix : "")
                .replace("%seconds%", seconds + secondSuffix);
    }
}
