package com.flora.team;

import com.flora.api.BloomAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class M_Team extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        BloomAPI.onRegisterEvent(new EventRandomTeam(), this);
        BloomAPI.onRegisterCommand("rteam", new CommandRandomTeam());
    }
}
