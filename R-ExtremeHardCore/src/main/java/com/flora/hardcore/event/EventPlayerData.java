package com.flora.hardcore.event;

import com.flora.api.BloomAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;

import java.time.LocalDate;

public class EventPlayerData implements Listener
{
    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!BloomAPI.getDataFile(uuid).canRead()) onCreateNewData(player, uuid);
        BloomAPI.updatePlayerData(uuid);
    }

    private void onCreateNewData(Player player, String uuid)
    {
        FileConfiguration config = BloomAPI.getDataConfig(uuid);

        config.set("name", player.getName());
        config.set("uuid", uuid);

        config.set("role", 0);
        config.set("penalty", Boolean.FALSE);
        config.set("penalty_diamond", 0);

        config.set("penalty_coupon", 0);

        config.set("death_count", 0);

        config.set("last_date", LocalDate.now().toString());
        config.set("register_date", LocalDate.now().toString());

        BloomAPI.saveDataFile(config, BloomAPI.getDataFile(uuid));
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent event)
    {
        BloomAPI.savePlayerData(event.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    private void onServerReloadEvent(ServerLoadEvent event)
    {
        BloomAPI.updateAllPlayerData();
    }
}
