package fr.robotv2.questplugin.quest;

import fr.robotv2.questplugin.QuestPlugin;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.util.FileUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.stream.Collectors;

public class QuestManager {

    private static final SplittableRandom QUEST_RANDOM = new SplittableRandom();

    private final QuestPlugin plugin;
    private final File questFolder;
    private final Map<String, Quest> quests;

    public QuestManager(QuestPlugin plugin, File questFolder) {
        this.plugin = plugin;
        this.questFolder = questFolder;
        this.quests = new HashMap<>();
    }

    public Quest fromId(@NotNull String id) {
        return quests.get(id);
    }

    public void clearQuests() {
        quests.clear();
    }

    @UnmodifiableView
    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(this.quests.values());
    }

    @UnmodifiableView
    public List<Quest> getQuests(QuestGroup group) {
        return getQuests().stream()
                .filter(quest -> Objects.equals(group, quest.getQuestGroup()))
                .collect(Collectors.toList());
    }

    @Nullable
    public Quest getRandomQuest(QuestGroup group) {

        final List<Quest> quests = this.getQuests(group);
        final int size = quests.size();

        if(quests.isEmpty()) {
            return null; // no quest for this reset id.
        }

        return quests.get(QUEST_RANDOM.nextInt(size));
    }

    @Nullable
    public Quest getRandomQuestFor(QuestPlayer questPlayer, QuestGroup group) {

        if(questPlayer.getActiveQuests(group).size() >= getQuests(group).size()) {
            return null;
        }

        Quest quest = null;

        while (quest == null || questPlayer.hasQuest(quest.getQuestId())) {
            quest = getRandomQuest(group);
            if(quest == null) break; // no quest for this reset id.
        }

        return quest;
    }

    public void loadQuests() {

        clearQuests();

        if(!questFolder.exists()) {
            questFolder.mkdirs();
        }

        FileUtil.iterateFiles(questFolder, (file) -> {
            final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if(configuration.isConfigurationSection("quests")) {
                final ConfigurationSection questSection = Objects.requireNonNull(configuration.getConfigurationSection("quests"));
                questSection.getKeys(false).forEach((key) -> loadQuest(key, questSection.getConfigurationSection(key)));
            } else {
                loadQuest(FileUtil.getFileNameWithoutExtension(file).toLowerCase(), configuration);
            }
        });
    }

    private void loadQuest(String questId, @NotNull ConfigurationSection section) {
        try {
            final Quest quest = new Quest(plugin, questId, section);
            quests.put(quest.getQuestId(), quest);
            this.plugin.getLogger().info(questId + " has been loaded successfully.");
        } catch (Exception exception) {
            plugin.getLogger().warning(" ");
            plugin.getLogger().warning(" WARNING - " + questId);
            plugin.getLogger().warning("An error occurred while loading quest '" + questId + "'");
            plugin.getLogger().warning("Error's message: " + exception.getMessage());
            plugin.getLogger().warning(" ");
            plugin.getLogger().warning("This quest will not be loaded. Please fix it and then reload");
            plugin.getLogger().warning("the plugin.");
            plugin.getLogger().warning(" ");
        }
    }
}
