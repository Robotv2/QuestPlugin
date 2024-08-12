package fr.robotv2.questplugin.util.placeholder.impl;

public class SimplePlaceholder {

    private final String from;
    private final String to;

    SimplePlaceholder(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public static SimplePlaceholder of(String from, String to) {
        return new SimplePlaceholder(from, to);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String apply(String text) {
        return text.replace(from, to);
    }
}
