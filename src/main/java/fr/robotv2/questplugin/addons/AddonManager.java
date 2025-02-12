package fr.robotv2.questplugin.addons;

import fr.robotv2.questplugin.QuestPlugin;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class AddonManager {

    private final QuestPlugin plugin;
    private final File addonFolder;
    private final Set<Addon> addons;

    public AddonManager(QuestPlugin plugin, File addonFolder) {
        this.plugin = plugin;
        this.addonFolder = addonFolder;
        this.addons = new HashSet<>();
    }

    public File getAddonFolder() {
        return addonFolder;
    }

    public Set<Addon> getAddons() {
        return addons;
    }

    public void load() {
        if(!addonFolder.exists()) {
            return;
        }

        QuestPlugin.logger().info("Loading addons from: " + addonFolder.getAbsolutePath());

        final File[] files = addonFolder.listFiles();
        if(files == null) {
            QuestPlugin.logger().warning("Failed to list files in addon folder.");
            return;
        }

        for(File file : files) {

            try {
                final ScanResult result = getClassGraph(file).scan();

                // find addon class
                final List<Class<Addon>> addonClasses = result.getSubclasses(Addon.class.getName()).loadClasses(Addon.class);
                if(addonClasses.isEmpty()) {
                    QuestPlugin.logger().warning("Failed to find addon class in file: " + file.getName());
                    continue;
                }

                if(addonClasses.size() > 1) {
                    QuestPlugin.logger().warning("Found multiple addon classes in file: " + file.getName());
                    continue;
                }

                final Class<? extends Addon> addonClass = addonClasses.get(0);
                final Addon addon = addonClass.newInstance();
                addons.add(addon);
                QuestPlugin.logger().info("Loaded " + addon.getName() + " (" + addon.getVersion() + ") has been loaded successfully.");

                // get and load all non-class resources
                // final ResourceList resources = result.getAllResources().nonClassFilesOnly();

                result.close();

            } catch (IOException | InstantiationException | IllegalAccessException exception) {
                QuestPlugin.logger().log(Level.SEVERE, "Failed to load addon from file: " + file.getName(), exception);
            }
        }
    }

    private ClassGraph getClassGraph(File file) throws IOException {
        return new ClassGraph()
                .overrideClassLoaders(new URLClassLoader(new URL[]{file.toURI().toURL()}, plugin.getClass().getClassLoader()))
                .acceptPackages("fr.robotv2")
                .enableAllInfo()
                ;
    }
}
