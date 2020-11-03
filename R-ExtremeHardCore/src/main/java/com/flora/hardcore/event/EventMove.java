package com.flora.hardcore.event;

import com.flora.api.BloomAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventMove implements Listener
{
    /* Player Move Control Event */
    @EventHandler
    public void onMoveEvent(PlayerMoveEvent event)
    {
        event.setCancelled(!BloomAPI.isMove);
    }

}
