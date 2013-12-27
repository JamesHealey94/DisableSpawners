package com.gmail.jameshealey1994.disablespawners;

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
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    /**
     * Cancels creature spawn event if creature was spawned via a spawner.
     *
     * @param event     event being handled
     */
    @EventHandler (priority = EventPriority.HIGH)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == SpawnReason.SPAWNER) {
            event.setCancelled(true);
        }
    }
}