package com.sweityuo.delyHordes;

import com.sweityuo.delyHordes.utils.ColorUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageManager {

    private final String onReload;
    private final List<String> onStarted;
    private final List<String> use;

    private final String noPermission;
    private final String lootAdded;
    private final String onStoped;

    private final String lootRemoved;
    private final String lootNoItemInHand;

    public MessageManager(ConfigurationSection root) {
        ConfigurationSection messages = root.getConfigurationSection("messages");

        if (messages == null) {
            this.onReload = ColorUtil.colorize("#8B6B1FПлагин перезагружен");
            this.onStarted = List.of(ColorUtil.colorize("#C89B2BОрды начали наступай скорее на арену!"));
            this.use = List.of(
                    "§eИспользование:",
                    "§f/delyHordes start",
                    "§f/delyHordes stop",
                    "§f/delyHordes menu",
                    "§f/delyHordes reload"
            );

            this.noPermission = "§cНет прав.";
            this.lootAdded = "§aПредмет добавлен в лут. Шанс: %chance%";
            this.lootRemoved = "§aПредмет удалён из лута.";
            this.onStoped = "§aВолна остановилась";
            this.lootNoItemInHand = "§cВ руке нет предмета!";
            return;
        }

        this.onReload = ColorUtil.colorize(messages.getString("onReload", "#8B6B1FПлагин перезагружен"));
        this.onStarted = ColorUtil.colorizeList(messages.getStringList("onStarted"));
        this.use = ColorUtil.colorizeList(messages.getStringList("use"));
        this.onStoped = ColorUtil.colorize(messages.getString("stoped"));
        this.noPermission = ColorUtil.colorize(messages.getString("noPermission", "§cНет прав."));
        this.lootAdded = ColorUtil.colorize(messages.getString("lootAdded", "§aПредмет добавлен в лут. Шанс: %chance%"));
        this.lootRemoved = ColorUtil.colorize(messages.getString("lootRemoved", "§aПредмет удалён из лута."));
        this.lootNoItemInHand = ColorUtil.colorize(messages.getString("lootNoItemInHand", "§cВ руке нет предмета!"));
    }

    private void sendList(CommandSender sender, List<String> lines) {
        for (String msg : lines) {
            sender.sendMessage(applyPlaceholders(sender, msg));
        }
    }

    private String applyPlaceholders(CommandSender sender, String msg) {
        if (sender instanceof Player player) {
            return PlaceholderAPI.setPlaceholders(player, msg);
        }
        return msg;
    }

    public void sendOnReload(CommandSender sender) {
        sender.sendMessage(applyPlaceholders(sender, onReload));
    }

    public void sendOnStarted(CommandSender sender) {
        sendList(sender, onStarted);
    }

    public void sendUse(CommandSender sender) {
        sendList(sender, use);
    }

    public void sendNoPermission(CommandSender sender) {
        sender.sendMessage(applyPlaceholders(sender, noPermission));
    }

    public void sendLootAdded(CommandSender sender, float chance) {
        String msg = lootAdded.replace("%chance%", String.valueOf(chance));
        sender.sendMessage(applyPlaceholders(sender, msg));
    }

    public void sendLootRemoved(CommandSender sender) {
        sender.sendMessage(applyPlaceholders(sender, lootRemoved));
    }

    public void sendLootNoItemInHand(CommandSender sender) {
        sender.sendMessage(applyPlaceholders(sender, lootNoItemInHand));
    }
    public void sendOnStoped(CommandSender sender) {
        sender.sendMessage(applyPlaceholders(sender, onStoped));
    }
}