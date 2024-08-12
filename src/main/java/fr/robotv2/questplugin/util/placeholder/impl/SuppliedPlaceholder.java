package fr.robotv2.questplugin.util.placeholder.impl;

import java.util.function.Supplier;

public class SuppliedPlaceholder {

    private final String from;
    private final Supplier<String> toSupplier;

    public SuppliedPlaceholder(String from, Supplier<String> toSupplier) {
        this.from = from;
        this.toSupplier = toSupplier;
    }

    public String apply(String text) {
        return text.replace(from, toSupplier.get());
    }
}
