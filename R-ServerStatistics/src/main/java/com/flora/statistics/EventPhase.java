package com.flora.statistics;

import com.flora.api.BloomAPI;
import org.bukkit.*;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventPhase implements Listener
{
    public static int checkPhase = 0;
    public static int getBlazeRod = 0;
    public static int checkBlaze = 0;

    public static boolean phaseA = false;
    public static boolean phaseB = false;
    public static boolean phaseC = false;
    public static boolean phaseD = false;

    public static Map<String, String> offline = new HashMap<>();

    @EventHandler
    private void saveStatistics(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        offline.put(player.getUniqueId().toString(), getPlayStatistics(player));
    }
    
    @EventHandler
    private void onGamePhaseCheckA(PlayerPortalEvent event)
    {
        if (checkPhase != 1) return;

        if (Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getEnvironment() == World.Environment.NETHER)
            checkPhase = 2;
    }

    @EventHandler
    private void onGamePhaseCheckB(EntityPickupItemEvent event)
    {
        if (checkPhase != 2) return;

        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();

        if (item.getType() == Material.BLAZE_ROD && !(item.hasItemMeta()))
        {
            getBlazeRod += event.getItem().getItemStack().getAmount();

            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.setLocalizedName("BlazeRod");
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    private void onGamePhaseCheckB(PlayerPortalEvent event)
    {
        if (checkPhase != 2) return;
        Player player = event.getPlayer();

        if (Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getEnvironment() == World.Environment.NORMAL)
        {
            for (int i = 0; i < player.getInventory().getContents().length; i++) {
                ItemStack item = event.getPlayer().getInventory().getItem(i);

                if (item == null) continue;
                if (item.getType() == Material.BLAZE_ROD)
                {
                    if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasLocalizedName() && !(item.getItemMeta().hasCustomModelData())) {
                        checkBlaze += item.getAmount();

                        ItemMeta meta = item.getItemMeta();
                        meta.setCustomModelData(1);
                        item.setItemMeta(meta);

                        if (checkBlaze > 6)
                        {
                            checkPhase = 3;

                            BloomAPI.getServerConfig().set("life", BloomAPI.getServerConfig().getInt("life") + 1);
                            BloomAPI.PLUGIN.saveConfig();
                        }
                    }
                }

            }

        }
    }

    @EventHandler
    private void onGamePhaseCheckC(PlayerTeleportEvent event)
    {
        if (checkPhase != 3) return;
        if (Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getEnvironment() == World.Environment.THE_END)
            checkPhase = 4;
    }

    @EventHandler
    private void onGamePhaseCheckD(EntityDeathEvent event)
    {
        if (checkPhase != 4) return;
        if (event.getEntity() instanceof EnderDragon)
            checkPhase = 5;
    }

    /* Save Statistics */
    public static void outPutStatistics()
    {
        for (Player p : Bukkit.getOnlinePlayers())
            offline.put(p.getUniqueId().toString(), getPlayStatistics(p));

        StringBuilder result = new StringBuilder(getStatisticsTitle() + "\n");

        for (String uuid : offline.keySet())
            result.append(offline.get(uuid)).append("\n");

        createFile(getGameInfo(), "GAME-INFO.txt");
        createFile(result.toString(), "PLAY-STATISTICS.csv");
    }

    public static void outPutStatistics(String deathSign)
    {
        for (Player p : Bukkit.getOnlinePlayers())
            offline.put(p.getUniqueId().toString(), getPlayStatistics(p));

        StringBuilder result = new StringBuilder(getStatisticsTitle() + "\n");

        for (String uuid : offline.keySet())
            result.append(offline.get(uuid)).append("\n");

        createFile(getGameInfo(deathSign), "GAME-INFO.txt");
        createFile(result.toString(), "PLAY-STATISTICS.csv");
    }

    private static String getGameInfo()
    {
        SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss");

        return  " \n" +
                "게임 플라이 타임 : " + form.format(new Date(Main.gameTime)) + "\n" +
                "최종 페이즈 : " + checkPhase + "\n" +
                " \n" +
                "사인 : - \n ";
    }

    private static String getGameInfo(String sign)
    {
        SimpleDateFormat form = new SimpleDateFormat("HH:mm:ss");

        return  " \n" +
                "게임 플라이 타임 : " + form.format(new Date(Main.gameTime)) + "\n" +
                "최종 페이즈 : " + checkPhase + "\n" +
                " \n" +
                "사인 : " + sign + "\n ";
    }

    private static String getStatisticsTitle()
    {
        StringBuilder result = new StringBuilder("Name, Role, Penalty, PenaltyDiamond, ");

        for (Material material : statisticsBlocks())
            result.append("MINE.").append(material.name()).append(", ");

        for (EntityType type : statisticsEntity())
            result.append("KILL.").append(type.name()).append(", ");

        result.append(Statistic.PLAY_ONE_MINUTE.name());

        return result.toString();
    }

    private static String getPlayStatistics(Player player)
    {
        String uuid = player.getUniqueId().toString();

        StringBuilder result = new StringBuilder(player.getName() + ", " + BloomAPI.getHashMapRole(uuid) + ", " + BloomAPI.getHashMapPenalty(uuid) + ", " + BloomAPI.getHashMapPenaltyDiamond(uuid) + ", ");

        for (Material material : statisticsBlocks())
            result.append(player.getStatistic(Statistic.MINE_BLOCK, material)).append(", ");

        for (EntityType type : statisticsEntity())
            result.append(player.getStatistic(Statistic.KILL_ENTITY, type)).append(", ");

        result.append(player.getStatistic(Statistic.PLAY_ONE_MINUTE));

        return result.toString();
    }

    private static void createFile(String text, String fileName) {
        Plugin plugin = JavaPlugin.getPlugin(Main.class);
        String path = plugin.getDataFolder().getPath();

        SimpleDateFormat formA = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formB = new SimpleDateFormat("HHmmss");

        String date = "_" + formA.format(new Date(System.currentTimeMillis()));
        String time = formB.format(new Date(System.currentTimeMillis())) + "_";

        int tryCount = BloomAPI.getServerConfig().getInt("tryCount");

        String fullPath = path + "\\" + tryCount + date;

        File file = new File(fullPath, time + fileName);

        if (!file.exists()) {
            file.mkdirs();

            if (!(file.isFile()))
                file.delete();
        }

        onWritingFile(file, text);
    }

    private static void onWritingFile(File file, String text)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));

            writer.write(text);
            writer.flush();

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private static Material[] statisticsBlocks()
    {
        return new Material[] { Material.GRAVEL, Material.STONE, Material.GRANITE, Material.DIORITE, Material.ANDESITE,
                                Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE,
                                Material.DIAMOND_ORE, Material.OBSIDIAN, Material.DIRT, Material.COBBLESTONE, Material.OAK_LOG,
                                Material.BIRCH_LOG, Material.SPRUCE_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
                                Material.NETHERRACK, Material.NETHER_QUARTZ_ORE, Material.GLOWSTONE, Material.END_STONE};
    }

    private static EntityType[] statisticsEntity()
    {
        return new EntityType[]   { EntityType.RABBIT, EntityType.CHICKEN, EntityType.SHEEP, EntityType.PIG, EntityType.COW,
                                    EntityType.DONKEY, EntityType.HORSE, EntityType.LLAMA, EntityType.COD, EntityType.SALMON,
                                    EntityType.PUFFERFISH, EntityType.TROPICAL_FISH, EntityType.IRON_GOLEM, EntityType.ZOMBIE, EntityType.DROWNED,
                                    EntityType.HUSK, EntityType.ZOMBIE_VILLAGER, EntityType.SKELETON, EntityType.STRAY, EntityType.SPIDER,
                                    EntityType.CREEPER, EntityType.ENDERMAN, EntityType.PHANTOM, EntityType.WITCH, EntityType.CAVE_SPIDER,
                                    EntityType.SILVERFISH, EntityType.PILLAGER, EntityType.PIG_ZOMBIE, EntityType.GHAST, EntityType.BLAZE,
                                    EntityType.WITHER_SKELETON, EntityType.ENDER_CRYSTAL, EntityType.ENDER_DRAGON};
    }
}
