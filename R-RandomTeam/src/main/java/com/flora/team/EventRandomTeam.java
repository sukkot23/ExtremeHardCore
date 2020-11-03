package com.flora.team;

import com.flora.api.BloomAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventRandomTeam implements Listener
{
    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        onReloadTeams(event.getPlayer());
    }

    private void onReloadTeams(Player player)
    {
        String uuid = player.getUniqueId().toString();
        FileConfiguration config = BloomAPI.getDataConfig(uuid);

        boolean isPenalty = config.getBoolean("penalty");
        int role = config.getInt("role");


        if (isPenalty) { setDataStatePenalty("§c[벌칙자] " + player.getName() + "§r", player); }
        else {
            switch (role) {
                case 0:
                    if (BloomAPI.gameState)
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam other");
                    else
                        setDataStateRole("§7" + player.getName() + "§r", player, role);
                    break;

                case 1:
                    setDataStateRole("§8[지하팀] §r" + player.getName(), player, role);
                    break;

                case 2:
                    setDataStateRole("§2[지상팀] §r" + player.getName(), player, role);
                    break;

                case 3:
                    setDataStateRole("§d[매니저] §r" + player.getName(), player, role);
            }
        }
    }

    public static void setDataStateRole(String display, Player player, int role)
    {
        String uuid = player.getUniqueId().toString();

        player.setDisplayName(display);
        player.setPlayerListName(display);

        BloomAPI.getRoleTeam(role).addEntry(player.getName());
    }

    public static void setDataStatePenalty(String display, Player player)
    {
        String uuid = player.getUniqueId().toString();

        player.setDisplayName(display);
        player.setPlayerListName(display);

        BloomAPI.getPenaltyTeam(true).addEntry(player.getName());
    }
}
