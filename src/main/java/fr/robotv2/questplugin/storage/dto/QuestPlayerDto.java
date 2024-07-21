package fr.robotv2.questplugin.storage.dto;

import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuestPlayerDto implements Identifiable<UUID> {

    private final UUID playerUniqueId;

    private final String playerName;

    private final List<UUID> activeQuests;

    public QuestPlayerDto(QuestPlayer questPlayer) {
        this(
                questPlayer.getId(),
                questPlayer.getPlayerName(),
                questPlayer.getActiveQuests().stream().map(ActiveQuest::getId).collect(Collectors.toList())
        );
    }

    public QuestPlayerDto(UUID playerUniqueId, String playerName, List<UUID> activeQuests) {
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.activeQuests = activeQuests;
    }

    @Override
    public UUID getId() {
        return playerUniqueId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<UUID> getActiveQuests() {
        return activeQuests;
    }
}
