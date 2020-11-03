package com.flora.hardcore.gui;

import com.flora.api.BloomAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import java.time.LocalDate;

public class InventoryPlayerEdit
{
    OfflinePlayer player;

    String name;
    String uuid;
    String last_date;
    String register_date;
    boolean penalty;
    int role;
    int penalty_diamond;
    int penalty_coupon;
    int death_count;

    public InventoryPlayerEdit(OfflinePlayer player)
    {
        this.player = player;
        String uuid = player.getUniqueId().toString();

        if (player.isOnline()) {
            this.name = BloomAPI.getHashMapName(uuid);
            this.uuid = BloomAPI.getHashMapUUID(uuid);
            this.last_date = LocalDate.now().toString();
            this.register_date = "(콘피그 데이터에서 확인가능)";
            this.penalty = BloomAPI.getHashMapPenalty(uuid);
            this.role = BloomAPI.getHashMapRole(uuid);
            this.penalty_diamond = BloomAPI.getHashMapPenaltyDiamond(uuid);
            this.penalty_coupon = BloomAPI.getHashMapPenaltyCoupon(uuid);
            this.death_count = BloomAPI.getHashMapDeathCount(uuid);
        } else {
            FileConfiguration config = BloomAPI.getDataConfig(player.getUniqueId().toString());

            this.name = config.getString("name");
            this.uuid = config.getString("uuid");
            this.last_date = config.getString("last_date");
            this.register_date = config.getString("register_date");
            this.penalty = config.getBoolean("penalty");
            this.role = config.getInt("role");
            this.penalty_diamond = config.getInt("penalty_diamond");
            this.penalty_coupon = config.getInt("penalty_coupon");
            this.death_count = config.getInt("death_count");
        }
    }

    public Inventory inv()
    {
        Inventory inv = Bukkit.createInventory(null, 36, "§8ㆍ " + name + " 의 정보");

        inv.setItem(4, InventoryIcon.iconHead(player, name, uuid, penalty, role, penalty_diamond, penalty_coupon, death_count, last_date, register_date));

        inv.setItem(20, InventoryIcon.iconPenalty(penalty));
        inv.setItem(24, InventoryIcon.iconPenaltyCoupon(penalty_coupon));
        inv.setItem(26, InventoryIcon.iconDeathCount(death_count));

        if (penalty)
            inv.setItem(22, InventoryIcon.iconDiamond(penalty_diamond));
        else
            inv.setItem(18, InventoryIcon.iconRole(role));

        return inv;
    }
}
