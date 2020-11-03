package com.flora.hardcore.event;

import com.flora.api.BloomAPI;
import com.flora.hardcore.command.CommandEdit;
import com.flora.hardcore.gui.InventoryIcon;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EventGameControl implements Listener
{
    HashMap<Player, List<ItemStack>> armorItems = new HashMap<>();


    /* Give Game Start Item */
    @EventHandler
    private void onGiveGameStartItem(PlayerJoinEvent event)
    {
        if (BloomAPI.gameState) return;
        String streamer = BloomAPI.getServerConfig().getString("streamName");
        Player player = event.getPlayer();

        if (player.getName().equalsIgnoreCase(streamer))
            player.getInventory().addItem(CommandEdit.itemTigerHair());
    }

    /* Game Start Action Event */
    @EventHandler
    private void onGameStartEvent(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();

        if (!(Objects.equals(event.getOffHandItem(), CommandEdit.itemTigerHair()))) return;
        if (BloomAPI.gameState) return;

        Objects.requireNonNull(event.getOffHandItem()).setAmount(0);
        gameStartScript();
    }

    /* Game Start Script - Inventory Clear TRUE */
    private void gameStartScript()
    {
        int tryCount = BloomAPI.getServerConfig().getInt("tryCount");

        new BukkitRunnable()
        {
            int count = 0;
            @Override
            public void run()
            {
                count++;

                switch (count) {
                    case 1:
                        BloomAPI.changeGameState();

                        for (World world : Bukkit.getWorlds()) {
                            world.setHardcore(true);
                            world.setDifficulty(Difficulty.HARD);
                            world.setTime(0);
                            world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
                        }

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            for (int i = 0; i < 101; i++)
                                player.sendMessage("");

                            player.sendMessage( " \n" +
                                    "§7───────────────────────────────────\n \n" +
                                    "                     §f☠ §c극한 하드코어 모드 §f☠   \n" +
                                    " \n" +
                                    "                §f단 한명이라도 죽는다면 서버가 리셋!!\n" +
                                    "                    §6동우동§f의 §c극한 하드코어 모드 \n" +
                                    "                        §bMADE BY SUKKOT\n" +
                                    "\n \n" +
                                    "§7───────────────────────────────────\n" +
                                    " ");

                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.3f, 0);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3, 9));
                            player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 3, 9));
                            player.sendTitle("§f☠ §c극한 하드코어 모드 §f☠", "현재 §6" + tryCount + "트§r 진행중", 30, 60, 30);
                            player.getInventory().clear();
                            Bukkit.setDefaultGameMode(GameMode.SURVIVAL);

                            if (player.isOp() && (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)) {
                                Location loc = player.getLocation().clone();
                                int y = player.getWorld().getHighestBlockYAt(loc) + 1;
                                loc.setY(y);

                                player.teleport(loc);
                            }

                            player.setGameMode(GameMode.SURVIVAL);
                        }

                        for (Entity e : Bukkit.getWorlds().get(0).getEntities())
                            if (e instanceof Item)
                                e.remove();
                        break;

                    case 2:
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam");
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rule clear");
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rule runTime 1");
                        break;

                    default:
                        cancel();
                }
            }
        }.runTaskTimer(BloomAPI.PLUGIN, 20L, 20L * 6);
    }


    /* Game ReStart Script - Inventory Clear FALSE */
    private void gameRestartScript()
    {
        int tryCount = BloomAPI.getServerConfig().getInt("tryCount");

        new BukkitRunnable()
        {
            @Override
            public void run() {
                if (!(BloomAPI.gameState))
                    BloomAPI.changeGameState();

                if (!(BloomAPI.isReset))
                    BloomAPI.changeIsReset();

                if (BloomAPI.isRetry)
                    BloomAPI.changeIsRetry();

                for (World world : Bukkit.getWorlds()) {
                    world.setHardcore(true);
                    world.setDifficulty(Difficulty.HARD);
                    world.setTime(0);
                    world.setGameRule(GameRule.DO_MOB_SPAWNING, true);
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (int i = 0; i < 101; i++)
                        player.sendMessage("");

                    player.sendMessage( " \n" +
                            "§7───────────────────────────────────\n \n" +
                            "                     §f☠ §c극한 하드코어 모드 §f☠   \n" +
                            " \n" +
                            "                §f단 한명이라도 죽는다면 서버가 리셋!!\n" +
                            "                    §6동우동§f의 §c극한 하드코어 모드 \n" +
                            "                        §bMADE BY SUKKOT\n" +
                            "\n \n" +
                            "§7───────────────────────────────────\n" +
                            " ");

                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_AMBIENT, 0.3f, 0);
                    player.sendTitle("§f☠ §c극한 하드코어 모드 §f☠", "현재 §6" + tryCount + "트§r 진행중", 30, 60, 30);
                    Bukkit.setDefaultGameMode(GameMode.SURVIVAL);

                    if (player.isOp() && (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)) {
                        Location loc = player.getLocation().clone();
                        int y = player.getWorld().getHighestBlockYAt(loc) + 1;
                        loc.setY(y);

                        player.teleport(loc);
                    }

                    player.setGameMode(GameMode.SURVIVAL);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rule clear");
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rule runTime 1");
                }
            }
        }.runTaskLater(BloomAPI.PLUGIN, 20L);
    }

    /* Operator SpectorMod Inventory Click Event */
    @EventHandler
    private void onIconButtonClickEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        if (!(isClickButton(event, player))) return;
        event.setCancelled(true);

        onInteractIcon(event, player);
    }

    private boolean isClickButton(InventoryClickEvent e, Player p)
    {
        if (!(p.isOp())) return false;
        if (!(p.getGameMode() == GameMode.SPECTATOR)) return false;
        if (e.getClickedInventory() == null) return false;
        if (e.getCurrentItem() == null) return false;
        if (e.getClickedInventory() != e.getWhoClicked().getInventory()) return false;
        return e.getSlot() == 36 || e.getSlot() == 37 || e.getSlot() == 38 || e.getSlot() == 39 || e.getSlot() == 40;
    }

    private void onInteractIcon(InventoryClickEvent event, Player player)
    {
        switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
            case BROWN_DYE:
                gameRestartScript();
                player.getInventory().setItem(37, InventoryIcon.iconOperator(37));
                player.getInventory().setItem(36, InventoryIcon.iconOperator(36));
                break;

            case GOLDEN_CARROT:
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rpenalty");
                player.getInventory().setItem(37, InventoryIcon.iconOperator(37));
                player.getInventory().setItem(36, InventoryIcon.iconOperator(36));
                break;

            case TOTEM_OF_UNDYING:
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "edit game retry");
                player.getInventory().setItem(37, InventoryIcon.iconOperator(37));
                break;

            case BARRIER:
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "edit game reset");
                player.getInventory().setItem(36, InventoryIcon.iconOperator(36));
                break;

            case ENDER_EYE:
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "rteam");
                break;
        }
    }


    /* Operator Player Set Inventory */
    @EventHandler
    private void onOperatorReSpawnEvent(PlayerRespawnEvent event)
    {
        Player player = event.getPlayer();
        if (!(player.isOp())) return;

        if (player.getGameMode() == GameMode.SPECTATOR)
            setOperatorCreInventory(player);
    }

    @EventHandler
    private void onOperatorGameModeChangeEvent(PlayerGameModeChangeEvent event)
    {
        Player player = event.getPlayer();
        if (!(player.isOp())) return;

        if (player.getGameMode() == GameMode.SPECTATOR)
            switch (event.getNewGameMode()) {
                case SURVIVAL:
                case ADVENTURE:
                case CREATIVE:
                    setOperatorSubInventory(player);
            }
        else if (event.getNewGameMode() == GameMode.SPECTATOR)
            setOperatorCreInventory(player);
    }

    private void setOperatorCreInventory(Player player)
    {
        List<ItemStack> itemList = new ArrayList<>();

        for (int i = 36; i < 41; i++) {
            ItemStack item = player.getInventory().getItem(i);

            if (item != null)
                itemList.add(item);
            else
                itemList.add(new ItemStack(Material.AIR));

            player.getInventory().setItem(i, InventoryIcon.iconOperator(i));
        }

        armorItems.put(player, itemList);
    }

    private void setOperatorSubInventory(Player player)
    {
        for (int i = 36; i < 41; i++) {
            if (armorItems.get(player) != null)
                player.getInventory().setItem(i, armorItems.get(player).get(i - 36));
            else
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
        }
    }
}
