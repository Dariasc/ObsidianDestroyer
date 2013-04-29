package com.pandemoneus.obsidianDestroyer;

import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The ObsidianDestroyer plugin.
 * 
 * Allows certain explosions to destroy Obsidian.
 * 
 * @author drtshock
 * 
 */
public final class ObsidianDestroyer extends JavaPlugin {

    /**
     * Plugin related stuff
     */
    private final ODCommands cmdExecutor = new ODCommands(this);
    private ODConfig config = new ODConfig(this);
    private final ODEntityListener entityListener = new ODEntityListener(this);
    private final ODJoinListener joinListener = new ODJoinListener(this);
    public static Logger log;

    public static boolean update = false;
    public static String name = "";

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisable() {
        config.saveDurabilityToFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEnable() {

        getCommand("obsidiandestroyer").setExecutor(cmdExecutor);
        getCommand("od").setExecutor(cmdExecutor);

        config.loadConfig();
        entityListener.setObsidianDurability(config.loadDurabilityFromFile());

        startMetrics();

        getServer().getPluginManager().registerEvents(entityListener, this);
        getServer().getPluginManager().registerEvents(joinListener, this);

        if(config.getCheckUpdate()) {
            Updater updater = new Updater(this, "obsidiandestroyer", this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
            update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE; 
            name = updater.getLatestVersionString();
        }
    }

    public void startMetrics() { 	
        PluginDescriptionFile pdfFile = this.getDescription();
        try {	
            Metrics metrics = new Metrics(this);

            metrics.addCustomData(new Metrics.Plotter("Obsidian Durability") {

                @Override
                public int getValue() {
                    return config.getoDurability();
                }
            });

            metrics.start();
        } catch (IOException e) {
            ObsidianDestroyer.log.warning("[" + pdfFile.getName() + "] Failed to submit the stats :-("); // Failed to submit the stats :-(
        }
    }

    /**
     * Returns the config of this plugin.
     * 
     * @return the config of this plugin
     */
    public ODConfig getODConfig() {
        return config;
    }

    /**
     * Returns the entity listener of this plugin.
     * 
     * @return the entity listener of this plugin
     */
    public ODEntityListener getListener() {
        return entityListener;
    }

}