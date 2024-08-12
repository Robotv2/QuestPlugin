package fr.robotv2.questplugin.api.configurations;

public interface Sendable<T> {
    void send(T value);
}
