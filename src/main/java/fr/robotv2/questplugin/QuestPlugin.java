package fr.robotv2.questplugin;

import fr.robotv2.questplugin.addons.Addon;
import fr.robotv2.questplugin.addons.AddonManager;
import fr.robotv2.questplugin.command.QuestPluginMainCommand;
import fr.robotv2.questplugin.conditions.ConditionManager;
import fr.robotv2.questplugin.group.QuestGroup;
import fr.robotv2.questplugin.group.QuestGroupManager;
import fr.robotv2.questplugin.listeners.QuestCosmeticListener;
import fr.robotv2.questplugin.listeners.StatisticListeners;
import fr.robotv2.questplugin.quest.Quest;
import fr.robotv2.questplugin.quest.QuestManager;
import fr.robotv2.questplugin.quest.type.QuestTypes;
import fr.robotv2.questplugin.storage.DatabaseManager;
import fr.robotv2.questplugin.storage.model.ActiveQuest;
import fr.robotv2.questplugin.storage.model.QuestPlayer;
import fr.robotv2.questplugin.util.Futures;
import fr.robotv2.questplugin.util.GroupUtil;
import fr.robotv2.questplugin.util.McVersion;
import fr.robotv2.questplugin.util.color.ColorProvider;
import fr.robotv2.questplugin.util.color.LegacyColorProvider;
import fr.robotv2.questplugin.util.color.ModernColorProvider;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.LibraryManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class QuestPlugin extends JavaPlugin {

    private QuestManager questManager;
    private QuestGroupManager questGroupManager;
    private DatabaseManager databaseManager;
    private BukkitCommandHandler commandHandler;

    private QuestPluginConfiguration questConfiguration;
    private QuestResetHandler resetHandler;
    private ConditionManager conditionManager;
    private AddonManager addonManager;

    private ColorProvider colorProvider;
    private LibraryManager libraryManager;

    public static QuestPlugin instance() {
        return JavaPlugin.getPlugin(QuestPlugin.class);
    }

    public static Logger logger() {
        return instance().getLogger();
    }

    public static void debug(String message) {
        instance().dbg(message);
    }

    public static LibraryManager libraryManager() {
        return instance().libraryManager;
    }

    @Override
    public void onLoad() {
        this.addonManager = new AddonManager(this, getRelativeFile("addons"));
        this.addonManager.load();
        this.addonManager.getAddons().forEach(Addon::onLoad);

        this.conditionManager = new ConditionManager(this);
        this.conditionManager.registerDefaultConditions();

        this.libraryManager = new BukkitLibraryManager(this, "data" + File.separator + "libraries");
        this.libraryManager.addMavenCentral();
        this.libraryManager.addJitPack();
    }

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.conditionManager.closeRegistration();
        this.addonManager.getAddons().forEach(Addon::onPreEnable);

        saveDefaultConfig();
        this.questConfiguration = new QuestPluginConfiguration();
        getQuestConfiguration().loadConfiguration(getConfig());

        this.questGroupManager = new QuestGroupManager(getRelativeFile("groups"));
        this.questManager = new QuestManager(this, getRelativeFile("quests"));
        this.conditionManager = new ConditionManager(this);

        registerDatabaseManager();

        getQuestGroupManager().loadGroups();
        getQuestManager().loadQuests();
        getDatabaseManager().init();

        this.resetHandler = new QuestResetHandler(this);
        this.colorProvider = McVersion.current().isAtLeast(1, 17) ? new ModernColorProvider() : new LegacyColorProvider();

        registerListeners();
        registerCommands();

        GroupUtil.initialize(this);

        this.addonManager.getAddons().forEach(Addon::onPostEnable);
    }

    @Override
    public void onDisable() {

        // disable addons
        getAddonManager().getAddons().forEach(Addon::onDisable);

        // save all players
        List<CompletableFuture<Void>> futures = Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> getDatabaseManager().savePlayer(player, true))
                .collect(Collectors.toList());

        // Wait for them to complete before closing the pool
        Futures.ofAll(futures).join();

        // Close the database
        if(getDatabaseManager() != null) {
            getDatabaseManager().close();
        }
    }

    public void onReload() {
        reloadConfig();
        getQuestConfiguration().loadConfiguration(getConfig());

        getQuestGroupManager().getGroups().forEach(QuestGroup::stopCronJob);
        getQuestGroupManager().loadGroups();
        getQuestManager().loadQuests();

        getAddonManager().getAddons().forEach(Addon::onReload);
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

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public QuestPluginConfiguration getQuestConfiguration() {
        return questConfiguration;
    }

    public QuestResetHandler getResetHandler() {
        return resetHandler;
    }

    public AddonManager getAddonManager() {
        return addonManager;
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

    public BukkitCommandHandler getCommandHandler() {
        return commandHandler;
    }

    private void registerDatabaseManager() {
        final String literal = getConfig().getString("database.type", "JSON");
        final Supplier<DatabaseManager> supplier = DatabaseManager.SUPPLIERS.get(literal.toUpperCase());
        if(supplier == null) {
            throw new IllegalArgumentException("Invalid database type: " + literal);
        }

        this.databaseManager = supplier.get();
    }

    private void registerListeners() {
        final PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new QuestCosmeticListener(this), this);
        pm.registerEvents(new StatisticListeners(), this);

        QuestTypes.getLoadedTypes().forEach((type) -> {
            try {
                type.registerListener();
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException exception) {
                getLogger().log(Level.SEVERE, "Failed to register listener for " + type.getLiteral(), exception);
            }
        });
    }

    private void registerCommands() {
        this.commandHandler = BukkitCommandHandler.create(this);

        commandHandler.registerValueResolver(QuestGroup.class, (context) -> getQuestGroupManager().getGroup(context.pop()));
        commandHandler.getAutoCompleter().registerSuggestion("groups", SuggestionProvider.map(getQuestGroupManager()::getGroups, QuestGroup::getGroupId));
        commandHandler.getAutoCompleter().registerParameterSuggestions(QuestGroup.class, "groups");

        commandHandler.registerValueResolver(Quest.class, (context) -> {
            final QuestGroup group = context.getResolvedArgument(QuestGroup.class);
            return getQuestManager().fromId(context.pop(), group.getGroupId());
        });

        commandHandler.getAutoCompleter().registerSuggestion("quests", (args, sender, command) -> {
            final String filter = args.size() > 1 ? args.get(args.size() - 2) : "";
            return getQuestManager().getQuests().stream()
                    .filter((quest) -> quest.getQuestGroup().getGroupId().equalsIgnoreCase(filter))
                    .map(Quest::getQuestId)
                    .collect(Collectors.toSet());
        });

        commandHandler.getAutoCompleter().registerSuggestion("target_quests", (args, sender, command) -> {
            final String filter = args.size() > 1 ? args.get(args.size() - 2) : "";
            Player player = Bukkit.getPlayer(filter);
            if(player == null) player = sender.as(BukkitCommandActor.class).requirePlayer();
            final QuestPlayer questPlayer = getDatabaseManager().getCachedQuestPlayer(player);
            if(questPlayer == null) return Collections.emptySet();
            return questPlayer.getActiveQuests().stream()
                    .map(ActiveQuest::getQuestId)
                    .collect(Collectors.toSet());
        });

        commandHandler.getAutoCompleter().registerParameterSuggestions(Quest.class, "quests");

        commandHandler.register(new QuestPluginMainCommand(this));
    }
}
