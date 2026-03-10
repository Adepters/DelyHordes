package com.sweityuo.delyHordes;

import com.sweityuo.delyHordes.listeners.CustomMenuListener;
import com.sweityuo.delyHordes.listeners.MobDeathListener;
import com.sweityuo.delyHordes.loot.LootContoller;
import com.sweityuo.delyHordes.loot.LootEditorMenu;
import com.sweityuo.delyHordes.mobs.MobManager;
import com.sweityuo.delyHordes.placeholders.MyExpansion;
import com.sweityuo.delyHordes.utils.BossBarUtil;
import com.sweityuo.delyHordes.utils.TopDamageUtil;
import com.sweityuo.delyHordes.utils.WavesUtil;
import com.sweityuo.delyHordes.yml.CustomYml;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance;

    private CustomYml lootFile;
    private MobManager mobManager;
    private MessageManager messageManager;
    private BossBarUtil bossBarUtil;
    private WavesUtil waves;
    private TopDamageUtil topDamageUtil;
    private LootContoller lootController;
    private LootEditorMenu lootEditorMenu;



    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new MyExpansion(this).register();
        }
        instance = this;
        lootFile = new CustomYml(this, "loot.yml");

        topDamageUtil = new TopDamageUtil(this);
        mobManager = new MobManager(getConfig().getConfigurationSection("mobs"));
        lootEditorMenu = new LootEditorMenu(this);
        lootController = new LootContoller();
        waves = new WavesUtil(this, mobManager);
        lootController = new LootContoller();
        bossBarUtil = new BossBarUtil(waves, this);
        messageManager = new MessageManager(getConfig());
        getServer().getPluginManager().registerEvents(
                new MobDeathListener(waves, this),
                this
        );
        getServer().getPluginManager().registerEvents(
                new CustomMenuListener(this),
                this
        );
        getLogger().info("DelyHordes включён!");
    }
    @Override
    public void onDisable() {
        getLogger().info("DelyHordes выключён!");
        bossBarUtil.shutdown();


    }
    public static Main getInstance() {
        return instance;
    }

    public TopDamageUtil getTopDamageUtil() {
        return topDamageUtil;
    }
    public MessageManager getMessageManager() {
        return messageManager;
    }
    public LootEditorMenu getLootEditorMenu() {
        return lootEditorMenu;
    }
    public LootContoller getLootController() {
        return lootController;
    }
    public CustomYml getLootFile() {
        return lootFile;
    }

    public WavesUtil getWaves() {
        return waves;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("delyHordes")) {
            if (!sender.hasPermission("delyHordes.admin")) {
                messageManager.sendNoPermission(sender);
                return true;
            }
            if (args.length == 0) {
                messageManager.sendUse(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("start")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    messageManager.sendOnStarted(player);
                }
                waves.start();

                return true;
            }
            if (args[0].equalsIgnoreCase("stop")) {
                messageManager.sendOnStoped(sender);
                waves.stop();

                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                waves.stop();
                reloadConfig();
                topDamageUtil.reload();
                bossBarUtil.shutdown();
                mobManager = new MobManager(getConfig().getConfigurationSection("mobs"));
                lootController = new LootContoller();
                waves.reload();
                bossBarUtil = new BossBarUtil(waves, this);
                messageManager = new MessageManager(getConfig());
                lootFile.reload();
                messageManager.sendOnReload(sender);
                return true;
            }


            if(args[0].equalsIgnoreCase("menu")) {
                if (!(sender instanceof Player player)) {
                    messageManager.sendNoPermission(sender);
                    return true;
                }

                lootEditorMenu.openMainMenu(player);
                return true;
            }

            messageManager.sendUse(sender);
            return true;
        }


        return false;
    }

}
