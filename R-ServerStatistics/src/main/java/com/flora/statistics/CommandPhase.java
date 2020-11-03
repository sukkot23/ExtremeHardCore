package com.flora.statistics;

import com.flora.api.BloomAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.TimeZone;

public class CommandPhase implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender.isOp())) { sender.sendMessage("§cDo Not use Command"); return false; }

        if (args.length > 0) {
            try {
                int phase = Integer.parseInt(args[0]);

                switch (phase) {
                    case 1:
                        EventPhase.checkPhase = 1;
                        EventPhase.phaseB = false;
                        EventPhase.phaseC = false;
                        EventPhase.phaseD = false;
                        BloomAPI.playerSendOpMessage("§e게임 진행도를 §61단계§e로 변경하였습니다");
                        break;

                    case 2:
                        EventPhase.checkPhase = 2;
                        EventPhase.phaseA = true;
                        EventPhase.phaseC = false;
                        EventPhase.phaseD = false;
                        BloomAPI.playerSendOpMessage("§e게임 진행도를 §62단계§e로 변경하였습니다");
                        break;

                    case 3:
                        EventPhase.checkPhase = 3;
                        EventPhase.phaseA = true;
                        EventPhase.phaseB = true;
                        EventPhase.phaseD = false;
                        BloomAPI.playerSendOpMessage("§e게임 진행도를 §63단계§e로 변경하였습니다");
                        break;

                    case 4:
                        EventPhase.checkPhase = 4;
                        EventPhase.phaseA = true;
                        EventPhase.phaseB = true;
                        EventPhase.phaseC = true;
                        BloomAPI.playerSendOpMessage("§e게임 진행도를 §64단계§e로 변경하였습니다");
                        break;

                    case 5:
                        EventPhase.checkPhase = 5;
                        EventPhase.phaseA = true;
                        EventPhase.phaseB = true;
                        EventPhase.phaseC = true;
                        EventPhase.phaseD = true;
                        BloomAPI.playerSendOpMessage("§e게임 진행도를 §65단계§e로 변경하였습니다");
                        break;

                    case 5973:
                        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));

                        if (args.length > 1)
                            EventPhase.outPutStatistics(StringUtils.join(Arrays.copyOfRange(args, 1, args.length), " ").replaceAll("&", "§"));
                        else
                            EventPhase.outPutStatistics();

                        break;

                    default:
                        sender.sendMessage("§c1~5까지 입력할 수 있습니다");
                        break;
                }

            } catch (NumberFormatException exception) {
                sender.sendMessage("§c숫자만 입력 가능합니다");
            }
        }
        return false;
    }
}
