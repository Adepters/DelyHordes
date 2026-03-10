package com.sweityuo.delyHordes.loot;


import com.sweityuo.delyHordes.Main;
import com.sweityuo.delyHordes.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class LootEditorMenu {
    private final Main plugin;
    private final NamespacedKey itemIdKey;

    public LootEditorMenu(Main plugin) {
        this.plugin = plugin;
        this.itemIdKey = new NamespacedKey(plugin, "honey_id");
    }


    public void openMainMenu(Player player) {
        int menuSize = 27;
        String menuTitle = ColorUtil.colorize("#323232Настройки:");
        Inventory inventory = Bukkit.createInventory(new MenuHolder("ITEM_MAIN"), menuSize, menuTitle);
        buildItems(inventory);
        player.openInventory(inventory);
    }

    private void buildItems(Inventory inventory) {


        ItemStack item = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ColorUtil.colorize("#F4C542Настройки"));

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("#F4C542ЛКМ - настроить лут");
        lore.add("#F4C542ПКМ - настроить шанс");

        meta.setLore(ColorUtil.colorizeList(lore));
        meta.getPersistentDataContainer().set(
                itemIdKey,
                PersistentDataType.STRING,
                ""
        );
        item.setItemMeta(meta);

        inventory.setItem(13, item);

    }

    public void OpenLootEditorMenu(Player player) {
        int menuSize = 54;
        String menuTitle = ColorUtil.colorize("#323232Лут:");
        Inventory inventory = Bukkit.createInventory(new MenuHolder("ITEM_LOOT_EDITOR"), menuSize, menuTitle);
        plugin.getLootController().loadLoot(inventory, false);
        player.openInventory(inventory);
    }
    public void OpenLootChanceMenu(Player player) {
        int menuSize = 54;
        String menuTitle = ColorUtil.colorize("#323232Редактор шансов:");
        Inventory inventory = Bukkit.createInventory(new MenuHolder("ITEM_CHANCE_EDITOR"), menuSize, menuTitle);
        plugin.getLootController().loadLoot(inventory, true);
        player.openInventory(inventory);
    }
}
