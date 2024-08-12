package fr.robotv2.questplugin.util.placeholder.impl;

@FunctionalInterface
public interface ValuePlaceholder<A> {
    String apply(String text, A value);
}
