package fr.robotv2.questplugin.storage.dto;

import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.QuestPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestPlayerDto implements Identifiable<UUID> {

    private final UUID playerUniqueId;

    private final String playerName;

    private final Map<String, Integer> questDone;

    public QuestPlayerDto(QuestPlayer questPlayer) {
        this.playerUniqueId = questPlayer.getUID();
        this.playerName = questPlayer.getPlayerName();
        this.questDone = new HashMap<>(questPlayer.getQuestDone());
    }

    @Override
    public UUID getUID() {
        return playerUniqueId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Map<String, Integer> getQuestDone() {
        return questDone;
    }
}
