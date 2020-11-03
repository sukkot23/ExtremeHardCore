package com.flora.end;

import com.flora.api.BloomAPI;
import org.bukkit.plugin.java.JavaPlugin;

public class M_End extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        BloomAPI.onRegisterEvent(new EventEnderDragon(), this);
    }
}
