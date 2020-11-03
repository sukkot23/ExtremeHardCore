package com.flora.statistics;

import com.flora.api.BloomAPI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class Main extends JavaPlugin
{
    public static boolean gameCheck = true;
    public static long gameTime = -32400000L;

    public static boolean checkColor = false;

    @Override
    public void onEnable() {
        BloomAPI.onRegisterCommand("phase", new CommandPhase());
        BloomAPI.onRegisterEvent(new EventPhase(), this);

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));

        Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(JavaPlugin.getPlugin(Main.class), new TimeRunnable(), 20L, 20L);
    }

    class TimeRunnable implements Runnable
    {
        SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss");

        @Override
        public void run() {
            setPlayerList(form, BloomAPI.gameState);
        }
    }

    private void setPlayerList(SimpleDateFormat form, boolean value)
    {
        String personnel = getPersonnel();

        if (value)
        {
            gameTime += 1000L;
            String missionMessage = getMissionMessage();

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setPlayerListHeader(  " \n    §6§l 동우동§f의 §c§l극한 하드코어§f 컨텐츠     \n" +
                        " \n " + personnel + " \n ");

                p.setPlayerListFooter(  " \n \n" +
                        " 플레이 타임 \n" +
                        form.format(gameTime) + "\n" +
                        " \n" +
                        isPhase(EventPhase.phaseA) + " ━━ " + isPhase(EventPhase.phaseB) + " ━━ " + isPhase(EventPhase.phaseC) + " ━━ " + isPhase(EventPhase.phaseD) + "\n" +
                        EventPhase.checkPhase + " 페이즈 \n" +
                        " \n" +
                        "   " + missionMessage + "   \n \n \n ");
            }
        }
        else
        {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.setPlayerListHeader(  " \n    §6§l 동우동§f의 §c§l극한 하드코어§f 컨텐츠     \n" +
                        " \n " + personnel + " \n ");

                p.setPlayerListFooter(  " \n \n" +
                        " 플레이 타임 \n" +
                        form.format(gameTime) + "\n" +
                        " \n" +
                        isPhase(EventPhase.phaseA) + " ━━ " + isPhase(EventPhase.phaseB) + " ━━ " + isPhase(EventPhase.phaseC) + " ━━ " + isPhase(EventPhase.phaseD) + "\n" +
                        EventPhase.checkPhase + " 페이즈 \n" +
                        " \n \n \n \n ");
            }
        }
    }

    private String isPhase(boolean value)
    {
        if (value) {
            return "§e✦§r";
        }
        else {
            return "✧";
        }
    }

    private String getPersonnel()
    {
        int fullSize = Bukkit.getServer().getMaxPlayers();
        int onlineSize = Bukkit.getOnlinePlayers().size();

        return "§9" + onlineSize + " / " + fullSize;
    }

    private String getMissionMessage()
    {
        switch (EventPhase.checkPhase) {
            case 0:
                if (gameCheck) {
                    EventPhase.checkPhase = 1;
                    gameCheck = false;
                }
                return "";

            case 1:
                EventPhase.phaseA = !EventPhase.phaseA;
                return "§5§l네더포탈§r을 건설하고, §c§l네더§r를 탐험하세요";

            case 2:
                EventPhase.phaseB = !EventPhase.phaseB;
                EventPhase.phaseA = true;
                return "§6§l블레이즈 막대§r를 구하세요\n    §e" + EventPhase.getBlazeRod + " §f/§a " + EventPhase.checkBlaze + " §f/§6 7";

            case 3:
                EventPhase.phaseC = !EventPhase.phaseC;
                EventPhase.phaseB = true;
                return "§3§l엔더유적§r을 찾아, §5§l엔더월드§r를 탐험하세요";

            case 4:
                EventPhase.phaseD = !EventPhase.phaseD;
                EventPhase.phaseC = true;
                return "§5§l엔더드래곤§r을 처치하세요";

            case 5:
                return "§6§l 극하드코어§e를§6§l 클리어§e 하였습니다!! ";
        }

        return "";
    }
}
