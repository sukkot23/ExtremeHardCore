package com.flora.rule.event;

import com.flora.api.BloomAPI;
import com.flora.rule.M_Rule;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EventTimeRule implements Listener
{
    public static boolean isOneHour = false;
    public static boolean isNightJoin = false;

    String message = "§7진행시간이 §e1시간§7이 넘어가서 참여할 수 없습니다" + "\n\n" + "§7다음 트라이에 참여해주세요!!";


    public static void TimeRunnable()
    {
        new BukkitRunnable() {
            final long startTime = System.currentTimeMillis();

            @Override
            public void run() {
                long time = System.currentTimeMillis() - this.startTime;
                long stopTime = 1000 * 60 * 60;

                if (time >= stopTime) {
                    isOneHour = true;
                    Bukkit.broadcastMessage("\n§e[알림]§f 플레이 타임이 1시간이 되었습니다\n" +
                                            "       현 시간부터 추가적으로 인원이 들어올 수 없습니다\n ");
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(JavaPlugin.getPlugin(M_Rule.class), 20L, 20L);
    }

    @EventHandler
    private void onOneHourTimeEvent(PlayerDeathEvent event)
    {
        if (isOneHour)
            addPenaltyDiamond(event.getEntity());
    }


    @EventHandler
    private void onPlayerLoginEvent(AsyncPlayerPreLoginEvent event)
    {
        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
        if (player.isOp()) return;

        if (BloomAPI.getServerConfig().getBoolean("staffOnly"))
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§a서버 점검 중");

        if (isOneHour)
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message);

        if (!(isNightJoin))
            if (isWorldTimeNight())
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, getWorldTimeRemainingMessage());
    }

    @EventHandler
    private void onServerMOTD(ServerListPingEvent event)
    {
        String mainMOTD =   "             §f☠§6§l 동우동§f의 §c§l극한 하드코어§f 컨텐츠 ☠\n" +
                            "                 §5트위치 §6팔로우! §c 유튜브 §6구독!";

        if (BloomAPI.getServerConfig().getBoolean("staffOnly"))
            event.setMotd(" \n                        §a서버 점검 중");

        else if (isOneHour)
            event.setMotd(" \n      §7현재 진행시간이 §e1시간§7이 넘어가서 접속할 수 없습니다");

        else if (!(isNightJoin) && isWorldTimeNight())
            event.setMotd(" \n        " + getWorldTimeRemainingMessage());

        else
            event.setMotd(mainMOTD);
    }


    private boolean isWorldTimeNight()
    {
        World w = Bukkit.getWorlds().get(0);
        int world_time = (int)w.getTime();

        return (world_time < 24000) && (world_time > 11999);
    }

    private String getWorldTimeRemainingMessage()
    {
        World w = Bukkit.getWorlds().get(0);
        int world_time = (int)w.getTime();

        int over_time = 24000 - world_time;
        int over_finish = (int)(over_time * 0.05D);

        int s = over_finish % 60;
        int m = over_finish / 60;

        return "§9밤§f에는 접속할 수 없습니다 §7(남은시간 : §6" + m + "분 " + s + "초§7)";
    }

    private void addPenaltyDiamond(Player player)
    {
        String uuid = player.getUniqueId().toString();
        int getCoupon = BloomAPI.getHashMapPenaltyCoupon(uuid);
        int getDiamond = BloomAPI.getHashMapPenaltyDiamond(uuid);

        if (getCoupon > 0) {
            BloomAPI.setHashMapPenaltyCoupon(uuid, getCoupon - 1);
        } else {
            BloomAPI.setHashMapPenalty(uuid, true);
            BloomAPI.setHashMapPenaltyDiamond(uuid, getDiamond + 20);

            int penaltyDiamond = BloomAPI.getHashMapPenaltyDiamond(player.getUniqueId().toString());
            BloomAPI.getScore("penaltyDiamond").getScore(player.getName()).setScore(penaltyDiamond);

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam reload");
        }
    }
}
