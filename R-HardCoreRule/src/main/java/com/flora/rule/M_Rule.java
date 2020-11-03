package com.flora.rule;

import com.flora.api.BloomAPI;
import com.flora.rule.event.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

public class M_Rule extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        new EventRecipeRemove();

        BloomAPI.onRegisterCommand("rule", new CommandRule(), new CommandRule());
        BloomAPI.onRegisterEvent(new EventVillageRule(), this);
        BloomAPI.onRegisterEvent(new EventBedRule(), this);
        BloomAPI.onRegisterEvent(new EventThreeSecondRule(), this);
        BloomAPI.onRegisterEvent(new EventTimeRule(), this);

        for (World world : Bukkit.getWorlds())
        {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }
    }


    /* Kick And Ban Script*/
    public static void onSanctionsPlayer(Player player, String reason, int expires)
    {
        long time = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * expires);

        if (expires < 1)
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "§c" +  reason  + "§4\n", null, null);
        else
            Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "§c" +  reason  + "§4\n", new Date(time), null);

        player.kickPlayer("§b제한사항(규칙)§f을 §6위반§f하였습니다\n\n" +
                          "§c 사유 : " + reason);
        Bukkit.broadcastMessage("\n" +
                                " §c" + player.getName() + "§f이(가) §b제한사항(규칙)§f을 §6위반§f하였습니다\n" +
                                " §c   사유 : " + reason + "\n ");
    }

    public static boolean isRuleViolationOperator(Player player)
    {
        if (player.isOp()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7!! 현재 룰을 위반 하는 행동을 하였습니다 !!"));
            return true;
        } else {
            return false;
        }
    }
}
