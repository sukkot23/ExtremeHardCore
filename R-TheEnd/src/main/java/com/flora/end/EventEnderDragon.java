package com.flora.end;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.flora.api.BloomAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class EventEnderDragon implements Listener
{
    @EventHandler
    private void onPlayerJoinEndEvent(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) return;
        if (Objects.requireNonNull(Objects.requireNonNull(event.getTo()).getWorld()).getEnvironment() != World.Environment.THE_END)
            return;

        showTypingTitleEffect(event.getPlayer());
    }


    @EventHandler
    private void EntityAttackEvent(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) { return; }
        EnderDragon dragon = (EnderDragon) event.getEntity();

        if (dragon.getBossBar() == null) { return; }
        if (event.getDamager().getType() == EntityType.PLAYER) {
            cancelledNearAttackDamage(event, dragon);
        }

        setEnderDragonHealthBar(dragon);
    }

    @EventHandler
    private void EnderDragonProjectileDamageEvent(ProjectileHitEvent event)
    {
        if (event.getHitEntity() == null) return;
        if (!(event.getHitEntity() instanceof EnderDragonPart)) return;
        if (event.getEntity().getShooter() == null) return;
        if (!(event.getEntity().getShooter() instanceof  Player)) return;

        EnderDragonPart part = (EnderDragonPart) event.getHitEntity();
        EnderDragon dragon = part.getParent();
        Player player = (Player) event.getEntity().getShooter();

        if(!(dragon.getPhase() == EnderDragon.Phase.SEARCH_FOR_BREATH_ATTACK_TARGET)) return;

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c* 드래곤의 방어태세로 인해 피해량이 감소합니다 *"));
        player.getWorld().playSound(dragon.getLocation(), Sound.ENTITY_ENDER_DRAGON_HURT, 100, 1);

        dragon.setHealth(dragon.getHealth() - 0.75);
        event.getEntity().remove();

        dragon.playEffect(EntityEffect.HURT);

        setEnderDragonHealthBar(dragon);
    }

    private void cancelledNearAttackDamage(EntityDamageByEntityEvent event, EnderDragon dragon)
    {
        event.setDamage(0);

        Team team = getBarrierTeam();
        if (!(team.getEntries().contains(dragon.getUniqueId().toString())))
            team.addEntry(dragon.getUniqueId().toString());

        BloomAPI.AllPlayerSendActionMessage("§9* 엔더드래곤의 강력한 힘에 의해 근거리 공격이 무효화 되었습니다 *");

        dragon.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2 * 20, 1, false, false));
    }

    @EventHandler
    private void EntityHealEvent(EntityRegainHealthEvent event)
    {
        if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) { return; }
        EnderDragon dragon = (EnderDragon) event.getEntity();

        if (dragon.getBossBar() == null) { return; }
        setEnderDragonHealthBar(dragon);
    }

    @EventHandler
    private void EntityHealingEvent(EnderDragonChangePhaseEvent event)
    {
        if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) { return; }
        EnderDragon dragon = event.getEntity();

        if (dragon.getBossBar() == null) { return; }
        setEnderDragonHealthBar(dragon);
    }

    @EventHandler
    private void EnderDragonSpawnEvent(CreatureSpawnEvent event)
    {
        if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) { return; }
        EnderDragon dragon = (EnderDragon) event.getEntity();

        if (dragon.getBossBar() == null) { return; }
        Team team = getBarrierTeam();
        team.addEntry(dragon.getUniqueId().toString());
        setEnderDragonHealthBar(dragon);
    }

    @EventHandler
    private void EnderDragonDeathEvent(EntityDeathEvent event)
    {
        if (!(event.getEntityType() == EntityType.ENDER_DRAGON)) { return; }
        if (!(event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END)) { return; }
        EnderDragon dragon = (EnderDragon) event.getEntity();

        Team team = getDeadTeam();
        if (!(team.getEntries().contains(dragon.getUniqueId().toString())))
            team.addEntry(dragon.getUniqueId().toString());

        dragon.setGlowing(true);
        kingsmanPartyEffect(dragon);
    }

    private void kingsmanPartyEffect(EnderDragon dragon)
    {
        World world = dragon.getWorld();

        for (Entity entity : world.getEntities())
        {
            if (entity == null) continue;
            if (entity.getType() == EntityType.ENDERMAN)
            {
                Enderman e = (Enderman) entity;
                kingsmanEffectForEnderMan(e);
            }
        }

        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    finalWorldSetting();
                }
            }.runTaskLater(JavaPlugin.getPlugin(M_End.class), 60L);
        } catch (Exception exception) {
            Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(M_End.class));
        }
    }

    private void kingsmanEffectForEnderMan(Enderman enderman)
    {
        enderman.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20*10, 5, true));

        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    enderman.remove();
                    onSpawnFireWork(enderman.getLocation());
                }
            }.runTaskLater(JavaPlugin.getPlugin(M_End.class), 60L);
        } catch (Exception exception) {
            Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(M_End.class));
        }
    }

    private void onSpawnFireWork(Location location)
    {
        Firework firework = (Firework) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.FIREWORK);

        FireworkMeta meta = firework.getFireworkMeta();

        meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL_LARGE)
                .withColor(Color.fromRGB(255, 128, 255))
                .withFade(Color.BLACK)
                .build());

        meta.addEffect(FireworkEffect.builder()
                .with(FireworkEffect.Type.BALL)
                .withColor(Color.WHITE)
                .withFade(Color.BLACK)
                .build());

        firework.setFireworkMeta(meta);
    }

    private void finalWorldSetting()
    {
        if (BloomAPI.gameState)
            BloomAPI.changeGameState();

        if (BloomAPI.isRetry)
            BloomAPI.changeIsRetry();

        if (BloomAPI.isReset)
            BloomAPI.changeIsReset();

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "phase 5973 " + "클리어");


        int tryCount = BloomAPI.getServerConfig().getInt("tryCount");

        for (World w : Bukkit.getWorlds()) {
            w.setHardcore(false);
            w.setDifficulty(Difficulty.PEACEFUL);
        }


        for(Player p : Bukkit.getOnlinePlayers())
            p.sendTitle("§5엔더드래곤이 격파되었습니다!!", "§6최종 도전횟수 §e" + tryCount + "트", 50, 200, 50);
    }

    private void setEnderDragonHealthBar(EnderDragon dragon)
    {
        int maxHealth = (int) Objects.requireNonNull(dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        int health = (int) dragon.getHealth();

        BossBar bar = dragon.getBossBar();
        assert bar != null;
        bar.setTitle("엔더드래곤 (" + health + "/" + maxHealth + ")");

        if (maxHealth / 2 < health) { bar.setColor(BarColor.GREEN); }
        else if (maxHealth / 2 / 2 < health) { bar.setColor(BarColor.YELLOW); }
        else if (maxHealth / 2 / 2 > health) { bar.setColor(BarColor.RED); }
    }



    private Team getBarrierTeam()
    {
        if (Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeam("dragon") == null) {
            Team newTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("dragon");
            newTeam.setColor(ChatColor.BLUE);
            return newTeam;
        }
        else
            return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dragon");
    }

    private Team getDeadTeam()
    {
        if (Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getTeam("dead") == null) {
            Team newTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("dead");
            newTeam.setColor(ChatColor.GOLD);
            return newTeam;
        }
        else
            return Bukkit.getScoreboardManager().getMainScoreboard().getTeam("dead");
    }




    /* 2020.07 - Old Source */
    private void showTypingTitleEffect(Player player)
    {
        try {
            String[] texts = "엔더드래곤을 잡고 평화를 되찾아주세요...".split("");
            int typingSpeed = 4;

            new BukkitRunnable() {
                String tex = "";
                int numbers = -1;

                public void run()
                {
                    numbers ++;
                    tex += texts[numbers];
                    player.sendTitle("", "§e" + tex, 0, 100, 0);

                    if (texts[numbers].equals(texts[numbers].trim()))
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 0.8F, 0.840896F);

                    if (numbers > texts.length - 2) cancel();
                }
            }.runTaskTimer(JavaPlugin.getPlugin(M_End.class), 20, typingSpeed);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(M_End.class));
        }
    }

    public void sendGlowingEffect(Entity entity)
    {
        entity.setGlowing(true);

        try {
            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.setGlowing(false);
                }
            }.runTaskLater(JavaPlugin.getPlugin(M_End.class), 30L);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getScheduler().cancelTasks(JavaPlugin.getPlugin(M_End.class));
        }
    }

    public void sendGlowingEffectPacket(Entity entity)
    {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, entity.getEntityId());

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);

        watcher.setEntity(entity);
        watcher.setObject(0, serializer,(byte) 64);

        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            for (Player p : Bukkit.getOnlinePlayers())
                ProtocolLibrary.getProtocolManager().sendServerPacket(p, packet); }
        catch (InvocationTargetException e) { e.printStackTrace(); }
    }
}
