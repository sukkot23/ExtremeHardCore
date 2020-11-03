package com.flora.hardcore;

import com.flora.api.BloomAPI;
import com.flora.hardcore.command.CommandEdit;
import com.flora.hardcore.event.*;
import org.bukkit.plugin.java.JavaPlugin;

public class ExtremeHardCore extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        register();
    }

    @Override
    public void onDisable()
    {
        BloomAPI.saveAllPlayerData();
    }

    private void register()
    {
        saveDefaultConfig();

        BloomAPI.onRegisterEvent(new EventPlayerData(), this);
        BloomAPI.onRegisterEvent(new EventGameControl(), this);
        BloomAPI.onRegisterEvent(new EventInventoryEdit(), this);
        BloomAPI.onRegisterEvent(new EventMove(), this);
        BloomAPI.onRegisterCommand("edit", new CommandEdit(), new CommandEdit());
    }
}
