package fr.robotv2.questplugin.quest;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class QuestManager {

    private static final SplittableRandom QUEST_RANDOM = new SplittableRandom();

    private final QuestPlugin plugin;
    private final File questFolder;
    private final Table<String, String, Quest> quests;

    public QuestManager(QuestPlugin plugin, File questFolder) {
        this.plugin = plugin;
        this.questFolder = questFolder;
        this.quests = HashBasedTable.create();
    }

    public Quest fromId(@NotNull String id, @NotNull String groupId) {
        return quests.get(id, groupId);
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
        return Collections.unmodifiableList(new ArrayList<>(quests.column(group.getGroupId()).values()));
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

    public Set<Quest> getNRandomQuests(QuestGroup group, int n) {

        final List<Quest> quests = this.getQuests(group);
        final int size = quests.size();
        final Set<Quest> randomQuests = new HashSet<>();

        if (quests.isEmpty() || n <= 0) {
            return randomQuests;
        }

        n = Math.min(n, size);

        while (randomQuests.size() < n) {
            Quest randomQuest = getRandomQuest(group);
            if (randomQuest != null) {
                randomQuests.add(randomQuest);
            }
        }

        return randomQuests;
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
            quests.put(quest.getQuestId(), quest.getQuestGroup().getGroupId(), quest);
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
