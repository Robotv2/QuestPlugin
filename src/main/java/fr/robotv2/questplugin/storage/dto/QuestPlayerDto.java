package fr.robotv2.questplugin.storage.dto;

import fr.maxlego08.sarah.Column;
import fr.robotv2.questplugin.storage.Identifiable;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class QuestPlayerDto implements Identifiable<UUID> {

    @Column("uuid")
    private final UUID playerUniqueId;

    @Column("name")
    private final String playerName;

    @Column("active_quests")
    private final List<UUID> activeQuests;

    @Column("quest_done")
    private final Map<String, Integer> questDone;

    public QuestPlayerDto(QuestPlayer questPlayer) {
        this(
                questPlayer.getId(),
                questPlayer.getPlayerName(),
                questPlayer.getActiveQuests().stream().map(ActiveQuest::getId).collect(Collectors.toList()),
                questPlayer.getQuestDone()
        );
    }

    public QuestPlayerDto(UUID playerUniqueId, String playerName, List<UUID> activeQuests, Map<String, Integer> questDone) {
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.activeQuests = activeQuests;
        this.questDone = questDone;
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

    public Map<String, Integer> getQuestDone() {
        return questDone;
    }
}
