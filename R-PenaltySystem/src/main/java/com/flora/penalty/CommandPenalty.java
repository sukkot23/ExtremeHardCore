package com.flora.penalty;

import com.flora.api.BloomAPI;
import com.flora.penalty.event.EventPlayerDeath;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPenalty implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (args.length > 0) {
            if (!(sender instanceof Player)) { sender.sendMessage("§cDo Not Use Command For Console"); return false; }

            if (EventPlayerDeath.deadLocation == null) return false;
            Location location = EventPlayerDeath.deadLocation.clone();

            ((Player) sender).teleport(location);
        }
        else  {
            if (!(sender.isOp())) { sender.sendMessage("§cDo Not use Command"); return false; }

            if (BloomAPI.gameState) return false;
            if (BloomAPI.isReset) return false;
            if (EventPlayerDeath.lastMap == null || EventPlayerDeath.lastMap.isEmpty()) return false;

            EventPlayerDeath.retryScript();
        }

        return false;
    }
}
