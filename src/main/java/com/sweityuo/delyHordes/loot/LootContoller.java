package com.sweityuo.delyHordes.loot;

import com.sweityuo.delyHordes.Main;
import com.sweityuo.delyHordes.utils.ColorUtil;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class LootContoller {

    private final NamespacedKey chanceKey = new NamespacedKey(Main.getInstance(), "loot_chance");



    public void saveLoot(Inventory inventory) {

        Main plugin = Main.getInstance();
        YamlConfiguration loot = plugin.getLootFile().getConfig();

        List<Map<String, Object>> items = new ArrayList<>();

        for (int i = 0; i < inventory.getSize(); i++) {

            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().isAir()) continue;

            ItemMeta meta = item.getItemMeta();

            if (meta != null) {

                List<String> lore = meta.getLore();

                if (lore != null) {

                    lore = new ArrayList<>(lore);

                    lore.removeIf(line ->
                            line.contains("Текущий шанс") ||
                                    line.contains("ЛКМ") ||
                                    line.contains("ПКМ")
                    );

                    meta.setLore(lore);
                    item.setItemMeta(meta);
                }
            }

            String encoded = encodeItem(item);
            if (encoded == null) continue;

            double chance = 100;

            if (meta != null && meta.getPersistentDataContainer()
                    .has(chanceKey, PersistentDataType.DOUBLE)) {

                chance = meta.getPersistentDataContainer()
                        .get(chanceKey, PersistentDataType.DOUBLE);
            }

            Map<String, Object> itemData = new HashMap<>();
            itemData.put("item", encoded);
            itemData.put("chance", chance);

            items.add(itemData);
        }

        loot.set("loot", items);
        plugin.getLootFile().save();
    }



    public void loadLoot(Inventory inventory, boolean isChangeEditor) {

        Main plugin = Main.getInstance();
        YamlConfiguration loot = plugin.getLootFile().getConfig();

        inventory.clear();

        if (!loot.contains("loot")) return;

        List<Map<?, ?>> items = loot.getMapList("loot");

        int slot = 0;

        for (Map<?, ?> map : items) {

            if (slot >= inventory.getSize()) break;

            String encoded = (String) map.get("item");
            if (encoded == null) continue;

            ItemStack item = decodeItem(encoded);
            if (item == null) continue;

            double chance = 100;

            if (map.containsKey("chance")) {
                chance = ((Number) map.get("chance")).doubleValue();
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            meta.getPersistentDataContainer()
                    .set(chanceKey, PersistentDataType.DOUBLE, chance);

            if (isChangeEditor) {
                applyChanceLore(meta, chance);
            }

            item.setItemMeta(meta);

            inventory.setItem(slot, item);
            slot++;
        }
    }



    public void changeChanceInRealTime(Inventory inventory, int slot, int delta) {

        ItemStack item = inventory.getItem(slot);
        if (item == null || item.getType().isAir()) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        double chance = 100;

        if (meta.getPersistentDataContainer().has(chanceKey, PersistentDataType.DOUBLE)) {
            chance = meta.getPersistentDataContainer()
                    .get(chanceKey, PersistentDataType.DOUBLE);
        }

        chance += delta;

        if (chance < 0) chance = 0;
        if (chance > 100) chance = 100;

        meta.getPersistentDataContainer()
                .set(chanceKey, PersistentDataType.DOUBLE, chance);

        applyChanceLore(meta, chance);

        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }





    Map<?, ?> rollByChance(List<Map<?, ?>> items) {

        double totalChance = 0;

        for (Map<?, ?> map : items) {
            totalChance += ((Number) map.get("chance")).doubleValue();
        }

        double random = Math.random() * totalChance;
        double current = 0;

        for (Map<?, ?> map : items) {

            current += ((Number) map.get("chance")).doubleValue();

            if (random <= current) {
                return map;
            }
        }

        return null;
    }



    public String encodeItem(ItemStack item) {

        try {

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BukkitObjectOutputStream data = new BukkitObjectOutputStream(output);

            data.writeObject(item);
            data.close();

            return Base64.getEncoder().encodeToString(output.toByteArray());

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }



    public ItemStack decodeItem(String base64) {

        try {

            byte[] bytes = Base64.getDecoder().decode(base64);

            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            BukkitObjectInputStream data = new BukkitObjectInputStream(input);

            ItemStack item = (ItemStack) data.readObject();
            data.close();

            return item;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }



    private void applyChanceLore(ItemMeta meta, double chance) {

        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            lore = new ArrayList<>(lore);
        }

        lore.removeIf(line ->
                line.contains("Текущий шанс") ||
                        line.contains("ЛКМ") ||
                        line.contains("ПКМ")
        );

        lore.add("#F4C542Текущий шанс: #9FE2FF" + chance);
        lore.add("#9FE2FFЛКМ - #F4C542увеличить на 1");
        lore.add("#9FE2FFЛКМ + SHIFT - #F4C542увеличить на 10");
        lore.add("#9FE2FFПКМ - #F4C542уменьшить на 1");
        lore.add("#9FE2FFПКМ + SHIFT - #F4C542уменьшить на 10");

        meta.setLore(ColorUtil.colorizeList(lore));
    }

    public void spawnItem(Location location) {

        Main plugin = Main.getInstance();
        YamlConfiguration loot = plugin.getLootFile().getConfig();

        if (!loot.contains("loot")) {
            return;
        }

        List<Map<?, ?>> items = loot.getMapList("loot");

        if (items.isEmpty()) return;

        Map<?, ?> selected = rollByChance(items);

        if (selected == null) return;

        String encoded = (String) selected.get("item");
        if (encoded == null) return;

        ItemStack item = decodeItem(encoded);
        if (item == null) return;

        location.getWorld().dropItemNaturally(location, item);
    }
}