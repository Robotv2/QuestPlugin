package fr.robotv2.questplugin;

import fr.robotv2.questplugin.api.database.IDatabaseManager;
import fr.robotv2.questplugin.command.QuestPluginMainCommand;
import fr.robotv2.questplugin.conditions.ConditionManager;
import fr.robotv2.questplugin.database.InternalDatabaseManager;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.group.QuestGroupManager;
import fr.robotv2.questplugin.listeners.QuestIncrementListener;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.QuestManager;
import fr.robotv2.questplugin.quest.context.block.BlockBreakListener;
import fr.robotv2.questplugin.quest.context.block.BlockPlaceListener;
import fr.robotv2.questplugin.util.GroupUtil;
import fr.robotv2.questplugin.util.McVersion;
import fr.robotv2.questplugin.util.color.ColorProvider;
import fr.robotv2.questplugin.util.color.LegacyColorProvider;
import fr.robotv2.questplugin.util.color.ModernColorProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class QuestPlugin extends JavaPlugin {

    private QuestManager questManager;
    private QuestGroupManager questGroupManager;
    private IDatabaseManager databaseManager;

    private QuestPluginConfiguration questConfiguration;
    private QuestResetHandler resetHandler;
    private ConditionManager conditionManager;

    private ColorProvider colorProvider;

    public static QuestPlugin instance() {
        return JavaPlugin.getPlugin(QuestPlugin.class);
    }

    public static Logger logger() {
        return instance().getLogger();
    }

    public static void debug(String message) {
        instance().dbg(message);
    }

    @Override
    public void onLoad() {
        this.conditionManager = new ConditionManager(this);
        this.conditionManager.registerDefaultConditions();
    }

    @Override
    public void onEnable() {

        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.conditionManager.closeRegistration();

        saveDefaultConfig();
        this.questConfiguration = new QuestPluginConfiguration();

        this.questGroupManager = new QuestGroupManager(getRelativeFile("groups"));
        this.questManager = new QuestManager(this, getRelativeFile("quests"));
        this.conditionManager = new ConditionManager(this);

        if(this.databaseManager != null) {
            databaseManager = new InternalDatabaseManager(this);
        }

        getQuestConfiguration().loadConfiguration(getConfig());

        getQuestGroupManager().loadGroups();
        getQuestManager().loadQuests();
        getDatabaseManager().init();

        this.resetHandler = new QuestResetHandler(this);
        this.colorProvider = McVersion.current().isAtLeast(1, 17) ? new ModernColorProvider() : new LegacyColorProvider();

        registerListeners();
        registerCommands();

        GroupUtil.initialize(this);
    }

    @Override
    public void onDisable() {
        getDatabaseManager().close();
    }

    public void onReload() {

        reloadConfig();
        getQuestConfiguration().loadConfiguration(getConfig());

        getQuestGroupManager().getGroups().forEach(QuestGroup::stopCronJob);
        getQuestGroupManager().loadGroups();
        getQuestManager().loadQuests();
    }

    public void dbg(String message) {
        if(getQuestConfiguration().isDebug()) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public QuestGroupManager getQuestGroupManager() {
        return questGroupManager;
    }

    public IDatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public void setDatabaseManager(IDatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public QuestPluginConfiguration getQuestConfiguration() {
        return questConfiguration;
    }

    public QuestResetHandler getResetHandler() {
        return resetHandler;
    }

    public File getRelativeFile(String path) {
        return new File(getDataFolder(), path);
    }

    public File getQuestDataFolder() {
        return getRelativeFile("data");
    }

    public ColorProvider getColorProvider() {
        return colorProvider;
    }

    public ConditionManager getConditionManager() {
        return conditionManager;
    }

    private void registerListeners() {
        final PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new QuestIncrementListener(this), this);

        pm.registerEvents(new BlockBreakListener(this), this);
        pm.registerEvents(new BlockPlaceListener(this), this);
    }

    private void registerCommands() {
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);

        handler.registerValueResolver(QuestGroup.class, (context) -> getQuestGroupManager().getGroup(context.pop()));
        handler.getAutoCompleter().registerSuggestion("groups", SuggestionProvider.map(getQuestGroupManager()::getGroups, QuestGroup::getGroupId));
        handler.getAutoCompleter().registerParameterSuggestions(QuestGroup.class, "groups");

        handler.registerValueResolver(Quest.class, (context) -> {
            final QuestGroup group = context.getResolvedArgument(QuestGroup.class);
            return getQuestManager().fromId(context.pop(), group.getGroupId());
        });
        handler.getAutoCompleter().registerSuggestion("quests", (args, sender, command) -> {
            final String filter = args.size() > 1 ? args.get(args.size() - 2) : "";
            return getQuestManager().getQuests().stream()
                    .filter((quest) -> quest.getQuestGroup().getGroupId().equalsIgnoreCase(filter))
                    .map(Quest::getQuestId)
                    .collect(Collectors.toSet());
        });
        handler.getAutoCompleter().registerParameterSuggestions(Quest.class, "quests");

        handler.register(new QuestPluginMainCommand(this));
    }
}
