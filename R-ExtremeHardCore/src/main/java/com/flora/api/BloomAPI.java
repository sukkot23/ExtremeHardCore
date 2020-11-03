package com.flora.api;

import com.flora.hardcore.ExtremeHardCore;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.bukkit.Bukkit.*;

public class BloomAPI
{
    public static final Plugin PLUGIN = JavaPlugin.getPlugin(ExtremeHardCore.class);
    public static Map<String, BloomHashMap> playerDataList = new HashMap<>();

    public static boolean gameState = false;
    public static boolean isMove = true;
    public static boolean isRetry = false;
    public static boolean isReset = true;

    /**
     *  Plugin Command, Event Register
     */
    /* Plugin Command Register Manager */
    public static void onRegisterCommand(String command, CommandExecutor executor)
    {
        Objects.requireNonNull(getPluginCommand(command)).setExecutor(executor);
    }

    public static void onRegisterCommand(String command, CommandExecutor executor, TabCompleter completer)
    {
        Objects.requireNonNull(getPluginCommand(command)).setExecutor(executor);
        Objects.requireNonNull(getPluginCommand(command)).setTabCompleter(completer);
    }

    /* Plugin Event Register Manager */
    public static void onRegisterEvent(Listener listener, Plugin plugin)
    {
        getPluginManager().registerEvents(listener, plugin);
    }


    /**
     *  Player File / Config Data & Server Config
     */
    /* Get Player Data File to UUID*/
    public static File getDataFile(String uuid)
    {
        return new File(PLUGIN.getDataFolder() + "\\playerdata", uuid + ".dat");
    }

    /* Get Player Data File List */
    public static File[] getDataFiles()
    {
        return new File(PLUGIN.getDataFolder() + "\\playerdata").listFiles();
    }

    /* Get Player Config Data to UUID */
    public static FileConfiguration getDataConfig(String uuid)
    {
        return YamlConfiguration.loadConfiguration(getDataFile(uuid));
    }

    /* Get Player Data File to PlayerName */
    public static File getDataFileToName(String playerName) throws NullPointerException
    {
        for (File file : getDataFiles()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            if (Objects.requireNonNull(config.getString("name")).equalsIgnoreCase(playerName))
                return file;
        }
        return null;
    }

    /* Get Player Data File to PlayerName */
    public static FileConfiguration getDataConfigToName(String playerName) throws NullPointerException
    {
        return YamlConfiguration.loadConfiguration(Objects.requireNonNull(getDataFileToName(playerName)));
    }

    /* Save Player Config Data */
    public static void saveDataFile(FileConfiguration config, File file)
    {
        try {
            config.save(file);
        } catch (IOException e) {
            System.out.println("§cFile I/O Error!!");
        }
    }

    /* Get Server Config */
    public static FileConfiguration getServerConfig()
    {
        return PLUGIN.getConfig();
    }


    /**
     * Online Player HashMap Data
     */
    /* Update Online Player Data*/
    public static void updatePlayerData(String uuid)
    {
        BloomHashMap value = new BloomHashMap(uuid);
        playerDataList.put(uuid, value);
    }

    /* Update Server Online Player Data */
    public static void updateAllPlayerData()
    {
        for (Player p : getOnlinePlayers())
            updatePlayerData(p.getUniqueId().toString());
    }

    /* Save Online Player Data */
    public static void savePlayerData(String uuid)
    {
        BloomHashMap value = playerDataList.get(uuid);
        FileConfiguration config = getDataConfig(uuid);

        config.set("role", value.role);
        config.set("penalty", value.penalty);
        config.set("penalty_diamond", value.penalty_diamond);
        config.set("penalty_coupon", value.penalty_coupon);
        config.set("death_count", value.death_count);

        config.set("last_date", LocalDate.now().toString());

        saveDataFile(config, getDataFile(uuid));
    }

    /* Save Online All Player Data */
    public static void saveAllPlayerData()
    {
        for (Player p : getOnlinePlayers())
            savePlayerData(p.getUniqueId().toString());
    }

    /**
     * Get HashMap Data
     */
    public static String getHashMapName(String uuid)
    {
        return playerDataList.get(uuid).name;
    }

    public static String getHashMapUUID(String uuid)
    {
        return playerDataList.get(uuid).uniqueID;
    }

    public static boolean getHashMapPenalty(String uuid)
    {
        return playerDataList.get(uuid).penalty;
    }

    public static int getHashMapRole(String uuid)
    {
        return playerDataList.get(uuid).role;
    }

    public static int getHashMapPenaltyDiamond(String uuid)
    {
        return playerDataList.get(uuid).penalty_diamond;
    }

    public static int getHashMapPenaltyCoupon(String uuid)
    {
        return playerDataList.get(uuid).penalty_coupon;
    }

