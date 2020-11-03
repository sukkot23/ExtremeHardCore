package com.flora.hardcore.event;

import com.flora.api.BloomAPI;
import com.flora.hardcore.gui.InventoryPlayerEdit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventInventoryEdit implements Listener
{
    /* Edit Inventory Click Event */
    @EventHandler
    public void onInventoryEditEvent(InventoryClickEvent event)
    {
        if (!(isEditInventory(event))) return;
       event.setCancelled(true);
        if (event.getClickedInventory() == event.getView().getBottomInventory()) { return; }

        ItemStack dataHead = event.getView().getTopInventory().getItem(4);
        assert dataHead != null;
        String uuid = getHeadUUID(dataHead);
        OfflinePlayer player = getOfflinePlayer(uuid);

        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            /* Role Normal - 0 A */
            case GHAST_SPAWN_EGG:
                onChangeRole(event, player,1, 3);
                break;

            /* Role UnderGround - 1 B */
            case SILVERFISH_SPAWN_EGG:
                onChangeRole(event, player,2, 0);
                break;

            /* Role Ground - 2 C*/
            case ZOMBIE_HORSE_SPAWN_EGG:
                onChangeRole(event, player,3, 1);
                break;

             /* Role Manager - 3 D*/
            case PIG_SPAWN_EGG:
                onChangeRole(event, player,0, 2);
                break;

            /* Penalty Switch */
            case RED_SHULKER_BOX:
                onChangePenalty(event, player, false);
                break;

            /* Penalty Switch */
            case GREEN_SHULKER_BOX:
                onChangePenalty(event, player, true);
                break;

            /* Penalty Diamond Edit */
            case DIAMOND:
                onChangePenaltyDiamond(event, player);
                break;

            /* Penalty Coupon Edit */
            case NAME_TAG:
                onChangePenaltyCoupon(event, player);
                break;

            /* Death Count Edit */
            case BONE:
                onChangeDeathCount(event, player);
                break;
        }
    }

    /* Change Role Event */
    private void onChangeRole(InventoryClickEvent event, OfflinePlayer player, int nextRole, int previousRole)
    {
        if (event.getClick().isLeftClick())
            setDataReloadRole(player, player.getUniqueId().toString(), nextRole);

        if (event.getClick().isRightClick())
            setDataReloadRole(player, player.getUniqueId().toString(), previousRole);

        updateInventory(event, player);
    }

    private void setDataReloadRole(OfflinePlayer player, String uuid, int role)
    {
        if (player.isOnline())
        {
            BloomAPI.setHashMapRole(player.getUniqueId().toString(), role);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam reload");
        }
        else
        {
            FileConfiguration config = BloomAPI.getDataConfig(uuid);

            config.set("role", role);
            BloomAPI.saveDataFile(config, BloomAPI.getDataFile(uuid));
        }
    }



    /* Change Penalty Event */
    private void onChangePenalty(InventoryClickEvent event, OfflinePlayer player, boolean value)
    {
        if (event.isLeftClick() || event.isRightClick())
            setDataReloadPenalty(player, player.getUniqueId().toString(), value);

        updateInventory(event, player);
    }

    private void setDataReloadPenalty(OfflinePlayer player, String uuid, boolean value)
    {
        if (value)
        {
            if (player.isOnline()) {
                BloomAPI.setHashMapPenalty(uuid, true);
                BloomAPI.setHashMapPenaltyDiamond(uuid, 1);
                updateTeam();
            } else {
                FileConfiguration config = BloomAPI.getDataConfig(uuid);

                config.set("penalty", true);
                config.set("penalty_diamond", 1);
                BloomAPI.saveDataFile(config, BloomAPI.getDataFile(uuid));
            }
        }
        else {
            if (player.isOnline()) {
                BloomAPI.setHashMapPenalty(uuid, false);
                BloomAPI.setHashMapPenaltyDiamond(uuid, 0);
                updateTeam();
                updateScore(player, uuid);
            } else {
                FileConfiguration config = BloomAPI.getDataConfig(uuid);

                config.set("penalty", false);
                config.set("penalty_diamond", 0);
                BloomAPI.saveDataFile(config, BloomAPI.getDataFile(uuid));
            }
        }
    }



    /* Change Penalty Diamond Event */
    private void onChangePenaltyDiamond(InventoryClickEvent event, OfflinePlayer player)
    {
        switch (event.getClick()) {
            case LEFT:
                setDataReloadDiamond(player, player.getUniqueId().toString(), 1);
                break;

            case RIGHT:
                setDataReloadDiamond(player, player.getUniqueId().toString(), 2);
                break;

            case SHIFT_LEFT:
                setDataReloadDiamond(player, player.getUniqueId().toString(), 3);
                break;

            case SHIFT_RIGHT:
                setDataReloadDiamond(player, player.getUniqueId().toString(), 4);
                break;
        }

        updateInventory(event, player);
    }

    private void setDataReloadDiamond(OfflinePlayer player, String uuid, int clickType)
    {
        if (player.isOnline()) {
            BloomAPI.setHashMapPenaltyDiamond(uuid, getIntCalculation(clickType, BloomAPI.getHashMapPenaltyDiamond(uuid)));
            updateScore(player, uuid);
        } else {
            FileConfiguration config = BloomAPI.getDataConfig(uuid);

            config.set("penalty_diamond", getIntCalculation(clickType, config.getInt("penalty_diamond")));
            BloomAPI.saveDataFile(config, BloomAPI.getDataFile(uuid));
        }
    }



    /* Change Penalty Coupon */
    private void onChangePenaltyCoupon(InventoryClickEvent event, OfflinePlayer player)
    {
        switch (event.getClick()) {
            case LEFT:
                setDataReloadCoupon(player, player.getUniqueId().toString(), 1);
                break;

            case RIGHT:
                setDataReloadCoupon(player, player.getUniqueId().toString(), 2);
                break;
        }

        updateInventory(event, player);

    }

    private void setDataReloadCoupon(OfflinePlayer player, String uuid, int clickType)
    {
        if (player.isOnline()) {
            BloomAPI.setHashMapPenaltyCoupon(uuid, getIntCalculation(clickType, BloomAPI.getHashMapPenaltyCoupon(uuid)));
        } else {
            FileConfiguration config = BloomAPI.getDataConfig(uuid);

            config.set("penalty_coupon", getIntCalculation(clickType, config.getInt("penalty_coupon")));
            BloomAPI.saveDataFile(config, BloomAPI.getDataFile(uuid));
        }
    }



    /* Change Death Count */
    private void onChangeDeathCount(InventoryClickEvent event, OfflinePlayer player)
    {
        switch (event.getClick()) {
            case LEFT:
                setDataReloadDeathCount(player, player.getUniqueId().toString(), 1);
                break;

            case RIGHT:
                setDataReloadDeathCount(player, player.getUniqueId().toString(), 2);
                break;

            case SHIFT_LEFT:
                setDataReloadDeathCount(player, player.getUniqueId().toString(), 3);
                break;

            case SHIFT_RIGHT:
                setDataReloadDeathCount(player, player.getUniqueId().toString(), 4);
                break;
        }

        updateInventory(event, player);
    }

    private void setDataReloadDeathCount(OfflinePlayer player, String uuid, int clickType)
    {
        if (player.isOnline()) {
            BloomAPI.setHashMapDeathCount(uuid, getIntCalculation(clickType, BloomAPI.getHashMapDeathCount(uuid)));
        } else {
            FileConfiguration config = BloomAPI.getDataConfig(uuid);

            config.set("death_count", getIntCalculation(clickType, config.getInt("death_count")));
            BloomAPI.saveDataFile(config, BloomAPI.getDataFile(uuid));
        }
    }





    /*
     * 1 : value +1
     * 2 : value +10
     * 3 : value -1
     * 4 : value -10
     */
    private int getIntCalculation(int mod, int value)
    {
        int result = 0;

        switch (mod) {
            case 1:
                result = value + 1;
                break;

            case 2:
                result = value - 1;
                break;

            case 3:
                result = value + 10;
                break;

            case 4:
                result = value - 10;
                break;
        }

        if (result < 0) result = 0;

        return result;
    }

    private void updateScore(OfflinePlayer player, String uuid)
    {
        BloomAPI.getScore("penaltyDiamond").getScore(Objects.requireNonNull(player.getName())).setScore(BloomAPI.getHashMapPenaltyDiamond(uuid));
    }

    private void updateTeam()
    {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam reload");
    }



    private void updateInventory(InventoryClickEvent event, OfflinePlayer player)
    {
        event.getWhoClicked().openInventory(new InventoryPlayerEdit(player).inv());
    }





    private OfflinePlayer getOfflinePlayer(String uuid)
    {
        return Bukkit.getOfflinePlayer(UUID.fromString(uuid));
    }

    private String getHeadUUID(ItemStack item)
    {
        if (!(Objects.requireNonNull(item.getItemMeta()).hasLore())) return "";
        List<String> lore = item.getItemMeta().getLore();

        assert lore != null;
        return lore.get(lore.size()-1).substring(2);
    }



    private boolean isEditInventory(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return false;
        if (event.getCurrentItem() == null) return false;

        return isCheckTitle(event.getView().getTitle());
    }


    private boolean isCheckTitle(String titleName)
    {
        Pattern pa = Pattern.compile("ㆍ (.*?) 의 정보");
        Matcher ma = pa.matcher(titleName);

        return ma.find();
    }
}
