package com.sweityuo.delyHordes.listeners;


import com.sweityuo.delyHordes.Main;
import com.sweityuo.delyHordes.loot.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class CustomMenuListener implements Listener {

    private final Main plugin;
    public CustomMenuListener(Main plugin)
    {
        this.plugin = plugin;
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) return;

        switch (holder.getMenuId()) {

            case "ITEM_MAIN" -> handleMainClick(event, player);
            case "ITEM_CHANCE_EDITOR" -> handleChanceEditorClick(event);
        }
    }


    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player player)) return;

        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) {
            return;
        }

        if (holder.getMenuId().equals("ITEM_LOOT_EDITOR") || holder.getMenuId().equals("ITEM_CHANCE_EDITOR")) {
            plugin.getLootController().saveLoot(event.getInventory());
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getLootEditorMenu().openMainMenu(player);
            });
        }
    }




    private void handleChanceEditorClick (InventoryClickEvent event){
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        if (event.getClick() == ClickType.LEFT) {
            plugin.getLootController().changeChanceInRealTime(
                    event.getInventory(),
                    event.getSlot(),
                    1
            );
        }

        if (event.getClick() == ClickType.SHIFT_LEFT) {
            plugin.getLootController().changeChanceInRealTime(
                    event.getInventory(),
                    event.getSlot(),
                    10
            );
        }

        if (event.getClick() == ClickType.RIGHT) {
            plugin.getLootController().changeChanceInRealTime(
                    event.getInventory(),
                    event.getSlot(),
                    -1
            );
        }

        if (event.getClick() == ClickType.SHIFT_RIGHT) {
            plugin.getLootController().changeChanceInRealTime(
                    event.getInventory(),
                    event.getSlot(),
                    -10
            );
        }
    }

    private void handleMainClick(InventoryClickEvent event, Player player) {

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        NamespacedKey itemIdKey = new NamespacedKey(plugin, "honey_id");

        if (!meta.getPersistentDataContainer().has(itemIdKey, PersistentDataType.STRING)) return;


        if (event.getClick() == ClickType.LEFT) {
            plugin.getLootEditorMenu().OpenLootEditorMenu(player);
        }

        if (event.getClick() == ClickType.RIGHT) {
            plugin.getLootEditorMenu().OpenLootChanceMenu(player);
        }
    }


}
