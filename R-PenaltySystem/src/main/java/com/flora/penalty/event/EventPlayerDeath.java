package com.flora.penalty.event;

import com.flora.api.BloomAPI;
import com.flora.penalty.M_Penalty;
import net.md_5.bungee.api.chat.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventPlayerDeath implements Listener
{
    public static Map<Player, Location> lastMap;
    public static Location deadLocation;

    @EventHandler
    private void onDeathEvent(PlayerDeathEvent event)
    {
        String streamName = BloomAPI.getServerConfig().getString("streamName");
        Player player = event.getEntity();

        if (!(player.getGameMode() == GameMode.SURVIVAL) || !(BloomAPI.gameState)) {
            event.setDeathMessage("");
            autoGameModeChange(player);
            return;
        }

        addDeathCount(player);
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "phase 5973 " + event.getDeathMessage());

        if (BloomAPI.getServerConfig().getInt("life") < 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(streamName))
                    p.sendTitle("§5스트리머가 사망하였습니다", "§a'ㅋㅋㅎㅈ'§f을(를) 눌러 조의를 표하세요", 30, 120, 30);
                else
                    p.sendTitle("§c" + player.getName() + " 님께서 사망하셨습니다", "잠시 후 서버가 리셋됩니다", 30, 120, 30);
            }

            resetTimerScript(player);
        } else {
            autoGameModeChange(player);
        }
    }

    private void autoGameModeChange(Player player)
    {
        GameMode gameMode = player.getGameMode();

        try {
            new BukkitRunnable() {
                @Override
                public void run() { player.setGameMode(gameMode); }
            }.runTaskLater(JavaPlugin.getPlugin(M_Penalty.class), 10L);
        } catch (Exception exception) {
            Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(M_Penalty.class));
        }
    }

    private void resetTimerScript(Player player)
    {
        if (BloomAPI.gameState)
            BloomAPI.changeGameState();

        int resetTime = BloomAPI.getServerConfig().getInt("resetTime");

        lastMap = getLastLocationMap();
        deadLocation = player.getLocation().clone();

        /* Send Json Message - Death Point */
        TextComponent a = new TextComponent("§4[ 데스포인트로 이동 ]");
        a.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rpenalty 1"));
        a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("텍스트를 클릭하세요").create()));

        /* Set Block Player Head */
        Block block = deadLocation.getBlock();
        block.setType(Material.PLAYER_HEAD, false);
        Skull skull = (Skull) block.getState();
        skull.setOwningPlayer(player);
        skull.update();

        Objects.requireNonNull(deadLocation.getWorld()).spigot().strikeLightningEffect(deadLocation, false);

        for (Player p : Bukkit.getOnlinePlayers())
        {
            p.playSound(p.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 100, 0);
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60 * 100, 1, true));
            p.setGameMode(GameMode.SPECTATOR);

            p.sendMessage(" ");
            p.sendMessage(" §c데스포인트§f를 보러가고 싶으시다면 아래 텍스트를 눌러주세요");
            p.spigot().sendMessage(a);
            p.sendMessage(" ");

        }

        for (World world : Bukkit.getWorlds())
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, true);


        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        new BukkitRunnable() {
            int time = resetTime;

            @Override
            public void run() {
                time--;

                String countDown = format.format(new Date(time * 1000));
                BloomAPI.AllPlayerSendActionMessage("§4" + countDown);


                if (!(BloomAPI.isReset)) {
                    BloomAPI.AllPlayerSendActionMessage("§a" + countDown);
                    Bukkit.broadcastMessage("§e리셋이 취소되었습니다");
                    cancel();
                }

                if (BloomAPI.isRetry) {
                    BloomAPI.AllPlayerSendActionMessage("§a" + countDown);
                    retryScript();
                    cancel();
                }

                if (time < 1) {
                    serverRestartScript();
                    cancel();
                }
            }
        }.runTaskTimer(BloomAPI.PLUGIN, 20L, 20L);
    }

    public static void retryScript()
    {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                count++;

                switch (count) {
                    case 1:
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.closeInventory();
                            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            p.sendTitle("", "§e잠시 후 게임이 재개됩니다", 0, 150, 0);
                        }
                        break;

                    case 2:
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "edit move");

                        for (World world : Bukkit.getWorlds()) {
                            world.setHardcore(false);
                            world.setDifficulty(Difficulty.PEACEFUL);
                        }
                        break;

                    case 3:
                        for (Player p : lastMap.keySet()) {
                            p.teleport(lastMap.get(p));

                            p.setGameMode(GameMode.SURVIVAL);

                            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 15, 0, true));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 15, 0, true));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 15, 0, true));
                        }
                        break;

                    case 4:
                        if (!(BloomAPI.gameState))
                            BloomAPI.changeGameState();

                        if (!(BloomAPI.isReset))
                            BloomAPI.changeIsReset();

                        if (BloomAPI.isRetry)
                            BloomAPI.changeIsRetry();

                        lastMap = null;
                        deadLocation = null;

                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "edit move");

                        for (World world : Bukkit.getWorlds()) {
                            world.setHardcore(true);
                            world.setDifficulty(Difficulty.HARD);
                        }

                        for (Player p : Bukkit.getOnlinePlayers())
                            p.sendTitle("", "｡◕‿ ◕｡", 0, 30, 0);
                        break;

                    default:
                        cancel();
                        break;
                }
            }
        }.runTaskTimer(BloomAPI.PLUGIN, 20L, 30L);
    }

    private void serverRestartScript()
    {
        for (Player p : Bukkit.getOnlinePlayers())
            p.kickPlayer("§c 서버가 초기화 됩니다! §f잠시 후 다시 참여해 주세요!! ");

        BloomAPI.getServerConfig().set("tryCount", BloomAPI.getServerConfig().getInt("tryCount") + 1);
        BloomAPI.getServerConfig().set("life", 1);
        BloomAPI.PLUGIN.saveConfig();

        for (File file : BloomAPI.getDataFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("role", 0);
            BloomAPI.saveDataFile(config, file);
        }

        Bukkit.getServer().spigot().restart();
    }



    /* LastPlay Player Location */
    private Map<Player, Location> getLastLocationMap()
    {
        Map<Player, Location> map = new HashMap<>();

        for (Player p : Bukkit.getOnlinePlayers())
            map.put(p, p.getLocation());

        return map;
    }

    /* Set Penalty Data */
    private void addDeathCount(Player player)
    {
        String uuid = player.getUniqueId().toString();
        int getCoupon = BloomAPI.getHashMapPenaltyCoupon(uuid);
        int getDiamond = BloomAPI.getHashMapPenaltyDiamond(uuid);
        int getDeathCount = BloomAPI.getHashMapDeathCount(uuid);

        if (getCoupon > 0) {
            BloomAPI.setHashMapPenaltyCoupon(uuid, getCoupon - 1);
        } else {
            BloomAPI.setHashMapPenalty(uuid, true);
            BloomAPI.setHashMapPenaltyDiamond(uuid, getDiamond + 20);

            EventReloadScore.onReloadScore(player);
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam reload");
        }

        BloomAPI.setHashMapDeathCount(uuid, getDeathCount + 1);
        BloomAPI.getServerConfig().set("life", BloomAPI.getServerConfig().getInt("life") - 1);
        BloomAPI.PLUGIN.saveConfig();
    }
}