package com.gmail.jameshealey1994.disablespawners;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
public class DisableSpawners extends JavaPlugin implements CommandExecutor, Listener {

    /**
     * Path to list of worlds plugin is active on.
     */
    public static final String PATH = "Disable Spawners On";

    /**
     * Permission needed to reload the config.
     */
    public static final String PERMISSION_RELOAD = "disablespawners.reload";

    /**
     * Permission needed to add worlds from the config,
     * disabling spawners in them.
     */
    public static final String PERMISSION_ADDWORLD = "disablespawners.addworld";

    /**
     * Permission needed to remove worlds from the config,
     * enabling spawners in them.
     */
    public static final String PERMISSION_REMOVEWORLD = "disablespawners.removeworld";

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
        final List<String> activeWorlds = this.getConfig().getStringList(PATH);
        final boolean isPluginActiveOnWorld =
                activeWorlds.contains(worldname.toLowerCase())
                ||
                activeWorlds.contains(worldname);

        if (isPluginActiveOnWorld) {
            if (event.getSpawnReason() == SpawnReason.SPAWNER) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Executes commands.
     *
     * @param sender            sender of the command
     * @param cmd               command the user sent
     * @param commandLabel      exact command the user sent
     * @param args              arguments given with the command
     * @return                  false if usage message needs to be shown, else
     *                          true
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (cmd.getName().equalsIgnoreCase("disablespawners")) {
            if (args.length > 0) {
                // Get worldname, either specified or current
                final String worldname;
                if (args.length > 1) {
                    worldname = args[1];
                } else {
                    if (sender instanceof Player) {
                        worldname = ((Player) sender).getWorld().getName();
                    } else {
                        sender.sendMessage("Please specify a worldname");
                        return true;
                    }
                }

                // Decide which action to perform
                switch (args[0].toLowerCase()) {
                    case "addworld": {
                        return addWorld(sender, worldname);
                    }
                    case "removeworld": {
                        return removeWorld(sender, worldname);
                    }
                    case "reload": {
                        return reload(sender);
                    }
                    default: {
                        return false;
                    }
                }
            }

            // If no arguments are specified, reload is the default action.
            return reload(sender);
        }
        return false;
    }

    /**
     * If sender has permissions, reload config file, else display permissions
     * message.
     *
     * @param sender    sender of the command
     * @return          if the sender executed the command correctly
     *                  (always true in this case)
     */
    public boolean reload(CommandSender sender) {
        if (sender.hasPermission(PERMISSION_RELOAD)) {
            reloadConfig();
            sender.sendMessage("Config Reloaded");
            return true;
        } else {
            sender.sendMessage("Permission denied - Requires '" + PERMISSION_RELOAD + "'");
            return true;
        }
    }

    /**
     * Adds a worldname to the config, disabling spawners on that world.
     *
     * @param sender        sender of the command
     * @param worldname     worldname to be added to the config
     * @return              if the sender executed the command correctly
     *                      (always true in this case)
     */
    private boolean addWorld(CommandSender sender, String worldname) {
        if (sender.hasPermission(PERMISSION_ADDWORLD)) {
            getConfig().set(PATH, getConfig().getStringList(PATH).add(worldname.toLowerCase()));
            saveConfig();
            sender.sendMessage("Spawners disabled on " + worldname);
            return true;
        } else {
            sender.sendMessage("Permission denied - Requires '" + PERMISSION_ADDWORLD + "'");
            return true;
        }
    }

    /**
     * Removes a worldname to the config, enabling spawners on that world.
     *
     * @param sender        sender of the command
     * @param worldname     worldname to be removed from the config
     * @return              if the sender executed the command correctly
     *                      (always true in this case)
     */
    private boolean removeWorld(CommandSender sender, String worldname) {
        if (sender.hasPermission(PERMISSION_REMOVEWORLD)) {
            getConfig().set(PATH, getConfig().getStringList(PATH).remove(worldname.toLowerCase()));
        saveConfig();
            sender.sendMessage("Spawners enabled on " + worldname);
            return true;
        } else {
            sender.sendMessage("Permission denied - Requires '" + PERMISSION_REMOVEWORLD + "'");
            return true;
        }
    }
}