package com.flora.penalty.event;

import com.flora.api.BloomAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventReloadScore implements Listener
{
    @EventHandler
    public void onPlayerScoreReloadEvent(PlayerJoinEvent event)
    {
        onReloadScore(event.getPlayer());
    }

    public static void onReloadScore(Player player)
    {
        int penaltyDiamond = BloomAPI.getHashMapPenaltyDiamond(player.getUniqueId().toString());
        BloomAPI.getScore("penaltyDiamond").getScore(player.getName()).setScore(penaltyDiamond);
    }
}
