package com.flora.rule.event;

import com.flora.rule.M_Rule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventThreeSecondRule implements Listener
{
    public static boolean isBurningOut = true;

    String reason = "3초 무적을 이용한 플레이";

    @EventHandler
    private void onThreeTimeOutEvent(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if (player.isOp()) return;

        if (player.getFireTicks() > 0) {
            M_Rule.onSanctionsPlayer(player, reason, 3);
        }
    }
}