    public static int getHashMapDeathCount(String uuid)
    {
        return playerDataList.get(uuid).death_count;
    }


    /**
     * Set HashMap Data
     */
    public static void setHashMapPenalty(String uuid, boolean value)
    {
        BloomHashMap hashValue =
                new BloomHashMap(uuid, getHashMapName(uuid), getHashMapUUID(uuid), value, getHashMapRole(uuid), getHashMapPenaltyDiamond(uuid), getHashMapPenaltyCoupon(uuid), getHashMapDeathCount(uuid));
        playerDataList.put(uuid, hashValue);
    }

    public static void setHashMapRole(String uuid, int value)
    {
        BloomHashMap hashValue =
                new BloomHashMap(uuid, getHashMapName(uuid), getHashMapUUID(uuid), getHashMapPenalty(uuid), value, getHashMapPenaltyDiamond(uuid), getHashMapPenaltyCoupon(uuid), getHashMapDeathCount(uuid));
        playerDataList.put(uuid, hashValue);
    }

    public static void setHashMapPenaltyDiamond(String uuid, int value)
    {
        BloomHashMap hashValue =
                new BloomHashMap(uuid, getHashMapName(uuid), getHashMapUUID(uuid), getHashMapPenalty(uuid), getHashMapRole(uuid), value, getHashMapPenaltyCoupon(uuid), getHashMapDeathCount(uuid));
        playerDataList.put(uuid, hashValue);
    }

    public static void setHashMapPenaltyCoupon(String uuid, int value)
    {
        BloomHashMap hashValue =
                new BloomHashMap(uuid, getHashMapName(uuid), getHashMapUUID(uuid), getHashMapPenalty(uuid), getHashMapRole(uuid), getHashMapPenaltyDiamond(uuid), value, getHashMapDeathCount(uuid));
        playerDataList.put(uuid, hashValue);
    }

    public static void setHashMapDeathCount(String uuid, int value)
    {
        BloomHashMap hashValue =
                new BloomHashMap(uuid, getHashMapName(uuid), getHashMapUUID(uuid), getHashMapPenalty(uuid), getHashMapRole(uuid), getHashMapPenaltyDiamond(uuid), getHashMapPenaltyCoupon(uuid), value);
        playerDataList.put(uuid, hashValue);
    }


    /**
     *  change Global variable value
     */
    public static void changeGameState()
    {
        BloomAPI.gameState = !BloomAPI.gameState;
    }

    public static void changeIsMove()
    {
        BloomAPI.isMove = !BloomAPI.isMove;
    }

    public static void changeIsRetry()
    {
        BloomAPI.isRetry = !BloomAPI.isRetry;
    }

    public static void changeIsReset()
    {
        BloomAPI.isReset = !BloomAPI.isReset;
    }



    /**
     * Player Send Message
     */
    /* Send Message for Operator Player */
    public static void playerSendOpMessage(String message)
    {
        for (Player p : getOnlinePlayers())
            if (p.isOp()) {
                p.sendMessage(message);
                System.out.println(message);
            }
    }

    /* Send Message for All Player */
    public static void AllPlayerSendPlayerMessage(String message)
    {
        for (Player p : getOnlinePlayers())
            p.sendMessage(message);
    }

    /* Send ActionBar Message for Player */
    public static void playerSendActionMessage(Player player, String message)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    /* Send ActionBar Message for All Player */
    public static void AllPlayerSendActionMessage(String message)
    {
        for (Player player : Bukkit.getOnlinePlayers())
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }




    /**
     * ScoreBoard Manager
     */
    public static Team getRoleTeam(int role)
    {
        switch (role) {
            case 0:
                return getTeam("normal", ChatColor.GRAY);

            case 1:
                return getTeam("underground", ChatColor.DARK_GRAY);

            case 2:
                return getTeam("ground", ChatColor.DARK_GREEN);

            case 3:
                return getTeam("manager", ChatColor.LIGHT_PURPLE);

            default:
                return null;
        }
    }

    public static Team getPenaltyTeam(boolean penalty)
    {
        if (penalty) return getTeam("penalty", ChatColor.RED);
        else return null;
    }

    private static Team getTeam(String teamName, ChatColor color)
    {
        Scoreboard board = Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard();
        Team team = board.getTeam(teamName);

        if (team == null) {
            Team newTeam = board.registerNewTeam(teamName);
            newTeam.setColor(color);
            return newTeam;
        }
        return team;
    }


    /**
     * ScoreBoard Object
     */
    public static Objective getScore(String objectName)
    {
        Scoreboard board = Objects.requireNonNull(getServer().getScoreboardManager()).getMainScoreboard();
        Objective score = board.getObjective(objectName);

        if (score == null) {
            Objective newScore = board.registerNewObjective(objectName, "dummy", objectName);
            newScore.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            return newScore;
        }
        return score;
    }

}
