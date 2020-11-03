package com.flora.rule.event;

import com.flora.rule.M_Rule;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;

import java.util.Objects;

public class EventBedRule implements Listener
{
    public static boolean isProtectBed = true;

    String reason = "침대 사용 금지";

    /* Bed Role */
    @EventHandler
    private void onBedEnterEvent(PlayerBedEnterEvent event)
    {
        if (!(isProtectBed)) return;
        event.setCancelled(true);
        Player player = event.getPlayer();

        if (M_Rule.isRuleViolationOperator(player)) return;

        M_Rule.onSanctionsPlayer(player, reason, 3);
    }

    @EventHandler
    private void onBedPlaceEvent(BlockPlaceEvent event)
    {
        if (!(isProtectBed)) return;
        if (!(isBed(event.getBlock()))) return;
        event.setCancelled(true);
        Player player = event.getPlayer();

        if (M_Rule.isRuleViolationOperator(player)) return;

        M_Rule.onSanctionsPlayer(player, reason, 3);
    }

    @EventHandler
    private void onBedBreakEvent(BlockBreakEvent event)
    {
        if (!(isProtectBed)) return;
        if (!(isBed(event.getBlock()))) return;
        event.setCancelled(true);
        Player player = event.getPlayer();

        if (M_Rule.isRuleViolationOperator(player)) return;

        M_Rule.onSanctionsPlayer(event.getPlayer(), reason, 3);
    }

    @EventHandler
    private void onPistonCancelledEvent(BlockPistonExtendEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    private void onXrayBlockEvent(EntityPoseChangeEvent event)
    {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Block composter = Objects.requireNonNull(player.getLocation().getWorld()).getBlockAt(player.getLocation());
        Block aidBlock = player.getLocation().getWorld().getBlockAt(player.getLocation().add(0, 1, 0));

        if (composter.getType() == Material.COMPOSTER && event.getPose() == Pose.SWIMMING) {
            switch (aidBlock.getType()) {
                case OAK_TRAPDOOR:
                case BIRCH_TRAPDOOR:
                case SPRUCE_TRAPDOOR:
                case JUNGLE_TRAPDOOR:
                case ACACIA_TRAPDOOR:
                case DARK_OAK_TRAPDOOR:
                case IRON_TRAPDOOR:
                case WATER:
                case AIR:
                    break;

                default:
                    player.setGameMode(GameMode.SPECTATOR);

                    Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "§c" +  "X-Ray(블럭투시) 사용"  + "§4\n", null, null);

                    Bukkit.broadcastMessage("\n" +
                            " §c" + player.getName() + "§f이(가) §4X-Ray(블럭투시)§f을(를) 사용하는 것을 감지하였습니다\n" +
                            " §c 무제한으로 §4X-Ray§c를 즐길 수 있는 §7Spectator 모드§c로 변경합니다\n ");
            }
        }
    }


    private boolean isBed(Block block)
    {
        switch (block.getType()) {
            case WHITE_BED:
            case ORANGE_BED:
            case MAGENTA_BED:
            case LIGHT_BLUE_BED:
            case YELLOW_BED:
            case LIME_BED:
            case PINK_BED:
            case GRAY_BED:
            case LIGHT_GRAY_BED:
            case CYAN_BED:
            case PURPLE_BED:
            case BLUE_BED:
            case BROWN_BED:
            case GREEN_BED:
            case RED_BED:
            case BLACK_BED:
                return true;

            default:
                return false;
        }
    }
}
