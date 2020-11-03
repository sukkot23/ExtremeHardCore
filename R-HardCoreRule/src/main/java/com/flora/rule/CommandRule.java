package com.flora.rule;

import com.flora.api.BloomAPI;
import com.flora.rule.event.EventBedRule;
import com.flora.rule.event.EventTimeRule;
import com.flora.rule.event.EventThreeSecondRule;
import com.flora.rule.event.EventVillageRule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRule implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender.isOp())) { sender.sendMessage("§cDo Not use Command"); return false; }

        if (args.length > 0) {
            switch (args[0]) {
                case "help":
                    commandHelper(sender, label);
                    break;

                case "state":
                    commandState(sender);
                    break;

                case "clear":
                    EventVillageRule.isProtectVillage = true;
                    EventVillageRule.isProtectVillager = true;
                    EventBedRule.isProtectBed = true;
                    EventThreeSecondRule.isBurningOut = true;
                    EventTimeRule.isNightJoin = false;
                    EventTimeRule.isOneHour = false;
                    sender.sendMessage("§e모든 룰을 기본값으로 설정하였습니다");
                    break;

                case "protectVillage":
                    commandProtectVillage(sender);
                    break;

                case "protectVillager":
                    commandProtectVillager(sender);
                    break;

                case "protectBed":
                    commandProtectBed(sender);
                    break;

                case "burningOut":
                    commandBurningOut(sender);
                    break;

                case "nightJoin":
                    commandNightJoin(sender);
                    break;

                case "timeOut":
                    commandTimeOut(sender);
                    break;

                case "runTime":
                    commandRunTime(sender, args.length > 1);
                    break;

                case "staff":
                    commandStaff(sender);
                    break;

                default:
                    sender.sendMessage("§6/" + label + "§7 help");
                    break;
            }
        }
        else {
            sender.sendMessage("§6/" + label + "§7 help");
        }
        return false;
    }

    private void commandHelper(CommandSender sender, String label)
    {
        sender.sendMessage("");
        sender.sendMessage("§e------------[ §bExtreme Hard Core Command §e]------------");
        sender.sendMessage("§6/" + label + "§7 state : §f게임 룰 상태를 확인합니다");
        sender.sendMessage("§6/" + label + "§7 protectVillage : §6마을 보호 §f룰을(를) 조정합니다");
        sender.sendMessage("§6/" + label + "§7 protectVillager : §6주민 보호 §f룰을(를) 조정합니다");
        sender.sendMessage("§6/" + label + "§7 protectBed : §6침대 사용 방지 §f룰을(를) 조정합니다");
        sender.sendMessage("§6/" + label + "§7 burningOut : §63초 무적 방지 §f룰을(를) 조정합니다");
        sender.sendMessage("§6/" + label + "§7 nightJoin : §6야간 접속 §f룰을(를) 조정합니다");
        sender.sendMessage("§6/" + label + "§7 timeOut : §61시간 초과 §f룰을(를) 조정합니다");
        sender.sendMessage("§6/" + label + "§7 runTime : §f1시간 타이머를 설정합니다");
        sender.sendMessage("§6/" + label + "§7 clear : §f게임 룰을 초기값으로 바꿉니다");
        sender.sendMessage("§6/" + label + "§7 staff :§f 서버를 점검하는 모드로 변경합니다");
        sender.sendMessage("");

    }

    private void commandProtectVillage(CommandSender sender)
    {
        EventVillageRule.isProtectVillage = !EventVillageRule.isProtectVillage;

        if (EventVillageRule.isProtectVillage)
            BloomAPI.playerSendOpMessage("마을 보호: §aON");
        else
            BloomAPI.playerSendOpMessage("마을 보호: §cOFF");
    }

    private void commandProtectVillager(CommandSender sender)
    {
        EventVillageRule.isProtectVillager = !EventVillageRule.isProtectVillager;

        if (EventVillageRule.isProtectVillager)
            BloomAPI.playerSendOpMessage("주민 보호: §aON");
        else
            BloomAPI.playerSendOpMessage("주민 보호: §cOFF");
    }

    private void commandProtectBed(CommandSender sender)
    {
        EventBedRule.isProtectBed = !EventBedRule.isProtectBed;

        if (EventBedRule.isProtectBed)
            BloomAPI.playerSendOpMessage("침대 보호: §aON");
        else
            BloomAPI.playerSendOpMessage("침대 보호: §cOFF");
    }

    private void commandBurningOut(CommandSender sender)
    {
        EventThreeSecondRule.isBurningOut = !EventThreeSecondRule.isBurningOut;

        if (EventThreeSecondRule.isBurningOut)
            BloomAPI.playerSendOpMessage("3초 무적 방지: §aON");
        else
            BloomAPI.playerSendOpMessage("3초 무적 방지: §cOFF");
    }

    private void commandNightJoin(CommandSender sender)
    {
        EventTimeRule.isNightJoin = !EventTimeRule.isNightJoin;

        if (EventTimeRule.isNightJoin)
            BloomAPI.playerSendOpMessage("야간 접속: §aON");
        else
            BloomAPI.playerSendOpMessage("야간 접속: §cOFF");
    }

    private void commandTimeOut(CommandSender sender)
    {
        EventTimeRule.isOneHour = !EventTimeRule.isOneHour;

        if (EventTimeRule.isOneHour)
            BloomAPI.playerSendOpMessage("1시간 초과: §aON");
        else
            BloomAPI.playerSendOpMessage("1시간 초과: §cOFF");
    }

    private void commandRunTime(CommandSender sender, boolean startValue)
    {
        if (startValue) {
            Bukkit.getServer().getScheduler().cancelTasks(JavaPlugin.getPlugin(M_Rule.class));
            EventTimeRule.TimeRunnable();
            sender.sendMessage("§b[R-HardCoreRule] §a1시간 타이머가 시작되었습니다");
        }
        else {
            Bukkit.getServer().getScheduler().cancelTasks(JavaPlugin.getPlugin(M_Rule.class));
            sender.sendMessage("§b[R-HardCoreRule] §c1시간 타이머가 중지되었습니다");
        }
    }

    private void commandState(CommandSender sender)
    {
        sender.sendMessage("");
        sender.sendMessage("§e---------------[ §fGame Rule §e]---------------");

        if (EventVillageRule.isProtectVillage)
            sender.sendMessage("§e마을 보호 :§a TRUE");
        else
            sender.sendMessage("§e마을 보호 :§c FALSE");

        if (EventVillageRule.isProtectVillager)
            sender.sendMessage("§e주민 보호 :§a TRUE");
        else
            sender.sendMessage("§e주민 보호 :§c FALSE");

        if (EventBedRule.isProtectBed)
            sender.sendMessage("§e침대 사용 방지 :§a TRUE");
        else
            sender.sendMessage("§e침대 사용 방지 :§c FALSE");

        if (EventThreeSecondRule.isBurningOut)
            sender.sendMessage("§e3초 무적 방지 :§a TRUE");
        else
            sender.sendMessage("§e3초 무적 방지 :§c FALSE");

        if (EventTimeRule.isNightJoin)
            sender.sendMessage("§e야간 난입 :§a TRUE");
        else
            sender.sendMessage("§e야간 난입 :§c FALSE");

        if (EventTimeRule.isOneHour)
            sender.sendMessage("§e1시간 초과 :§a TRUE");
        else
            sender.sendMessage("§e1시간 초과 :§c FALSE");

        sender.sendMessage("");
    }

    private void commandStaff(CommandSender sender)
    {
        boolean isStaffOnly = BloomAPI.getServerConfig().getBoolean("staffOnly");
        BloomAPI.getServerConfig().set("staffOnly", !(isStaffOnly));
        BloomAPI.PLUGIN.saveConfig();

        if (!(isStaffOnly))
        {
            BloomAPI.playerSendOpMessage("서버점검: §aON");

            for (Player player : Bukkit.getOnlinePlayers()){
                if (!(player.isOp()))
                    player.kickPlayer("§a서버 점검중");
            }
        }
        else
            BloomAPI.playerSendOpMessage("서버점검: §cOFF");
    }



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!(sender.isOp())) { sender.sendMessage("§cDo Not use Command"); return null; }

        List<String> empty = new ArrayList<String>() {{ add(""); }};
        String[] commands = {"help", "state", "protectVillage", "protectVillager", "protectBed", "burningOut", "nightJoin", "timeOut", "clear", "staff" };

        if (args.length == 1)
            return tabCommandMain(commands, args[0]);
        else
            return empty;
    }

    private List<String> tabCommandMain(String[] commands, String args)
    {
        List<String> list = new ArrayList<>(Arrays.asList(commands));

        return tabCompleteSort(list, args);
    }

    private List<String> tabCompleteSort(List<String> list, String args)
    {
        List<String> sortList = new ArrayList<>();
        for (String s : list)
        {
            if (args.isEmpty()) return list;

            if (s.toLowerCase().startsWith(args.toLowerCase()))
                sortList.add(s);
        }
        return sortList;
    }
}
