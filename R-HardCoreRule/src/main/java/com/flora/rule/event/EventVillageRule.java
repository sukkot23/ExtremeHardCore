package com.flora.rule.event;

import com.flora.rule.M_Rule;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class EventVillageRule implements Listener
{
    public static boolean isProtectVillager = true;
    public static boolean isProtectVillage = true;

    String reasonA = "주민 마을 약탈";
    String reasonB = "주민 공격";
    String reasonC = "주민 거래 시도";

    int detectRadiusWidth = 30;
    int detectRadiusHeight = 15;


    /* Village Role */
    @EventHandler
    private void onVillageBlockProtectEvent(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        if (!(isProtectVillage)) return;

        if (isNearByVillager(event.getBlock()))
        {
            if (M_Rule.isRuleViolationOperator(player)) return;

            event.setCancelled(true);
            M_Rule.onSanctionsPlayer(player, reasonA, 3);
        }
    }

    @EventHandler
    private void onVillageBlockProtectEvent(BlockPlaceEvent event)
    {
        Player player = event.getPlayer();
        if (!(isProtectVillage)) return;

        if (isNearByVillager(event.getBlock()))
        {
            if (player.isOp()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onVillageBukkitEmptyEvent(PlayerBucketEmptyEvent event)
    {
        Player player = event.getPlayer();
        if (!(isProtectVillage)) return;

        if (isNearByVillager(event.getBlock()))
        {
            if (player.isOp()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onVillageBukkitEmptyEvent(PlayerBucketFillEvent event)
    {
        Player player = event.getPlayer();
        if (!(isProtectVillage)) return;

        if (isNearByVillager(event.getBlock()))
        {
            if (player.isOp()) return;
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onVillageCropProtectEvent(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        if (!(isProtectVillage)) return;

        if (event.getAction() == Action.PHYSICAL && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.FARMLAND)
        {
            if (isNearByVillager(event.getClickedBlock()))
            {
                if (M_Rule.isRuleViolationOperator(player)) return;
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onVillageArmorStandProtectEvent(PlayerArmorStandManipulateEvent event)
    {
        Player player = event.getPlayer();
        if (!(isProtectVillage)) return;

        ArmorStand stand = event.getRightClicked();
        Block block = stand.getWorld().getBlockAt(stand.getLocation());

        if (isNearByVillager(block))
        {
            event.setCancelled(true);

            if (!(event.getPlayerItem().getType() == Material.AIR)) return;
            if (M_Rule.isRuleViolationOperator(player)) return;
            M_Rule.onSanctionsPlayer(player, reasonA, 3);
        }
    }

    @EventHandler
    private void onVillagerProtectEvent(EntityDamageEvent event)
    {
        if (!(isProtectVillager)) return;
        if (!(event.getEntity() instanceof Villager)) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onVillagerProtectEvent(EntityDamageByEntityEvent event)
    {
        if (!(isProtectVillager)) return;
        if (!(event.getEntity() instanceof Villager)) return;

        switch (event.getDamager().getType()) {
            case PLAYER:
                Player player = (Player) event.getDamager();

                if (M_Rule.isRuleViolationOperator(player)) return;

                M_Rule.onSanctionsPlayer(player, reasonB, 3);
                break;

            case ZOMBIE:
            case ZOMBIE_VILLAGER:
                event.getDamager().remove();
                break;
        }
    }

    @EventHandler
    private void onVillageEntityProtectEvent(EntityDamageByEntityEvent event)
    {
        if (!(isProtectVillage)) return;
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();

        switch (event.getEntityType()) {
            case ARMOR_STAND:
            case CHICKEN:
            case SHEEP:
            case PIG:
            case COW:
            case HORSE:
            case CAT:
                Block block = event.getEntity().getWorld().getBlockAt(event.getEntity().getLocation());

                if (isNearByVillager(block))
                {
                    if (player.isOp()) return;

                    event.setCancelled(true);
                }
        }
    }

    @EventHandler
    private void onVillagerCuredEvent(CreatureSpawnEvent event)
    {
        if (event.getEntity() instanceof Witch && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.LIGHTNING)
        {
            Objects.requireNonNull(event.getLocation().getWorld()).spawnEntity(event.getLocation(), EntityType.VILLAGER);
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Villager && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CURED)
        {
            Objects.requireNonNull(event.getLocation().getWorld()).spawnEntity(event.getLocation(), EntityType.ZOMBIE_VILLAGER);
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onVillagerVehicleEnterEvent(VehicleEnterEvent event)
    {
        if (!(isProtectVillager)) return;
        if (!(event.getEntered() instanceof Villager)) return;
        event.setCancelled(true);
    }

    @EventHandler
    private void onVillagerTradeEvent(PlayerAdvancementDoneEvent event)
    {
        if (!(event.getAdvancement().getKey().getKey().equalsIgnoreCase("adventure/trade"))) return;
        Player player = event.getPlayer();

        if (M_Rule.isRuleViolationOperator(player)) return;

        M_Rule.onSanctionsPlayer(player, reasonC, 3);
    }

    @EventHandler
    private void onVillageChestEvent(InventoryOpenEvent event)
    {
        if (!(isProtectVillage)) return;

        Player player = (Player) event.getPlayer();
        if (event.getInventory().getLocation() == null) return;

        Block block = Objects.requireNonNull(event.getInventory().getLocation().getWorld()).getBlockAt(event.getInventory().getLocation());
        if (isNearByVillager(block))
        {
            if (player.isOp()) return;

            event.setCancelled(true);
        }
    }



    /* Old Source */
    @EventHandler
    private void onVillageEnterEvent(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        if (!(isProtectVillage)) return;
        if (!(isNearByVillager(player))) return;

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.1f, 2);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§4!!§c 근처에 마을이 존재하거나, 마을에 침입하였습니다 §4!!"));
    }

    private boolean isNearByVillager(Player player)
    {
        List<Entity> entityList = player.getNearbyEntities(detectRadiusWidth + 5, detectRadiusHeight, detectRadiusWidth + 5);

        for (Entity entity : entityList)
            if (entity instanceof Villager)
                return true;

        return false;
    }

    private boolean isNearByVillager(Block block)
    {
        Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation(),
                detectRadiusWidth, detectRadiusHeight, detectRadiusWidth, (e) -> e.getType() == EntityType.VILLAGER);

        return entities.size() >= 1;
    }
}
