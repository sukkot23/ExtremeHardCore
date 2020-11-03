package com.flora.team;

import com.flora.api.BloomAPI;
import com.flora.api.BloomHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandRandomTeam implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender.isOp())) { sender.sendMessage("§cDo Not use Command"); return false; }

        if (args.length > 0) {
            if ("reload".equals(args[0]))
                onRandomTeamReload();
            else if ("other".equals(args[0]))
                onRandomTeamOther();
            else
                onPlayRandomTeam();
        }
        else  {
            onPlayRandomTeam();
        }

        return false;
    }

    private void onRandomTeamReload()
    {
        for (Player player : Bukkit.getOnlinePlayers())
            onReloadTeamsForHashMap(player);
    }

    /* Other Player Join Random Team */
    private void onRandomTeamOther()
    {
        List<Player> userList = new ArrayList<>();
        List<Player> penaltyList = new ArrayList<>();
        List<Player> underGroundList = new ArrayList<>();
        List<Player> groundList = new ArrayList<>();

        Map<String, BloomHashMap> map = BloomAPI.playerDataList;

        for (Player p : Bukkit.getOnlinePlayers())
        {
            String key = p.getUniqueId().toString();

            if (map.get(key).penalty)
                penaltyList.add(p);
            else if (map.get(key).role == 1)
                underGroundList.add(p);
            else if (map.get(key).role == 2)
                groundList.add(p);
            else
                userList.add(p);
        }

        int maxValue = Bukkit.getOnlinePlayers().size();
        int penaltyValue = penaltyList.size();
        int underGroundValue = underGroundList.size();
        int minValue = (int) Math.round(0.6D * maxValue);
        int finalValue = minValue - penaltyValue - underGroundValue;

        if (finalValue < 1)  {
            for (Player pick_ground : userList)
                setRoleGround(pick_ground.getUniqueId().toString());
        }
        else {
            for (int i = 0; i < finalValue; i++)
            {
                int idx = (int) (Math.random() * userList.size());

                Player pick_under = userList.get(idx);
                setRoleUnderGround(pick_under.getUniqueId().toString());

                userList.remove(pick_under);
            }

            for (Player pick_ground : userList)
                setRoleGround(pick_ground.getUniqueId().toString());
        }

        for (Player p : Bukkit.getOnlinePlayers())
            onReloadTeamsForHashMap(p);
    }


    /* Random Team Script */
    private void onPlayRandomTeam()
    {
        /* Normal Users */
        List<Player> userList = new ArrayList<>();
        /* Is Penalty Users */
        List<Player> penaltyList = new ArrayList<>();

        /* Online Player Data ListMap */
        Map<String, BloomHashMap> map = BloomAPI.playerDataList;

        /* Classify Users */
        for (Player p : Bukkit.getOnlinePlayers())
        {
            String key = p.getUniqueId().toString();

            if (map.get(key).penalty)
                penaltyList.add(p);
            else
                userList.add(p);
        }

        /* Basic Parameter */
        int maxValue = Bukkit.getOnlinePlayers().size();
        int penaltyValue = penaltyList.size();
        int minValue = (int) Math.round(0.6D * maxValue);
        int finalValue = minValue - penaltyValue;

        /* Online User is 1 member or Penalty User is all OnlineMember */
        if (finalValue < 1)  {
            for (Player pick_ground : userList)
                setRoleGround(pick_ground.getUniqueId().toString());

            for (Player pick_penalty : penaltyList)
                setRolePenalty(pick_penalty.getUniqueId().toString());
        }
        /* Classify Underground Team, Ground Team */
        else {
            for (int i = 0; i < finalValue; i++)
            {
                int idx = (int) (Math.random() * userList.size());

                Player pick_under = userList.get(idx);
                setRoleUnderGround(pick_under.getUniqueId().toString());

                userList.remove(pick_under);
            }

            for (Player pick_ground : userList)
                setRoleGround(pick_ground.getUniqueId().toString());

            for (Player pick_penalty : penaltyList)
                setRolePenalty(pick_penalty.getUniqueId().toString());
        }

        /* Team Result Announcement Script */
        onShowResultAnnouncementTeam();
    }

    private void setRoleUnderGround(String uuid)
    {
        BloomAPI.setHashMapRole(uuid, 1);
    }

    private void setRoleGround(String uuid)
    {
        BloomAPI.setHashMapRole(uuid, 2);
    }

    private void setRolePenalty(String uuid)
    {
        BloomAPI.setHashMapPenalty(uuid, true);
    }

    private void onShowResultAnnouncementTeam()
    {
        for (Player p : Bukkit.getOnlinePlayers())
        {
            String uuid = p.getUniqueId().toString();

            if (BloomAPI.getHashMapPenalty(uuid))
                onViewScript(p, "§c벌칙자§r", "§7지하로 내려가 §b다이아§7를 캐오십시오.");
            else
                switch (BloomAPI.getHashMapRole(uuid)) {
                    case 0:
                        onViewScript(p, "§7무직자§r", "§c무직은 버그입니다^^ 매니저에게 문의 바랍니다");
                        break;

                    case 1:
                        onViewScript(p, "§8지하팀§r", "지하에서 §9광물§r을 §9채굴§r하세요");
                        break;

                    case 2:
                        onViewScript(p, "§2지상팀§r", "지상에서 §9건축 §r또는 §9채집§r을 하세요");
                        break;

                    case 3:
                        onViewScript(p, "§d매니저§r", "§dHello, Manager");
                        break;
                }
        }
    }

    private void onViewScript(Player player, String roleName, String roleAdvice)
    {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                count++;

                switch (count) {
                    case 1:
                        player.sendTitle("당신은 [     ] 입니다", "", 10, 100, 0);
                        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.8F, 0.0F);
                        break;

                    case 2:
                        player.sendTitle("당신은 [" + roleName + "] 입니다", "", 0, 100, 0);
                        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.8F, 0.0F);
                        break;

                    case 3:
                        player.sendTitle("당신은 [" + roleName + "] 입니다", roleAdvice, 0, 100, 10);
                        player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_AMBIENT, 0.8F, 1.0F);
                        onReloadTeamsForHashMap(player);
                        cancel();
                        break;

                    default:
                        cancel();
                }
            }
        }.runTaskTimer(BloomAPI.PLUGIN, 20L, 35L);
    }

    private void onReloadTeamsForHashMap(Player player)
    {
        String uuid = player.getUniqueId().toString();

        boolean isPenalty = BloomAPI.getHashMapPenalty(uuid);
        int role = BloomAPI.getHashMapRole(uuid);


        if (isPenalty) { EventRandomTeam.setDataStatePenalty("§c[벌칙자] " + player.getName() + "§r", player); }
        else {
            switch (role) {
                case 0:
                    EventRandomTeam.setDataStateRole("§7" + player.getName() + "§r", player, role);
                    break;

                case 1:
                    EventRandomTeam.setDataStateRole("§8[지하팀] §r" + player.getName(), player, role);
                    break;

                case 2:
                    EventRandomTeam.setDataStateRole("§2[지상팀] §r" + player.getName(), player, role);
                    break;

                case 3:
                    EventRandomTeam.setDataStateRole("§d[매니저] §r" + player.getName(), player, role);
            }
        }
    }
}
