package com.gmail.jameshealey1994.disablespawners;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bukkit Plugin to disable creatures from spawning via Monster Spawners.
 * Further description at http://dev.bukkit.org/bukkit-plugins/disablespawners/
 *
 * @author JamesHealey94 <jameshealey1994.gmail.com>
 */
public class DisableSpawners extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {

        // Save default config if one does not exist
        saveDefaultConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Cancels creature spawn event if creature was spawned via a spawner.
     *
     * @param event     event being handled
     */
    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        final String worldname = event.getLocation().getWorld().getName();
        final String configString = "Disable Spawners On";
        final List<String> activeWorlds = this.getConfig().getStringList(configString);
        final boolean isPluginActiveOnWorld = activeWorlds.contains(worldname);

        if (isPluginActiveOnWorld) {
            if (event.getSpawnReason() == SpawnReason.SPAWNER) {
                event.setCancelled(true);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("disablespawners")) {
            if (sender.hasPermission("disablespawners.reload")) {
                reloadConfig();
                sender.sendMessage("Config Reloaded");
                return true;
            }
        }
        return false;
    }
}