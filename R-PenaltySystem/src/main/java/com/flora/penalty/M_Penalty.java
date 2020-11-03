package com.flora.penalty;

import com.flora.api.BloomAPI;
import com.flora.penalty.event.EventPenaltyDiamond;
import com.flora.penalty.event.EventPlayerDeath;
import com.flora.penalty.event.EventReloadScore;
import org.bukkit.plugin.java.JavaPlugin;

public class M_Penalty extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        BloomAPI.onRegisterEvent(new EventReloadScore(), this);
        BloomAPI.onRegisterEvent(new EventPlayerDeath(), this);
        BloomAPI.onRegisterEvent(new EventPenaltyDiamond(), this);

        BloomAPI.onRegisterCommand("rpenalty", new CommandPenalty());
    }
}
