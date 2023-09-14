package com.wsx.timefreezelite;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class TimeFreezeLite extends JavaPlugin implements Listener {

    public static boolean isTimeFrozen = false;
    public static boolean isEnable = true;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info(ChatColor.GREEN + "timefreeze loading");
        saveDefaultConfig();
        isEnable = getConfig().getBoolean("isEnable");
        getServer().getPluginManager().registerEvents(this, this);
        stopTime();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.getLogger().info(ChatColor.GREEN + "timefreeze unload");
        restartTime();
    }

    private void stopTime() {
        if (!isTimeFrozen) {
            isTimeFrozen = true;
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            }
            this.getLogger().info(ChatColor.AQUA + "time stop!");
        }
    }

    private void restartTime() {
        if (isTimeFrozen) {
            isTimeFrozen = false;
            for (World world : Bukkit.getWorlds()) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            }
            this.getLogger().info(ChatColor.AQUA + "time start!");
        }

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!isEnable) return;
        restartTime();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!isEnable) return;
        if (Bukkit.getOnlinePlayers().size() == 1) {
            stopTime();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("timefreeze")) {
            if (args.length == 0) {
                sender.sendMessage("timefreeze enable:" + isEnable);
                sender.sendMessage(ChatColor.GREEN + "/timefreeze help" + ChatColor.WHITE + " -know all the commands of this plugin");
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("switch")) {
                    if (isEnable) {
                        isEnable = false;
                        restartTime();
                        Bukkit.broadcastMessage(ChatColor.RED + "timefreeze plugin disable!");
                    } else {
                        isEnable = true;
                        if (Bukkit.getOnlinePlayers().isEmpty()) stopTime();
                        else restartTime();
                        Bukkit.broadcastMessage(ChatColor.GREEN + "timefreeze plugin enable!");
                    }
                } else if (args[0].equalsIgnoreCase("true")) {
                    if (!isEnable) {
                        isEnable = true;
                        if (Bukkit.getOnlinePlayers().isEmpty()) stopTime();
                        else restartTime();
                    }
                    Bukkit.broadcastMessage(ChatColor.GREEN + "timefreeze plugin enable!");
                } else if (args[0].equalsIgnoreCase("false")) {
                    if (isEnable) {
                        isEnable = false;
                        restartTime();
                    }
                    Bukkit.broadcastMessage(ChatColor.RED + "timefreeze plugin disable!");
                } else if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.GREEN + "/timefreeze switch" + ChatColor.WHITE + "-to switch the plugin enable/disable");
                    sender.sendMessage(ChatColor.GREEN + "/timefreeze [true/false]" + ChatColor.WHITE + "-to enable/disable the plugin");
                } else {
                    sender.sendMessage(ChatColor.RED + "wrong args!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "wrong args!");
            }
            this.getLogger().info(ChatColor.GREEN + ",enable:" + isEnable);
            getConfig().set("isEnable", isEnable);
            saveConfig();
            return true;
        }
        Bukkit.broadcastMessage(ChatColor.RED + "wrong command.");
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>(Arrays.asList("help", "true", "false", "switch"));
            result.removeIf(s -> !s.contains(args[0].toLowerCase(Locale.ROOT)));
            return result;
        } else return null;
    }
}
